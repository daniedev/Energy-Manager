package daniedev.github.energymanager.provider

import daniedev.github.energymanager.model.NotifyConfirmationRequest
import daniedev.github.energymanager.model.NotifyConfirmationResponse
import daniedev.github.energymanager.model.RequestPowerFromLocationRequest
import daniedev.github.energymanager.model.RequestPowerFromLocationResponse
import io.reactivex.Single

interface EnergyManagerServiceProvider {

    fun requestPowerFromLocation(request: RequestPowerFromLocationRequest): Single<RequestPowerFromLocationResponse>

    fun notifyConfirmationToUser(request: NotifyConfirmationRequest): Single<NotifyConfirmationResponse>
}