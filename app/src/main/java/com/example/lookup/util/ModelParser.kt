package com.example.lookup.util

import android.util.Log
import com.example.lookup.data.UserModel

object ModelParser {
    private const val MODEL_DELIMITER = "/"

    private val defaultFiles = mapOf(
        Pair("men","men.obj"),
        Pair("men-180-70","men-180-70.obj"),
        Pair("female","female.obj"),
    )

    private val fittingFiles = mapOf(
        Pair("men${MODEL_DELIMITER}shoes","men-shoes.stl")
    )

    fun getFilename(userModel: UserModel): String {

        val height = userModel.getHeight()
        val weight = userModel.getWeight()
        val size = userModel.getSize()

        if (height >= 180f) {
            return defaultFiles["men-180-70"]!!
        }
        if (height >= 180f) {
            return defaultFiles["men"]!!
        }
        if (height >= 160f) {
            return defaultFiles["female"]!!
        }
        return "WAITAO.obj"
    }

    fun getFittingModelFilename(userModelFilename: String, clothName: String): String? {
        val filename = "${userModelFilename.split(".")[0]}$MODEL_DELIMITER$clothName"
        Log.d(
            "ModelParser",
            "getFittingModelFilename() called with: userModelFilename = ${userModelFilename.split(".")[0]}, clothName = $clothName"
        )
        return fittingFiles[filename]
    }
}
