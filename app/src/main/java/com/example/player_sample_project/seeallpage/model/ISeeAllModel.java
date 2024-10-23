package com.example.player_sample_project.seeallpage.model;

import com.example.player_sample_project.seeallpage.modelclassPoJo.NewResponseData;
import java.util.List;

public interface ISeeAllModel {

    // response through listeners
    interface OnFinishedListener {
        void onSuccess(List<NewResponseData> dataArrayList);
        void onFailure(Throwable t);
    }

    // request access
    void getDataListModel(OnFinishedListener onSuccessListener);

}
