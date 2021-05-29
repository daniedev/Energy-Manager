package daniedev.github.energymanager.shared.dialog

data class DialogEvent(
    val title: String,
    val message: String? = null,
    val positiveButtonMessage: String? = null,
    val negativeButtonMessage: String? = null,
    val itemList: Array<String>? = null,
    val shouldPublishUserInput: EventContext? = null,
    val shouldDismissDialogOnTouchOutSide: Boolean = false
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DialogEvent

        if (!itemList.contentEquals(other.itemList)) return false

        return true
    }

    override fun hashCode(): Int {
        return itemList.contentHashCode()
    }
}
