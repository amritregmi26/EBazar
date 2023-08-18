package com.android.mbman.ebazar

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColor
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import com.android.mbman.ebazar.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.flFragment


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Fragment Initialization
        val accountFragment = AccountFragment()
        val helpFragment = HelpFragment()
        val listProductFragment = ListProductFragment()
        val homeFragment = HomeFragment()

        setCurrentFragment(homeFragment)

        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId)
            {
                R.id.miHome -> setCurrentFragment(homeFragment)
                R.id.miHelp-> setCurrentFragment(helpFragment)
                R.id.miAcc -> setCurrentFragment(accountFragment)
                R.id.miList -> setCurrentFragment(listProductFragment)
            }
            true
        }
    }

    private fun setCurrentFragment(fragment: Fragment)
    {
        supportFragmentManager.beginTransaction().apply {
            replace(binding.flFragment.id, fragment)
            commit()
        }
    }
}