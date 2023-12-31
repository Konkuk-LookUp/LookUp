package com.example.lookup.view.body

import android.content.ContentResolver
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.content.ContentResolverCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import com.example.lookup.R
import com.example.lookup.databinding.FragmentBodyBinding
import com.example.lookup.module.model.Model
import com.example.lookup.module.model.ModelSurfaceView
import com.example.lookup.module.model.ModelViewerApplication
import com.example.lookup.module.model.obj.ObjModel
import com.example.lookup.module.model.ply.PlyModel
import com.example.lookup.module.model.stl.StlModel
import com.example.lookup.module.model.util.Util
import com.example.lookup.util.PreferenceManager
import com.example.lookup.view.body.input.AddBodyActivity
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.util.Locale


class BodyFragment : Fragment() {
    private lateinit var binding: FragmentBodyBinding
    private var modelView: ModelSurfaceView? = null
    private val disposables = CompositeDisposable()
    private lateinit var contextWrapper:ContextWrapper
    private lateinit var contextThemeWrapper: ContextThemeWrapper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentBodyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        contextWrapper = ContextWrapper(context)
        contextThemeWrapper = ContextThemeWrapper(context,R.style.Theme_LookUp)

        initBtn()

        binding.progressBar.visibility = View.GONE

        ViewCompat.setOnApplyWindowInsetsListener(binding.bodyFragment) { _, insets ->
            (binding.progressBar.layoutParams as FrameLayout.LayoutParams).apply {
                topMargin = insets.systemWindowInsetTop
                bottomMargin = insets.systemWindowInsetBottom
                leftMargin = insets.systemWindowInsetLeft
                rightMargin = insets.systemWindowInsetRight
            }
            insets.consumeSystemWindowInsets()
        }
    }

    private fun loadModelByFilename(filename:String) {
        val initUri =
            Uri.parse(SERVER_URL+filename)
        Log.d(TAG, "loadModelByFilename() called with: initUri = $initUri")
        beginLoadModel(initUri)
    }

    private fun initBtn() {
        binding.startCameraBtn.setOnClickListener {
            addModel()
        }
    }
    private fun addModel(){
        val intent = Intent(activity, AddBodyActivity::class.java)
        startActivity(intent)
    }

    override fun onStart() {
        super.onStart()
        renderModel()
        if(ModelViewerApplication.currentModel == null){
            visibilityOfNotFoundModel(View.VISIBLE)
            return
        }
    }

    private fun visibilityOfNotFoundModel(visible:Int) {
        binding.notFoundModelImg.visibility = visible
        binding.notFoundModelText.visibility = visible
    }

    private fun renderModel() {
        createNewModelView(ModelViewerApplication.currentModel)

        val filename = PreferenceManager.getString(requireContext(), "filename")
        Log.d(TAG, "filename: $filename")

        if (ModelViewerApplication.currentModel != null) {
            Log.d(TAG, "title: ${ModelViewerApplication.currentModel!!.title}")
            activity?.title = ModelViewerApplication.currentModel!!.title
            if (ModelViewerApplication.currentModel!!.title == filename) {
                return
            }
        }

        if (!filename.isNullOrBlank()) {
            loadModelByFilename(filename!!)
        }
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
            visibilityOfNotFoundModel(View.VISIBLE)
            binding.bodyFragment.removeView(modelView)
        }
        visibilityOfNotFoundModel(View.GONE)
        modelView = ModelSurfaceView(requireContext(), model)
        binding.bodyFragment.addView(modelView, 0)
    }

    private fun beginLoadModel(uri: Uri) {
        binding.progressBar.visibility = View.VISIBLE
        binding.bodyFragment.removeView(modelView)
        binding.notFoundModelText.visibility = View.GONE
        binding.notFoundModelImg.visibility = View.GONE

        disposables.add(Observable.fromCallable {
            var model: Model? = null
            var stream: InputStream? = null
            try {
                val cr = contextWrapper.applicationContext.contentResolver
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
                ModelViewerApplication.currentModel = model
                model!!
            } finally {
                Util.closeSilently(stream)
            }
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doAfterTerminate {
                binding.progressBar.visibility = View.GONE
                binding.notFoundModelText.visibility = View.GONE
                binding.notFoundModelImg.visibility = View.GONE
            }
            .subscribe({
                setCurrentModel(it)
            }, {
                it.printStackTrace()
                if(ModelViewerApplication.currentModel == null){
                    binding.notFoundModelText.visibility = View.VISIBLE
                    binding.notFoundModelImg.visibility = View.VISIBLE
                }
                Toast.makeText(contextWrapper.applicationContext, getString(R.string.open_model_error, it.message), Toast.LENGTH_SHORT).show()
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
        activity?.title = model.title
        binding.progressBar.visibility = View.GONE
    }

    companion object{
        const val TAG = "BodyFragment"
        const val SERVER_URL = "http://ec2-3-36-70-109.ap-northeast-2.compute.amazonaws.com:3000/get-obj/default%2F"
    }
}
