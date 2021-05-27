package daniedev.github.energymanager.di.modules

import android.app.Application
import android.content.Context
import android.util.DisplayMetrics
import dagger.Module
import dagger.Provides

@Module
class AppModule() {

    @Provides
    fun providesContext(application: Application): Context {
        return application
    }

    @Provides
    fun provideDisplayMetrics(context: Context): DisplayMetrics {
        return context.resources.displayMetrics
    }
}


