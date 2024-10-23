package com.example.player_sample_project.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class NetworkObserveReceiver extends BroadcastReceiver {
    private final NetworkConnected listener;

    public NetworkObserveReceiver(NetworkConnected listener) {
        this.listener = listener;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //Method 1
        NetworkInfo activeNetwork = cManager.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        listener.connectionReceived(isConnected);


        /**
         * Alternative Method to update state
         */
//        cManager.registerNetworkCallback(new NetworkRequest.Builder().build(), new ConnectivityManager.NetworkCallback() {
//            @Override
//            public void onAvailable(@NonNull Network network) {
//                super.onAvailable(network);
//            }
//
//            @Override
//            public void onLost(@NonNull Network network) {
//                super.onLost(network);
//            }
//        });

    }


    public interface NetworkConnected {
        void connectionReceived(boolean isConnect);
    }
}
