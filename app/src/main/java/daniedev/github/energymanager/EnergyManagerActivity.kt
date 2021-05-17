 package daniedev.github.energymanager

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.installations.remote.TokenResult
import com.google.firebase.messaging.FirebaseMessaging
import daniedev.github.energymanager.databinding.ActivityMapsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class EnergyManagerActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private val viewModel: EnergyManagerViewModel by viewModels()
    private var mapData = ArrayList<MapData>()
    private var fireBaseToken: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

            // Log and toast
            Log.v(TAG, fireBaseToken!!)
        })
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

}

data class MapData(
    var latLng: LatLng,
    var marker: Marker? = null,
    val position: Int,
    val title: String = "",
    val availablePower: Int
)