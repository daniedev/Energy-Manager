package daniedev.github.energymanager.provider

import daniedev.github.energymanager.model.RequestPowerFromLocationRequest
import daniedev.github.energymanager.model.RequestPowerFromLocationResponse
import daniedev.github.energymanager.service.EnergyManagerService
import io.reactivex.Single
import javax.inject.Inject

class EnergyManagerServiceProviderImpl @Inject constructor(private val energyManagerService: EnergyManagerService) :
    EnergyManagerServiceProvider {

    override fun requestPowerFromLocation(request: RequestPowerFromLocationRequest): Single<RequestPowerFromLocationResponse> =
        energyManagerService.requestPowerFromLocation(request)
}