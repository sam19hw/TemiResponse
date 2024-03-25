package com.sam19hw.temiresponse.data.fcm

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.sam19hw.temiresponse.ui.NavTestActivity

class NavWorker(val appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private var firebaseOnline : Boolean = false
    override fun doWork(): Result {
        //val inoutSize = inputData.size()

        try {
            // Obtain the FirebaseAnalytics instance.
            firebaseAnalytics = Firebase.analytics
            firebaseOnline = true
        }catch (e: Throwable){
            Log.e(TAG, "Could not initialise Firebase analytics, continuing without")
            firebaseOnline = false

        }
        val service =
            inputData.getString("ServiceName") ?: return Result.failure()
        val users =
            inputData.getString("UserId") ?: return Result.failure()

        val alarmTime =
            inputData.getString("AlarmTime") ?: return Result.failure()

        val alarmLocation =
            inputData.getString("Location") ?: return Result.failure()

        Log.d(TAG, "Performing long running task in scheduled job, with data: $service, $users")

        // TODO(developer): add ability to parse the input data to determine which activity to start, not just start the navigation activity,
        //  perhaps have the worker check if the robots location is equal to that of the required location, and if not then start the navigation activity

        // Remote Log of data to Firebase Analytics, provides log that the message was sent and received
        if (firebaseOnline) {
            var bundle: Bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, users)
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, service)
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Beginning navigation to user")
            //firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LEVEL_START, bundle)
            firebaseAnalytics?.logEvent("nav_start", bundle)
        }




        Handler(Looper.getMainLooper()).post(Runnable {

            val intent = Intent(appContext, NavTestActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            appContext.startActivity(intent)
            Log.d("OpenActivityWorker", "doWork requested")
        })
        return Result.success()
    }

    companion object {
        private val TAG = "NavWorker"
    }
}