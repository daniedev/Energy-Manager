package daniedev.github.energymanager.factory

import com.google.gson.Gson
import daniedev.github.energymanager.config.BASE_URL
import daniedev.github.energymanager.service.EnergyManagerService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class EnergyManagerServiceFactory constructor(
    private val gson: Gson,
    private val okHttpClient: OkHttpClient
) {
    fun createEnergyManagerService(): EnergyManagerService =
        buildRetrofit().create(EnergyManagerService::class.java)

    private fun buildRetrofit(): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .build()
    }
}