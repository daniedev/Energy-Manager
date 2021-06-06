package daniedev.github.energymanager.provider

import android.content.Context
import android.content.res.Resources
import javax.inject.Inject

class ResourceProvider @Inject constructor(private val context: Context) {

    fun getResources(): Resources = context.resources

    fun getString(id: Int) = getResources().getString(id)

    fun getColor(id: Int) = getResources().getColor(id)
}