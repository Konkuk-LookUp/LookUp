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
        data.add(MyCloth(R.drawable.cloth_example,"갈색 긴팔 셔츠","filename"))
        data.add(MyCloth(R.drawable.cloth_example,"회색 긴팔 셔츠","filename"))
        data.add(MyCloth(R.drawable.cloth_example,"베이지색 폴로 셔츠","filename"))
        data.add(MyCloth(R.drawable.cloth_example,"하늘색 하와이안 셔츠","filename"))
    }

    private fun initRecycler(){
        layoutManager = LinearLayoutManager(activity,LinearLayoutManager.VERTICAL,false)
        clothAdapter = MyClothAdapter(data)
        clothAdapter.itemClickListener=object:MyClothAdapter.OnItemClickListener{
            override fun OnItemClick(data: MyCloth, pos: Int) {
                val cloth = data.name //R.drawable 형식
                //TODO 옷 받아온걸로 인텐트에 값을 넘겨야하고 받아서 확인하는 작업 추가해야함
                PreferenceManager.setString(requireContext(),data.filename,"white_sneakers.obj")
                val intent = Intent(requireContext(), ClothActivity::class.java)
                startActivity(intent)
            }
        }
        binding.clothrecyclerView.layoutManager = layoutManager
        binding.clothrecyclerView.adapter = clothAdapter
    }

    companion object{
//        const val CLOTH_FILENAME = "cloth_filename"
    }
}
