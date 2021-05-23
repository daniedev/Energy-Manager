package daniedev.github.energymanager.di.modules

import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import daniedev.github.energymanager.factory.EnergyManagerServiceFactory
import daniedev.github.energymanager.provider.EnergyManagerServiceProvider
import daniedev.github.energymanager.provider.EnergyManagerServiceProviderImpl
import okhttp3.OkHttpClient

@Module(includes = [NetworkModule::class])
class EnergyManagerServiceModule {

    @Provides
    fun provideEnergyManagerServiceProvider(
        gson: Gson,
        okHttpClient: OkHttpClient
    ): EnergyManagerServiceProvider {
        return EnergyManagerServiceProviderImpl(
            EnergyManagerServiceFactory(
                gson,
                okHttpClient
            ).createEnergyManagerService()
        )
    }
}