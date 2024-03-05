package com.sam19hw.temiresponse.ui

import android.graphics.Path.Op
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import com.sam19hw.temiresponse.R
import com.sam19hw.temiresponse.data.APICaller
import com.sam19hw.temiresponse.databinding.ActivityRetrofitDoorBinding

class RetrofitDoorActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRetrofitDoorBinding
    var api: APICaller = APICaller()
    private var doorNumber: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_retrofit_door)
        binding = ActivityRetrofitDoorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.button3.setOnClickListener { OpenDoor() }
        binding.button3.text = "Open Door"
        binding.button4.setOnClickListener { CloseDoor() }
        binding.button4.text = "Close Door"
        Log.d("RetrofitActivity","Activity Started")
    }

    public fun OpenDoor(){
        api.OpenDoor(doorNumber,true)
        Log.d("RetrofitActivity","Opening Door number: " + doorNumber)
    }
    public fun CloseDoor(){
        api.OpenDoor(doorNumber,false)
        Log.d("RetrofitActivity","Closing Door number: " + doorNumber)
    }

}