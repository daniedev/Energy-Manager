package daniedev.github.energymanager.factory

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import daniedev.github.energymanager.R
import daniedev.github.energymanager.provider.ResourceProvider
import daniedev.github.energymanager.utils.dialog.DialogEvent
import daniedev.github.energymanager.utils.dialog.EventContext
import daniedev.github.energymanager.view.DialogActivity
import daniedev.github.energymanager.utils.common.DIALOG_INTENT_EXTRA
import javax.inject.Inject

class EnergyManagerNotificationFactory @Inject constructor(
    private val resourceProvider: ResourceProvider,
    private val applicationContext: Context
) {

    private lateinit var notificationManager: NotificationManager

    private lateinit var notificationBuilder: NotificationCompat.Builder

    fun buildNotification(
        notificationTitle: String?,
        notificationBody: String,
        eventContext: EventContext?,
        context: Context
    ) {
        notificationManager =
            context.getSystemService(FirebaseMessagingService.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setupNotificationChannel()
        }
        initializeNotificationBuilder(context, notificationTitle, notificationBody)
        eventContext?.let { customizeNotification(notificationBody, it, context) }
        notificationManager.notify(0, notificationBuilder.build())
    }

    private fun customizeNotification(
        messageBody: String,
        eventContext: EventContext,
        context: Context
    ) {
        when (eventContext) {
            EventContext.REQUEST_POWER_PUSH_NOTIFICATION -> {
                val intent = Intent(context, DialogActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.putExtra(
                    DIALOG_INTENT_EXTRA,
                    with(resourceProvider) {
                        DialogEvent(
                            message = "$messageBody. ${getString(R.string.request_confirmation_text)}",
                            positiveButtonMessage = getString(R.string.request_confirmation_accept),
                            negativeButtonMessage = getString(R.string.request_confirmation_deny),
                            shouldPublishUserInput = eventContext
                        )
                    }
                )
                val pendingIntent = PendingIntent.getActivity(
                    context,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
                notificationBuilder.setContentIntent(pendingIntent)
                applicationContext.startService(intent)
            }
            else -> return
        }
    }

    private fun initializeNotificationBuilder(
        context: Context,
        notificationTitle: String?,
        notificationBody: String
    ) {
        val defaultSoundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        notificationBuilder =
            NotificationCompat.Builder(
                context,
                resourceProvider.getString(R.string.default_notification_channel_id)
            )
                .setSmallIcon(R.drawable.ic_bolt_black)
                .setContentText(notificationBody)
                .setColor(resourceProvider.getColor(R.color.red_700))
                .setLights(Color.RED, 1, 1)
                .setSound(defaultSoundUri)
                .setAutoCancel(true)
        notificationTitle?.let { notificationBuilder.setContentTitle(it) }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun setupNotificationChannel() {
        val channelId = resourceProvider.getString(R.string.default_notification_channel_id)
        val channelName = resourceProvider.getString(R.string.default_notification_channel_name)
        val channel =
            NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
        channel.enableLights(true)
        channel.lightColor = Color.RED
        channel.enableVibration(true)
        notificationManager.createNotificationChannel(channel)
    }
}