package com.example.vplayed_test.api
import com.example.vplayed_test.data.Data
import com.example.vplayed_test.postApiDataclass.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST

interface BaseApi {
    @GET("/api/v0/location")
    fun searchData(): retrofit2.Call<Data>

    @POST("/medias/api/v2/browse")
    fun postData():retrofit2.Call<Response>

    companion object{

        fun getRetrofitInstance(): Retrofit {
            return Retrofit.Builder()
                .baseUrl("https://finalspaceapi.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        fun getRetrofitInstancePost(): Retrofit {
            return Retrofit.Builder()
                .baseUrl("https://staging.masskomgroup.com/")
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