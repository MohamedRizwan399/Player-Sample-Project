package com.example.vplayed_test.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.vplayed_test.R

class ProfileBaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_base)
    }
}