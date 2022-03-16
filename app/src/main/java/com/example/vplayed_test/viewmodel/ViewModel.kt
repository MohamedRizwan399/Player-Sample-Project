package com.example.vplayed_test.viewmodel

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.vplayed_test.data.Data
import com.example.vplayed_test.data.DataItem
import com.example.vplayed_test.api.BaseApi
import com.example.vplayed_test.postApiDataclass.AlbumData
import com.example.vplayed_test.postApiDataclass.Movies
import retrofit2.Call
import retrofit2.Response

class ViewModel: ViewModel() {

//    var repo : Repo? = null
    var data : MutableLiveData<MutableList<DataItem>> = MutableLiveData()
    var data1 : MutableLiveData<MutableList<com.example.vplayed_test.postApiDataclass.Data>> = MutableLiveData()


    fun getLiveDataObserver():MutableLiveData<MutableList<DataItem>>{
        return data
    }
    fun getLiveDataObserver1():MutableLiveData<MutableList<com.example.vplayed_test.postApiDataclass.Data>>{
        return data1
    }

    fun apiCall()
    {
        val retrofitInstance = BaseApi.getRetrofitInstance()
        val retroService = retrofitInstance.create(BaseApi::class.java)
        val call = retroService.searchData()
        call.enqueue(object : retrofit2.Callback<Data> {
            override fun onResponse(call: retrofit2.Call<Data>, response: Response<Data>) {
                data.postValue(response.body())
            }

            override fun onFailure(call: retrofit2.Call<Data>, t: Throwable) {
                data.postValue(null)
            }


        })
    }

    fun apiCall1()
    {
        val retrofitInstance = BaseApi.getRetrofitInstancePost()
        val retroService = retrofitInstance.create(BaseApi::class.java)
        val call = retroService.postData()
        call.enqueue(object : retrofit2.Callback<Movies> {
            override fun onResponse(call: Call<Movies>, response: Response<Movies>) {
                data1.postValue(response.body()?.response?.browse_data?.album_data?.data as MutableList<com.example.vplayed_test.postApiDataclass.Data>)
            }
            override fun onFailure(call: Call<Movies>, t: Throwable) {
                  data1.postValue(null)
            }


        })
    }
//     suspend fun search() {
//        val results = repo?.search()
//        if (results != null && results.isSuccessful) {
//            val league = results.body()?.response
//            data.postValue(league)
//        }
//    }
}



