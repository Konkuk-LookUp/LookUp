package com.example.lookup.view.body.input

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.lookup.MainActivity
import com.example.lookup.R
import com.example.lookup.data.SizeInfo
import com.example.lookup.data.UserModel
import com.example.lookup.databinding.ActivityGenerateModelBinding
import com.example.lookup.util.ModelParser
import com.example.lookup.util.PreferenceManager

class GenerateModelActivity : AppCompatActivity() {
    lateinit var binding:ActivityGenerateModelBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGenerateModelBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initLayout()
    }

    private fun initLayout() {
        var height :Float
        var weight :Float
        var shoulder:Float
        var arm:Float
        var leg:Float
        var waist:Float
        var check = false
        binding.apply {
            generateBtn.setOnClickListener {
                check = false
                try {
                    if (validateHeight()) {
                        clearInputText()
                        makeToast("${GenerateModelActivity.MIN_HEIGHT} cm 이상 ${GenerateModelActivity.MAX_HEIGHT} cm 이하의 숫자를 입력하세요.")
                        return@setOnClickListener
                    }
                    if (validateWeight()) {
                        clearInputText()
                        makeToast("${GenerateModelActivity.MIN_WEIGHT} cm 이상 ${GenerateModelActivity.MAX_WEIGHT} cm 이하의 숫자를 입력하세요.")
                        return@setOnClickListener
                    }
                    if (validateArm()) {
                        clearInputText()
//                        makeToast("${GenerateModelActivity.MIN_HEIGHT} cm 이상 ${GenerateModelActivity.MAX_HEIGHT} cm 이하의 숫자를 입력하세요.")
                        return@setOnClickListener
                    }
                    if (validateLeg()) {
                        clearInputText()
//                        makeToast("${GenerateModelActivity.MIN_HEIGHT} cm 이상 ${GenerateModelActivity.MAX_HEIGHT} cm 이하의 숫자를 입력하세요.")
                        return@setOnClickListener
                    }
                    if (validateWaist()) {
                        clearInputText()
//                        makeToast("${GenerateModelActivity.MIN_HEIGHT} cm 이상 ${GenerateModelActivity.MAX_HEIGHT} cm 이하의 숫자를 입력하세요.")
                        return@setOnClickListener
                    }
                    if (validateShoulder()) {
                        clearInputText()
//                        makeToast("${GenerateModelActivity.MIN_HEIGHT} cm 이상 ${GenerateModelActivity.MAX_HEIGHT} cm 이하의 숫자를 입력하세요.")
                        return@setOnClickListener
                    }
                    //TODO 모델 정보 수정
//                    userModel = UserModel(name, height.toString(), weight.toString())
                    check = true
                }catch(e:NumberFormatException){
                    check = false
                    clearInputText()
                    Toast.makeText(this@GenerateModelActivity, "유효한 숫자를 입력하세요.", Toast.LENGTH_SHORT).show()
                }
                if(check) {
                    height = editHeight.text.toString().toFloat()
                    weight = editWegiht.text.toString().toFloat()
                    shoulder= editShoulder.text.toString().toFloat()
                    arm = editArm.text.toString().toFloat()
                    leg = editLeg.text.toString().toFloat()
                    waist = editWaist.text.toString().toFloat()
                    val sizeInfo = SizeInfo(shoulder,arm,arm,weight,waist,leg,leg)

                    var userModel = UserModel(height, weight,sizeInfo)
                    generateModel(userModel)

                }
            }


        }
    }
    private fun generateModel(userModel:UserModel){
        val fileName = ModelParser.getFilename(userModel)
        PreferenceManager.setString(this,"filename",fileName)

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun clearInputText(){
        binding.apply {
            editHeight.text.clear()
            editWegiht.text.clear()
            editArm.text.clear()
            editShoulder.text.clear()
            editLeg.text.clear()
            editWaist.text.clear()
        }
    }
    private fun validateWaist(): Boolean {
        val waist = binding.editWaist.text.toString()
        return waist.isBlank()
    }
    private fun validateShoulder(): Boolean {
        val shoulder = binding.editShoulder.text.toString()
        return shoulder.isBlank()// || (shoulder.toFloat() < MIN_WEIGHT) || (shoulder.toFloat() > MAX_WEIGHT)
    }
    private fun validateLeg(): Boolean {
        val leg = binding.editLeg.text.toString()
        return leg.isBlank()// || (leg.toFloat() < MIN_WEIGHT) || (leg.toFloat() > MAX_WEIGHT)
    }
    private fun validateArm(): Boolean {
        val arm = binding.editArm.text.toString()
        return arm.isBlank()// || (arm.toFloat() < MIN_WEIGHT) || (arm.toFloat() > MAX_WEIGHT)
    }
    private fun validateWeight(): Boolean {
        val weight = binding.editWegiht.text.toString()
        return weight.isBlank() || (weight.toFloat() < MIN_WEIGHT) || (weight.toFloat() > MAX_WEIGHT)
    }

    private fun validateHeight(): Boolean {
        val height = binding.editHeight.text.toString()
        return binding.editHeight.text.isBlank() || (height.toFloat() < MIN_HEIGHT)|| (height.toFloat() > MAX_HEIGHT)
    }
    private fun makeToast(text: String) {
        Toast.makeText(this@GenerateModelActivity, text, Toast.LENGTH_SHORT)
            .show()
    }
    companion object{
        const val MAX_HEIGHT = 190f;
        const val MIN_HEIGHT = 160f;
        const val MAX_WEIGHT = 90f;
        const val MIN_WEIGHT = 60f;
        //TODO shoulder, leg, arm ,waist 에 대한 min max?
        const val MAX_SHOULDER = 70f
        const val MIN_SHOULDER = 20f
    }
}
