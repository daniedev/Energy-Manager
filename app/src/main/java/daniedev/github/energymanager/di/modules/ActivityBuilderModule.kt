package daniedev.github.energymanager.di.modules

import dagger.Module
import dagger.android.ContributesAndroidInjector
import daniedev.github.energymanager.view.EnergyManagerActivity

@Module
abstract class ActivityBuilderModule {

    @ContributesAndroidInjector
    abstract fun contributeEnergyManagerActivity(): EnergyManagerActivity

}