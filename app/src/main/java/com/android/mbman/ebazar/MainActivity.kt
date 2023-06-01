package com.android.mbman.ebazar

import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsControllerCompat


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = (WindowManager.LayoutParams.FLAG_FULLSCREEN or WindowInsetsControllerCompat.BEHAVIOR_SHOW_BARS_BY_SWIPE)
        setContentView(R.layout.login)

    }
}