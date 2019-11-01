package com.tylerb.makeupsearch

import android.app.Application
import com.tylerb.makeupsearch.di.DaggerMakeupComponent
import com.tylerb.makeupsearch.di.MakeupComponent

class App: Application() {

    lateinit var makeupComponent: MakeupComponent

    override fun onCreate() {
        super.onCreate()
        makeupComponent = DaggerMakeupComponent.create()
    }
}