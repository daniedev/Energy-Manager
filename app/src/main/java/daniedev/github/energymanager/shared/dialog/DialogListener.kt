package daniedev.github.energymanager.shared.dialog

interface DialogListener {
    fun onDialogButtonPressed(eventContext: EventContext, buttonPressed: Int) {}
    fun onDialogItemSelected(eventContext: EventContext, itemSelected: Int) {}
}