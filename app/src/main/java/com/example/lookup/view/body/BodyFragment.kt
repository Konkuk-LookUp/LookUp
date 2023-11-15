package com.example.lookup.view.body

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.lookup.R
import com.example.lookup.databinding.FragmentBodyBinding


class BodyFragment : Fragment() {
    lateinit var binding: FragmentBodyBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentBodyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initBtn()
    }

    private fun initBtn() {
        binding.startCameraBtn.setOnClickListener {
            startCamera()
        }
    }

    private fun startCamera(){
        val intent = Intent(activity,BodyCameraActivity::class.java)
        startActivity(intent)
    }
}