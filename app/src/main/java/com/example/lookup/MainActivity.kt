package com.example.lookup

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.lookup.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationBarView

class MainActivity : AppCompatActivity() , NavigationBarView.OnItemSelectedListener{
    lateinit var binding:ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initLayout()

    }

    private fun initLayout() {
        binding.navView.run{
            setOnItemSelectedListener {
                onNavigationItemSelected(it)
            }
            //초기 화면 홈으로 설정
            selectedItemId = R.id.nav_body
            val currentFragment = supportFragmentManager.findFragmentById(R.id.main_frm)
        }
    }
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_body -> { // 메인 화면
                /* fragment 로 구현 */
//                supportFragmentManager.beginTransaction().replace(R.id.main_frm, ).commit()
            }
            R.id.nav_clothes -> {
//                supportFragmentManager.beginTransaction().replace(R.id.main_frm, ).commit()
            }
            R.id.nav_fit -> {
//                supportFragmentManager.beginTransaction().replace(R.id.main_frm,).commit()
            }
            R.id.nav_client -> {
//                supportFragmentManager.beginTransaction().replace(R.id.main_frm, ).commit()
            }
        }
        return true
    }
}