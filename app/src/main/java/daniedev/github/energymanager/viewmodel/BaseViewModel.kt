package daniedev.github.energymanager.viewmodel

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import daniedev.github.energymanager.utils.dialog.DialogListener

abstract class BaseViewModel : ViewModel(), LifecycleObserver, DialogListener