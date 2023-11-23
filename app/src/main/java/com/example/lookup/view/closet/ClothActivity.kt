package com.example.lookup.view.closet

import android.content.ContentResolver
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentResolverCompat
import com.example.lookup.R
import com.example.lookup.databinding.ActivityClothBinding
import com.example.lookup.module.model.Model
import com.example.lookup.module.model.ModelSurfaceView
import com.example.lookup.module.model.ModelViewerApplication
import com.example.lookup.module.model.obj.ObjModel
import com.example.lookup.module.model.ply.PlyModel
import com.example.lookup.module.model.stl.StlModel
import com.example.lookup.module.model.util.Util
import com.example.lookup.util.PreferenceManager
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.util.Locale

class ClothActivity : AppCompatActivity() {
    private lateinit var binding: ActivityClothBinding
    private var modelView: ModelSurfaceView? = null
    private val disposables = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClothBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()
        createNewModelView(ModelViewerApplication.currentClothModel)

        val clothFilename = PreferenceManager.getString(this, CLOTH_FILENAME)
        Log.d(TAG, "filename: $clothFilename")

        if (ModelViewerApplication.currentClothModel != null) {
            Log.d(TAG, "title: ${ModelViewerApplication.currentClothModel!!.title}")
            title = ModelViewerApplication.currentClothModel!!.title
            if(ModelViewerApplication.currentClothModel!!.title == clothFilename){
                Log.d(TAG, "title: ${ModelViewerApplication.currentClothModel!!.title}")
                return
            }
        }

        if (!clothFilename.isNullOrBlank()) {
            loadModelByFilename(clothFilename!!)
        }
    }

    private fun loadModelByFilename(filename:String) {
        val initUri =
            Uri.parse(SERVER_URL+filename)
        beginLoadModel(initUri)
    }

    override fun onPause() {
        super.onPause()
        modelView?.onPause()
    }

    override fun onResume() {
        super.onResume()
        modelView?.onResume()
    }

    override fun onDestroy() {
        disposables.clear()
        super.onDestroy()
    }

    private fun createNewModelView(model: Model?) {
        if (modelView != null) {
            binding.bodyFragment.removeView(modelView)
        }
        modelView = ModelSurfaceView(this, model)
        binding.bodyFragment.addView(modelView, 0)
    }

    private fun beginLoadModel(uri: Uri) {
        binding.progressBar.visibility = View.VISIBLE

        disposables.add(
            Observable.fromCallable {
            var model: Model? = null
            var stream: InputStream? = null
            try {
                val cr = applicationContext.contentResolver
                val fileName = getFileName(cr, uri)
                stream = if ("http" == uri.scheme || "https" == uri.scheme) {
                    val client = OkHttpClient()
                    val request: Request = Request.Builder().url(uri.toString()).build()
                    val response = client.newCall(request).execute()

                    // TODO: figure out how to NOT need to read the whole file at once.
                    ByteArrayInputStream(response.body!!.bytes())
                } else {
                    cr.openInputStream(uri)
                }
                if (stream != null) {
                    if (!fileName.isNullOrEmpty()) {
                        model = when {
                            fileName.lowercase(Locale.ROOT).endsWith(".stl") -> {
                                StlModel(stream)
                            }
                            fileName.lowercase(Locale.ROOT).endsWith(".obj") -> {
                                ObjModel(stream)
                            }
                            fileName.lowercase(Locale.ROOT).endsWith(".ply") -> {
                                PlyModel(stream)
                            }
                            else -> {
                                // assume it's STL.
                                StlModel(stream)
                            }
                        }
                        model.title = fileName.split("/")[1].trim()
                    } else {
                        // assume it's STL.
                        // TODO: autodetect file type by reading contents?
                        model = StlModel(stream)
                    }
                }
                ModelViewerApplication.currentClothModel = model
                model!!
            } finally {
                Util.closeSilently(stream)
            }
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doAfterTerminate {
                binding.progressBar.visibility = View.GONE
            }
            .subscribe({
                setCurrentModel(it)
            }, {
                it.printStackTrace()
                Toast.makeText(applicationContext, getString(R.string.open_model_error, it.message), Toast.LENGTH_SHORT).show()
            }))
    }

    private fun getFileName(cr: ContentResolver, uri: Uri): String? {
        if ("content" == uri.scheme) {
            val projection = arrayOf(MediaStore.MediaColumns.DISPLAY_NAME)
            ContentResolverCompat.query(cr, uri, projection, null, null, null, null)?.use { metaCursor ->
                if (metaCursor.moveToFirst()) {
                    return metaCursor.getString(0)
                }
            }
        }
        return uri.lastPathSegment
    }

    private fun setCurrentModel(model: Model) {
        createNewModelView(model)
        Toast.makeText(applicationContext, R.string.open_model_success, Toast.LENGTH_SHORT).show()
        title = model.title
        binding.progressBar.visibility = View.GONE
    }


    companion object{
        const val TAG = "ClothActivity"
        const val SERVER_URL = "http://ec2-3-36-70-109.ap-northeast-2.compute.amazonaws.com:3000/get-obj/cloth%2F"
        const val CLOTH_FILENAME = "cloth_filename"
    }
}
