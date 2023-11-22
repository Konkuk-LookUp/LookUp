package com.example.lookup

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.lookup.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider


class LoginActivity : AppCompatActivity() {
    lateinit var binding:ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    lateinit var gso : GoogleSignInOptions
    lateinit var mGoogleSignInClient : GoogleSignInClient
    val RC_SIGN_IN = 9001
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        googleSetUp()
        initLayout()


    }

    override fun onStart() {
        super.onStart()
//        val account = GoogleSignIn.getLastSignedInAccount(this)
        val account = FirebaseAuth.getInstance().currentUser
//        updateUI(account) ->  ret val null 아니면 메인으로 넘어가야함
        if(account != null){
            val intent = Intent(this@LoginActivity,MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initLayout() {
        binding.googleLogin.setOnClickListener {
            signIn()
        }
    }
    private fun googleSetUp(){
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun signIn() {
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // GoogleSignInClient.getSignInIntent(...)에서 시작된 인텐트의 결과
        if (requestCode == RC_SIGN_IN) {
            // 이 호출에서 반환된 Task는 항상 완료되므로 리스너를 연결할 필요가 없습니다.
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }
    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            // 성공적으로 로그인되었으므로 인증된 UI를 보여줍니다.
//            updateUI(account) // 화면전환
            if(account != null){
                firebaseAuthWithGoogle(account.idToken!!)
                /* 유저 정보 저장 가능 */
//                val personName: String = account.getDisplayName()!!
//                val personGivenName: String = account.getGivenName()!!
//                val personFamilyName: String = account.getFamilyName()!!
//                val personEmail: String = account.getEmail()!!
//                val personId: String = account.getId()!!
//                val personPhoto: Uri = account.getPhotoUrl()!!
            }else{
//                val initIntent = Intent(this@LoginActivity,null)
//                startActivity(initIntent)
                Toast.makeText(this@LoginActivity,"로그인 실패",Toast.LENGTH_SHORT).show()
            }
        } catch (e: ApiException) {
            // ApiException 상태 코드는 실패의 상세한 이유를 나타냅니다.
            // 자세한 정보는 GoogleSignInStatusCodes 클래스 참조를 확인하세요.
            Log.w("google_login", "signInResult:failed code=" + e.statusCode)
//            updateUI(null)
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("google_login", "signInWithCredential:success")
                    val user: FirebaseUser = auth.currentUser!!
                    val intent = Intent(this@LoginActivity,MainActivity::class.java)
                    startActivity(intent)

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("google_login", "signInWithCredential:failure", task.exception)
                    Toast.makeText(this@LoginActivity, "로그인 실패", Toast.LENGTH_SHORT).show()
                }
            }
    }
    private fun signOut() {
        mGoogleSignInClient.signOut()
            .addOnCompleteListener(this) {
                // [START_EXCLUDE]
//               /* 로그아웃 관련 코드 작성 */

                // [END_EXCLUDE]
            }
    }
}