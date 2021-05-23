package daniedev.github.energymanager.view

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import dagger.android.AndroidInjection
import daniedev.github.energymanager.R
import daniedev.github.energymanager.databinding.ActivityMapsBinding
import daniedev.github.energymanager.viewmodel.EnergyManagerViewModel
import javax.inject.Inject


class EnergyManagerActivity : AppCompatActivity(), OnMapReadyCallback,
    GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private var mapData = ArrayList<MapData>()
    private var fireBaseToken: String? = null
    private var currentLatitude: String? = null
    private var currentLongitude: String? = null

    private val CHANNEL_ID = "energy_manager"
    private val CHANNEL_NAME = "Energy Manager"
    private val CHANNEL_DESC = "Energy Manager Notifications"

    @Inject
    lateinit var viewModel: EnergyManagerViewModel

    @Inject
    lateinit var auth: FirebaseAuth

    @Inject
    lateinit var database: FirebaseDatabase

    @Inject
    lateinit var usersDatabase: DatabaseReference

    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (auth.currentUser == null) {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
            return
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        var count = 0
        viewModel.availablePlaces.forEach {
            mapData.add(
                MapData(
                    latLng = it.key,
                    title = it.value,
                    position = count++,
                    availablePower = it.key.latitude.toString().let { latitude ->
                        latitude.substring(latitude.length - 2).toInt()
                    }
                )
            )
        }
        initFirebase()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_MAX)
            notificationChannel.description = CHANNEL_DESC
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }

    }

    private fun initFirebase() {
        val TAG = "Energy Tracker"
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.e(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            fireBaseToken = task.result
            registerDevice()

            // Log and toast
            Log.v(TAG, fireBaseToken!!)
        })
    }

    private fun registerDevice() {
        getCurrentLocationFromUser()
    }

    private fun getCurrentLocationFromUser() {
        // setup the alert builder
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Tell us where you live")

// add a radio button list
        val places: Array<String> = viewModel.availablePlaces.values.map { it }.toTypedArray()

        val checkedItem = -1 // cow
        builder.setSingleChoiceItems(places, checkedItem) { dialog, which ->
            val latLng = viewModel.availablePlaces.keys.elementAt(which)
            currentLatitude = latLng.latitude.toString()
            currentLongitude = latLng.longitude.toString()
            // user checked an item
        }


// add OK and Cancel buttons
        builder.setPositiveButton("OK") { _, _ ->
            var userInfo: User? = null
            val email = auth.currentUser?.email

            // user clicked OK
            if (email != null && fireBaseToken != null && currentLatitude != null && currentLongitude != null) {
                userInfo = User(email, currentLatitude!!, currentLongitude!!, fireBaseToken!!)
            }

            userInfo.let {
                val usrStr = Gson().toJson(it, User::class.java)
                if (auth.currentUser != null) {
                    usersDatabase.child(auth.currentUser!!.uid)
                        .setValue(userInfo)
                        .addOnCompleteListener {
                            Toast.makeText(this, "token saved", Toast.LENGTH_LONG).show()
                        }.addOnFailureListener {
                            Toast.makeText(this, "FAILED", Toast.LENGTH_LONG).show()

                        }
                }
            }


        }
        builder.setNegativeButton("Cancel") { _, _ ->
            Toast.makeText(this, "Please select a location to continue", Toast.LENGTH_LONG).show()
            registerDevice()
        }


// create and show the alert dialog
        val dialog = builder.create()
        dialog.show()
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mapData.forEach { data ->
            data.marker = mMap.addMarker(MarkerOptions().position(data.latLng))
                .also {
                    it.tag = data.position
                    it.title = data.title
                    it.snippet = "Available Power: ${data.availablePower} KW"
                }
        }

        // Set a listener for marker click.
        mMap.setOnMarkerClickListener(this)
        mMap.setOnInfoWindowClickListener(this)

        val latLongBuilder = LatLngBounds.Builder()
        for (latLng in viewModel.availablePlaces)
            latLongBuilder.include(latLng.key)
        val latLngBounds = latLongBuilder.build()

        val width = resources.displayMetrics.widthPixels
        val updateCamera = CameraUpdateFactory.newLatLngBounds(latLngBounds, width, 300, 0)
        mMap.animateCamera(updateCamera)
    }

    override fun onMarkerClick(p0: Marker?): Boolean = false

    override fun onInfoWindowClick(p0: Marker?) {

        val currentPlaceInfo = mapData[p0?.tag as Int]
        AlertDialog.Builder(this@EnergyManagerActivity)
            .setTitle(currentPlaceInfo.title)
            .setMessage(
                """
        Available Power: ${currentPlaceInfo.availablePower} kW
        Would you like to request?
        """.trimIndent()
            )
            .setPositiveButton("Yes") { dialog, which ->
                Toast.makeText(this, "your request has been sent", Toast.LENGTH_LONG).show()
            }
            .setNegativeButton("No") { dialog, which ->
                // do nothing
            }
            .show()
    }

    //Todo verify behaviour of manifest attributes.
    private fun displayNotification() {
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_bolt_black_24dp)
            .setContentTitle("Energy Manager")
            .setContentText("You have been requested to provide Energy")
            .setPriority(NotificationCompat.PRIORITY_MAX)

        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(1, notificationBuilder.build())
    }

    override fun onStart() {
        super.onStart()
        auth = Firebase.auth
        if (auth.currentUser == null) {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
            return
        }
    }
}

data class MapData(
    var latLng: LatLng,
    var marker: Marker? = null,
    val position: Int,
    val title: String = "",
    val availablePower: Int
)

data class User(val email: String, val latitude: String, val longitude: String, val token: String)