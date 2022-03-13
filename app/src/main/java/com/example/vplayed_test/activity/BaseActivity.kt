package com.example.vplayed_test.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player


class BaseActivity :BroadcastReceiver(),Player.Listener{
    private lateinit var player: ExoPlayer
//    private val player1:PlayerActivity= PlayerActivity()
    override fun onReceive(p0: Context?, intent: Intent?) {
         val receive= intent?.getStringExtra(TelephonyManager.EXTRA_STATE)
             .equals(TelephonyManager.EXTRA_STATE_RINGING)
    player.pause()
    }

}