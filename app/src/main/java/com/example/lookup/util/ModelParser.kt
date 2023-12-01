package com.example.lookup.util

import android.util.Log
import com.example.lookup.data.UserModel
import com.google.common.math.DoubleMath.roundToInt
import kotlin.math.roundToInt

object ModelParser {
    private const val TAG = "ModelParser"
    private const val MODEL_DELIMITER = "/"

    private val defaultFiles = mapOf(
        Pair("men-160-60","men-160-60.obj"),
        Pair("men-160-65","men-160-65.obj"),
        Pair("men-160-70","men-160-70.obj"),
        Pair("men-160-75","men-160-75.obj"),
        Pair("men-160-80","men-160-80.obj"),
        Pair("men-160-85","men-160-85.obj"),
        Pair("men-160-90","men-160-90.obj"),
        Pair("men-170-60","men-170-60.obj"),
        Pair("men-170-65","men-170-65.obj"),
        Pair("men-170-70","men-170-70.obj"),
        Pair("men-170-75","men-170-75.obj"),
        Pair("men-170-80","men-170-80.obj"),
        Pair("men-170-85","men-170-85.obj"),
        Pair("men-170-90","men-170-90.obj"),
        Pair("men-180-60","men-180-60.obj"),
        Pair("men-180-65","men-180-65.obj"),
        Pair("men-180-70","men-180-70.obj"),
        Pair("men-180-75","men-180-75.obj"),
        Pair("men-180-80","men-180-80.obj"),
        Pair("men-180-85","men-180-85.obj"),
        Pair("men-180-90","men-180-90.obj"),
        Pair("men-190-60","men-190-60.obj"),
        Pair("men-190-65","men-190-65.obj"),
        Pair("men-190-70","men-190-70.obj"),
        Pair("men-190-75","men-190-75.obj"),
        Pair("men-190-80","men-190-80.obj"),
        Pair("men-190-85","men-190-85obj"),
        Pair("men-190-90","men-190-90.obj"),
        Pair("female","female.obj"),
    )

    private val fittingFiles = mapOf(
        Pair("men-180-70${MODEL_DELIMITER}shoes","men-180-70-shoes.stl"),
        Pair("men-180-70${MODEL_DELIMITER}hood","men-180-70-hood.obj"),
        Pair("men-180-70${MODEL_DELIMITER}shortpants","men-180-70-shortpants.obj"),
        Pair("men-180-70${MODEL_DELIMITER}longpants","men-180-70-longpants.obj"),
        Pair("men-180-70${MODEL_DELIMITER}tshirt","men-180-70-tshirt.obj")
    )

    fun getFilename(userModel: UserModel): String {

        val height = (userModel.getHeight()/10.0).roundToInt()*10
        val weight = roundWeight(userModel.getWeight())
        val size = userModel.getSize()

        val key = "men-$height-$weight"
        Log.d(TAG, "key = $key")

        if (defaultFiles.containsKey(key)) {
            return defaultFiles[key]!!
        }
        return "female.obj"
    }

    private fun roundWeight(weight: Int): Int {
        val rangeStart = weight / 5 * 5
        val rangeEnd = rangeStart + 5
        val middle = (rangeStart + rangeEnd) / 2.0

        return if (weight < middle) {
            rangeStart
        } else {
            rangeEnd
        }
    }

    fun getFittingModelFilename(userModelFilename: String, clothName: String): String? {
        val filename = "${userModelFilename.split(".")[0]}$MODEL_DELIMITER$clothName"
        Log.d(
            "ModelParser",
            "getFittingModelFilename() called with: $filename and ${fittingFiles[filename]}"
        )
        return fittingFiles[filename]
    }
}
