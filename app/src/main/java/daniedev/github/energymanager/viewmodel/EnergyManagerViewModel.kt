package daniedev.github.energymanager.viewmodel

import android.content.DialogInterface.BUTTON_POSITIVE
import android.content.SharedPreferences
import android.util.DisplayMetrics
import android.widget.Toast
import androidx.lifecycle.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import daniedev.github.energymanager.R
import daniedev.github.energymanager.model.NotifyConfirmationRequest
import daniedev.github.energymanager.model.RequestPowerFromLocationRequest
import daniedev.github.energymanager.model.User
import daniedev.github.energymanager.provider.EnergyManagerServiceProvider
import daniedev.github.energymanager.provider.ResourceProvider
import daniedev.github.energymanager.provider.TokenProvider
import daniedev.github.energymanager.utils.common.*
import daniedev.github.energymanager.utils.dialog.DialogEvent
import daniedev.github.energymanager.utils.dialog.EventContext
import daniedev.github.energymanager.utils.dialog.EventContext.*
import daniedev.github.energymanager.utils.firebase.NODE_USERS
import daniedev.github.energymanager.utils.storage.DataShelf
import daniedev.github.energymanager.view.SignInActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.reflect.KClass

class EnergyManagerViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val displayMetrics: DisplayMetrics,
    private val databaseReference: DatabaseReference,
    private val tokenProvider: TokenProvider,
    private val sharedPreferences: SharedPreferences,
    private val energyManagerServiceProvider: EnergyManagerServiceProvider,
    private val dataShelf: DataShelf,
    private val resourceProvider: ResourceProvider
) : BaseViewModel(), OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener,
    GoogleMap.OnMarkerClickListener {

    private var mapData = ArrayList<MapData>()
    private lateinit var googleMap: GoogleMap
    private var fireBaseToken: String? = null
    private lateinit var userLocationInfo: UserLocationInfo
    private lateinit var selectedLocation: LatLng
    private var disposable: CompositeDisposable = CompositeDisposable()
    val startActivityEvent: LiveData<KClass<*>>
        get() = _startActivityEvent
    private val _startActivityEvent = MutableLiveData<KClass<*>>()
    val showDialogEvent: LiveData<DialogEvent>
        get() = _showDialogEvent
    private val _showDialogEvent = MutableLiveData<DialogEvent>()
    val startLoadingMaps: LiveData<Boolean>
        get() = _startLoadingMaps
    private val _startLoadingMaps = MutableLiveData<Boolean>()
    val showToastMessage: LiveData<Pair<String, Int>>
        get() = _showToastMessage
    private val _showToastMessage = MutableLiveData<Pair<String, Int>>()
    val showProgressBar: LiveData<Boolean>
        get() = _showProgressBar
    private val _showProgressBar = MutableLiveData(false)

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        checkForAuthentication()
    }

    private fun checkForAuthentication() {
        if (firebaseAuth.currentUser == null)
            _startActivityEvent.postValue(SignInActivity::class)
        else
            fetchToken()
    }

    private fun fetchToken() {
        viewModelScope.launch {
            tokenProvider.tokenStateFlow
                .filter { it.isNotEmpty() }
                .collect {
                    fireBaseToken = it
                    validateUserLocationCache(sharedPreferences.getUserLocationCache())
                }
        }
    }

    override fun onMapReady(p0: GoogleMap) {
        googleMap = p0
        mapData.forEach { data ->
            data.marker = googleMap.addMarker(MarkerOptions().position(data.latLng))
                .also {
                    it?.tag = data.position
                    it?.title = data.title
                    it?.snippet = resourceProvider.getResources()
                        .getString(R.string.available_power, data.availablePower)
                }
        }
        googleMap.setOnMarkerClickListener(this)
        googleMap.setOnInfoWindowClickListener(this)
        val latLongBuilder = LatLngBounds.Builder()
        for (latLng in availablePlaces)
            latLongBuilder.include(latLng.key)
        val latLngBounds = latLongBuilder.build()
        val updateCamera =
            CameraUpdateFactory.newLatLngBounds(latLngBounds, displayMetrics.widthPixels, 300, 0)
        googleMap.animateCamera(updateCamera)
    }

    override fun onMarkerClick(p0: Marker): Boolean {
        selectedLocation = p0.position
        return false
    }

    override fun onInfoWindowClick(p0: Marker) {
        val currentPlaceInfo = mapData[p0.tag as Int]
        with(resourceProvider) {
            val message = getResources()
                .getString(R.string.request_text, currentPlaceInfo.availablePower)
            val dialogEvent =
                DialogEvent(
                    currentPlaceInfo.title,
                    message,
                    getString(R.string.dialog_button_message_yes),
                    getString(R.string.dialog_button_message_no),
                    shouldPublishUserInput = REQUEST_POWER_CONFIRMATION
                )
            _showDialogEvent.postValue(dialogEvent)
        }
    }

    override fun onDialogButtonPressed(eventContext: EventContext, buttonPressed: Int) {
        val isPositiveResponseReceived = buttonPressed == BUTTON_POSITIVE
        when (eventContext) {
            FETCH_LOCATION -> {
                removeCurrentLocationMarker()
                showAvailablePlaces()
                registerDevice()
                cacheUserLocation()
            }
            REQUEST_POWER_CONFIRMATION -> {
                if (isPositiveResponseReceived) {
                    requestPowerFromLocation()
                }
            }
            REQUEST_POWER_PUSH_NOTIFICATION -> {
                sendPowerRequestResponseToUser(isPositiveResponseReceived)
            }
        }
    }

    private fun requestPowerFromLocation() {
        val request = RequestPowerFromLocationRequest(
            requesterName = firebaseAuth.currentUser?.displayName
                ?: resourceProvider.getString(R.string.generic_user_name_singular),
            requesterDeviceToken = fireBaseToken!!,
            receiverLocation = selectedLocation,
            eventContext = REQUEST_POWER_PUSH_NOTIFICATION
        )
        disposable.add(
            energyManagerServiceProvider.requestPowerFromLocation(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { showLoading() }
                .doFinally { hideLoading() }
                .subscribe(
                    { response -> showToastMessage(response.message) },
                    { e ->
                        e.printStackTrace()
                        showToastMessage(
                            e.localizedMessage
                                ?: resourceProvider.getString(R.string.generic_error_message)
                        )
                    })
        )
    }

    private fun showToastMessage(message: String) =
        _showToastMessage.postValue(message to Toast.LENGTH_LONG)

    private fun sendPowerRequestResponseToUser(isRequestGranted: Boolean) {
        val request = NotifyConfirmationRequest(
            isRequestGranted,
            dataShelf.pop(REQUESTER_DEVICE_TOKEN) as String
        )
        disposable.add(
            energyManagerServiceProvider.notifyConfirmationToUser(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    showLoading()
                }
                .doFinally {
                    hideLoading()
                }
                .subscribe(
                    { response ->
                        showToastMessage(response.message)
                    },
                    { e ->
                        e.printStackTrace()
                        showToastMessage(
                            e.localizedMessage
                                ?:  resourceProvider.getString(R.string.generic_error_message)
                        )
                    })
        )

    }


    override fun onDialogItemSelected(eventContext: EventContext, itemSelected: Int) {
        when (eventContext) {
            FETCH_LOCATION -> {
                userLocationInfo = UserLocationInfo(
                    locationReference = itemSelected,
                    coordinates = availablePlaces.keys.elementAt(itemSelected)
                )
            }
            else -> return
        }
    }

    private fun removeCurrentLocationMarker() {
        availablePlaces.remove(userLocationInfo.coordinates)
        updateMapData()
    }

    private fun updateMapData() {
        var count = 0
        mapData.clear()
        availablePlaces.forEach {
            mapData.add(
                MapData(
                    latLng = it.key,
                    title = it.value,
                    position = count++,
                    availablePower = Random().nextInt(900) + 100
                )
            )
        }
    }

    private fun showAvailablePlaces() = _startLoadingMaps.postValue(true)

    private fun validateUserLocationCache(userLocationReference: Int) {
        if (userLocationReference != RESOURCE_NOT_AVAILABLE_INT) {
            userLocationInfo = UserLocationInfo(
                locationReference = userLocationReference,
                coordinates = availablePlaces.keys.elementAt(userLocationReference)
            )
            removeCurrentLocationMarker()
            showAvailablePlaces()
        } else
            getLocationInfoFromUser()
    }

    private fun SharedPreferences.getUserLocationCache() =
        this.getInt(USER_LOCATION, RESOURCE_NOT_AVAILABLE_INT)

    private fun getLocationInfoFromUser() {
        val fetchLocationDialog = DialogEvent(
            title = resourceProvider.getString(R.string.fetch_location_dialog_header),
            positiveButtonMessage = resourceProvider.getString(R.string.dialog_button_message_okay),
            itemList = availablePlaces.values.map { it }.toTypedArray(),
            shouldPublishUserInput = FETCH_LOCATION
        )
        _showDialogEvent.postValue(fetchLocationDialog)
    }

    private fun registerDevice() {
        val userInfo: User?
        val name = firebaseAuth.currentUser?.displayName ?:resourceProvider.getString(R.string.generic_user_name)
        val email = firebaseAuth.currentUser?.email
        if (email != null && fireBaseToken != null) {
            userInfo = User(
                name,
                email,
                userLocationInfo.coordinates.latitude.toString(),
                userLocationInfo.coordinates.longitude.toString(),
                fireBaseToken!!
            )
        } else
            return

        userInfo.let {
            databaseReference.child(NODE_USERS)
                .child(firebaseAuth.currentUser!!.uid)
                .setValue(userInfo)
        }
    }

    private fun showLoading() = _showProgressBar.postValue(true)

    private fun hideLoading() = _showProgressBar.postValue(false)

    private fun cacheUserLocation() =
        sharedPreferences.edit().putInt(USER_LOCATION, userLocationInfo.locationReference).apply()

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}