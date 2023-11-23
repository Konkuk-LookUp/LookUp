package com.example.lookup.view.closet

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.lookup.R
import com.example.lookup.databinding.FragmentBodyBinding
import com.example.lookup.databinding.FragmentClosetBinding
import com.example.lookup.util.PreferenceManager

class ClosetFragment : Fragment() {
    lateinit var binding: FragmentClosetBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentClosetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initBtn()
    }

    private fun initBtn() {
        binding.tempBtn.setOnClickListener {
            PreferenceManager.setString(requireContext(),CLOTH_FILENAME,"white_sneakers.obj")

            val intent = Intent(requireContext(), ClothActivity::class.java)
            startActivity(intent)
        }
    }

    companion object{
        const val CLOTH_FILENAME = "cloth_filename"
    }
}
