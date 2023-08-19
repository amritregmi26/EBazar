package com.android.mbman.ebazar

import android.app.Application
import com.google.firebase.FirebaseApp

class EbazarApp: Application() {

    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)
    }
}