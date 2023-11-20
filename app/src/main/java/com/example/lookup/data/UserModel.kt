package com.example.lookup.data

import com.example.lookup.module.pose.data.BodyPart
import com.example.lookup.module.pose.data.KeyPoint
import com.example.lookup.util.SizeCalculator
import kotlin.reflect.full.memberProperties

class UserModel() {
    lateinit var sizeInfo: SizeInfo

    constructor(userHeight:Float, keyPoints:List<KeyPoint>):this(){
        val pixelHeight = SizeCalculator.setPixelHeight(
            SizeCalculator.findKeyPoint(BodyPart.LEFT_EAR, keyPoints!!),
            SizeCalculator.findKeyPoint(BodyPart.LEFT_ANKLE, keyPoints!!)
        )
        sizeInfo = SizeInfo(keyPoints!!,pixelHeight,userHeight)
    }
}
