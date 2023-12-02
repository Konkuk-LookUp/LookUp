package com.example.lookup.view.body

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.lookup.MainActivity
import com.example.lookup.R
import com.example.lookup.data.SizeInfo
import com.example.lookup.data.UserModel
import com.example.lookup.databinding.ActivityBodyCameraBinding
import com.example.lookup.module.pose.data.BodyPart
import com.example.lookup.module.pose.data.Device
import com.example.lookup.module.pose.data.KeyPoint
import com.example.lookup.module.pose.data.Person
import com.example.lookup.module.pose.image.ImageSource
import com.example.lookup.module.pose.ml.ModelType
import com.example.lookup.module.pose.ml.MoveNet
import com.example.lookup.module.pose.ml.MoveNetMultiPose
import com.example.lookup.module.pose.ml.PoseNet
import com.example.lookup.module.pose.ml.Type
import com.example.lookup.util.ModelParser
import com.example.lookup.util.PreferenceManager
import com.example.lookup.util.SizeCalculator
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class BodyCameraActivity : AppCompatActivity() {
    lateinit var binding: ActivityBodyCameraBinding
    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService
//    private lateinit var sound:MediaActionSound

    /** Default pose estimation model is 1 (MoveNet Thunder)
     * 0 == MoveNet Lightning model
     * 1 == MoveNet Thunder model
     * 2 == MoveNet MultiPose model
     * 3 == PoseNet model
     **/
    private var modelPos = 1

    /** Default device is CPU */
    private var device = Device.CPU

    private var imageSource: ImageSource? = null
    private var keyPoints: List<KeyPoint>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBodyCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }

        // Set up the listeners for take photo and video capture buttons
        binding.imageCaptureButton.setOnClickListener { takePhoto() }

        cameraExecutor = Executors.newSingleThreadExecutor()

        initImageSource()
    }

    private fun initImageSource() {
        imageSource =
            ImageSource(this, object : ImageSource.ImageSourceListener {

                override fun onDetectedInfo(person: Person) {
                    keyPoints = person.keyPoints
                    Log.d(TAG, "score: " + person.score)
                    for (key in keyPoints!!) {
                        Log.d(
                            TAG,
                            "bodyPart : " + key.bodyPart.name + ", locate : " + key.coordinate.toString()
                        )
                    }
                    setUserModel()
                }
            })
    }

    private fun setUserModel() {
        val height = intent.getFloatExtra("height", 180f)
        val weight = intent.getFloatExtra("weight", 70f)

        var userModel = UserModel(height, weight, keyPoints!!)
        val fileName = ModelParser.getFilename(userModel)
        Log.d(TAG, "filename : $fileName")
        PreferenceManager.setString(this,"filename",fileName)
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return
//        sound = MediaActionSound();
//        sound.play(SHUTTER_CLICK)

        // Create time stamped name and MediaStore entry.
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
            }
        }

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(
                contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
            .build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    val errorMsg = "사진 저장 실패: ${exc.message}"
                    Toast.makeText(baseContext, errorMsg, Toast.LENGTH_SHORT).show()
                    Log.e(TAG, errorMsg, exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = output.savedUri

                    if (savedUri == null) {
                        Log.e(TAG, "사진 저장 위치를 찾을 수 없습니다.")
                        return
                    }

                    val msg = "사진 저장 성공"
                    Log.d(TAG, msg)
                    createPoseEstimator()
                    imageSource?.processImageFromGallery(savedUri)
                }
            }
        )
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .build()

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
//        sound.release()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(
                    this,
                    "카메라 기능을 위해선 권한이 필요합니다.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun createPoseEstimator() {
        // For MoveNet MultiPose, hide score and disable pose classifier as the model returns
        // multiple Person instances.
        val poseDetector = when (modelPos) {
            0 -> {
                // MoveNet Lightning (SinglePose)
                MoveNet.create(this, device, ModelType.Lightning)
            }

            1 -> {
                // MoveNet Thunder (SinglePose)
                MoveNet.create(this, device, ModelType.Thunder)
            }

            2 -> {
                // MoveNet (Lightning) MultiPose
                // Movenet MultiPose Dynamic does not support GPUDelegate
                if (device == Device.GPU) {
                    showToast(getString(R.string.tfe_pe_gpu_error))
                }
                MoveNetMultiPose.create(
                    this,
                    device,
                    Type.Dynamic
                )
            }

            3 -> {
                // PoseNet (SinglePose)
                PoseNet.create(this, device)
            }

            else -> {
                null
            }
        }
        poseDetector?.let { detector ->
            imageSource?.setDetector(detector)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    companion object {
        private const val TAG = "BodyCamera"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf(
                Manifest.permission.CAMERA
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }
}
