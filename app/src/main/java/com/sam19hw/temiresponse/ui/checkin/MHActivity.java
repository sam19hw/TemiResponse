package com.sam19hw.temiresponse.ui.checkin;

import static android.content.ContentValues.TAG;
import static java.lang.Thread.sleep;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.sam19hw.temiresponse.data.Util;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.robotemi.sdk.*;
//import com.robotemi.sdk.Robot.Companion.getInstance;
//import com.robotemi.sdk.TtsRequest.Companion.create;
import com.robotemi.sdk.activitystream.ActivityStreamPublishMessage;
import com.robotemi.sdk.listeners.*;
import com.robotemi.sdk.model.DetectionData;

import com.sam19hw.temiresponse.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MHActivity extends AppCompatActivity implements Robot.NlpListener,OnRobotReadyListener,Robot.ConversationViewAttachesListener,Robot.WakeupWordListener,Robot.ActivityStreamPublishListener, Robot.TtsListener, OnBeWithMeStatusChangedListener, OnGoToLocationStatusChangedListener, OnLocationsUpdatedListener,
        OnDetectionStateChangedListener,
        OnDetectionDataChangedListener,
        OnUserInteractionChangedListener   {

    private Robot robot;
    ConstraintLayout greeting;
    ConstraintLayout displayBar;
    ConstraintLayout green;
    ConstraintLayout amber;
    ConstraintLayout red;
    ConstraintLayout start;
    YouTubePlayerView youTubePlayerView;
    Button startButton;
    Button noButton;
    Button yesButton;
    float x;
    float y;
    int amberCount;
    Map<UUID, Integer> State = new HashMap<UUID, Integer>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Setup all values, objects, and classes
        Log.d("MHApp", "Creating");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mh_activity_main);
        Log.d("MHApp", "Start Layout Loaded");
        greeting = findViewById(R.id.mH_Greeting);
        displayBar = findViewById(R.id.mH_Display_bar);
        green = findViewById(R.id.mH_Green);
        amber = findViewById(R.id.mH_Amber);
        red = findViewById(R.id.mH_Red);
        start = findViewById(R.id.Debug_Sart);
        startButton = findViewById(R.id.Debug_Start_Button);
        //YouTubePlayerView youTubePlayerView = findViewById(R.id.youtube_player_view);
        youTubePlayerView = findViewById(R.id.youtube_player_view);
        getLifecycle().addObserver(youTubePlayerView);

        // Set all the layouts to gone at the start of the application
        greeting.setVisibility(View.GONE);
        displayBar.setVisibility(View.GONE);
        green.setVisibility(View.GONE);
        amber.setVisibility(View.GONE);
        red.setVisibility(View.GONE);
        start.setVisibility(View.GONE);

        // Set the robot instance for the robot listeners
        robot = Robot.getInstance();

        // Simplify logic by ensuring that State is not null
        State.put(UUID.fromString("8cb08ab9-d7fb-4b11-b9cd-322c19041930"),0);



        // Begin the actual application functions
        robot.goTo("seat");
        //mHCheckUp();
        //MHCheck(0);

    }

    protected void onStart() {
        Log.d("MHApp", "Starting");
        super.onStart();
        Robot.getInstance().addOnRobotReadyListener(this);
        Robot.getInstance().addNlpListener(this);
        Robot.getInstance().addOnBeWithMeStatusChangedListener(this);
        Robot.getInstance().addOnGoToLocationStatusChangedListener(this);
        Robot.getInstance().addConversationViewAttachesListenerListener(this);
        Robot.getInstance().addWakeupWordListener(this);
        Robot.getInstance().addTtsListener(this);
        Robot.getInstance().addOnLocationsUpdatedListener(this);
        Robot.getInstance().addOnRobotReadyListener(this);
        Robot.getInstance().addOnDetectionStateChangedListener(this);
        Robot.getInstance().addOnDetectionDataChangedListener(this);
        Robot.getInstance().addOnUserInteractionChangedListener(this);
    }


    protected void onStop() {
        Log.d("MHApp", "Stopping");
        super.onStop();
        Robot.getInstance().removeOnRobotReadyListener(this);
        Robot.getInstance().removeNlpListener(this);
        Robot.getInstance().removeOnBeWithMeStatusChangedListener(this);
        Robot.getInstance().removeOnGoToLocationStatusChangedListener(this);
        Robot.getInstance().removeConversationViewAttachesListenerListener(this);
        Robot.getInstance().removeWakeupWordListener(this);
        Robot.getInstance().removeTtsListener(this);
        Robot.getInstance().removeOnLocationsUpdateListener(this);
        Robot.getInstance().removeOnRobotReadyListener(this);
        Robot.getInstance().removeOnDetectionStateChangedListener(this);
        Robot.getInstance().removeOnDetectionDataChangedListener(this);
        Robot.getInstance().removeOnUserInteractionChangedListener(this);
        youTubePlayerView.release();
    }

    //Deprecated - Left in until refactoring is complete
    private void mHCheckUp() {
        start.setVisibility(View.GONE);
        greeting.setVisibility(View.VISIBLE);
        Log.d("MHApp", "Starting greeting");
        robot.speak(TtsRequest.create(getString(R.string.greeting),false));
        /* ToDo replace with wait for end of tts */
        int secs = 5; // Delay in seconds

        Util.delay(secs, new Util.DelayCallback() {
            @Override
            public void afterDelay() {
                // Do something after delay
                Log.d("MHApp", "Waited for delay");
                greeting.setVisibility(View.VISIBLE);
                robot.speak(TtsRequest.create("delay was run asynchronously"));
            }
        });
        //GET MOOD RESPONSE;
        robot.speak(TtsRequest.create(getString(R.string.askMood)));
        displayBar.setVisibility(View.VISIBLE);
        //Say Mood Response
        Log.d("MHApp", "Getting Mood Response");
        getResponse();
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        //Identify Category of Response
        Log.d("MHApp", "Categorising Response");
        if (x>75){
            //green
            Log.d("MHApp", "Response as Green, x = " + String.valueOf(x));
            displayBar.setVisibility(View.INVISIBLE);
            responseGreen();
        } else if (x>25 & x<75) {
            //Amber
            Log.d("MHApp", "Response as Amber, x = " + String.valueOf(x));
            displayBar.setVisibility(View.INVISIBLE);
            responseAmber();
        } else if (x<25) {
            //Red
            Log.d("MHApp", "Response as Red, x = " + String.valueOf(x));
            displayBar.setVisibility(View.INVISIBLE);
            responseRed();
        }

        Log.d("MHApp", "Restarting as Planned");
        greeting.setVisibility(View.INVISIBLE);
        displayBar.setVisibility(View.INVISIBLE);
        green.setVisibility(View.INVISIBLE);
        amber.setVisibility(View.INVISIBLE);
        red.setVisibility(View.INVISIBLE);

        mHRestart();
    }

    //Case statements to run stages of the protocol in one function when the relevant listener has returned (goto or tts)
    // Allows for Async calls to return to the current stage in the protocol while maintaining code readability
    private void MHCheck(int state){
        //Check redundancy of code in the case that we are passing an int that is stored in the map with the uuid, when if we know the uuid we can lookup here
        switch(state){

            //Greeting
            case 0:
                Log.d("MHApp", "Started stage 1");
                start.setVisibility(View.GONE);
                greeting.setVisibility(View.VISIBLE);
                Log.d("MHApp", "Starting greeting");
                TtsRequest speak = TtsRequest.create(getString(R.string.greeting), false);
                robot.speak(speak);
                State.put(speak.getId(), 1);
                break;

            // Asking for Response
            case 1:
                Log.d("MHApp", "Started stage 2");
                speak = TtsRequest.create(getString(R.string.askMood));
                robot.speak(speak);
                State.put(speak.getId(), 2);
                Log.d("MHApp", "Getting Mood Response");
                greeting.setVisibility(View.GONE);
                displayBar.setVisibility(View.VISIBLE);
                break;

            // Get User input
            case 2:
                Log.d("MHApp", "Started stage 3");
                getResponse();
                break;

            //Identify Category of Response
            case 3:
                Log.d("MHApp", "Started stage 4");
                Log.d("MHApp", "Categorising Response");
                if (x>75){
                    //green
                    Log.d("MHApp", "Response as Green, x = " + String.valueOf(x));
                    displayBar.setVisibility(View.GONE);
                    responseGreen();
                } else if (x>25 & x<75) {
                    //Amber
                    Log.d("MHApp", "Response as Amber, x = " + String.valueOf(x));
                    displayBar.setVisibility(View.GONE);
                    responseAmber();
                } else if (x<25) {
                    //Red
                    Log.d("MHApp", "Response as Red, x = " + String.valueOf(x));
                    displayBar.setVisibility(View.GONE);
                    responseRed();
                }
                break;

            // Returning to normal duties
            // Currently just return to base, but should hand back to Temi Controller main loop after app integration
            // TODO Add more speech to show intent in a personable way, then start a function call to hand back the activity, perhaps onPause()
            case 4:
                robot.speak(TtsRequest.create("I'll see you later"));
                Log.d("MHApp", "Restarting as Planned");
                greeting.setVisibility(View.INVISIBLE);
                displayBar.setVisibility(View.INVISIBLE);
                green.setVisibility(View.INVISIBLE);
                amber.setVisibility(View.INVISIBLE);
                red.setVisibility(View.INVISIBLE);

                //mHRestart();
                robot.goTo("home base");
                finish();
                break;
        }
    }

    //Deprecated Debug to ask to start the protocol
    private void mHRestart() {
        start.setVisibility(View.VISIBLE);
        startButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Start Button Clicked", Toast.LENGTH_SHORT).show();
                start.setVisibility(View.INVISIBLE);
                //mHCheckUp();
                MHCheck(0);
            }
        });
    }

    private void responseRed() {

        red.setVisibility(View.VISIBLE);
        //Say Red Response
        //robot.speak(TtsRequest.create(getString(R.string.badMood)));
        TtsRequest speak = TtsRequest.create(getString(R.string.badMood));
        robot.speak(speak);
        Toast.makeText(getApplicationContext(), "An error has occured in API call", Toast.LENGTH_LONG).show();
        State.put(speak.getId(), 4);

    }

    // TODO Add in ability to start alternative apps that the user may request, such as a MH chat bot.
    private void responseAmber() {
        amber.setVisibility(View.VISIBLE);
        noButton = findViewById(R.id.mH_no);
        yesButton = findViewById(R.id.mH_yes);

        //TODO Complete Response Based on Url
        robot.speak(TtsRequest.create(getString(R.string.amberMood)+getString(R.string.amberHelp)));
        //robot.speak(TtsRequest.create(getString(R.string.amberHelp)));

        WebView amberWebView = (WebView) findViewById(R.id.WebView);
        amberWebView.loadUrl("https://www.google.com");



        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                String videoId = "AImuCtIokl0";
                youTubePlayer.loadVideo(videoId, 0);
                youTubePlayer.play();
            }
        });

        yesButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Yes Button Clicked", Toast.LENGTH_SHORT).show();
                Log.d("MHApp", "Yes Button Clicked, getting response again");
                youTubePlayerView.release();

                float prevX = x;
                x = 0;
                getResponse();
                if (prevX <= x){
                    responseRed();
                }
                else if (prevX >= x+0.35){
                    responseGreen();
                }
                else {
                    amberCount +=1;
                    responseAmber();
                }
            }
        });

        //Redo?
        noButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "No Button Clicked", Toast.LENGTH_SHORT).show();
                youTubePlayerView.release();

                responseRed();

            }
        });
    }

    private void responseGreen() {
        green.setVisibility(View.VISIBLE);
        //Say Green Response
        TtsRequest speak = TtsRequest.create(getString(R.string.goodMood));
        robot.speak(speak);
        State.put(speak.getId(), 4);
    }

    private void getResponse(){
        Log.d("MHApp", "Getting user input");
        View displayBar = findViewById(R.id.mh_Display_Image);

        displayBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                displayBar.performClick();
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    y = event.getY();
                    float widthP = event.getX();
                    DisplayMetrics displayMetrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                    int height = displayMetrics.heightPixels;
                    int width = displayMetrics.widthPixels;
                    x = (widthP / width) * 100;

                    Log.d("MHApp", "Response is, x = " + String.valueOf(x));
                    if (x != 0){
                        MHCheck(3);
                        return true;
                    } else {
                        return false;
                    }
                }
                else{
                    Log.d("MHApp", "Else response is, x = " + String.valueOf(event.getX()));
                    Log.d("MHApp", "Else response is, " + String.valueOf(event.toString()));

                    return true;
                }
            }
        });
    }

            @Override
            public void onPublish(@NonNull ActivityStreamPublishMessage activityStreamPublishMessage) {

            }

            @Override
            public void onConversationAttaches(boolean b) {

            }

            @Override
            public void onNlpCompleted(@NonNull NlpResult nlpResult) {

            }

            @Override
            public void onTtsStatusChanged(@NonNull TtsRequest ttsRequest) {
                String Text = ttsRequest.toString();
                Log.d("MHApp", "TTS changed. State is, " + State.toString() + "TTs is, tts = " + Text);
                UUID id = ttsRequest.getId();
                if (ttsRequest.getStatus().equals(TtsRequest.Status.COMPLETED) && State.containsKey(id)){
                    Log.d("MHApp", "TTS completed moving to state " + State.get(id));
                    /* ToDo replace with wait for end of tts */
                    int secs = 2; // Delay in seconds

                    Util.delay(secs, new Util.DelayCallback() {
                        @Override
                        public void afterDelay() {
                            // Start next Stage after delay
                            Log.d("MHApp", "Waited for delay");
                            MHCheck(State.get(id));
                        }
                    });
                }
                else if ( ttsRequest.getStatus().equals(TtsRequest.Status.COMPLETED) ) {
                    Log.d("MHApp", "TTS completed, but unknown state or no state change needed for text: " + ttsRequest.getSpeech());
                }
            }

            @Override
            public void onWakeupWord(@NonNull String s, int i) {

            }

            @Override
            public void onBeWithMeStatusChanged(@NonNull String s) {

            }

            @Override
            public void onGoToLocationStatusChanged(@NonNull String location, @NonNull String status, int i, @NonNull String description) {
                //robot.speak(TtsRequest.create(location, false));
                //robot.speak(TtsRequest.create(status, false));
                Log.d("MHApp", "Location changed to state " + location + " " + status + " " + description);
                if (location.equals("seat") && status.equals("complete")){
                    MHCheck(0);
                }
                if (location.equals("home base") && status.equals("complete")){
                    robot.goTo("seat");
                }

            }

            @Override
            public void onLocationsUpdated(@NonNull List<String> list) {

            }

            @Override
            public void onRobotReady(boolean b) {
                if (b) {
                  robot.hideTopBar();


                    Log.i(TAG, "Set detection mode: ON");
                    robot.setDetectionModeOn(true, 2.0f); // Set detection mode on; set detection distance to be 2.0 m

                    Log.i(TAG, "Set track user: ON");
                    robot.setTrackUserOn(true); // Set tracking mode on
                    // Note: When exiting the application, track user will still be enabled unless manually disabled
                }


            }

    @Override
    public void onDetectionDataChanged(@NonNull DetectionData detectionData) {
        if (detectionData.isDetected()) {
            Log.i(TAG, "OnDetectionDataChanged: " + detectionData.getDistance() + " m");
        }
    }

    @Override
    public void onDetectionStateChanged(int state) {
        switch (state) {
            case OnDetectionStateChangedListener.IDLE:
                // No active detection and/or 10 seconds have passed since the last detection was lost
                Log.i(TAG, "OnDetectionStateChanged: IDLE");
                break;
            case OnDetectionStateChangedListener.LOST:
                // When human-target is lost
                Log.i(TAG, "OnDetectionStateChanged: LOST");
                break;
            case OnDetectionStateChangedListener.DETECTED:
                // Human is detected
                Log.i(TAG, "OnDetectionStateChanged: DETECTED");
                break;
            default:
                // This should not happen
                Log.i(TAG, "OnDetectionStateChanged: UNKNOWN");
                break;
        }
    }

    


    @Override
    public void onUserInteraction(boolean isInteracting) {
        if (isInteracting) {
            // User is interacting with the robot:
            // - User is detected
            // - User is interacting by touch, voice, or in telepresence-mode
            // - Robot is moving
            Log.i(TAG, "OnUserInteraction: TRUE");
        } else {
            // User is not interacting with the robot
            Log.i(TAG, "OnUserInteraction: FALSE");
        }
    }
    }

