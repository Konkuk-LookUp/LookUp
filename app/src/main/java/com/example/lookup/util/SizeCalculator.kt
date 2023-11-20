package com.example.lookup.util

import android.graphics.PointF
import android.util.Log
import com.example.lookup.module.pose.data.BodyPart
import com.example.lookup.module.pose.data.KeyPoint
import kotlin.math.pow
import kotlin.math.sqrt

object SizeCalculator {
    private val TAG = "SizeCalculator"

    fun findKeyPoint(point: BodyPart, keyPoints: List<KeyPoint>): PointF {
        val keyPoint = keyPoints.firstOrNull { it.bodyPart == point }

        return keyPoint!!.coordinate
    }

    fun setSize(
        name: String,
        firstPoint: PointF,
        secondPoint: PointF,
        pixelHeight: Float,
        userHeight: Float
    ): Float {
        val ratio = userHeight / pixelHeight
        val size = calculateDistance(firstPoint, secondPoint) * ratio;
        Log.d(TAG, "setSize() called with: $name = $size")
        return size
    }

    fun setPixelHeight(
        firstPoint: PointF,
        secondPoint: PointF,
    ): Float {
        val size = calculateDistance(firstPoint, secondPoint);
        Log.d(TAG, "setPixelHeight() called with: pixelHeight = $size")
        return size
    }

    private fun calculateDistance(firstPoint: PointF, secondPoint: PointF): Float {
        return sqrt((secondPoint.x - firstPoint.x).pow(2) + (secondPoint.y - firstPoint.y).pow(2))
    }
}
