package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.util.Log
import com.example.R

class AvoAudioManager(private val context: Context) {
    private var soundPool: SoundPool? = null
    private var musicPlayer: MediaPlayer? = null

    // Sound IDs
    private var sfxSparkCollect: Int = 0
    private var sfxSparkDrop: Int = 0
    private var sfxAvoWalk: Int = 0
    private var sfxAvoSqueak: Int = 0
    private var sfxLevelComplete: Int = 0
    private var sfxCelebration: Int = 0
    private var sfxUiButton: Int = 0

    var isSoundEnabled: Boolean = true
    var isMusicEnabled: Boolean = true
        set(value) {
            field = value
            if (value) {
                resumeMusic()
            } else {
                pauseMusic()
            }
        }

    private var lastWalkSoundTime = 0L

    init {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(8)
            .setAudioAttributes(audioAttributes)
            .build()

        loadSounds()
        initMusicPlayer()
    }

    private fun loadSounds() {
        try {
            soundPool?.let { pool ->
                sfxSparkCollect = pool.load(context, R.raw.sfx_spark_collect, 1)
                sfxSparkDrop = pool.load(context, R.raw.sfx_spark_drop, 1)
                sfxAvoWalk = pool.load(context, R.raw.sfx_avo_walk, 1)
                sfxAvoSqueak = pool.load(context, R.raw.sfx_avo_squeak, 1)
                sfxLevelComplete = pool.load(context, R.raw.sfx_level_complete, 1)
                sfxCelebration = pool.load(context, R.raw.sfx_celebration, 1)
                sfxUiButton = pool.load(context, R.raw.sfx_ui_button, 1)
            }
        } catch (e: Exception) {
            Log.e("AvoAudioManager", "Error loading sound effects", e)
        }
    }

    private fun initMusicPlayer() {
        try {
            musicPlayer = MediaPlayer.create(context, R.raw.avo_theme_music).apply {
                isLooping = true
                setVolume(0.45f, 0.45f)
            }
            if (isMusicEnabled) {
                musicPlayer?.start()
            }
        } catch (e: Exception) {
            Log.e("AvoAudioManager", "Error initializing background music", e)
        }
    }

    fun playSparkCollect() {
        if (!isSoundEnabled) return
        soundPool?.play(sfxSparkCollect, 0.9f, 0.9f, 1, 0, 1.0f)
    }

    fun playSparkDrop() {
        if (!isSoundEnabled) return
        soundPool?.play(sfxSparkDrop, 0.8f, 0.8f, 1, 0, 1.0f)
    }

    fun playAvoWalk() {
        if (!isSoundEnabled) return
        val now = System.currentTimeMillis()
        if (now - lastWalkSoundTime > 320) {
            lastWalkSoundTime = now
            soundPool?.play(sfxAvoWalk, 0.5f, 0.5f, 0, 0, (0.95f + Math.random().toFloat() * 0.1f))
        }
    }

    fun playAvoSqueak() {
        if (!isSoundEnabled) return
        soundPool?.play(sfxAvoSqueak, 0.85f, 0.85f, 1, 0, (0.95f + Math.random().toFloat() * 0.15f))
    }

    fun playLevelComplete() {
        if (!isSoundEnabled) return
        soundPool?.play(sfxLevelComplete, 1.0f, 1.0f, 2, 0, 1.0f)
    }

    fun playCelebration() {
        if (!isSoundEnabled) return
        soundPool?.play(sfxCelebration, 1.0f, 1.0f, 2, 0, 1.0f)
    }

    fun playUiButton() {
        if (!isSoundEnabled) return
        soundPool?.play(sfxUiButton, 0.6f, 0.6f, 0, 0, 1.0f)
    }

    fun pauseMusic() {
        try {
            if (musicPlayer?.isPlaying == true) {
                musicPlayer?.pause()
            }
        } catch (e: Exception) {
            Log.e("AvoAudioManager", "Error pausing music", e)
        }
    }

    fun resumeMusic() {
        try {
            if (isMusicEnabled && musicPlayer?.isPlaying == false) {
                musicPlayer?.start()
            }
        } catch (e: Exception) {
            Log.e("AvoAudioManager", "Error resuming music", e)
        }
    }

    fun release() {
        soundPool?.release()
        soundPool = null
        musicPlayer?.release()
        musicPlayer = null
    }
}
