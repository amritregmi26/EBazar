package com.android.mbman.ebazar

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.android.mbman.ebazar.databinding.ActivityMainBinding
import com.android.mbman.ebazar.home.HomeFragment
import com.android.mbman.ebazar.productlist.ListProductFragment


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Fragment Initialization
        val accountFragment = AccountFragment()
        val listProductFragment = ListProductFragment()
        val homeFragment = HomeFragment()

        setCurrentFragment(homeFragment)

        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId)
            {
                R.id.miHome -> setCurrentFragment(homeFragment)
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
    override fun onBackPressed() {
        finishAffinity()
    }
}