package daniedev.github.energymanager.viewmodel

import android.content.DialogInterface.BUTTON_POSITIVE
import android.util.DisplayMetrics
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import daniedev.github.energymanager.model.User
import daniedev.github.energymanager.provider.TokenProvider
import daniedev.github.energymanager.shared.dialog.DialogEvent
import daniedev.github.energymanager.shared.dialog.EventContext
import daniedev.github.energymanager.shared.dialog.EventContext.FETCH_LOCATION
import daniedev.github.energymanager.shared.viewmodel.BaseViewModel
import daniedev.github.energymanager.utils.common.MapData
import daniedev.github.energymanager.utils.common.availablePlaces
import daniedev.github.energymanager.utils.firebase.NODE_USERS
import daniedev.github.energymanager.view.SignInActivity
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.reflect.KClass

class EnergyManagerViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val displayMetrics: DisplayMetrics,
    private val databaseReference: DatabaseReference,
    private val tokenProvider: TokenProvider
) : BaseViewModel(), OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMarkerClickListener {

    private var mapData = ArrayList<MapData>()
    private lateinit var googleMap: GoogleMap
    private var fireBaseToken: String? = null
    private lateinit var currentUserLocation: LatLng
    private lateinit var selectedLocation: LatLng
    val startActivityEvent: LiveData<KClass<*>>
        get() = _startActivityEvent
    private val _startActivityEvent = MutableLiveData<KClass<*>>()
    val showDialogEvent: LiveData<DialogEvent>
        get() = _showDialogEvent
    private val _showDialogEvent = MutableLiveData<DialogEvent>()

    init {
        var count = 0
        availablePlaces.forEach {
            mapData.add(
                MapData(
                    latLng = it.key,
                    title = it.value,
                    position = count++,
                    availablePower = it.key.latitude.toString().let { latitude ->
                        latitude.substring(latitude.length - 2).toInt() * 10
                    }
                )
            )
        }
    }

    override fun onStart() {
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
                    getLocationInfoFromUser()
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
                    it?.snippet = "Available Power: ${data.availablePower} KW"
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
        val message = """
        Available Power: ${currentPlaceInfo.availablePower} kW
        Would you like to request?
        """.trimIndent()
        val dialogEvent =
            DialogEvent(currentPlaceInfo.title, message, "yes", "no")
        _showDialogEvent.postValue(dialogEvent)
    }

    override fun onDialogButtonPressed(eventContext: EventContext, buttonPressed: Int) {
        val isPositiveResponseReceived = buttonPressed == BUTTON_POSITIVE
        when (eventContext) {
            FETCH_LOCATION -> {
                if (isPositiveResponseReceived)
                    registerDevice()
            }
            else -> return
        }
    }

    override fun onDialogItemSelected(eventContext: EventContext, itemSelected: Int) {
        when (eventContext) {
            FETCH_LOCATION -> {
                currentUserLocation = availablePlaces.keys.elementAt(itemSelected)
            }
            else -> return
        }
    }

    private fun getLocationInfoFromUser() {
        val fetchLocationDialog = DialogEvent(
            title = "Tell us where you live",
            positiveButtonMessage = "yes",
            itemList = availablePlaces.values.map { it }.toTypedArray(),
            shouldPublishUserInput = FETCH_LOCATION
        )
        _showDialogEvent.postValue(fetchLocationDialog)
    }

    private fun registerDevice() {
        val userInfo: User?
        val email = firebaseAuth.currentUser?.email
        if (email != null && fireBaseToken != null) {
            userInfo = User(
                email,
                currentUserLocation.latitude.toString(),
                currentUserLocation.longitude.toString(),
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
}