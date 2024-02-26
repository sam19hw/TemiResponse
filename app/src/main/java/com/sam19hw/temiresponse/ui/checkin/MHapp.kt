package com.sam19hw.temiresponse.ui.checkin

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
//import com.robotemi.sdk.Robot.Companion.getInstance;
//import com.robotemi.sdk.TtsRequest.Companion.create;
import androidx.constraintlayout.widget.ConstraintLayout
import com.sam19hw.temiresponse.data.Utils
import com.robotemi.sdk.*
//import com.robotemi.sdk.Robot.Companion.*
//import com.robotemi.sdk.TtsRequest.Companion.*
import com.robotemi.sdk.activitystream.ActivityStreamPublishMessage
import com.robotemi.sdk.listeners.*
import com.robotemi.sdk.model.DetectionData
import com.sam19hw.temiresponse.R

public class MHapp() : AppCompatActivity(), Robot.NlpListener, OnRobotReadyListener,
    Robot.ConversationViewAttachesListener, Robot.WakeupWordListener,
    Robot.ActivityStreamPublishListener,
    Robot.TtsListener, OnBeWithMeStatusChangedListener, OnGoToLocationStatusChangedListener,
    OnLocationsUpdatedListener, OnDetectionStateChangedListener, OnDetectionDataChangedListener,
    OnUserInteractionChangedListener{


    private lateinit var robot: Robot
    lateinit var greeting: ConstraintLayout
    lateinit var displayBar: ConstraintLayout
    lateinit var green: ConstraintLayout
    lateinit var amber: ConstraintLayout
    lateinit var red: ConstraintLayout
    lateinit var start: ConstraintLayout
    lateinit var startButton: Button
    var x = 0f
    var y = 0f
    var amberCount = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("MHApp", "Starting")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mh_activity_main)
        Log.d("MHApp", "Start Layout Loaded")
        greeting = findViewById(R.id.mH_Greeting)
        displayBar = findViewById(R.id.mH_Display_bar)
        green = findViewById(R.id.mH_Green)
        amber = findViewById(R.id.mH_Amber)
        red = findViewById(R.id.mH_Red)
        start = findViewById(R.id.Debug_Sart)
        startButton = findViewById<Button>(R.id.Debug_Start_Button)
        greeting.setVisibility(View.GONE)
        displayBar.setVisibility(View.GONE)
        green.setVisibility(View.GONE)
        amber.setVisibility(View.GONE)
        red.setVisibility(View.GONE)
        start.setVisibility(View.GONE)
        robot = Robot.getInstance()


        //robot.goTo("Seat");
        mHCheckUp()
    }

    override fun onStart() {
        super.onStart()
        Robot.getInstance().addOnRobotReadyListener(this)
        Robot.getInstance().addNlpListener(this)
        Robot.getInstance().addOnBeWithMeStatusChangedListener(this)
        Robot.getInstance().addOnGoToLocationStatusChangedListener(this)
        Robot.getInstance().addConversationViewAttachesListenerListener(this)
        Robot.getInstance().addWakeupWordListener(this)
        Robot.getInstance().addTtsListener(this)
        Robot.getInstance().addOnLocationsUpdatedListener(this)
        Robot.getInstance().addOnRobotReadyListener(this)
        Robot.getInstance().addOnDetectionStateChangedListener(this)
        Robot.getInstance().addOnDetectionDataChangedListener(this)
        Robot.getInstance().addOnUserInteractionChangedListener(this)
    }

    override fun onStop() {
        super.onStop()
        Robot.getInstance().removeOnRobotReadyListener(this)
        Robot.getInstance().removeNlpListener(this)
        Robot.getInstance().removeOnBeWithMeStatusChangedListener(this)
        Robot.getInstance().removeOnGoToLocationStatusChangedListener(this)
        Robot.getInstance().removeConversationViewAttachesListenerListener(this)
        Robot.getInstance().removeWakeupWordListener(this)
        Robot.getInstance().removeTtsListener(this)
        Robot.getInstance().removeOnLocationsUpdateListener(this)
        Robot.getInstance().removeOnRobotReadyListener(this)
        Robot.getInstance().removeOnDetectionStateChangedListener(this)
        Robot.getInstance().removeOnDetectionDataChangedListener(this)
        Robot.getInstance().removeOnUserInteractionChangedListener(this)
    }

    private fun mHCheckUp() {
        start.setVisibility(View.GONE)
        greeting.setVisibility(View.VISIBLE)
        Log.d("MHApp", "Starting greeting")
        robot.speak(TtsRequest.create(getString(R.string.greeting), false))
        /* ToDo replace with wait for end of tts */
        val secs = 5 // Delay in seconds
        Utils.delay(secs, object : Utils.DelayCallback {
            override fun afterDelay() {
                // Do something after delay
                Log.d("MHApp", "Waited for delay")
                greeting.setVisibility(View.VISIBLE)
                robot.speak(TtsRequest.create("delay was run asynchronously"))
            }
        })
        //GET MOOD RESPONSE;
        robot.speak(TtsRequest.create(getString(R.string.askMood)))
        displayBar.setVisibility(View.VISIBLE)
        //Say Mood Response
        Log.d("MHApp", "Getting Mood Response")
        x = response
        try {
            Thread.sleep(1000)
        } catch (e: InterruptedException) {
            throw RuntimeException(e)
        }
        //Identify Category of Response
        Log.d("MHApp", "Categorising Response")
        if (x > 75) {
            //green
            Log.d("MHApp", "Response as Green, x = $x")
            displayBar.setVisibility(View.INVISIBLE)
            responseGreen()
        } else if ((x > 25) and (x < 75)) {
            //Amber
            Log.d("MHApp", "Response as Amber, x = $x")
            displayBar.setVisibility(View.INVISIBLE)
            responseAmber()
        } else if (x < 25) {
            //Red
            Log.d("MHApp", "Response as Red, x = $x")
            displayBar.setVisibility(View.INVISIBLE)
            responseRed()
        }
        Log.d("MHApp", "Restarting as Planned")
        greeting.setVisibility(View.INVISIBLE)
        displayBar.setVisibility(View.INVISIBLE)
        green.setVisibility(View.INVISIBLE)
        amber.setVisibility(View.INVISIBLE)
        red.setVisibility(View.INVISIBLE)
        mHRestart()
    }

    private fun mHRestart() {
        start.setVisibility(View.VISIBLE)
        startButton!!.setOnClickListener {
            Toast.makeText(applicationContext, "Button Clicked", Toast.LENGTH_SHORT).show()
            start.setVisibility(View.INVISIBLE)
            mHCheckUp()
        }
    }

    private fun responseRed() {
        red.setVisibility(View.VISIBLE)
        //Say Red Response
        robot.speak(TtsRequest.create(getString(R.string.badMood)))
    }

    private fun responseAmber() {
        amber.setVisibility(View.VISIBLE)
        //TODO Complete Response Based on Url
        robot.speak(TtsRequest.create(getString(R.string.amberMood)))
        robot.speak(TtsRequest.create(getString(R.string.amberHelp)))

        //Redo?
        val prevX = x
        response
        if (prevX <= x) {
            responseRed()
        } else if (prevX >= x + 0.35) {
            responseGreen()
        } else {
            amberCount += 1
            responseAmber()
        }
    }

    private fun responseGreen() {
        green.setVisibility(View.VISIBLE)
        //Say Green Response
        robot.speak(TtsRequest.create(getString(R.string.goodMood)))
    }

    //TODO Either Touch or speak the response, currently only touch is implemented with a setOnTouchListener, needs to start a async nlp
    private val response: Float
        private get() {
            Log.d("MHApp", "Getting user input")
            val displayBar = findViewById<View>(R.id.mh_Display_Image)
            displayBar.setOnTouchListener { v, event ->
                if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                    x = event.x
                    y = event.y
                    Log.d("MHApp", "Response is, x = $x")
                }
                true
            }
            return if (x.toDouble() != 99999.0) {
                x
            } else {
                Float.NaN
            }
        }

    override fun onPublish(activityStreamPublishMessage: ActivityStreamPublishMessage) {}
    override fun onConversationAttaches(b: Boolean) {}
    override fun onNlpCompleted(nlpResult: NlpResult) {}
    override fun onTtsStatusChanged(ttsRequest: TtsRequest) {}
    override fun onWakeupWord(s: String, i: Int) {}
    override fun onBeWithMeStatusChanged(s: String) {}
    override fun onGoToLocationStatusChanged(
        location: String,
        status: String,
        i: Int,
        description: String
    ) {
        robot.speak(TtsRequest.create(location, false))
        robot.speak(TtsRequest.create(status, false))
        if (description != null) {
            robot.speak(TtsRequest.create(description, false))
        }
    }

    override fun onLocationsUpdated(locations: List<String>) {
        TODO("Not yet implemented")
    }

    override fun onRobotReady(b: Boolean) {
        if (b) {
            robot.hideTopBar()
            Log.i(ContentValues.TAG, "Set detection mode: ON")
            robot.setDetectionModeOn(
                true,
                2.0f
            ) // Set detection mode on; set detection distance to be 2.0 m
            Log.i(ContentValues.TAG, "Set track user: ON")
            //robot?.SetTrackUser(true) // Set tracking mode on
            //Robot.getInstance().trackUserOn(true) // Set tracking mode on
            Robot.getInstance().trackUserOn // Set tracking mode on through the parent instance due to type miss-match
            // Note: When exiting the application, track user will still be enabled unless manually disabled
        }
    }

    override fun onDetectionDataChanged(detectionData: DetectionData) {
        if (detectionData.isDetected) {
            Log.i(
                ContentValues.TAG,
                "OnDetectionDataChanged: " + detectionData.distance + " m"
            )
        }
    }

    override fun onDetectionStateChanged(state: Int) {
        when (state) {
            OnDetectionStateChangedListener.IDLE ->                 // No active detection and/or 10 seconds have passed since the last detection was lost
                Log.i(ContentValues.TAG, "OnDetectionStateChanged: IDLE")

            OnDetectionStateChangedListener.LOST ->                 // When human-target is lost
                Log.i(ContentValues.TAG, "OnDetectionStateChanged: LOST")

            OnDetectionStateChangedListener.DETECTED ->                 // Human is detected
                Log.i(ContentValues.TAG, "OnDetectionStateChanged: DETECTED")

            else ->                 // This should not happen
                Log.i(ContentValues.TAG, "OnDetectionStateChanged: UNKNOWN")
        }
    }

    override fun onUserInteraction(isInteracting: Boolean) {
        if (isInteracting) {
            // User is interacting with the robot:
            // - User is detected
            // - User is interacting by touch, voice, or in telepresence-mode
            // - Robot is moving
            Log.i(ContentValues.TAG, "OnUserInteraction: TRUE")
        } else {
            // User is not interacting with the robot
            Log.i(ContentValues.TAG, "OnUserInteraction: FALSE")
        }
    }

}