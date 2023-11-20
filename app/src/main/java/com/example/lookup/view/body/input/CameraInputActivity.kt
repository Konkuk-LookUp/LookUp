package com.example.lookup.view.body

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.lookup.databinding.ActivityCameraInputBinding

class CameraInputActivity : AppCompatActivity() {
    lateinit var binding:ActivityCameraInputBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraInputBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initLayout()
    }

    private fun initLayout() {
        TODO("Not yet implemented")
    }
}