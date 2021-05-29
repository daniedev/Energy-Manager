package daniedev.github.energymanager.provider

import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class TokenProvider @Inject constructor() {

    val tokenStateFlow = MutableStateFlow("")

    init {
        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            tokenStateFlow.value = it
        }
    }
}