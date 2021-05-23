package daniedev.github.energymanager.di.component

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import daniedev.github.energymanager.application.EnergyManagerApplication
import daniedev.github.energymanager.di.modules.*
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidInjectionModule::class,
        ActivityBuilderModule::class,
        NetworkModule::class,
        AppModule::class,
        EnergyManagerServiceModule::class,
        FirebaseModule::class
    ]
)
interface AppComponent : AndroidInjector<EnergyManagerApplication> {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }
}