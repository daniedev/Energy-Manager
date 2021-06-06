package daniedev.github.energymanager.di.modules

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.DisplayMetrics
import dagger.Module
import dagger.Provides
import daniedev.github.energymanager.utils.data.DataShelf
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Singleton

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

    @Provides
    fun provideSharedPreferences(application: Application): SharedPreferences {
        return application.getSharedPreferences("CachedValues",Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideDataContainer(): DataShelf {
        return DataShelf(ConcurrentHashMap())
    }
}


