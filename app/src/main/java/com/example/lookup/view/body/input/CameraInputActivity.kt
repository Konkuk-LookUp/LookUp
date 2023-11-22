package com.example.lookup.view.body.input

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
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
        var height = 180.0
        var weight = 70.0
        binding.apply {
            //TODO 입력 값에 대한 예외처리 구현
            generateBtn.setOnClickListener {
                try {
                    if (editHeight.text.isNotEmpty()) {
                        height = editHeight.text.toString().toDouble()
                    }
                    if (editWegiht.text.isNotEmpty()) {
                        weight = editWegiht.text.toString().toDouble()
                    }
                    startCamera(height, weight)
                }catch (e:NumberFormatException){
                    // 잘못된 값 입력
                    editHeight.text.clear()
                    editWegiht.text.clear()
                    Toast.makeText(this@CameraInputActivity, "유효한 숫자를 입력하세요.", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }
    private fun startCamera(height:Double, weight:Double){
        val intent = Intent(this, BodyCameraActivity::class.java)
        intent.putExtra("키",height)
        intent.putExtra("몸무게",weight)
        startActivity(intent)
    }
}