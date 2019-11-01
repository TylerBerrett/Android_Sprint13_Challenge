package com.tylerb.makeupsearch.di

import com.tylerb.makeupsearch.retrofit.ApiCall
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

@Module
object MakeupModule {

    private const val BASE_URL = "https://makeup-api.herokuapp.com/api/v1/"

    @Provides
    fun retrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }

    @Provides
    fun getPokemon(retrofit: Retrofit) = retrofit.create(ApiCall::class.java)

}