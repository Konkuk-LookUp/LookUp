package com.example.lookup.util

import android.util.Log
import com.example.lookup.data.UserModel

object ModelParser {
    private const val TAG = "ModelParser"
    private const val MODEL_DELIMITER = "/"

    private val defaultFiles = mapOf(
        Pair("men-180-70","men-180-70.obj"),
        Pair("men-170-70","men.obj"),
        Pair("female","female.obj"),
    )

    private val fittingFiles = mapOf(
        Pair("men-180-70${MODEL_DELIMITER}shoes","men-180-70-shoes.stl")
    )

    fun getFilename(userModel: UserModel): String {

        val height = userModel.getHeight()
        val weight = userModel.getWeight()
        val size = userModel.getSize()

        val key = "men-$height-$weight"
        Log.d(TAG, "key = $key")

        if (defaultFiles.containsKey(key)) {
            return defaultFiles[key]!!
        }
        return "female.obj"
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
