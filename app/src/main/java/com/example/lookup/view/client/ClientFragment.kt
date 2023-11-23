package com.example.lookup.view.client

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.lookup.R
import com.example.lookup.databinding.FragmentClientBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
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

    private fun initLayout() {
        binding?.userEmail?.text = auth.currentUser?.email.toString()
        Log.d("sign_out",auth.currentUser?.email.toString())
        Log.d("sign_out","check")
        binding?.googleSignout?.setOnClickListener {
            val user = FirebaseAuth.getInstance().currentUser
            user!!.delete()!!.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    revokeAccess()
                    Log.d("sing_out", "User account deleted.")
                    Toast.makeText(context,"회원 탈퇴 성공", Toast.LENGTH_SHORT).show()
                    this.activity?.finishAffinity()
                }
                else{
                    Toast.makeText(context,"다시 로그인 후 이용해주세요.", Toast.LENGTH_SHORT).show()
                    auth.signOut()
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initLayout()
    }
    private fun revokeAccess(){
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        // Build a GoogleSignInClient with the options specified by gso.
        val mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        mGoogleSignInClient.revokeAccess().addOnCompleteListener {
            Log.d("google_signout","회원탈퇴 성공")
        }
        auth.signOut()

    }
}

