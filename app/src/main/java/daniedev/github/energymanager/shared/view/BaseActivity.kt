package daniedev.github.energymanager.shared.view

import android.app.AlertDialog
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import daniedev.github.energymanager.shared.dialog.DialogEvent
import daniedev.github.energymanager.shared.viewmodel.BaseViewModel

open class BaseActivity : AppCompatActivity(),
    LifecycleOwner {

    fun showAlertDialog(viewModel: BaseViewModel, dialogEvent: DialogEvent) {
        with(AlertDialog.Builder(this)) {
            with(dialogEvent) {
                setTitle(title)
                message?.let { setMessage(it) }
                positiveButtonMessage?.run {
                    setPositiveButton(positiveButtonMessage) { _, buttonPressed ->
                        shouldPublishUserInput?.let { eventContext ->
                            viewModel.onDialogButtonPressed(
                                eventContext,
                                buttonPressed
                            )
                        }
                    }
                }
                negativeButtonMessage?.run {
                    setNegativeButton(negativeButtonMessage) { _, buttonPressed ->
                        shouldPublishUserInput?.let { eventContext ->
                            viewModel.onDialogButtonPressed(
                                eventContext,
                                buttonPressed
                            )
                        }
                    }
                }
                itemList?.run {
                    setSingleChoiceItems(itemList, -1) { _, itemSelected ->
                        shouldPublishUserInput?.let { eventContext ->
                            viewModel.onDialogItemSelected(
                                eventContext,
                                itemSelected
                            )
                        }
                    }
                }
                setCancelable(shouldDismissDialogOnTouchOutSide)
            }
            show()
        }
    }

    fun showToastMessage(message: String, duration: Int) =
        Toast.makeText(this, message, duration).show()
}