package daniedev.github.energymanager.model

import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName
import daniedev.github.energymanager.utils.dialog.EventContext


data class RequestPowerFromLocationRequest(

    @SerializedName("requesterName")
    val requesterName: String,

    @SerializedName("requesterDeviceToken")
    val requesterDeviceToken: String,

    @SerializedName("receiverLocation")
    val receiverLocation: LatLng,

    @SerializedName("eventContext")
    val eventContext: EventContext
)

