package com.example.player_sample_project.app

import android.content.Context
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.google.firebase.auth.FirebaseAuth
import java.io.File

object FirebaseAuthInstance {
    val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
}

object CacheManagerInstance {
    private var mediaCache: SimpleCache? = null
    fun getMediaCache(context: Context): SimpleCache {
        if (mediaCache == null) {
            val cacheSize = 100 * 1024 * 1024 //100 MB
            mediaCache = SimpleCache(File(context.cacheDir, "media"), LeastRecentlyUsedCacheEvictor(cacheSize.toLong()))
        }
        return mediaCache!!
    }
}
