package com.usj.petcare

import android.app.Application
import com.usj.petcare.data.AppContainer

/**
 * Created once per app process. It owns the [AppContainer], so every screen
 * shares the same database and repositories.
 */
class PetCareApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
