package daniedev.github.energymanager.provider

import daniedev.github.energymanager.model.NotifyConfirmationRequest
import daniedev.github.energymanager.model.NotifyConfirmationResponse
import daniedev.github.energymanager.model.RequestPowerFromLocationRequest
import daniedev.github.energymanager.model.RequestPowerFromLocationResponse
import daniedev.github.energymanager.service.EnergyManagerService
import io.reactivex.Single
import javax.inject.Inject

class EnergyManagerServiceProviderImpl @Inject constructor(private val energyManagerService: EnergyManagerService) :
    EnergyManagerServiceProvider {

    override fun requestPowerFromLocation(request: RequestPowerFromLocationRequest): Single<RequestPowerFromLocationResponse> =
        energyManagerService.requestPowerFromLocation(request)

    override fun notifyConfirmationToUser(request: NotifyConfirmationRequest): Single<NotifyConfirmationResponse> =
        energyManagerService.notifyConfirmationToUser(request)
}