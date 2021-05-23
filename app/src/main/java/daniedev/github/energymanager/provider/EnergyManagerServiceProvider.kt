package daniedev.github.energymanager.provider

import io.reactivex.Single

interface EnergyManagerServiceProvider {

    fun notifyUser(token: String): Single<String>

}