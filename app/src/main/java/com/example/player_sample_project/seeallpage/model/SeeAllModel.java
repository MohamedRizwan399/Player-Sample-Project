package com.example.player_sample_project.seeallpage.model;

import android.util.Log;

import androidx.annotation.NonNull;
import com.example.player_sample_project.api.BaseApi;
import com.example.player_sample_project.seeallpage.modelclassPoJo.NewResponse;
import com.example.player_sample_project.seeallpage.modelclassPoJo.NewResponseData;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SeeAllModel implements ISeeAllModel {
    List<NewResponseData> data;

    @Override
    public void getDataListModel(final ISeeAllModel.OnFinishedListener onFinishedListener) {
        BaseApi.BaseApiInterface apiService = BaseApi.Companion.getRetrofitSeeAll().create(BaseApi.BaseApiInterface.class);
        Call<NewResponse> call = apiService.getApiSeeAllResults(BaseApi.API_KEY);

        call.enqueue(new Callback<NewResponse>() {
            @Override
            public void onResponse(@NonNull Call<NewResponse> call, @NonNull Response<NewResponse> response) {
                if (response.body() != null) {
                    Log.i("seeall-", "response--" + response.body());
                    data = response.body().getArticles();
                    onFinishedListener.onSuccess(data);
                } else onFinishedListener.onFailure(new Error());
            }

            @Override
            public void onFailure(@NonNull Call<NewResponse> call, @NonNull Throwable t) {
                Log.i("seeall-", "failure--" + t);
                onFinishedListener.onFailure(t);
            }
        });
    }

}
