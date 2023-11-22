package com.example.lookup.util

import com.example.lookup.data.UserModel

object ModelParser {

    private val files = mapOf(
        Pair("sample_model","sample_model.stl"),
        Pair("men_wear_shoes","men_wear_shoes.obj"),
        Pair("default_model","default_model.obj"),
    )

    fun getFileName(userModel: UserModel): String {

        val height = userModel.getHeight()
        val weight = userModel.getWeight()
        val size = userModel.getSize()

        if (height >= 180f) {
            return files["sample_model"]!!
        }
        if (height >= 170f) {
            return files["default_model"]!!
        }
        return "WAITAO.obj"
    }
}
