package daniedev.github.energymanager.utils.dialog

enum class EventContext(val eventName: String) {
    REQUEST_POWER_CONFIRMATION("REQUEST_POWER_CONFIRMATION"),
    FETCH_LOCATION("FETCH_LOCATION"),
    REQUEST_POWER_PUSH_NOTIFICATION("REQUEST_POWER_PUSH_NOTIFICATION")
}