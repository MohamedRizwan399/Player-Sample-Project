package com.example.player_sample_project.ads;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.player_sample_project.R;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback;

/**
 * This activity used to load and show the ads
 */
public class AdsActivity extends AppCompatActivity {

    private RewardedAd rewardedVideoAd;
    private RewardedInterstitialAd rewardedInterstitialFullAd;
    private String adId="ca-app-pub-3940256099942544/5224354917";
    Button button;
    Button buttonFull;
    ImageView adBackButton;
    AdManagerAdRequest adRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ads);
        button = findViewById(R.id.show_ads_rewards);
        buttonFull = findViewById(R.id.show_ads_interstitial);
        adBackButton = findViewById(R.id.ad_back);
        adBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        MobileAds.initialize(this);
        adRequest = new AdManagerAdRequest.Builder().build();

        //for video ad
        loadRewardedVideoAd();
        //showRewardedVideoAd();

        // for fullscreen video ad
        loadFullScreenRewardedVideoAd();
        showRewardedInterstitialFullAds();
    }

    public void loadFullScreenRewardedVideoAd() {
    RewardedInterstitialAd.load(this, "/21775744923/example/rewarded_interstitial",
            adRequest, new RewardedInterstitialAdLoadCallback() {
                @Override
                public void onAdLoaded(@NonNull RewardedInterstitialAd rewardedInterstitialAd) {
                    super.onAdLoaded(rewardedInterstitialAd);
                    rewardedInterstitialFullAd = rewardedInterstitialAd;
                    Log.i("videoAd","videoAd - Rewards Interstitial Ads Loaded successfully" +rewardedInterstitialFullAd);
                    buttonFull.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showRewardedInterstitialFullAds();
                        }
                    });
                }
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                }

            });
    }

    public void showRewardedInterstitialFullAds() {
        if (rewardedInterstitialFullAd != null) {
            rewardedInterstitialFullAd.show(this, new OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    int reward = rewardItem.getAmount();
                    String rewardType = rewardItem.getType();
                    Log.i("rewardsInterstitial", "Rewards & Type:" + reward + " " + rewardType);

                }
            });
        } else {
            Toast.makeText(this, "Ads will show in few secs..", Toast.LENGTH_LONG).show();
        }
    }



    public void loadRewardedVideoAd() {
        RewardedAd.load(this, "/6499/example/rewarded",
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        super.onAdFailedToLoad(loadAdError);
                        Log.i("videoAd","videoAd - rewards Ads FAIL to load--" +loadAdError);
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                        super.onAdLoaded(rewardedAd);
                        rewardedVideoAd = rewardedAd;
                        Log.i("videoAd","videoAd - rewards Ads Loaded successfully");
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                showRewardedVideoAd();
                            }
                        });

                    }
                });
    }

    public void showRewardedVideoAd() {
        if (rewardedVideoAd != null) {
            rewardedVideoAd.show(this, new OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    int reward = rewardItem.getAmount();
                    String rewardType = rewardItem.getType();
                    Log.i("rewards","Rewards & Type:"+reward +" "+rewardType);
                }
            });
        } else {
            Toast.makeText(this, "check again", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}