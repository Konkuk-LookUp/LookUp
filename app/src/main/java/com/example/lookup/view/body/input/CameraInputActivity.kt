package com.example.lookup.view.body.input

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.lookup.databinding.ActivityCameraInputBinding
import com.example.lookup.view.body.BodyCameraActivity

class CameraInputActivity : AppCompatActivity() {
    lateinit var binding: ActivityCameraInputBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraInputBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initLayout()
    }

    private fun initLayout() {
        var height: Float
        var weight: Float
        binding.apply {
            //TODO 입력 값에 대한 예외처리 구현
            generateBtn.setOnClickListener {
                try {
                    if (validateHeight()) {
                        clearInputText()
                        makeToast("$MIN_HEIGHT cm 이상 $MAX_HEIGHT cm 이하의 숫자를 입력하세요.")
                        return@setOnClickListener
                    }
                    if (validateWeight()) {
                        clearInputText()
                        makeToast("$MIN_WEIGHT kg 이상 $MAX_WEIGHT kg 이하의 숫자를 입력하세요.")
                        return@setOnClickListener
                    }
                    height = editHeight.text.toString().toFloat()
                    weight = editWegiht.text.toString().toFloat()
                    startCamera(height, weight)
                } catch (e: NumberFormatException) {
                    // 잘못된 값 입력
                    clearInputText()
                    makeToast("유효한 숫자를 입력하세요.")
                }
            }
        }
    }

    private fun makeToast(text: String) {
        Toast.makeText(this@CameraInputActivity, text, Toast.LENGTH_SHORT)
            .show()
    }

    private fun ActivityCameraInputBinding.clearInputText() {
        editHeight.text.clear()
        editWegiht.text.clear()
    }

    private fun validateWeight(): Boolean {
        val weight = binding.editWegiht.text.toString()
        return weight.isBlank() || (weight.toFloat() < MIN_WEIGHT) || (weight.toFloat() > MAX_WEIGHT)
    }

    private fun validateHeight(): Boolean {
        val height = binding.editHeight.text.toString()
        return binding.editHeight.text.isBlank() || (height.toFloat() < MIN_HEIGHT)|| (height.toFloat() > MAX_HEIGHT)
    }

    private fun startCamera(height: Float, weight: Float) {
        val intent = Intent(this, BodyCameraActivity::class.java)
        intent.putExtra("height", height)
        intent.putExtra("weight", weight)
        startActivity(intent)
    }

    companion object{
        const val MAX_HEIGHT = 190f;
        const val MIN_HEIGHT = 160f;
        const val MAX_WEIGHT = 90f;
        const val MIN_WEIGHT = 60f;
    }
}
