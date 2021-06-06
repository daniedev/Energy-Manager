package daniedev.github.energymanager.utils.dialog

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DialogEvent(
    val title: String? = null,
    val message: String? = null,
    val positiveButtonMessage: String? = null,
    val negativeButtonMessage: String? = null,
    val itemList: Array<String>? = null,
    val shouldPublishUserInput: EventContext? = null,
    val shouldDismissDialogOnTouchOutSide: Boolean = false
) : Parcelable {

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
