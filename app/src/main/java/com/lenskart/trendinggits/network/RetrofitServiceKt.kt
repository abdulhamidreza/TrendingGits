package com.lenskart.trendinggits.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET


interface RetrofitServiceKt {

    @GET("/trending")
    suspend fun getUserListData(): Response<String>

    companion object {
        var retrofitServiceKt: RetrofitServiceKt? = null
        fun getInstance(): RetrofitServiceKt {
            val okHttpClient = OkHttpClient().newBuilder()
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
                .build()
            if (retrofitServiceKt == null) {
                val retrofit = Retrofit.Builder()
                    .client(okHttpClient)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .baseUrl("https://github.com/")
                    .build()
                retrofitServiceKt = retrofit.create(RetrofitServiceKt::class.java)
            }
            return retrofitServiceKt!!
        }

    }
}