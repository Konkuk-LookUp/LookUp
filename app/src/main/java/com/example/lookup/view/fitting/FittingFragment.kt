package com.example.lookup.view.fitting

import android.content.ContentResolver
import android.content.ContextWrapper
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.content.ContentResolverCompat
import androidx.core.content.ContextCompat.getColor
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import com.example.lookup.R
import com.example.lookup.databinding.FragmentFittingBinding
import com.example.lookup.module.model.Model
import com.example.lookup.module.model.ModelSurfaceView
import com.example.lookup.module.model.ModelViewerApplication
import com.example.lookup.module.model.obj.ObjModel
import com.example.lookup.module.model.ply.PlyModel
import com.example.lookup.module.model.stl.StlModel
import com.example.lookup.module.model.util.Util
import com.example.lookup.util.ModelParser
import com.example.lookup.util.PreferenceManager
import com.example.lookup.view.body.BodyFragment
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.util.Locale

class FittingFragment : Fragment() {
    lateinit var binding: FragmentFittingBinding
    private var modelView: ModelSurfaceView? = null
    private val disposables = CompositeDisposable()
    private lateinit var contextWrapper:ContextWrapper
    private lateinit var contextThemeWrapper: ContextThemeWrapper

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

        binding.progressBar.visibility = View.GONE
        binding.notFoundModelView.visibility = View.GONE
        initLayout()

