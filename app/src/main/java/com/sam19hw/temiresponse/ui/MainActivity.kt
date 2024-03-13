package com.sam19hw.temiresponse.ui

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.*
import com.robotemi.sdk.Robot
import com.sam19hw.temiresponse.data.ActivityItem
import com.sam19hw.temiresponse.data.ActivityAdapter
import com.sam19hw.temiresponse.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {


    lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        Robot.getInstance().showTopBar()
        val activityItems = ArrayList<ActivityItem>()


        // Lookup the recyclerview in activity binding
        val rvContacts = binding.recycler

        // Initialize list
        //activityItems = ActivityItem.createActivitiesList(20)
        activityItems.add(ActivityItem.createActivity("NavTestActivity", "com.sam19hw.temiresponse.ui.NavTestActivity",  true))
        activityItems.add(ActivityItem.createActivity("MHActivity", "com.sam19hw.temiresponse.ui.checkin.MHActivity", true))
        activityItems.add(ActivityItem.createActivity("MHapp", "com.sam19hw.temiresponse.ui.checkin.MHapp", false))
        activityItems.add(ActivityItem.createActivity("RetrofitDoor", "com.sam19hw.temiresponse.ui.RetrofitDoorActivity", true))
        activityItems.add(ActivityItem.createActivity("FirebaseTestActivity", "com.sam19hw.temiresponse.ui.FcmActivity", true))
        activityItems.add(ActivityItem.createActivity("ThresholdCrossing", "com.sam19hw.temiresponse.ui.ThresholdCrossing", false))
        activityItems.add(ActivityItem.createActivity("TemiControllerJetpack", "com.sam19hw.temiresponse.ui.TemiControllerJetpack", false))
        activityItems.add(ActivityItem.createActivity("TemiLayout", "com.sam19hw.temiresponse.ui.TemiLayout", false))


        // Create adapter passing in the sample user data
        val adapter = ActivityAdapter(activityItems, this@MainActivity)
        // Attach the adapter to the recyclerview to populate items
        rvContacts.adapter = adapter
        // Set layout manager to position the items
        rvContacts.layoutManager = LinearLayoutManager(this)




        // Assign ItemAdapter instance to our RecylerView
        //binding?.rvItemsList?.adapter = ItemAdapter

        // Applying OnClickListener to our Adapter
        adapter.setOnClickListener(object :
            ActivityAdapter.OnClickListener {
            override fun onClick(position: Int, model: ActivityItem) {
                val intent = Intent(this@MainActivity, Class.forName(model.path))
                //val intent = Intent(this@MainActivity, NavTestActivity::class.java)
                // Passing the data to the
                // EmployeeDetails Activity
                //intent.putExtra(NEXT_SCREEN, model)
                startActivity(intent)
            }
        })


        //Switch to other Activity -- manual override of recycler list
        //val j = Intent(this@MainActivity, MHapp::class.java)
        //val j = Intent(this@MainActivity, MHActivity::class.java)
        //val j = Intent(this@MainActivity, NavTestActivity::class.java)
        //startActivity(j)

    }

    override fun onResume() {
        super.onResume()
        Robot.getInstance().showTopBar()
    }
}
