package com.example.lookup.module.pose.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.util.Log
import com.example.lookup.module.pose.data.Person
import com.example.lookup.module.pose.ml.MoveNetMultiPose
import com.example.lookup.module.pose.ml.PoseDetector
import com.example.lookup.module.pose.ml.TrackerType

class ImageSource(
    private val context: Context,
    private val listener: ImageSourceListener? = null
) {

    companion object {
        private const val PREVIEW_WIDTH = 640
        private const val PREVIEW_HEIGHT = 480

        /** Threshold for confidence score. */
        private const val MIN_CONFIDENCE = .2f
        private const val TAG = "ImageSource"
    }

    private val lock = Any()
    private var detector: PoseDetector? = null
    private var isTrackerEnabled = false

    fun processImageFromGallery(imageUri: Uri) {
        val imageBitmap = loadBitmapFromGallery(imageUri) ?: return

        // 이미지 회전 (예시: 90도 회전)
        val rotatedBitmap = rotateBitmap(imageBitmap, 90f)

        // 여기서 'processImage' 함수로 이미지 처리
        processImage(rotatedBitmap)
    }

    private fun loadBitmapFromGallery(imageUri: Uri): Bitmap? {
        Log.d(TAG, "loadBitmapFromGallery() called with: imageUri = $imageUri")
        return context.contentResolver.openInputStream(imageUri)?.use { inputStream ->
            BitmapFactory.decodeStream(inputStream)
        }
    }

    private fun rotateBitmap(source: Bitmap, angle: Float): Bitmap {
        Log.d(TAG, "rotateBitmap() called with: source = $source, angle = $angle")
        val matrix = Matrix().apply { postRotate(angle) }
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

    fun setDetector(detector: PoseDetector) {
        synchronized(lock) {
            if (this.detector != null) {
                this.detector?.close()
                this.detector = null
            }
            this.detector = detector
        }
    }

    /**
     * Set Tracker for Movenet MuiltiPose model.
     */
    fun setTracker(trackerType: TrackerType) {
        isTrackerEnabled = trackerType != TrackerType.OFF
        (this.detector as? MoveNetMultiPose)?.setTracker(trackerType)
    }

    fun close() {
        detector?.close()
        detector = null
    }

    // process image
    private fun processImage(bitmap: Bitmap) {
        Log.d(TAG, "processImage() called with: bitmap = $bitmap")
        val persons = mutableListOf<Person>()

        detector?.estimatePoses(bitmap)?.let {
            persons.addAll(it)
        }

        // if the model returns only one item, show that item's score.
        if (persons.isNotEmpty()) {
            listener?.onDetectedInfo(persons[0])
        }
    }

    interface ImageSourceListener{
        fun onDetectedInfo(person: Person)
    }
}
