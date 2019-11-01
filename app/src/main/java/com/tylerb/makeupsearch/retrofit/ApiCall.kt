package com.tylerb.makeupsearch.retrofit

import com.tylerb.makeupsearch.model.Makeup
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiCall {

    @GET("products.json")
    fun getMakeupBrand(@Query("brand") brand: String): Single<List<Makeup>>

}