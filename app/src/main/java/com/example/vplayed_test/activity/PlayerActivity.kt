package com.example.vplayed_test.activity

import android.Manifest
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.vplayed_test.R
import com.google.android.exoplayer2.ui.DefaultTimeBar
import com.google.android.exoplayer2.ui.PlayerView
import com.google.common.collect.ImmutableList
import kotlin.time.Duration.Companion.milliseconds
import androidx.core.app.ActivityCompat

import android.content.pm.PackageManager

import androidx.core.content.ContextCompat

import com.google.android.material.snackbar.Snackbar

import android.R.string.no
import com.example.vplayed_test.app.Utils
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.util.EventLogger


class PlayerActivity : AppCompatActivity(),Player.Listener {
    private lateinit var player: ExoPlayer
    private lateinit var playerView: PlayerView
    private lateinit var progressBar: ProgressBar
    private lateinit var titleTv: TextView
    private lateinit var buttonShare: ImageButton
    private lateinit var reverse: ImageView
    private lateinit var forward: ImageView
    private lateinit var settings: ImageView
    private var settingsBottomsheet=PlayerSettingsBottomSheet()
    private var shoTrackSelector: DefaultTrackSelector? = null


    private var mLastClickTime: Long = 0
    private lateinit var seekbar: DefaultTimeBar
    private var handler: Handler = Handler()
    var fullscreen: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        supportActionBar?.hide()
        setContentView(R.layout.activity_player)



        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
            != PackageManager.PERMISSION_GRANTED)
            {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_PHONE_STATE),
                1)
        }

        progressBar = findViewById(R.id.progress)
        titleTv = findViewById(R.id.title1)
        reverse=findViewById(R.id.exo_rew)
        forward=findViewById(R.id.exo_ffwd)
        settings=findViewById(R.id.settings)


        setupPlayer()
        mediaFiles()
        //get the saved data after orientation happens
        savedInstanceState?.getInt("mediaItem")?.let { restoredMedia ->
            val seekTime = savedInstanceState.getLong("SeekTime")
            player.seekTo(restoredMedia, seekTime)
            player.play()
        }
        reverse.setOnClickListener {
            player.seekTo(player.currentPosition-10000)
        }
        forward.setOnClickListener {
            player.seekTo(player.currentPosition+10000)
        }




//            player
//                .createMessage { messageType: Int, payload: Any? ->
//                    Toast.makeText(this, "message", Toast.LENGTH_SHORT).show()
//                }
//                .setLooper(Looper.getMainLooper())
//                .setPosition( /* mediaItemIndex= */0,  /* positionMs= */30000)
//                .setDeleteAfterDelivery(false)
//                .send()



        val run = object : Runnable {
            override fun run() {
                val pos1 = player.currentPosition.milliseconds

                handler.postDelayed(this, 1000)
                Log.i("msg", "$pos1")
                val pos2 = (player.duration * 90 / 100).milliseconds
                Log.i("msg1", "$pos2")
                //calculation for 90 percent of video duration
                //Alert shows in 8.57 minutes
                if (pos2 < pos1) {
                    player.pause()
                    alert()
                    handler.removeCallbacksAndMessages(null)
                }
            }
        }
        handler.post(run)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
