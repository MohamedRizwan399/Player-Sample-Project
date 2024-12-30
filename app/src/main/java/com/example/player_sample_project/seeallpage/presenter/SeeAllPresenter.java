package com.example.player_sample_project.seeallpage.presenter;

import com.example.player_sample_project.data_mvvm.Data;
import com.example.player_sample_project.seeallpage.model.ISeeAllModel;
import com.example.player_sample_project.seeallpage.model.SeeAllModel;
import com.example.player_sample_project.seeallpage.view.ISeeAllView;
import java.util.List;

public class SeeAllPresenter implements ISeeAllPresenter, ISeeAllModel.OnFinishedListener {
    private final ISeeAllView dataListViews;
    private final ISeeAllModel dataListModel;

    public SeeAllPresenter(ISeeAllView dataListView) {
        this.dataListViews = dataListView;
        dataListModel = new SeeAllModel();
    }

    @Override
    public void requestData() {
        dataListModel.getDataListModel(this);
    }

    @Override
    public void onSuccess(List<Data> dataArrayList) {
        dataListViews.setDataToRecyclerview(dataArrayList);
    }

    @Override
    public void onFailure(Throwable t) {
        dataListViews.onApiResponseFailure(t);
    }
}
