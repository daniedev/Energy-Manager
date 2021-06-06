package daniedev.github.energymanager.model

import com.google.gson.annotations.SerializedName

data class NotifyConfirmationResponse(
    @SerializedName("message")
    val message: String,

    @SerializedName("status")
    val status: Int
)
