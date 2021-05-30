package daniedev.github.energymanager.provider

import daniedev.github.energymanager.model.RequestPowerFromLocationRequest
import daniedev.github.energymanager.model.RequestPowerFromLocationResponse
import io.reactivex.Single

interface EnergyManagerServiceProvider {

    fun requestPowerFromLocation(request: RequestPowerFromLocationRequest): Single<RequestPowerFromLocationResponse>

}