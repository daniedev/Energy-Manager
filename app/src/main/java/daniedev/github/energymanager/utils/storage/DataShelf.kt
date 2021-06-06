package daniedev.github.energymanager.utils.storage

import java.util.concurrent.ConcurrentHashMap

class DataShelf constructor(private val dataStore: ConcurrentHashMap<String, Any>) {

    fun stash(key: String, value: Any) {
        dataStore[key] = value
    }

    fun pop(key: String): Any? = dataStore.remove(key)
}