        ViewCompat.setOnApplyWindowInsetsListener(binding.fittingFragment) { _, insets ->
            (binding.progressBar.layoutParams as FrameLayout.LayoutParams).apply {
                topMargin = insets.systemWindowInsetTop
                bottomMargin = insets.systemWindowInsetBottom
                leftMargin = insets.systemWindowInsetLeft
                rightMargin = insets.systemWindowInsetRight
            }
            insets.consumeSystemWindowInsets()
        }
    }

    private fun initLayout() {
        ArrayAdapter.createFromResource(requireContext(),
            R.array.cloth_type,
            R.layout.dropdown_item
//            android.R.layout.simple_spinner_dropdown_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        binding.apply{
            //TODO 옷 클릭시 기능 구현
            cloth1.setOnClickListener {
                changeClothBorderLine("cloth1")
                setFittingModel("hood")
            }
            cloth2.setOnClickListener {
                changeClothBorderLine("cloth2")
                setFittingModel(TSHIRT)
            }
            cloth3.setOnClickListener {
                changeClothBorderLine("cloth3")
                setFittingModel("longpants")
            }
            cloth4.setOnClickListener {
                changeClothBorderLine("cloth4")
                setFittingModel("shortpants")
            }
            cloth5.setOnClickListener {
                changeClothBorderLine("cloth5")
                setFittingModel(SHOES)
            }
        }
    }

    private fun changeClothBorderLine(selected:String) {
        val borderWidth = 8

        binding.cloth1.borderColor = getColor(requireContext(),R.color.cloth_background)
        binding.cloth1.borderWidth = 0
        binding.cloth2.borderColor = getColor(requireContext(),R.color.cloth_background)
        binding.cloth2.borderWidth = 0
        binding.cloth3.borderColor = getColor(requireContext(),R.color.cloth_background)
        binding.cloth3.borderWidth = 0
        binding.cloth4.borderColor = getColor(requireContext(),R.color.cloth_background)
        binding.cloth4.borderWidth = 0
        binding.cloth5.borderColor = getColor(requireContext(),R.color.cloth_background)
        binding.cloth5.borderWidth = 0
        if(selected == "cloth1"){
            binding.cloth1.borderColor = getColor(requireContext(),R.color.cloth_border)
            binding.cloth1.borderWidth = borderWidth
            return
        }
        if(selected == "cloth2"){
            binding.cloth2.borderColor = getColor(requireContext(),R.color.cloth_border)
            binding.cloth2.borderWidth = borderWidth
            return
        }
        if(selected == "cloth3"){
            binding.cloth3.borderColor = getColor(requireContext(),R.color.cloth_border)
            binding.cloth3.borderWidth = borderWidth
            return
        }
        if(selected == "cloth4"){
            binding.cloth4.borderColor = getColor(requireContext(),R.color.cloth_border)
            binding.cloth4.borderWidth = borderWidth
            return
        }
        if(selected == "cloth5"){
            binding.cloth5.borderColor = getColor(requireContext(),R.color.cloth_border)
            binding.cloth5.borderWidth = borderWidth
            return
        }
    }

    private fun setFittingModel(clothName: String) {
        val userModelName = PreferenceManager.getString(requireContext(), USER_MODEL_FILENAME)
        val fittingModelFilename = ModelParser.getFittingModelFilename(userModelName!!, clothName)

        if(ModelViewerApplication.currentModel == null){
            binding.notFoundModelView.visibility = View.VISIBLE
            Toast.makeText(requireContext(), "모델을 먼저 추가해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        if (!fittingModelFilename.isNullOrBlank()) {
            PreferenceManager.setString(requireContext(),FITTING_MODEL_FILENAME,fittingModelFilename)
            renderFittingModel()
            return
        }
        Toast.makeText(requireContext(), "존재하지 않는 옷입니다.", Toast.LENGTH_SHORT).show()
    }

    override fun onStart() {
        super.onStart()
        renderModel()
        if(ModelViewerApplication.currentModel == null){
            binding.notFoundModelView.visibility = View.VISIBLE
            return
        }
    }

    private fun renderFittingModel() {
        createNewModelView(ModelViewerApplication.currentFittingModel)

        val filenameFitting = PreferenceManager.getString(requireContext(), FITTING_MODEL_FILENAME)
        Log.d(TAG, "filename_fitting: $filenameFitting")

        if (ModelViewerApplication.currentFittingModel != null) {
            Log.d(TAG, "title: ${ModelViewerApplication.currentFittingModel!!.title}")
            activity?.title = ModelViewerApplication.currentFittingModel!!.title
            if (ModelViewerApplication.currentFittingModel!!.title == filenameFitting) {
                return
            }
        }

        if (!filenameFitting.isNullOrBlank()) {
            loadModelByFilename(filenameFitting!!,1)
        }
    }

    private fun renderModel() {
        createNewModelView(ModelViewerApplication.currentModel)

        val filename = PreferenceManager.getString(requireContext(), "filename")
        Log.d(BodyFragment.TAG, "filename: $filename")

        if (ModelViewerApplication.currentModel != null) {
            Log.d(BodyFragment.TAG, "title: ${ModelViewerApplication.currentModel!!.title}")
            activity?.title = ModelViewerApplication.currentModel!!.title
        }

        if (!filename.isNullOrBlank()) {
            loadModelByFilename(filename!!,0)
        }
    }

    private fun loadModelByFilename(filename:String, targetModel:Int) {
        var initUri:Uri = Uri.parse(SERVER_URL +filename)
        if(targetModel == 0){
            initUri = Uri.parse(SERVER_URL_MODEL +filename)
        }else if(targetModel == 1){
            initUri = Uri.parse(SERVER_URL +filename)
        }
        beginLoadModel(initUri,targetModel)
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
            binding.notFoundModelView.visibility = View.VISIBLE
            binding.fittingFragment.removeView(modelView)
        }
        binding.notFoundModelView.visibility = View.GONE
        modelView = ModelSurfaceView(requireContext(), model)
        binding.fittingFragment.addView(modelView, 0)
    }

    private fun beginLoadModel(uri: Uri,targetModel: Int) {
        binding.progressBar.visibility = View.VISIBLE
        binding.notFoundModelView.visibility = View.GONE
        binding.fittingFragment.removeView(modelView)

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
                                    Log.d(TAG, "beginLoadModel() called + $fileName")
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
                    if(targetModel == 0){
                        ModelViewerApplication.currentModel = model
                    }
                    else if(targetModel == 1){
                        ModelViewerApplication.currentFittingModel = model
                    }
                    model!!
                } finally {
                    Util.closeSilently(stream)
                }
            }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterTerminate {
                    binding.progressBar.visibility = View.GONE
                    binding.notFoundModelView.visibility = View.GONE
                }
                .subscribe({
                    setCurrentModel(it)
                }, {
                    it.printStackTrace()
                    if(ModelViewerApplication.currentModel == null){
                        binding.notFoundModelView.visibility = View.VISIBLE
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
//        Toast.makeText(contextWrapper.applicationContext, R.string.open_model_success, Toast.LENGTH_SHORT).show()
        activity?.title = model.title
        binding.progressBar.visibility = View.GONE
    }

    companion object{
        const val TAG = "FittingFragment"
        const val SERVER_URL = "http://ec2-3-36-70-109.ap-northeast-2.compute.amazonaws.com:3000/get-obj/fitting%2F"
        const val SERVER_URL_MODEL = "http://ec2-3-36-70-109.ap-northeast-2.compute.amazonaws.com:3000/get-obj/default%2F"
        const val USER_MODEL_FILENAME = "filename"
        const val FITTING_MODEL_FILENAME = "fitting_filename"
        const val SHOES = "shoes"
        const val TSHIRT = "tshirt"
    }
}
