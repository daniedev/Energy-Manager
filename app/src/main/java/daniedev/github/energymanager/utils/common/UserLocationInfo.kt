package daniedev.github.energymanager.utils.common

import com.google.android.gms.maps.model.LatLng

data class UserLocationInfo(
    val locationReference: Int,
    val coordinates: LatLng
)