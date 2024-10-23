package com.example.player_sample_project.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import com.example.player_sample_project.R
import com.example.player_sample_project.app.AppController
import com.example.player_sample_project.authentication.LoginBaseActivity

class SplashScreen : AppCompatActivity() {
    private lateinit var motionLayout: MotionLayout
    private lateinit var appController: AppController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash_screen)
        appController = AppController(this)

        motionLayout=findViewById(R.id.splashLayout)
        motionLayout.setTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionStarted(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int
            ) {

            }

            override fun onTransitionChange(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int,
                progress: Float
            ) {

            }

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                val isLoggedInData = appController.getLoginStatus("userId", null.toString())
                if (isLoggedInData != null && isLoggedInData != "null" ) {
                    startActivity(Intent(this@SplashScreen,MainActivity::class.java))
                } else {
                    startActivity(Intent(this@SplashScreen, LoginBaseActivity::class.java))
                }
                finish()
            }

            override fun onTransitionTrigger(
                motionLayout: MotionLayout?,
                triggerId: Int,
                positive: Boolean,
                progress: Float
            ) {
            }
        })
    }

    override fun onStart() {
        super.onStart()
        appController.clearPreferencesApiData() // Everytime clear api data before go to homeScreen
        Log.i("connection-", "clear prefs from splashscreen")
    }
}