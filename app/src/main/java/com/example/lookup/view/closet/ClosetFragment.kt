package com.example.lookup.view.closet

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lookup.R
import com.example.lookup.databinding.FragmentClosetBinding
import com.example.lookup.util.PreferenceManager

class ClosetFragment : Fragment() {
    lateinit var binding: FragmentClosetBinding
    lateinit var clothAdapter: MyClothAdapter
    val data:ArrayList<MyCloth> = ArrayList()
    lateinit var layoutManager: LinearLayoutManager
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentClosetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initData()
        initRecycler()
    }

    private fun initData() {
        data.add(MyCloth(R.drawable.cloth_example,"후드티","hood.obj"))
        data.add(MyCloth(R.drawable.cloth_example,"티셔츠","tshirt.obj"))
        data.add(MyCloth(R.drawable.cloth_example,"긴바지","longpants.obj"))
        data.add(MyCloth(R.drawable.cloth_example,"반바지","shortpants.obj"))
        data.add(MyCloth(R.drawable.cloth_example,"운동화","white_sneakers.obj"))
    }

    private fun initRecycler(){
        layoutManager = LinearLayoutManager(activity,LinearLayoutManager.VERTICAL,false)
        clothAdapter = MyClothAdapter(data)
        clothAdapter.itemClickListener=object:MyClothAdapter.OnItemClickListener{
            override fun OnItemClick(data: MyCloth, pos: Int) {
                PreferenceManager.setString(requireContext(), CLOTH_FILENAME,data.filename)
                val intent = Intent(requireContext(), ClothActivity::class.java)
                startActivity(intent)
            }
        }
        binding.clothrecyclerView.layoutManager = layoutManager
        binding.clothrecyclerView.adapter = clothAdapter
    }

    companion object{
        const val CLOTH_FILENAME = "cloth_filename"
    }
}
