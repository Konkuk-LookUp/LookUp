package com.example.lookup.view.fitting

import android.Manifest
import android.content.ContentResolver
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentResolverCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import com.example.lookup.R
import com.example.lookup.databinding.FragmentFittingBinding
import com.example.lookup.httpConnection.HttpFunc
import com.example.lookup.module.model.Model
import com.example.lookup.module.model.ModelSurfaceView
import com.example.lookup.module.model.ModelViewerApplication
import com.example.lookup.module.model.obj.ObjModel
import com.example.lookup.module.model.ply.PlyModel
import com.example.lookup.module.model.stl.StlModel
import com.example.lookup.module.model.util.Util
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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

class FittingFragment : Fragment() {
    lateinit var binding: FragmentFittingBinding
    private lateinit var sampleModels: List<String>
    private var sampleModelIndex = 0
    private var modelView: ModelSurfaceView? = null
    private val disposables = CompositeDisposable()
    private lateinit var contextWrapper:ContextWrapper
    private lateinit var contextThemeWrapper: ContextThemeWrapper
    private lateinit var httpManager:HttpFunc
    private val serverUrl = " http://ec2-3-36-70-109.ap-northeast-2.compute.amazonaws.com:3000/get-obj/obj파일이름"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFittingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        contextWrapper = ContextWrapper(context)
        contextThemeWrapper = ContextThemeWrapper(context, R.style.Theme_LookUp)
        httpManager = HttpFunc(requireContext())

        binding.progressBar.visibility = View.GONE
        initLayout()

        ViewCompat.setOnApplyWindowInsetsListener(binding.bodyFragment) { _, insets ->
            (binding.progressBar.layoutParams as FrameLayout.LayoutParams).apply {
                topMargin = insets.systemWindowInsetTop
                bottomMargin = insets.systemWindowInsetBottom
                leftMargin = insets.systemWindowInsetLeft
                rightMargin = insets.systemWindowInsetRight
            }
            insets.consumeSystemWindowInsets()
        }

        sampleModels = contextThemeWrapper.assets.list("")!!.filter { it.endsWith(".stl") }
    }

    private fun initLayout() {
        ArrayAdapter.createFromResource(requireContext(),
            R.array.cloth_type,
            R.layout.dropdown_item
//            android.R.layout.simple_spinner_dropdown_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinner.adapter = adapter
        }
        binding.apply{
            //TODO 옷 클릭시 기능 구현
            cloth1.setOnClickListener {
                serverUrl // url 수정해서 접근?
//                httpManager.GET()
            }
            cloth2.setOnClickListener {

            }
            cloth3.setOnClickListener {

            }
            cloth4.setOnClickListener {

            }
        }
    }

    override fun onStart() {
        super.onStart()
        createNewModelView(ModelViewerApplication.currentModel)
        if (ModelViewerApplication.currentModel != null) {
            activity?.title = ModelViewerApplication.currentModel!!.title
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
            binding.bodyFragment.removeView(modelView)
        }
        modelView = ModelSurfaceView(requireContext(), model)
        binding.bodyFragment.addView(modelView, 0)
    }

    private fun beginLoadModel(uri: Uri) {
        binding.progressBar.visibility = View.VISIBLE

        disposables.add(
            Observable.fromCallable {
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
                        model.title = fileName
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
            }
            .subscribe({
                setCurrentModel(it)
            }, {
                it.printStackTrace()
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
        Toast.makeText(contextWrapper.applicationContext, R.string.open_model_success, Toast.LENGTH_SHORT).show()
        activity?.title = model.title
        binding.progressBar.visibility = View.GONE
    }

    private fun loadSampleModel() {
        try {
            val stream = contextThemeWrapper.assets.open(sampleModels[sampleModelIndex++ % sampleModels.size])
            setCurrentModel(StlModel(stream))
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun showAboutDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.app_name)
            .setMessage(R.string.about_text)
            .setPositiveButton(android.R.string.ok, null)
            .show()
    }


}
