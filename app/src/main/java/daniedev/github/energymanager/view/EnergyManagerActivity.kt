package daniedev.github.energymanager.view

import android.content.Intent
import android.os.Bundle
import com.google.android.gms.maps.SupportMapFragment
import dagger.android.AndroidInjection
import daniedev.github.energymanager.R
import daniedev.github.energymanager.databinding.ActivityMapsBinding
import daniedev.github.energymanager.shared.view.BaseActivity
import daniedev.github.energymanager.viewmodel.EnergyManagerViewModel
import javax.inject.Inject

class EnergyManagerActivity : BaseActivity() {

    private lateinit var binding: ActivityMapsBinding

    @Inject
    lateinit var viewModel: EnergyManagerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        this.lifecycle.addObserver(viewModel)
        subscribeToEvents()
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(viewModel)
    }

    private fun subscribeToEvents() {
        viewModel.startActivityEvent.observe(this, { clazz ->
            val intent = Intent(this, clazz.java)
            startActivity(intent)
            finish()
        })
        viewModel.showDialogEvent.observe(this, { dialogEvent ->
            showAlertDialog(viewModel, dialogEvent)
        })
    }
}