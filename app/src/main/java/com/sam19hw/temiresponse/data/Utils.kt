package com.sam19hw.temiresponse.data

import android.os.Handler


object Utils {
    public fun delay(secs: Int, delayCallback: DelayCallback) {
        val handler = Handler()
        handler.postDelayed(
            { delayCallback.afterDelay() },
            (secs * 1000).toLong()
        ) // afterDelay will be executed after (secs*1000) milliseconds.
    }

    // Delay mechanism
    public interface DelayCallback {
        fun afterDelay()
    }
}

