package daniedev.github.energymanager.view

import android.os.Bundle
import dagger.android.AndroidInjection
import daniedev.github.energymanager.utils.dialog.DialogEvent
import daniedev.github.energymanager.viewmodel.DialogViewModel
import daniedev.github.energymanager.utils.common.DIALOG_INTENT_EXTRA
import javax.inject.Inject

class DialogActivity : BaseActivity() {

    @Inject
    lateinit var viewModel: DialogViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        val bundle = intent.extras
        showAlertDialog(viewModel, bundle?.get(DIALOG_INTENT_EXTRA) as DialogEvent)
        viewModel.finishActivity.observe(this, { finish() })
    }
}