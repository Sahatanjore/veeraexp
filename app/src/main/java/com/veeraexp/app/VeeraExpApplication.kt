package com.veeraexp.app

import android.app.Application
import com.veeraexp.app.data.db.AppDatabase

class VeeraExpApplication : Application() {

    // Single Room database instance for the whole app.
    val database: AppDatabase by lazy { AppDatabase.getInstance(this) }

    override fun onCreate() {
        super.onCreate()
    }
}
