package com.example.player_sample_project.app;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.player_sample_project.R;

public class Utils {
    private static Toast toastLong = null;

    public static void hideSystemUI(Activity activity) {
        activity.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        );
    }


    /**
     * Method to log the error
     *
     * @param e Exception raised by the logger
     */
    public static void logError(Exception e) {
        if (e != null && !TextUtils.isEmpty(e.getMessage()))
            Log.e("error", e.getMessage());
    }


    /**
     * Method to display the Toast message
     *
     * @param context Context
     * @param message Message to show
     */
    public static void showLongMessage(Context context, String message) {
        try {
            LayoutInflater inflaterToast = ((Activity) context).getLayoutInflater();
            View layout = inflaterToast.inflate(R.layout.custom_toast, null);
            TextView text = (TextView) layout
                    .findViewById(R.id.custom_toast_message);
            text.setText(message);
            if (toastLong != null)
                toastLong.cancel();
            toastLong = new Toast(context);
            toastLong.setGravity(Gravity.BOTTOM, 0, 160);
            toastLong.setDuration(Toast.LENGTH_LONG);
            toastLong.setView(layout);
            toastLong.show();
        } catch (Exception e) {
            logError(e);
        }
    }

    /**
     * To check the internet connectivity and show error message if network not available.
     * @param context Contains the context
     * @return boolean True ,if internet is available means false
     */
    public static boolean checkNetworkAndShowDialog(Context context) {
        if (!checkNetConnection(context)) {
            showLongMessage(context, context.getString(R.string.check_internet));
            return false;
        }
        return true;
    }


    /**
     * Checking internet state.
     *
     * @param context Activity context
     * @return boolean True if internet is enabled else false
     */
    public static boolean checkNetConnection(Context context) {
        ConnectivityManager miManager = (ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        NetworkInfo miInfo = miManager.getActiveNetworkInfo();
        boolean networkStatus = false;

        //Checking the network connection is in wifi or mobile data
        if (miInfo != null && miInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            networkStatus = true;
        } else if (miInfo != null && miInfo.getType() == ConnectivityManager.TYPE_MOBILE &&
                miInfo.isConnectedOrConnecting())
            networkStatus = true;
        return networkStatus;
    }
}
