package com.example.player_sample_project.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.player_sample_project.api.BaseApi
import com.example.player_sample_project.app.AppController
import com.example.player_sample_project.app.Utils
import com.example.player_sample_project.data_mvvm.Data
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ViewModel(application: Application): AndroidViewModel(application) {
    private val appController: AppController = AppController(application)
    var data: MutableLiveData<MutableList<Data>> = MutableLiveData()

    fun getLiveDataObserver(): MutableLiveData<MutableList<Data>> {
        return data
    }

    fun apiCallHomeScreen(context: Context, isPrefExists: Boolean) {
        if (Utils.checkNetConnection(context) && !isPrefExists) {
            Log.i("connection-","vm internet ON IF")

            val retrofitInstance = BaseApi.getRetrofitHome()
            val retroService = retrofitInstance.create(BaseApi.BaseApiInterface::class.java)
            val call = retroService.getDataListFree()

            call.enqueue(object :
                Callback<List<Data>> {
                override fun onResponse(
                    call: Call<List<Data>>,
                    response: Response<List<Data>>
                ) {
                    if (response.isSuccessful && response.code() == 200) {
                        val jsonData = Gson().toJson(response.body())
                        appController.storePreferenceApiData("homePageResponse", jsonData)
                        data.postValue(response.body() as MutableList<Data>)
                    } else data.postValue(null)
                }

                override fun onFailure(
                    call: Call<List<Data>>,
                    t: Throwable
                ) {
                    Log.e("onFailure", "Api response failure ${t.message}")
                    data.postValue(null)
                }
            })

        } else {
            Log.i("connection-","vm internet OFF else")
            setStoredResponseForHome(context)
        }
    }

    // for api handled when offline and stores data to avoid multiple api hits
    private fun setStoredResponseForHome(context: Context) {
        val offlineResponse = appController.getPreferenceApiData("homePageResponse", "")
        if (offlineResponse != null && offlineResponse != "") {
            try {
                val offlineData1: MutableList<Data> = Gson().fromJson(offlineResponse, object : TypeToken<MutableList<Data>>() {}.type)
                data.postValue(offlineData1)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            Log.i("connection-", "off resp --->null")
            data.postValue(null)
        }
    }

}


