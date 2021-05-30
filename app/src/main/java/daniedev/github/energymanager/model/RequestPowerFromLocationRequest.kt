package daniedev.github.energymanager.model

import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName


data class RequestPowerFromLocationRequest(

    @SerializedName("senderName")
    val senderName: String,

    @SerializedName("receiverLocation")
    val receiverLocation: LatLng
)

