package daniedev.github.energymanager.provider

import daniedev.github.energymanager.service.EnergyManagerService
import io.reactivex.Single
import javax.inject.Inject

class EnergyManagerServiceProviderImpl @Inject constructor(private val energyManagerService: EnergyManagerService) :
    EnergyManagerServiceProvider {

    override fun notifyUser(token: String): Single<String> = energyManagerService.notifyUser(token)
}