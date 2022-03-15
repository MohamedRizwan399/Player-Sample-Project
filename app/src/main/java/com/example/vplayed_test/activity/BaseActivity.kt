package com.example.vplayed_test.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player


class BaseActivity :BroadcastReceiver(),Player.Listener{
    private var playerActivity=PlayerActivity()
    override fun onReceive(p0: Context?, intent: Intent?) {
         val receive= intent?.getStringExtra(TelephonyManager.EXTRA_STATE)
             .equals(TelephonyManager.EXTRA_STATE_RINGING)
playerActivity.pause()
    }

}