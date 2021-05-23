package daniedev.github.energymanager.service

import io.reactivex.Single
import retrofit2.http.POST

interface EnergyManagerService {

    @POST("/notifyUser")
    fun notifyUser(token: String): Single<String>
}