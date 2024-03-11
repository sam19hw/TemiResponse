package com.sam19hw.temiresponse.ui

import android.annotation.SuppressLint
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.robotemi.sdk.Robot
import com.robotemi.sdk.listeners.OnDetectionDataChangedListener
import com.robotemi.sdk.listeners.OnDetectionStateChangedListener
import com.robotemi.sdk.listeners.OnGoToLocationStatusChangedListener
import com.robotemi.sdk.listeners.OnLocationsUpdatedListener
import com.robotemi.sdk.listeners.OnRobotReadyListener
import com.robotemi.sdk.map.MapModel
import com.robotemi.sdk.map.OnLoadMapStatusChangedListener
import com.robotemi.sdk.model.DetectionData
import com.robotemi.sdk.navigation.model.Position
import com.robotemi.sdk.permission.Permission
import com.sam19hw.temiresponse.R
import com.sam19hw.temiresponse.data.APICaller
import com.sam19hw.temiresponse.data.Util
import com.sam19hw.temiresponse.databinding.ActivityNavTestBinding

class NavTestActivity : AppCompatActivity(), OnRobotReadyListener, OnGoToLocationStatusChangedListener,
    OnLocationsUpdatedListener, OnDetectionStateChangedListener, OnDetectionDataChangedListener, OnLoadMapStatusChangedListener {

    private lateinit var robot: Robot
    private lateinit var binding: ActivityNavTestBinding
    private var targetLocation : String = ""
    var api: APICaller = APICaller()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nav_test)

        binding = ActivityNavTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d("MHApp", "Start Layout Loaded")

        robot = Robot.getInstance()
        targetLocation = "home base"
        robot.goTo(targetLocation)
        //mapChange("Lara hall", false, null)

    }


    override fun onStart() {
        super.onStart()
        Robot.getInstance().addOnRobotReadyListener(this)
        Robot.getInstance().addOnGoToLocationStatusChangedListener(this)
        Robot.getInstance().addOnLocationsUpdatedListener(this)
        Robot.getInstance().addOnDetectionStateChangedListener(this)
        Robot.getInstance().addOnDetectionDataChangedListener(this)
        Robot.getInstance().addOnLoadMapStatusChangedListener(this)
    }

    override fun onStop() {
        super.onStop()
        Robot.getInstance().removeOnRobotReadyListener(this)
        Robot.getInstance().removeOnGoToLocationStatusChangedListener(this)
        Robot.getInstance().removeOnLocationsUpdateListener(this)
        Robot.getInstance().removeOnDetectionStateChangedListener(this)
        Robot.getInstance().removeOnDetectionDataChangedListener(this)
        //Robot.getInstance().removeOnLoadMapStatusChangedListener(this)
    }


    @SuppressLint("CheckResult")
    override fun onRobotReady(isReady: Boolean) {
        if (isReady) {
            //robot.hideTopBar()
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
            Log.d("Map", "${robot.getMapList()}")
            Log.d("map", "location ${robot.getMapData()?.locations?.toString()}")
            if (targetLocation != "") {
                Log.d("Nav", "Robot ready, resuming goal to go to $targetLocation")
                robot.goTo(targetLocation)
            }
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

    override fun onGoToLocationStatusChanged(
        location: String,
        status: String,
        descriptionId: Int,
        description: String
    ) {
        //TODO("Not yet implemented the position of the map change, which needs to load
        // locations as positions from the map data nad then send as a command to mapChange")
        Log.d("Nav", "Location changed with data: $location, $status, $description, $descriptionId" )

        if (status == "complete") {
            if (location == targetLocation) {
                Log.d("Nav", "Target location reached")
                targetLocation = ""
            }
            when (location) {
                "leaving" -> {
                    Log.d("Nav", "Leaving")
                    robot.goTo("left")
                }

                "left" -> {
                    Log.d("Nav", "Left")
                    //mapChange("hall")
                    robot.goTo("lift entrance")
                }

                "lift entrance" -> {
                    Log.d("Nav", "Starting multi location cross to enter")
                    robot.goTo("entering")
                }

                "entering" -> {
                    Log.d("Nav", "Entering")
                    robot.goTo("entrance")
                }

                "entrance" -> {
                    Log.d("Nav", "entered")
                    try {
                        //api.OpenDoor(1,false)
                    }catch (e: Exception){
                        Log.e("api", "Error in closing lara hall door, $e")
                    }
                    val position = Position(3.332714f,0.384067f,1.691611f,0)
                    robot.getMapData()?.locations?.forEach {
                        Log.d("Map", it.toString())
                    }
                    mapChange("fourier", true, position )
                    targetLocation = "home base"
                }

                "Home Base" -> {
                    Log.d("Nav", "restarting navigation as planned from Home Base")
                    targetLocation = "leaving"
                    try {
                        //api.OpenDoor(1,true)
                    }catch (e: Exception){
                        Log.e("api", "Error in opening lara hall door, $e")
                    }
                    try {
                        //robot.loadMap("219f6b94d8c13c7c6737ca9351827f3c", false, null)
                        val position = Position(0f,0f,0f,0)
                        mapChange("fourier lab", true, position)
                    }catch (e: Exception){
                        Log.e("Map", "Error in loading lara hall map, $e")
                    }
                    //targetLocation = "leaving"
                }

                "home base" -> {
                    Log.d("Nav", "restarting navigation as planned from home base")

                    targetLocation = "leaving"
                    try {
                        //api.OpenDoor(1,true)
                    }catch (e: Exception){
                        Log.e("api", "Error in opening lara hall door, $e")
                    }
                    try {
                        //robot.loadMap("219f6b94d8c13c7c6737ca9351827f3c", false, null)
                        val position = Position(0f,0f,0f,0)
                        //LayerPose(x=-0.07379, y=0.004862, theta=-0.001249)
                        mapChange("fourier lab", true, position)
                    }catch (e: Exception){
                        Log.e("Map", "Error in loading lara hall map, $e")
                    }
                }
            }
        } else if (status == "abort") {
            val secs = 5 // Delay in seconds
            Log.e("Nav", "Navigation aborted, retrying in $secs seconds")
            robot.repose()

            Util.delay(secs) { // Start next Stage after delay
                Log.d("Nav", "Waited for delay after navigation aborted to account for system delays after callback. Going to location $location")
                robot.goTo(location)
            }
        }

    }

    private fun mapChange(s: String, reposeRequired: Boolean, position: Position?) {
        // TODO add in multi map test, swapping to the map of the hallway in the LARA lab, and then back again to the LARA when in the lab
        //
        Log.d("Map", "Map change requested with with $s")
        if (mapList.isEmpty()) {
            getMapList()
        }
        if (robot.checkSelfPermission(Permission.MAP) != Permission.GRANTED) {
            Log.e("Map","Error, permission not granted")
            return
        }
        val mapListString: MutableList<String> = ArrayList()
        var pos: Int? = null
        for (i in mapList.indices) {
            mapListString.add(mapList[i].name)
            if (mapList[i].name == s) {
                pos = i
            }
        }
        if (pos?.equals(null) == false) {
            if (position == null) {
                robot.loadMap(mapList[pos].id, reposeRequired)
            } else {
                robot.loadMap(mapList[pos].id, reposeRequired, position)
            }
            Log.d("Map", "Map change initiated successfully")
        } else  {
            Log.d("Map", "Map did not change with $pos and $mapList")
            robot.goTo(targetLocation)
        }
    }

    private var mapList: List<MapModel> = ArrayList()
    private fun getMapList() {
        Log.d("Map", "Map list requested")
        mapList = robot.getMapList()
    }


    override fun onLocationsUpdated(locations: List<String>) {
        //TODO("Not yet implemented, don't imagine that it needs to be but maybe " +
       //         "useful to log if I need to update the locations of the start and stop crossing the threshold")
        //Log.d("Nav","locations updated")
    }


    override fun onLoadMapStatusChanged(status: Int) {
        if (status == 0) {
            Log.d("Map", "On Load Map Listener status Complete, moving to: $targetLocation")
            if (targetLocation != ""){
                /* ToDo replace with wait for end of positioning */
                val secs = 5 // Delay in seconds

                Util.delay(secs) { // Start next Stage after delay
                    Log.d("Nav", "Waited for delay after map change to account for system delays after callback. Going to location $targetLocation")
                    robot.goTo(targetLocation)
                }
                //targetLocation = ""
            }
        }
        else Log.d("Map", "On Load Map Listener status $status")
    }
}