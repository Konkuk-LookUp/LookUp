package com.example.lookup.data

import com.example.lookup.module.pose.data.BodyPart
import com.example.lookup.module.pose.data.KeyPoint
import com.example.lookup.util.SizeCalculator

class UserModel() {
    private lateinit var sizeInfo: SizeInfo
    private var height = 180f
    private var weight = 70f

    // 생성자 하나 만들었습니다.
    constructor(height:Float, weight:Float,info:SizeInfo):this(){
        this.height = height
        this.weight = weight
        this.sizeInfo = info
    }

    constructor(height:Float, weight:Float, keyPoints:List<KeyPoint>):this(){
        this.height = height
        this.weight = weight
        setSizeInfo(keyPoints, height)
    }

    private fun setSizeInfo(
        keyPoints: List<KeyPoint>,
        height: Float
    ) {
        val pixelHeight = SizeCalculator.setPixelHeight(
            SizeCalculator.findKeyPoint(BodyPart.LEFT_EAR, keyPoints!!),
            SizeCalculator.findKeyPoint(BodyPart.LEFT_ANKLE, keyPoints!!)
        )
        sizeInfo = SizeInfo(keyPoints!!, pixelHeight, height)
    }

    fun getHeight():Float{
        return height
    }

    fun getWeight():Float{
        return height
    }

    fun getSize():SizeInfo{
        return sizeInfo
    }
}
