package com.example.player_sample_project.subscription;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.player_sample_project.R;

public class SharedPreference {
    private final Context context;
    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;

    public SharedPreference(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }


    public void setPremium(int value) {
        editor.putInt("Premium", value);
        editor.apply();
    }

    public int getPremium() {
        return sharedPreferences.getInt("Premium", 0);
    }

}
