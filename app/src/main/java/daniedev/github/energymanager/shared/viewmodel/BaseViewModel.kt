package daniedev.github.energymanager.shared.viewmodel

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import daniedev.github.energymanager.shared.dialog.DialogListener

abstract class BaseViewModel : ViewModel(), LifecycleObserver, DialogListener