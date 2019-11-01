package com.tylerb.makeupsearch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.tylerb.makeupsearch.model.Makeup
import com.tylerb.makeupsearch.retrofit.ApiCall
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MainActivity : AppCompatActivity() {



    @Inject
    lateinit var daggerApi: ApiCall

    lateinit var disposable: Disposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        (application as App).makeupComponent.inject(this)

        disposable = daggerApi.getMakeupBrand("maybelline")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {makeup: List<Makeup> -> println(makeup[0])},
                {fail -> println(fail.message)}
            )


    }


    



    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }
}
