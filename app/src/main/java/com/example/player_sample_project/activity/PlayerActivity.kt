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
import com.example.player_sample_project.app.CacheManagerInstance
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaMetadata
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.DefaultTimeBar
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.util.MimeTypes
import com.google.android.exoplayer2.util.Util
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.admanager.AdManagerInterstitialAd
import com.google.android.gms.ads.admanager.AdManagerInterstitialAdLoadCallback
import kotlin.time.Duration.Companion.milliseconds


class PlayerActivity : AppCompatActivity() ,Player.Listener, TelephonyReceiver.OnCallReceive {
    private lateinit var player: ExoPlayer
    private lateinit var playerView: PlayerView
    private lateinit var progressBar: ProgressBar
    private lateinit var titleTv: TextView
    private lateinit var purchaseButton: Button
    private var buyProductAdShown: Boolean = false
    private lateinit var buttonShare: ImageView
    private lateinit var fullscreenclick:ImageView
    private lateinit var reverse: ImageView
    private lateinit var forward: ImageView
    private lateinit var settings: ImageView
    private lateinit var footerPlay: ImageView
    private lateinit var footerPause: ImageView
    //private var shoTrackSelector: DefaultTrackSelector? = null
    private var telephonyReceiver = TelephonyReceiver(this)
    private var dynamicshare: DynamicLinkShare = DynamicLinkShare()
    private lateinit var watermark: TextView
    private lateinit var watermark_Landscape: TextView

    private var mAdManagerInterstitialAd: AdManagerInterstitialAd? = null
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
        purchaseButton = findViewById(R.id.buy_product)
        val playerView = findViewById<PlayerView>(R.id.video_view)

        reverse = playerView.findViewById(R.id.exo_rew)
        forward = playerView.findViewById(R.id.exo_fwd)
        settings = playerView.findViewById(R.id.settings)
        fullscreenclick = playerView.findViewById(R.id.exo_fullscreen)
        footerPlay = playerView.findViewById(R.id.exo_play_footer)
        footerPause = playerView.findViewById(R.id.exo_pause_footer)
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
        purchaseButton.setOnClickListener {
            if (!buyProductAdShown) loadFullScreenAds()
            else Toast.makeText(this, "InApp Purchase not implemented", Toast.LENGTH_LONG).show()
        }
        buttonShare.setOnClickListener {
            val newDeepLink = dynamicshare.buildDeepLink(Uri.parse(DEEP_LINK_URL))
            shareDeepLink(newDeepLink.toString())
            player.pause()
        }
        updateCustomPlayPauseClickEvent() // custom play/pause click listeners

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

    override fun onStart() {
        super.onStart()
        Log.i("player-","onStart")
        player.play()
    }

    override fun onResume() {
        super.onResume()
        Log.i("player-","onResume")
        val telephonyFilter = IntentFilter("android.intent.action.PHONE_STATE")
        registerReceiver(telephonyReceiver,telephonyFilter)
        player.currentPosition
        player.play()
    }

    override fun onPause() {
        super.onPause()
        Log.i("player-","onPause")
        unregisterReceiver(telephonyReceiver)
    }

    override fun onStop() {
        super.onStop()
        Log.i("player-","onStop")
        player.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("player-","onDestroy")
        buyProductAdShown = false
        player.removeListener(this)
    }

    // Using this function to show the interstitial(fullscreen) ads after player ends
    private fun loadFullScreenAds() {
        var adRequest = AdManagerAdRequest.Builder().build()
        AdManagerInterstitialAd.load(this,"/6499/example/interstitial",
            adRequest,object :AdManagerInterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: AdManagerInterstitialAd) {
                    super.onAdLoaded(interstitialAd)
                    player.pause()
                    mAdManagerInterstitialAd = interstitialAd
                    mAdManagerInterstitialAd!!.show(this@PlayerActivity)
                    buyProductAdShown = true // Shown 1 time
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

    /**
     * Handled initial player setup in this function and setBufferDurationsMs to customize the LoadControl
     */
    private fun setupPlayer() {
        val loadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                3000,  // Minimum buffer before start or to keep
                10000, // Maximum buffer to keep
                2500,
                2000
            ).build()
        player = ExoPlayer.Builder(this@PlayerActivity)
            .setLoadControl(loadControl)
            .build()
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

    /**
     * Handled Player tracks(like Auto, 144p, 240p, etc) in this function
     * Currently not implemented
     */
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

    /**
     *  Handled the MediaItems and add it to player and prepare to stream
     *  Cache setup when large files/high traffic to improve performance and reduce load time
     */
    private fun mediaFiles() {
        val cache = CacheManagerInstance.getMediaCache(this) // use the global cache to manage single instance
        val dataSourceFactory = DefaultDataSourceFactory(this, Util.getUserAgent(this, getString(R.string.app_name)))
        val cacheDataSourceFactory = CacheDataSource.Factory()
            .setCache(cache)
            .setUpstreamDataSourceFactory(dataSourceFactory)

        val mediaItem1 = MediaItem.Builder().setUri(getString(R.string.tearsOfSteelHls)) // its HLS file
            .setMimeType(MimeTypes.APPLICATION_M3U8) // set MIME type for HLS
            .build()
        val mediaItem2 = MediaItem.fromUri(getString(R.string.audio_url_mp3))
        val mediaItem3 = MediaItem.fromUri(getString(R.string.sintel))

        // MediaSource objects creation for each mediaItem to use cache
        val mediaSource1: MediaSource = HlsMediaSource.Factory(cacheDataSourceFactory).createMediaSource(mediaItem1)
        val mediaSource2: MediaSource = ProgressiveMediaSource.Factory(cacheDataSourceFactory).createMediaSource(mediaItem2)
        val mediaSource3: MediaSource = ProgressiveMediaSource.Factory(cacheDataSourceFactory).createMediaSource(mediaItem3)

        /*val newItems: List<MediaItem> = ImmutableList.of(mediaItem1, mediaItem2, mediaItem3)
        player.addMediaItems(newItems)*/

        player.setMediaSources(listOf(mediaSource1, mediaSource2, mediaSource3))
        player.prepare()
    }

    // It handles state of the player
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
                player.play()
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

    // Handles the play/pause based on player state
    override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
        super.onPlayWhenReadyChanged(playWhenReady, reason)
        if (playWhenReady) {
            Log.i("player-", "updateFooterPlayPause- is playing")
            footerPlay.visibility = View.INVISIBLE
            footerPause.visibility = View.VISIBLE
        } else {
            Log.i("player-", "updateFooterPlayPause- is no playing")
            footerPlay.visibility = View.VISIBLE
            footerPause.visibility = View.INVISIBLE
        }
    }

    // Handled custom play/pause click events
    private fun updateCustomPlayPauseClickEvent() {
        footerPlay.setOnClickListener {
            player.playWhenReady = true
            footerPlay.visibility = View.INVISIBLE
            footerPause.visibility = View.VISIBLE
        }
        footerPause.setOnClickListener {
            player.playWhenReady = false
            footerPlay.visibility = View.VISIBLE
            footerPause.visibility = View.INVISIBLE
        }
    }

    override fun onPlayerError(error: PlaybackException) {
        super.onPlayerError(error)
        Toast.makeText(this, "Media Player has a ${error.message}", Toast.LENGTH_LONG).show()
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

