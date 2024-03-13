package com.sam19hw.temiresponse.ui

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.Firebase
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.messaging.messaging
import com.sam19hw.temiresponse.R
import com.sam19hw.temiresponse.data.Util
import com.sam19hw.temiresponse.data.fcm.NavWorker
import com.sam19hw.temiresponse.databinding.ActivityFcmBinding

class FcmActivity : AppCompatActivity() {

        private val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission(),
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "Notifications permission granted", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(
                    this,
                    "FCM can't post notifications without POST_NOTIFICATIONS permission",
                    Toast.LENGTH_LONG,
                ).show()
            }
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            val binding = ActivityFcmBinding.inflate(layoutInflater)
            setContentView(binding.root)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Create channel to show notifications.
                val channelId = getString(R.string.default_notification_channel_id)
                val channelName = getString(R.string.default_notification_channel_name)
                val notificationManager = getSystemService(NotificationManager::class.java)
                notificationManager?.createNotificationChannel(
                    NotificationChannel(
                        channelId,
                        channelName,
                        NotificationManager.IMPORTANCE_LOW,
                    ),
                )
            }

            // If a notification message is tapped, any data accompanying the notification
            // message is available in the intent extras. In this sample the launcher
            // intent is fired when the notification is tapped, so any accompanying data would
            // be handled here. If you want a different intent fired, set the click_action
            // field of the notification message to the desired intent. The launcher intent
            // is used when no click_action is specified.
            //
            // Handle possible data accompanying notification message.
            // [START handle_data_extras]
            intent.extras?.let {
                for (key in it.keySet()) {
                    val value = intent.extras?.getString(key)
                    Log.d(TAG, "Key: $key Value: $value")
                }
            }
            // [END handle_data_extras]

            binding.subscribeButton.setOnClickListener {
                Log.d(TAG, "Subscribing to testMSG topic")
                // [START subscribe_topics]
                Firebase.messaging.subscribeToTopic("testMSG")
                    .addOnCompleteListener { task ->
                        var msg = getString(R.string.msg_subscribed)
                        if (!task.isSuccessful) {
                            msg = getString(R.string.msg_subscribe_failed)
                        }
                        Log.d(TAG, msg)
                        Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    }
                // [END subscribe_topics]
            }

            binding.logTokenButton.setOnClickListener {
                // Get token
                // [START log_reg_token]
                Firebase.messaging.token.addOnCompleteListener(
                    OnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                            return@OnCompleteListener
                        }

                        // Get new FCM registration token
                        val token = task.result

                        // Log and toast
                        val msg = getString(R.string.msg_token_fmt, token)
                        Log.d(TAG, msg)
                        Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    },
                )
                // [END log_reg_token]
            }

            binding.runWorkerButton.setOnClickListener {
                // Run Worker Thread
                val message = RemoteMessage.Builder("test")
                message.data.put("service","401")
                message.data.put("user","1")
                message.messageType = "data"
                var remote = message.build()
                scheduleJob(remote)
            }

            Toast.makeText(this, "See README for setup instructions", Toast.LENGTH_SHORT).show()
            askNotificationPermission()
        }

        private fun scheduleJob(remoteMessage: RemoteMessage) {
            // [START dispatch_job]
            val work = OneTimeWorkRequest.Builder(NavWorker::class.java)
                .setInputData(
                    workDataOf(
                    "service" to remoteMessage.data["service"],
                    "user" to remoteMessage.data["user"]

                )
                ).build()
            WorkManager.getInstance(this).beginWith(work).enqueue()
            // [END dispatch_job]
        }

        private fun askNotificationPermission() {
            // This is only necessary for API Level > 33 (TIRAMISU)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    // FCM SDK (and your app) can post notifications.
                } else {
                    // Directly ask for the permission
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }

        companion object {

            private const val TAG = "FCMActivity"
        }
    }
