package com.example.player_sample_project.activity

import android.Manifest
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.player_sample_project.R
import com.example.player_sample_project.activity.DynamicLinkShare.Companion.DEEP_LINK_URL
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaMetadata
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.DefaultTimeBar
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.admanager.AdManagerInterstitialAd
import com.google.android.gms.ads.admanager.AdManagerInterstitialAdLoadCallback
import com.google.common.collect.ImmutableList
import kotlin.time.Duration.Companion.milliseconds


class PlayerActivity : AppCompatActivity() ,Player.Listener, TelephonyReceiver.OnCallReceive {
    private lateinit var player: ExoPlayer
    private lateinit var playerView: PlayerView
    private lateinit var progressBar: ProgressBar
    private lateinit var titleTv: TextView
    private var boolean = false
    private lateinit var buttonShare: ImageView
    private lateinit var fullscreenclick:ImageView
    private lateinit var reverse: ImageView
    private lateinit var forward: ImageView
    private lateinit var settings: ImageView
    //private var shoTrackSelector: DefaultTrackSelector? = null
    private var telephonyReceiver = TelephonyReceiver(this)
    private var dynamicshare: DynamicLinkShare = DynamicLinkShare()
    private lateinit var watermark: TextView
    private lateinit var watermark_Landscape: TextView

