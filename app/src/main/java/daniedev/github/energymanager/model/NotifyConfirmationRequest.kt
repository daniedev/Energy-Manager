package daniedev.github.energymanager.model

import com.google.gson.annotations.SerializedName

data class NotifyConfirmationRequest(

    @SerializedName("isRequestGranted")
    val isRequestGranted: Boolean,

    @SerializedName("recipientToken")
    val recipientToken: String
)
