package com.example.player_sample_project.seeallpage.view;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.player_sample_project.R;
import com.example.player_sample_project.ads.AdsActivityDemo;
import com.example.player_sample_project.data_mvvm.Data;
import com.example.player_sample_project.seeallpage.presenter.SeeAllPresenter;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.gms.ads.admanager.AdManagerInterstitialAd;
import com.google.android.gms.ads.admanager.AdManagerInterstitialAdLoadCallback;
import java.util.ArrayList;
import java.util.List;

public class SeeAllActivity extends AppCompatActivity implements ISeeAllView {
    Activity activity;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView recyclerView;
    private List<Data> dataList;
    private SeeAllPresenter seeAllPresenter;
    private SeeAllAdapter seeAllAdapter;
    private ProgressBar seeAllProgress;
    private AdManagerInterstitialAd mAdManagerInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_allactivity);

        // To change the default to preferred color of app description when goes to phone recent
        ActivityManager.TaskDescription appDescription = new ActivityManager.TaskDescription(getString(R.string.app_name), null,
                ContextCompat.getColor(this, R.color.dark_white));
        setTaskDescription(appDescription);

        dataList = new ArrayList<>();
        ImageView back = findViewById(R.id.toolbar_back);
        ImageView adsClick = findViewById(R.id.ads_click);
        TextView textView = findViewById(R.id.see_all_header_title);
        seeAllProgress = findViewById(R.id.see_all_progress);
        recyclerView = findViewById(R.id.see_all_recycler);

        String receivedHeaderTitle = getIntent().getStringExtra("title");
        textView.setText(receivedHeaderTitle);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        seeAllPresenter = new SeeAllPresenter(this);
        seeAllPresenter.requestData();
        seeAllProgress.setVisibility(View.VISIBLE);

        adsClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadInterstitialAds();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    public void loadInterstitialAds(){
        AdManagerAdRequest adRequest = new AdManagerAdRequest.Builder().build();
        AdManagerInterstitialAd.load(this,"/6499/example/interstitial", adRequest,
                new AdManagerInterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull AdManagerInterstitialAd interstitialAd) {
                        // The mAdManagerInterstitialAd reference will be null until an ad is loaded.
                        mAdManagerInterstitialAd = interstitialAd;
                        mAdManagerInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdClicked() {
                                super.onAdClicked();
                            }

                            @Override
                            public void onAdDismissedFullScreenContent() {
                                super.onAdDismissedFullScreenContent();
                                Log.i("close", "closed");
                                Intent intent=new Intent(getApplicationContext(), AdsActivityDemo.class);
                                startActivity(intent);
                            }

                        });

                        mAdManagerInterstitialAd.show(activity);
                        Log.i("loaded", "onAdLoaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i("failure", loadAdError.toString());
                        mAdManagerInterstitialAd = null;
                    }
                });
    }

    @Override
    public void setDataToRecyclerview(List<Data> dataListArray) {
        dataList.addAll(dataListArray);
        seeAllAdapter = new SeeAllAdapter(dataList,SeeAllActivity.this);
        recyclerView.setAdapter(seeAllAdapter);
        seeAllProgress.setVisibility(View.GONE);
    }

    @Override
    public void onApiResponseFailure(Throwable throwable) {
        Toast.makeText(activity, "Response failed due to Api services", Toast.LENGTH_LONG).show();
        seeAllProgress.setVisibility(View.GONE);
    }
}