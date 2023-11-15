package com.example.lookup.navFragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.lookup.databinding.FragmentClientBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ClientFragment : Fragment() {
    var binding: FragmentClientBinding ?= null
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentClientBinding.inflate(inflater,container,false)
        auth = Firebase.auth

        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.userEmail?.text = auth.currentUser?.email
        binding?.googleSignout?.setOnClickListener {
            val user = FirebaseAuth.getInstance().currentUser
            user!!.delete()!!.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("sing_out", "User account deleted.")
                    Toast.makeText(context,"회원 탈퇴 성공", Toast.LENGTH_SHORT).show()
                    this.activity?.finishAffinity()
                }
//                else{
//                    Toast.makeText(context,"다시 로그인 후 이용해주세요.", Toast.LENGTH_SHORT).show()
//                    auth.signOut()
//                }
            }

        }
    //TODO layout 초기화

    }

}