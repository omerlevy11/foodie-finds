package com.foodie_finds

import android.app.Application
import android.content.Context

class FoodieFindsApp : Application() {

    object Globals {
        var appContext: Context? = null
    }

    override fun onCreate() {
        super.onCreate()
        Globals.appContext = applicationContext
    }

}