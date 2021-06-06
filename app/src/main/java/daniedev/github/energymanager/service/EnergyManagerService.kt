package daniedev.github.energymanager.service

import daniedev.github.energymanager.model.NotifyConfirmationRequest
import daniedev.github.energymanager.model.NotifyConfirmationResponse
import daniedev.github.energymanager.model.RequestPowerFromLocationRequest
import daniedev.github.energymanager.model.RequestPowerFromLocationResponse
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.POST

interface EnergyManagerService {

    @POST("/requestPower")
    fun requestPowerFromLocation(@Body requestPowerFromLocationRequest: RequestPowerFromLocationRequest): Single<RequestPowerFromLocationResponse>

    @POST("/sendResponse")
    fun notifyConfirmationToUser(@Body notifyConfirmationRequest: NotifyConfirmationRequest): Single<NotifyConfirmationResponse>
}