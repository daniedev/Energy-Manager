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

    private lateinit var mapFragment: SupportMapFragment

    @Inject
    lateinit var viewModel: EnergyManagerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        this.lifecycle.addObserver(viewModel)
        mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        subscribeToEvents()
    }

    private fun subscribeToEvents() {
        with(viewModel) {
            startLoadingMaps.observe(this@EnergyManagerActivity, {
                mapFragment.getMapAsync(this)
                startLoadingMaps.removeObservers(this@EnergyManagerActivity)
            })
            startActivityEvent.observe(this@EnergyManagerActivity, { clazz ->
                val intent = Intent(this@EnergyManagerActivity, clazz.java)
                startActivity(intent)
                finish()
            })
            showDialogEvent.observe(this@EnergyManagerActivity, { dialogEvent ->
                showAlertDialog(this, dialogEvent)
            })
            showToastMessage.observe(this@EnergyManagerActivity, { toastEvent ->
                showToastMessage(toastEvent.first, toastEvent.second)
            })
        }
    }
}