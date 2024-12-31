package com.example.player_sample_project.seeallpage.model;

import android.util.Log;
import androidx.annotation.NonNull;
import com.example.player_sample_project.api.BaseApi;
import com.example.player_sample_project.data_mvvm.Data;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SeeAllModel implements ISeeAllModel {
    List<Data> data;

    @Override
    public void getDataListModel(final ISeeAllModel.OnFinishedListener onFinishedListener) {
        BaseApi.BaseApiInterface apiService = BaseApi.Companion.getRetrofitSeeAll().create(BaseApi.BaseApiInterface.class);
        Call<List<Data>> call = apiService.getApiSeeAllResults();

        call.enqueue(new Callback<List<Data>>() {
            @Override
            public void onResponse(@NonNull Call<List<Data>> call, @NonNull Response<List<Data>> response) {
                if (response.body() != null) {
                    Log.i("seeall-", "response--" + response.body());
                    data = response.body();
                    onFinishedListener.onSuccess(data);
                } else onFinishedListener.onFailure(new Error());
            }

            @Override
            public void onFailure(@NonNull Call<List<Data>> call, @NonNull Throwable t) {
                Log.i("seeall-", "failure--" + t);
                onFinishedListener.onFailure(t);
            }
        });
    }

}
