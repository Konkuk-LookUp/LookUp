package com.example.lookup.view.body.input

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.lookup.databinding.ActivityCameraInputBinding
import com.example.lookup.view.body.BodyCameraActivity

class CameraInputActivity : AppCompatActivity() {
    lateinit var binding:ActivityCameraInputBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraInputBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initLayout()
    }

    private fun initLayout() {
        var height = "180"
        var weight = "70"
        binding.apply {
            //TODO 입력 값에 대한 예외처리 구현
            generateBtn.setOnClickListener {
                if(editHeight.text.isNotEmpty()){
                    height = editHeight.text.toString()
                }
                if(editWegiht.text.isNotEmpty()){
                    weight = editWegiht.text.toString()
                }
                startCamera(height,weight)
            }
        }

    }
    private fun startCamera(height:String, weight:String){
        val intent = Intent(this, BodyCameraActivity::class.java)
        intent.putExtra("키",height)
        intent.putExtra("몸무게",weight)
        startActivity(intent)
    }
}