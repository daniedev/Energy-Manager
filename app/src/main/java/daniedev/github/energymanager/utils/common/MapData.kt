package daniedev.github.energymanager.utils.common

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker

data class MapData(
    var latLng: LatLng,
    var marker: Marker? = null,
    val position: Int,
    val title: String = "",
    val availablePower: Int
)