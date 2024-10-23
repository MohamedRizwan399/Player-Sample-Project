package com.example.player_sample_project.api

import com.example.player_sample_project.data_mvvm.Data
import com.example.player_sample_project.seeallpage.modelclassPoJo.NewResponse
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class BaseApi {
    companion object {
        const val API_KEY: String = "6004b8fcb1604003b4ead57854e8d2c2"

        fun getRetrofitHome(): Retrofit {
            return Retrofit.Builder()
                .baseUrl("https://finalspaceapi.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        fun getRetrofitSeeAll(): Retrofit {
            return Retrofit.Builder()
                .baseUrl("https://newsapi.org/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
    }

    interface BaseApiInterface {
        @GET("/api/v0/location")
        fun searchData(): retrofit2.Call<Data>

        @GET("top-headlines?country=us")
        fun getApiSeeAllResults(@Query("apiKey") apiKey: String?): Call<NewResponse>

        @GET("/api/v0/character")
        fun getDataListFree(): Call<List<Data>>
    }

}