    private var mAdManagerInterstitialAd: AdManagerInterstitialAd? = null
    private var mLastClickTime: Long = 0
    private lateinit var seekbar: DefaultTimeBar
    private var handler: Handler = Handler()
    var isLandscapeview: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        supportActionBar?.hide()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_PHONE_STATE), 1)
        }

        progressBar = findViewById(R.id.progress_circular)
        titleTv = findViewById(R.id.title1)
        reverse = findViewById(R.id.exo_rew)
        forward = findViewById(R.id.exo_fwd)
        settings = findViewById(R.id.settings)
        fullscreenclick = findViewById(R.id.exo_fullscreen)
        buttonShare = findViewById(R.id.share)
        watermark = findViewById(R.id.watermark)
        watermark_Landscape = findViewById(R.id.watermark_landscape)

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
        fullscreenclick.setOnClickListener {
            fullscreenLayout()
        }
        buttonShare.setOnClickListener {
            val newDeepLink = dynamicshare.buildDeepLink(Uri.parse(DEEP_LINK_URL))
            shareDeepLink(newDeepLink.toString())
            player.pause()
        }

        // Handled the calculations to show alert when 90 percent of the video runs
        val run = object : Runnable {
            override fun run() {
                val pos1 = player.currentPosition.milliseconds
                handler.postDelayed(this, 1000)
                Log.i("msg", "$pos1")
                val pos2 = (player.duration * 90 / 100).milliseconds
                Log.i("msg1", "$pos2")
                if (pos2 < pos1) {
                    player.pause()
                    alert() // display alert dialog
                    handler.removeCallbacksAndMessages(null)
                }
            }
        }
        handler.post(run)
    }

    // Using this function to show the interstitial(fullscreen) ads after player ends
    private fun loadFullScreenAds() {
        var adRequest = AdManagerAdRequest.Builder().build()
        AdManagerInterstitialAd.load(this,"/6499/example/interstitial",
            adRequest,object :AdManagerInterstitialAdLoadCallback(){
                override fun onAdLoaded(interstitialAd: AdManagerInterstitialAd) {
                    super.onAdLoaded(interstitialAd)
                    mAdManagerInterstitialAd=interstitialAd
                    mAdManagerInterstitialAd!!.show(this@PlayerActivity)
                }

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    super.onAdFailedToLoad(p0)
                }
            })
    }

    private fun shareDeepLink(deepLink: String) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_SUBJECT, "FirebaseDeepLink")
        intent.putExtra(Intent.EXTRA_TEXT, deepLink)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(Intent.createChooser(intent,"Share Via"))
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        //window.decorView.systemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN)
        updateLayout(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
    }

    private fun updateLayout(isLandscape: Boolean) {
        val view = findViewById<ConstraintLayout>(R.id.constraint1)
        if (isLandscape) {
            view.visibility = View.INVISIBLE
            val metrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(metrics)
            val videoView = findViewById<FrameLayout>(R.id.video_layout)
            val params = videoView.getLayoutParams()
            params.width = metrics.widthPixels
            params.height = metrics.heightPixels
            videoView.setLayoutParams(params)
            watermark.visibility=View.INVISIBLE
            watermark_Landscape.visibility=View.VISIBLE
        } else {
            watermark.visibility=View.VISIBLE
            watermark_Landscape.visibility=View.INVISIBLE
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

    private fun fullscreenLayout() {
        val view = findViewById<ConstraintLayout>(R.id.constraint1)
        if (isLandscapeview) {
            isLandscapeview = false
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE)
            view.visibility = View.INVISIBLE

            val metrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(metrics)
            val videoView = findViewById<FrameLayout>(R.id.video_layout)
            val params = videoView.getLayoutParams()
            params.width = metrics.widthPixels
            params.height = metrics.heightPixels
            videoView.setLayoutParams(params)
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT)
            view.visibility = View.VISIBLE
            val metrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(metrics)
            val videoView = findViewById<FrameLayout>(R.id.video_layout)
            val params = videoView.getLayoutParams()
            params.width = metrics.widthPixels
            params.height = (230 * metrics.density).toInt()
            videoView.setLayoutParams(params)
            isLandscapeview=true
        }
    }

    private fun setupPlayer() {
        player = ExoPlayer.Builder(this@PlayerActivity).build()
        playerView = findViewById(R.id.video_view)
        playerView.player = player

        /*val factory = AdaptiveTrackSelection.Factory()
        shoTrackSelector = DefaultTrackSelector(this, factory)
        player = setExoPlayerTracks(shoTrackSelector)
        player.addAnalyticsListener(EventLogger(shoTrackSelector, PlayerActivity::class.java.simpleName))*/

        player.addListener(this)
        player.currentPosition
        watermark.visibility = View.VISIBLE
    }

    private fun setExoPlayerTracks(trackSelector: DefaultTrackSelector?): ExoPlayer {
        val bandwidthMeter = DefaultBandwidthMeter.Builder(this).build()
        trackSelector!!.setParameters(trackSelector
            .buildUponParameters()
            .setMaxVideoSizeSd()
        )
       
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
        return player
    }

    private fun mediaFiles() {
        val mediaItem1 = MediaItem.fromUri(getString(R.string.bigbuckbunny))
        val mediaItem2 = MediaItem.fromUri(getString(R.string.audio_url_mp3))
        val mediaItem3 = MediaItem.fromUri(getString(R.string.tearsofsteel))

        val newItems: List<MediaItem> = ImmutableList.of(mediaItem1,mediaItem2,mediaItem3)
        player.addMediaItems(newItems)
        player.prepare()
    }

    // It handles loading state of the player
    override fun onPlaybackStateChanged(state: Int) {
        when (state) {
            Player.STATE_IDLE -> {
                Log.i("player-","player idle state")
            }

            Player.STATE_BUFFERING -> {
                Log.i("player-","player buffer state")
                progressBar.visibility = View.VISIBLE
            }

            Player.STATE_READY -> {
                Log.i("player-","player ready state")
                progressBar.visibility = View.INVISIBLE
                settings.visibility=View.VISIBLE

                settings.setOnClickListener {
                    //showSelectionDialog(0)
                    Toast.makeText(this, "clicked", Toast.LENGTH_SHORT).show()
                }
            }

            Player.EVENT_PLAYBACK_PARAMETERS_CHANGED -> {
                Log.i("player-","player playback changed state")
                Toast.makeText(this, "next", Toast.LENGTH_SHORT).show()
            }

            Player.STATE_ENDED -> {
                Log.i("player-","player Ended state")
                loadFullScreenAds()
            }

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
        val telephonyFilter = IntentFilter("android.intent.action.PHONE_STATE")
        registerReceiver(telephonyReceiver,telephonyFilter)
        player.currentPosition
        player.play()
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(telephonyReceiver)
    }

    override fun onStart() {
        super.onStart()
        player.play()
    }

    private fun alert() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.custom_alert, null)
        val mBuilder = this.let { it1 ->
            AlertDialog.Builder(it1, R.style.CustomDialog)
                .setView(dialogView)
                .setCancelable(false)
        }
        val alertDialog = mBuilder.show()
        dialogView.findViewById<Button>(R.id.button).setOnClickListener {
            alertDialog?.dismiss()
            player.playWhenReady = true
        }
    }

    override fun callReceived(boolean: Boolean) {
        player.pause()
    }

}

