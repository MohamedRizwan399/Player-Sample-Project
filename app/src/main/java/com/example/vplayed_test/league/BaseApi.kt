package com.example.vplayed_test.league
import com.example.vplayed_test.data.Data
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface BaseApi {
    @GET("/api/v0/location")
    fun searchLeague(): Call<Data>

    companion object{

        fun getRetrofitInstance(): Retrofit {
            return Retrofit.Builder()
                .baseUrl("https://finalspaceapi.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

//        val instance: BaseApi by lazy {
//            val retrofit = Retrofit.Builder()
//                .baseUrl("https://api-devgs.vplayed.com")
//                .addConverterFactory(GsonConverterFactory.create()) // Using Gson to parse JSON response
//                .build()
//                retrofit.create(BaseApi::class.java)
//        }
    }
}