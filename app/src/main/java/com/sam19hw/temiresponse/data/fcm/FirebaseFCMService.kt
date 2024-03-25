package com.sam19hw.temiresponse.data.fcm


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.sam19hw.temiresponse.ui.MainActivity
import com.sam19hw.temiresponse.R

class FirebaseFCMService : FirebaseMessagingService() {

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // Obtain the FirebaseAnalytics instance.
        try {
            // Obtain the FirebaseAnalytics instance.
            firebaseAnalytics = Firebase.analytics
            firebaseOnline = true
        }catch (e: Throwable){
            Log.e(TAG, "Could not initialise Firebase analytics, continuing without")
            firebaseOnline = false
        }

        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: ${remoteMessage.from}")

        // Remote Log of data to Firebase Analytics, provides log that the message was sent and received
        if (firebaseOnline) {
            var bundle: Bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, remoteMessage.messageId)
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, remoteMessage.from)
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "message")
            //firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LEVEL_START, bundle)
            firebaseAnalytics.logEvent("fcm_received", bundle)
        }

        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")

            // Check if data needs to be processed by long running job
            if (isLongRunningJob()) {
                // For long-running tasks (10 seconds or more) use WorkManager.
                // For all current firebase messages the execution will involve navigation so should use WorkManager
                //TODO parse the data message to the intent of the message, the worker thread needs to know this
                scheduleJob(remoteMessage)
            } else {
                // Handle message within 10 seconds - Not used
                handleNow()
            }
        }

        // Check if message contains a notification payload.
        // Currently no messages will need to send a notification payload, here for redundancy if notification becomes useful for testing or debug
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
            it.body?.let { body -> sendNotification(body) }
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]

    // Currently hard coded as all fcm jobs will include navigation
    //TODO find way to determine based on data type what the length of execution will be
    private fun isLongRunningJob() = true

    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private var firebaseOnline : Boolean = false

    // [START on_new_token]
    /**
     * Called if the FCM registration token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the
     * FCM registration token is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        sendRegistrationToServer(token)
    }
    // [END on_new_token]

    /**
     * Schedule async work using WorkManager.
     * Begins the doWork function in the attached NavWorker,
     * TODO Should determine which type of navigation and what needs to be done after the navigation, i.e. local goto dining room and start MH app
     */
    private fun scheduleJob(remoteMessage: RemoteMessage) {
        // [START dispatch_job]
        Log.d(TAG,"Scheduling job, starting worker thread")
        val work = OneTimeWorkRequest.Builder(NavWorker::class.java)
            .setInputData(workDataOf(
                "UserId" to remoteMessage.data["UserId"],
                "ServiceName" to remoteMessage.data["ServiceName"],
                "AlarmTime" to remoteMessage.data["AlarmTime"],
                "Location" to remoteMessage.data["Location"]
            )).build()
        WorkManager.getInstance(this).beginWith(work).enqueue()
        // [END dispatch_job]
    }

    /**
     * Handle time allotted to BroadcastReceivers.
     * Stub Not in Use
     */
    private fun handleNow() {
        Log.e(TAG, "TODO: Short lived task is not implemented now")
    }

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM registration token with any server-side account
     * maintained by your application.
     *
     * Not in use, no central repository for tokens is required for small scale topic only messages
     *
     * @param token The new token.
     */
    private fun sendRegistrationToServer(token: String?) {
        Log.d(TAG, "sendRegistrationTokenToServer($token)")
    }

    public fun sendMessage(data: String, topic: String) {
        TODO("Send Message function has not been tested")
        Log.d(TAG, "sending message to topic $data")

    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private fun sendNotification(messageBody: String) {
        val requestCode = 0
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this,
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE,
        )

        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(getString(R.string.fcm_message))
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT,
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notificationId = 0
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    companion object {

        private const val TAG = "FirebaseFCMService"
    }

}