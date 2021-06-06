package daniedev.github.energymanager.utils.dialog

interface DialogListener {
    fun onDialogButtonPressed(eventContext: EventContext, buttonPressed: Int) {}
    fun onDialogItemSelected(eventContext: EventContext, itemSelected: Int) {}
}