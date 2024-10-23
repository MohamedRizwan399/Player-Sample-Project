package com.example.player_sample_project.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager


open class TelephonyReceiver(private val listener: OnCallReceive) :BroadcastReceiver() {

    override fun onReceive(p0: Context?, intent: Intent?) {
         if (intent?.getStringExtra(TelephonyManager.EXTRA_STATE)
             .equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                 listener.callReceived(true)
         }

    }

    interface OnCallReceive {
        fun callReceived(boolean: Boolean)
    }
}