package com.example.autocar

import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.media.MediaBrowserServiceCompat

class MyMediaBrowserService : MediaBrowserServiceCompat() {

    private lateinit var mediaSession: MediaSessionCompat

    override fun onCreate() {
        super.onCreate()
        Log.d("AutoDemo", "MyMediaBrowserService created")
        mediaSession = MediaSessionCompat(applicationContext, "AutoDemoSession").apply {
            setPlaybackState(
                PlaybackStateCompat.Builder()
                    .setState(PlaybackStateCompat.STATE_NONE, 0, 1f)
                    .build()
            )
            setActive(true)
        }
        setSessionToken(mediaSession.sessionToken)
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot {
        Log.d("AutoDemo", "onGetRoot called by $clientPackageName")
        return BrowserRoot("root_id", null)
    }

    override fun onLoadChildren(
        parentMediaId: String,
        result: Result<List<MediaBrowserCompat.MediaItem>>
    ) {
        Log.d("AutoDemo", "onLoadChildren: $parentMediaId")

        val items = mutableListOf<MediaBrowserCompat.MediaItem>()

        val desc = MediaDescriptionCompat.Builder()
            .setMediaId("track_hello")
            .setTitle(getString(R.string.track_hello))
            .setSubtitle(getString(R.string.track_hello_album))
            .build()

        items.add(MediaBrowserCompat.MediaItem(
            desc,
            MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
        ))

        result.sendResult(items)
    }
}
