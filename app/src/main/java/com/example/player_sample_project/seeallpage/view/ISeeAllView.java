package com.example.player_sample_project.seeallpage.view;

import com.example.player_sample_project.data_mvvm.Data;
import java.util.List;

public interface ISeeAllView {

    // Through set the response Data
    void setDataToRecyclerview(List<Data> dataListArray);

    // Through handle failures to view
    void onApiResponseFailure(Throwable throwable);
}
