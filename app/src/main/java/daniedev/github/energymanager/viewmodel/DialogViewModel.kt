package daniedev.github.energymanager.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import daniedev.github.energymanager.utils.dialog.EventContext
import javax.inject.Inject

class DialogViewModel @Inject constructor(private val energyManagerViewModel: EnergyManagerViewModel) :
    BaseViewModel() {

    val finishActivity: LiveData<Boolean>
        get() = _finishActivity
    private val _finishActivity = MutableLiveData<Boolean>()

    override fun onDialogButtonPressed(eventContext: EventContext, buttonPressed: Int) {
        if (eventContext == EventContext.REQUEST_POWER_PUSH_NOTIFICATION)
            energyManagerViewModel.onDialogButtonPressed(eventContext, buttonPressed)
        _finishActivity.postValue(true)
    }
}