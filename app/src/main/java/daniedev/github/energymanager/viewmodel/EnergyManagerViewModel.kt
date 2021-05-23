package daniedev.github.energymanager.viewmodel

import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import daniedev.github.energymanager.provider.EnergyManagerServiceProvider
import javax.inject.Inject

class EnergyManagerViewModel @Inject constructor(private val energyManagerServiceProvider: EnergyManagerServiceProvider) : ViewModel() {
    val availablePlaces = mapOf (
        GUPTA_BHAVAN to "Gupta bhavan",
        BETHESDA_HOSPITAL to "Bethesda Hospital",
        BHARATH_UNIVERSITY to "Bharath University",
        shakthiDrivingSchool to "Shakthi Driving School",
        house1 to "House1",
        kopiKada to "Kopi Kada",
        ashokManorApartments to "Ashok Manor Apartments",
        rubyBuilders to "Ruby Builders",
        vknApartments to "Vkn Apartments",
        faaridhHotel to "Faaridh Hotel"
    )

    companion object {
        private val GUPTA_BHAVAN = LatLng(12.922782432969658, 80.14294548731063)
        private val BETHESDA_HOSPITAL = LatLng(12.919813084586302, 80.14433271624547)
        private val BHARATH_UNIVERSITY = LatLng(12.907934430427538, 80.14196426450523)
        private val shakthiDrivingSchool = LatLng(12.9041333, 80.1528503)
        private val house1 = LatLng(12.9057435, 80.1542149)
        private val kopiKada = LatLng(12.9063624, 80.1557384)
        private val ashokManorApartments = LatLng(12.9047320, 80.1536372)
        private val rubyBuilders = LatLng(12.9066713, 80.1547674)
        private val vknApartments = LatLng(12.9023685, 80.1562024)
        private val faaridhHotel = LatLng(12.9044908, 80.1534830)
    }
}