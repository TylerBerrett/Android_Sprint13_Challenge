package com.tylerb.makeupsearch.util

import com.crashlytics.android.Crashlytics

fun breadCrumb(activity: String, about: String){
    val crumb = "$activity - $about"
    Crashlytics.log(crumb)
}