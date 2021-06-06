package daniedev.github.energymanager.di.modules

import dagger.Module
import dagger.android.ContributesAndroidInjector
import daniedev.github.energymanager.utils.firebase.EnergyManagerMessagingService

@Module
abstract class ServiceBuilderModule {

    @ContributesAndroidInjector
    abstract fun contributesEnergyManagerMessagingService(): EnergyManagerMessagingService
}