//        window.decorView.systemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN)
        updateLayout(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
    }

    private fun updateLayout(isLandscape: Boolean) {
        var view = findViewById<ConstraintLayout>(R.id.constraint1)
        if (isLandscape) {
            view.visibility = View.INVISIBLE
//            View.SYSTEM_UI_FLAG_FULLSCREEN


            val metrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(metrics)
            val videoView = findViewById<FrameLayout>(R.id.video_layout)
            val params = videoView.getLayoutParams()
            params.width = metrics.widthPixels
            params.height = metrics.heightPixels

            videoView.setLayoutParams(params)


        } else {
            view.visibility = View.VISIBLE
            val metrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(metrics)
            val videoView = findViewById<FrameLayout>(R.id.video_layout)
            val params = videoView.getLayoutParams()
            params.width = metrics.widthPixels
            params.height = (230 * metrics.density).toInt()
            videoView.setLayoutParams(params)

        }
    }

    private fun setupPlayer() {
        player = ExoPlayer.Builder(this).build()
        playerView = findViewById(R.id.video_view)
        playerView.player = player
        val factory = AdaptiveTrackSelection.Factory()
        shoTrackSelector = DefaultTrackSelector(this, factory)
        player = setSimpleExoPlayer(shoTrackSelector)
        player?.addAnalyticsListener(EventLogger(shoTrackSelector, PlayerActivity::class.java.simpleName))


        player.addListener(this)
        player.currentPosition

    }

    private fun setSimpleExoPlayer(
        trackSelector: DefaultTrackSelector?,
    ): ExoPlayer {
        val bandwidthMeter = DefaultBandwidthMeter.Builder(this).build()

        trackSelector!!.setParameters(trackSelector
            .buildUponParameters()
            .setMaxVideoSizeSd()
        )

        @DefaultRenderersFactory.ExtensionRendererMode
        val extensionRendererMode = DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON

        val renderersFactory = DefaultRenderersFactory(this)
            .setExtensionRendererMode(extensionRendererMode)

        val loadControl = DefaultLoadControl.Builder()
            .setTargetBufferBytes(DefaultLoadControl.DEFAULT_TARGET_BUFFER_BYTES)
            .setPrioritizeTimeOverSizeThresholds(true)
            .build()


        player = ExoPlayer.Builder(this, renderersFactory)
            .setLoadControl(loadControl)
            .setTrackSelector(trackSelector)
            .setBandwidthMeter(bandwidthMeter)
            .build()
        playerView.player = player
        return player!!
    }

    private fun mediaFiles() {
        val mediaItem = MediaItem.fromUri(getString(R.string.elephant))
        val mediaItem1 = MediaItem.fromUri(getString(R.string.sintel))
//        val mediaItem2 = MediaItem.fromUri(getString(R.string.elephant))


        val newItems: List<MediaItem> = ImmutableList.of(mediaItem,mediaItem1)
        player.addMediaItems(newItems)
        player.prepare()

    }


    // It handles loading state of the player
    override fun onPlaybackStateChanged(state: Int) {
        when (state) {
            Player.STATE_BUFFERING -> {
                progressBar.visibility = View.VISIBLE

            }

            Player.STATE_READY -> {
                progressBar.visibility = View.INVISIBLE
                settings.visibility=View.VISIBLE

                settings.setOnClickListener {
                    showSelectionDialog(0)
                playerView.hideController()

                }

            }


            Player.STATE_ENDED -> {
                alert()

            }
        }
    }

    private fun showSelectionDialog(title:Int){
        if (shoTrackSelector != null) {
            player.pause()
            val trackSelectionDialog = TrackSelectionDialog.createForTrackSelector(
                title, shoTrackSelector)
            {
                Utils.hideSystemUI(this)
                player.play()
            }
            trackSelectionDialog.show(supportFragmentManager,null)
        }
    }

    override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {

        titleTv.text = mediaMetadata.title ?: mediaMetadata.displayTitle ?: "no title"

    }

    // save details if Activity is destroyed, orientation changes
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // current play position
        outState.putLong("SeekTime", player.currentPosition)
        // current mediaItem
        outState.putInt("mediaItem", player.currentMediaItemIndex)
    }

    override fun onStop() {
        super.onStop()
        player.pause()
    }

    override fun onResume() {
        super.onResume()
        player.currentPosition
        player.play()
    }

    override fun onStart() {
        super.onStart()
        player.play()
    }

fun playing(){
    player.play()
}


    private fun alert() {
        val dialogview = LayoutInflater.from(this).inflate(R.layout.custom_alert, null)
        val mBuilder = this.let { it1 ->
            AlertDialog.Builder(it1, R.style.CustomDialog)
                .setView(dialogview)
                .setCancelable(false)

        }
        val alertDialog = mBuilder.show()
//        window.setGravity(Gravity.TOP)

        dialogview.findViewById<Button>(R.id.button).setOnClickListener {
            alertDialog?.dismiss()
            player.playWhenReady = true
        }

    }



}

