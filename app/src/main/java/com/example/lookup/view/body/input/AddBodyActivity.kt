package com.example.lookup.view.body.input

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.lookup.databinding.ActivityAddBodyBinding

class AddBodyActivity : AppCompatActivity() {
    lateinit var binding: ActivityAddBodyBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBodyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initLayout()
    }

    private fun initLayout() {
        binding.apply {
            textInput.setOnClickListener {
                startText()
            }
            cameraInput.setOnClickListener {
                startCamera()
            }
        }
    }

    private fun startText() {
        val intent = Intent(this, GenerateModelActivity::class.java)
        startActivity(intent)
    }

    private fun startCamera(){
        val intent = Intent(this, CameraInputActivity::class.java)
        startActivity(intent)
    }
}