package com.example.player_sample_project.subscription;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.example.player_sample_project.R;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;

/**
 * Currently BillingClient disabled in this activity due to the playConsole deactivated, It will implement in future
 * */
public class SubscriptionActivity extends AppCompatActivity {

    private BillingClient billingClient;
    Button subscribe_button;
    Activity activity;
    SharedPreference prefs;
    TextView txt_subscribed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription);
        subscribe_button = findViewById(R.id.subs_button);
        prefs = new SharedPreference(this);
        txt_subscribed = findViewById(R.id.txt_subscribed);

        if (prefs.getPremium() == 1) {
            txt_subscribed.setText("You are a premium subscriber");
        } else {
            txt_subscribed.setText("You are not subscribed");
        }
        Toast.makeText(this, "Billing is disabled, It will implement soon", Toast.LENGTH_LONG).show();

        billingClient = BillingClient.newBuilder(this)
                .setListener(new PurchasesUpdatedListener() {
                    @Override
                    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && list!=null) {
                            for (Purchase purchase:list){
                                verifySubPurchase(purchase);
                            }
                        }
                    }
                })
                .enablePendingPurchases()
                .build();

        //To connect with googlePlay
        startConnection();
    }

    public void startConnection() {
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingServiceDisconnected() {
                startConnection();
            }

            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    showProducts();
                }
            }
        });
    }

    public void showProducts() {
        QueryProductDetailsParams queryProductDetailsParams = QueryProductDetailsParams.newBuilder()
                        .setProductList(ImmutableList.of(QueryProductDetailsParams.Product.newBuilder()
                                .setProductId("product_id_example")
                                .setProductType(BillingClient.ProductType.SUBS)
                                .build()))
                        .build();

        billingClient.queryProductDetailsAsync(queryProductDetailsParams, new ProductDetailsResponseListener() {
            @Override
            public void onProductDetailsResponse(@NonNull BillingResult billingResult, @NonNull List<ProductDetails> list) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    //process the result
                    for (ProductDetails productDetails : list) {

                        if (productDetails.getProductType().equals("abc")) {
                            //Now update the UI
                            subscribe_button.setText(productDetails.getOneTimePurchaseOfferDetails()+ " Per Month");
                            subscribe_button.setOnClickListener(view -> {
                                launchPurchaseFlow(productDetails);
                            });

                        }
                    }
                }
            }
        });
    }

    //for old version
    void showProductsOld() {
        List<String> skuList = new ArrayList<>();
        skuList.add("sub_premium");
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(BillingClient.SkuType.SUBS);
        billingClient.querySkuDetailsAsync(params.build(),
                new SkuDetailsResponseListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onSkuDetailsResponse(@NonNull BillingResult billingResult,
                                                     List<SkuDetails> skuDetailsList) {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                            // Process the result.
                            for (SkuDetails skuDetails : skuDetailsList) {
                                if (skuDetails.getSku().equals("sub_premium")) {
                                    //Now update the UI
                                }
                            }
                        }
                    }
                });
    }

    public void launchPurchaseFlow(ProductDetails productDetails) {
        ImmutableList productDetailsParamsList =
                ImmutableList.of(BillingFlowParams.ProductDetailsParams.newBuilder()
                                // retrieve a value for "productDetails" by calling queryProductDetailsAsync()
                                .setProductDetails(productDetails)
                                // to get an offer token, call ProductDetails.getSubscriptionOfferDetails()
                                // for a list of offers that are available to the user
                                .build()
                );
        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailsParamsList)
                .build();

        // Launch the billing flow
        BillingResult billingResult = billingClient.launchBillingFlow(activity, billingFlowParams);
    }

    public void verifySubPurchase(Purchase purchases) {
        AcknowledgePurchaseParams acknowledgePurchaseParams = AcknowledgePurchaseParams
                .newBuilder()
                .setPurchaseToken(purchases.getPurchaseToken())
                .build();

        billingClient.acknowledgePurchase(acknowledgePurchaseParams, new AcknowledgePurchaseResponseListener() {
            @Override
            public void onAcknowledgePurchaseResponse(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    //Toast.makeText(SubscriptionActivity.this, "Item Consumed", Toast.LENGTH_SHORT).show();
                    // Handle the success of the consume operation.
                    //user prefs to set premium
                    Toast.makeText(activity, "You are a premium user now", Toast.LENGTH_SHORT).show();

                    //Setting premium to 1
                    // 1 - premium
                    //0 - no premium
                    prefs.setPremium(1);
                }
            }
        });

        Log.i("token", "Purchase Token: " + purchases.getPurchaseToken());
        Log.i("time", "Purchase Time: " + purchases.getPurchaseTime());
        Log.i("orderId", "Purchase OrderID: " + purchases.getOrderId());
    }

}