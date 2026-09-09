package com.example.ui

import android.media.MediaPlayer
import android.net.Uri
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.VideoView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun AvoVideoPlayer(
    videoResId: Int,
    modifier: Modifier = Modifier,
    isLooping: Boolean = true,
    isMuted: Boolean = false,
    isPlaying: Boolean = true,
    targetTimeMs: Int? = null,
    onVideoPrepared: (durationMs: Int) -> Unit = {},
    onVideoCompleted: () -> Unit = {}
) {
    val context = LocalContext.current
    var mediaPlayerRef by remember { mutableStateOf<MediaPlayer?>(null) }
    var videoViewRef by remember { mutableStateOf<VideoView?>(null) }

    val videoUri = remember(videoResId) {
        Uri.parse("android.resource://${context.packageName}/$videoResId")
    }

    DisposableEffect(videoUri) {
        onDispose {
            try {
                videoViewRef?.stopPlayback()
                mediaPlayerRef?.release()
                mediaPlayerRef = null
            } catch (_: Exception) {}
        }
    }

    // Dynamic screen switching / camera cut seeking based on proximity to area
    LaunchedEffect(targetTimeMs, videoViewRef) {
        targetTimeMs?.let { timeMs ->
            videoViewRef?.let { vv ->
                try {
                    vv.seekTo(timeMs)
                    if (isPlaying && !vv.isPlaying) {
                        vv.start()
                    }
                } catch (_: Exception) {}
            }
        }
    }

    LaunchedEffect(isMuted, mediaPlayerRef) {
        mediaPlayerRef?.let { mp ->
            try {
                val vol = if (isMuted) 0f else 1f
                mp.setVolume(vol, vol)
            } catch (_: Exception) {}
        }
    }

    LaunchedEffect(isPlaying, videoViewRef) {
        videoViewRef?.let { vv ->
            try {
                if (isPlaying) {
                    if (!vv.isPlaying) vv.start()
                } else {
                    if (vv.isPlaying) vv.pause()
                }
            } catch (_: Exception) {}
        }
    }

    Box(modifier = modifier.fillMaxSize().background(Color.Black)) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                VideoView(ctx).apply {
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    setVideoURI(videoUri)
                    setOnPreparedListener { mp ->
                        mediaPlayerRef = mp
                        mp.isLooping = isLooping
                        val vol = if (isMuted) 0f else 1f
                        mp.setVolume(vol, vol)
                        // Preserve original 25 FPS video scale
                        mp.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING)
                        onVideoPrepared(mp.duration)
                        if (isPlaying) {
                            start()
                        }
                    }
                    setOnCompletionListener {
                        onVideoCompleted()
                    }
                    videoViewRef = this
                }
            },
            update = { vv ->
                videoViewRef = vv
            }
        )
    }
}
