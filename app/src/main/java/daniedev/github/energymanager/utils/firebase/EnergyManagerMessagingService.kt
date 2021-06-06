package daniedev.github.energymanager.utils.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.android.AndroidInjection
import daniedev.github.energymanager.factory.EnergyManagerNotificationFactory
import daniedev.github.energymanager.utils.dialog.EventContext
import daniedev.github.energymanager.utils.common.EVENT_CONTEXT
import daniedev.github.energymanager.utils.common.NOTIFICATION_MESSAGE
import daniedev.github.energymanager.utils.common.NOTIFICATION_TITLE
import daniedev.github.energymanager.utils.common.REQUESTER_DEVICE_TOKEN
import daniedev.github.energymanager.utils.storage.DataShelf
import javax.inject.Inject

class EnergyManagerMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    lateinit var databaseReference: DatabaseReference

    @Inject
    lateinit var dataShelf: DataShelf

    @Inject
    lateinit var notificationFactory: EnergyManagerNotificationFactory

    override fun onCreate() {
        super.onCreate()
        AndroidInjection.inject(this)
    }
    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        try {
            var eventContext: EventContext? = null
            var notificationTitle: String? = null
            var notificationBody = ""
            with(remoteMessage) {
                data[NOTIFICATION_TITLE]?.run { notificationTitle = this }
                data[NOTIFICATION_MESSAGE]?.run { notificationBody = this }
                data[REQUESTER_DEVICE_TOKEN]?.run { dataShelf.stash(REQUESTER_DEVICE_TOKEN, this) }
                data[EVENT_CONTEXT]?.let { eventName ->
                    for (event in EventContext.values()) {
                        if (event.eventName == eventName)
                            eventContext = event
                    }
                }
            }
            notificationFactory.buildNotification(
                notificationTitle,
                notificationBody,
                eventContext,
                this
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Called if the FCM registration token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the
     * FCM registration token is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        firebaseAuth.currentUser?.uid?.let { uniqueID ->
            databaseReference.child(NODE_USERS).child(uniqueID).child(NODE_TOKEN).setValue(p0)
        }
    }
}