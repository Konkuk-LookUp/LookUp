package com.example.lookup.view.body.input

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.lookup.data.UserModel
import com.example.lookup.databinding.ActivityGenerateModelBinding
import com.example.lookup.httpConnection.HttpFunc

class GenerateModelActivity : AppCompatActivity() {
    lateinit var binding:ActivityGenerateModelBinding

    val url = "http://ec2-3-36-70-109.ap-northeast-2.compute.amazonaws.com:3000/get-obj/obj파일이름"
    val httpManager = HttpFunc(this)
    // function 동작시 url 넘기기
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGenerateModelBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initLayout()
    }

    private fun initLayout() {
        var height = 180.0
        var weight = 70.0
        var userModel:UserModel
        var check = false
        binding.apply {
            generateBtn.setOnClickListener {
                try {
                    if (editHeight.text.isNotEmpty()) {
                        height = editHeight.text.toString().toDouble()
                    }
                    if (editWegiht.text.isNotEmpty()) {
                        weight = editWegiht.text.toString().toDouble()
                    }
                    //TODO 모델 정보 수정
//                    userModel = UserModel(name, height.toString(), weight.toString())
                    check = true
                }catch(e:NumberFormatException){
                    check = false
                    editHeight.text.clear()
                    editWegiht.text.clear()
                    editShoulder.text.clear()
                    editArm.text.clear()
                    editWegiht.text.clear()
                    Toast.makeText(this@GenerateModelActivity, "유효한 숫자를 입력하세요.", Toast.LENGTH_SHORT).show()
                }
                if(check) {
                    httpManager.GET("WAITAO.obj")
                }
            }

            //TODO 모델 생성 정보 서버에 넘기기
            //TODO 파일로 저장 or string stream?
            //TODO 생성하기를 통해 intent 값을 넘김

        }
    }
}