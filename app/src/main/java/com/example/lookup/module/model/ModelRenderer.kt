package com.example.lookup.module.model

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.lookup.module.model.obj.ObjModel
import com.example.lookup.module.model.stl.StlModel
import com.example.lookup.util.PreferenceManager
import com.google.android.play.integrity.internal.f
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.pow

/*
* Copyright 2017 Dmitry Brant. All rights reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
class ModelRenderer(private val model: Model?,private val context: Context) : GLSurfaceView.Renderer {
    private val light = Light(floatArrayOf(0.0f, 0.0f, MODEL_BOUND_SIZE * 10, 1.0f))
    private val floor = Floor()

    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)

    private var rotateAngleX = 0f
    private var rotateAngleY = 0f
    private var translateX = 0f
    private var translateY = 0f
    private var translateZ = 0f

    fun translate(dx: Float, dy: Float, dz: Float) {
        val translateScaleFactor = MODEL_BOUND_SIZE / 200f
        translateX += dx * translateScaleFactor
        translateY += dy * translateScaleFactor
        if (dz != 0f) {
            translateZ /= dz
        }
        updateViewMatrix()
    }

    fun rotate(aX: Float, aY: Float) {
        val rotateScaleFactor = 0.5f
        rotateAngleX -= aX * rotateScaleFactor
        rotateAngleY += aY * rotateScaleFactor
        updateViewMatrix()
    }

    private fun updateViewMatrix() {
        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, translateZ, 0f, 0f, 0f, 0f, 1.0f, 0.0f)
        Matrix.translateM(viewMatrix, 0, -translateX, -translateY, 0f)
        Matrix.rotateM(viewMatrix, 0, rotateAngleX, 1f, 0f, 0f)
        Matrix.rotateM(viewMatrix, 0, rotateAngleY, 0f, 1f, 0f)
    }

    override fun onDrawFrame(unused: GL10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        floor.draw(viewMatrix, projectionMatrix, light)
        model?.draw(viewMatrix, projectionMatrix, light)
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        val ratio = width.toFloat() / height
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, Z_NEAR, Z_FAR)

        // initialize the view matrix
        rotateAngleX = 0f
        rotateAngleY = 0f
        translateX = 0f
        val filename = PreferenceManager.getString(context, "filename")
        val height:Float = if (!filename.isNullOrBlank()) {
            filename.split("-")[1].toFloat()
        }else{
            160f
        }

        if(model is StlModel){
            translateY = MODEL_BOUND_SIZE / 2.2f
            translateZ = -1* (-0.6*(height-160)+78).toFloat()
        }else if(model is ObjModel){
            translateY = -MODEL_BOUND_SIZE /6f
            translateZ = -1* (-0.6*(height-160)+70).toFloat()
        }
        updateViewMatrix()

        // Set light matrix before doing any other transforms on the view matrix
        light.applyViewMatrix(viewMatrix)

        // By default, rotate the model towards the user a bit
        rotateAngleX = -15.0f
//        rotateAngleY = 15.0f
        updateViewMatrix()
    }

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
//        GLES20.glClearColor(0.2f, 0.2f, 0.2f, 1f)
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1f)
        GLES20.glEnable(GLES20.GL_CULL_FACE)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        //GLES20.glEnable(GLES20.GL_BLEND);
        //GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        floor.setup(MODEL_BOUND_SIZE)
        model?.let {
            it.setup(MODEL_BOUND_SIZE)
            floor.setOffsetY(it.floorOffset)
        }
    }

    companion object {
        private const val MODEL_BOUND_SIZE = 50f
        private const val Z_NEAR = 2f
        private const val Z_FAR = MODEL_BOUND_SIZE * 10
    }
}
