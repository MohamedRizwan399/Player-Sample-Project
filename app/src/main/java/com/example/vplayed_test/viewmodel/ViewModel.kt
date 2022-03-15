package com.example.vplayed_test.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.vplayed_test.data.Data
import com.example.vplayed_test.data.DataItem
import com.example.vplayed_test.api.BaseApi
import retrofit2.Response

class ViewModel: ViewModel() {

//    var repo : Repo? = null
    var data : MutableLiveData<MutableList<DataItem>> = MutableLiveData()

    fun getLiveDataObserver():MutableLiveData<MutableList<DataItem>>{
        return data
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
//     suspend fun search() {
//        val results = repo?.search()
//        if (results != null && results.isSuccessful) {
//            val league = results.body()?.response
//            data.postValue(league)
//        }
//    }
}


