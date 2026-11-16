package com.usj.petcare

import android.app.Application
import com.usj.petcare.data.AppContainer

class PetCareApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
