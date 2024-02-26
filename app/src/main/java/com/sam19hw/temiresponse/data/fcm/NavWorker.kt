package com.sam19hw.temiresponse.data.fcm

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.sam19hw.temiresponse.ui.NavTestActivity

class NavWorker(val appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {

    override fun doWork(): Result {
        val inputData =
            inputData.getString("MessageData") ?: return Result.failure()
        Log.d(TAG, "Performing long running task in scheduled job, with data: $inputData")

        // TODO(developer): add ability to parse the input data to determine which activity to start, not just start the navigation activity,
        //  perhaps have the worker check if the robots location is equal to that of the required location, and if not then start the navigation activity


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