package com.example.lookup.data

import com.example.lookup.module.pose.data.BodyPart
import com.example.lookup.module.pose.data.KeyPoint
import com.example.lookup.util.SizeCalculator.findKeyPoint
import com.example.lookup.util.SizeCalculator.setSize
import kotlin.reflect.full.memberProperties

data class SizeInfo(
    var shoulder: Float,
    var leftArm: Float,
    var rightArm: Float,
    var topTotalSize: Float,
    var waist: Float,
    var leftLeg: Float,
    var rightLeg: Float
) {
    constructor(keyPoints: List<KeyPoint>, pixelHeight: Float, userHeight:Float) : this(
        setSize(
            "Shoulder",
            findKeyPoint(BodyPart.LEFT_SHOULDER, keyPoints),
            findKeyPoint(BodyPart.RIGHT_SHOULDER, keyPoints),
            pixelHeight,
            userHeight
        ),
        setSize(
            "LeftArm",
            findKeyPoint(BodyPart.LEFT_SHOULDER, keyPoints),
            findKeyPoint(BodyPart.LEFT_WRIST, keyPoints),
            pixelHeight,
            userHeight
        ),
        setSize(
            "RightArm",
            findKeyPoint(BodyPart.RIGHT_SHOULDER, keyPoints),
            findKeyPoint(BodyPart.RIGHT_WRIST, keyPoints),
            pixelHeight,
            userHeight
        ),
        setSize(
            "TopTotalSize",
            findKeyPoint(BodyPart.LEFT_SHOULDER, keyPoints),
            findKeyPoint(BodyPart.LEFT_HIP, keyPoints),
            pixelHeight,
            userHeight
        ),
        setSize(
            "Waist",
            findKeyPoint(BodyPart.LEFT_HIP, keyPoints),
            findKeyPoint(BodyPart.RIGHT_HIP, keyPoints),
            pixelHeight,
            userHeight
        ),
        setSize(
            "LeftLeg",
            findKeyPoint(BodyPart.LEFT_HIP, keyPoints),
            findKeyPoint(BodyPart.LEFT_ANKLE, keyPoints),
            pixelHeight,
            userHeight
        ),
        setSize(
            "RightLeg",
            findKeyPoint(BodyPart.RIGHT_HIP, keyPoints),
            findKeyPoint(BodyPart.RIGHT_ANKLE, keyPoints),
            pixelHeight,
            userHeight
        )
    )
}
