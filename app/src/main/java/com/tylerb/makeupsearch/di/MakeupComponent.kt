package com.tylerb.makeupsearch.di

import com.tylerb.makeupsearch.MainActivity
import dagger.Component

@Component(modules = [MakeupModule::class])
interface MakeupComponent {

    fun inject(mainActivity: MainActivity)

}