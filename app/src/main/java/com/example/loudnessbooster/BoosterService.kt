package com.example.loudnessbooster

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.audiofx.Equalizer
import android.media.audiofx.LoudnessEnhancer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

class BoosterService : Service() {

    inner class LocalBinder : Binder() {
        fun getService() = this@BoosterService
    }

    private val binder = LocalBinder()
    private var enhancer: LoudnessEnhancer? = null
    private var equalizer: Equalizer? = null

    var isRunning = false
        private set
    var currentPercent = 0f
        private set

    private val MAX_GAIN_MB = 1500

    override fun onCreate() {
        super.onCreate()
        startForegroundWithNotification()
        initEnhancer()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    private fun initEnhancer() {
        try {
            enhancer = LoudnessEnhancer(0).apply {
                setTargetGain(0)
                enabled = false
            }
            equalizer = Equalizer(0, 0).apply {
                enabled = true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setGain(percent: Float) {
        currentPercent = percent.coerceIn(0f, 100f)
        val gainMb = (currentPercent / 100f * MAX_GAIN_MB).toInt()
        try {
            enhancer?.setTargetGain(gainMb)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setBandLevel(band: Short, gainMb: Int) {
        try {
            equalizer?.setBandLevel(band, (gainMb * 100).toShort())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getNumberOfBands(): Short = equalizer?.numberOfBands ?: 0

    fun setEnabled(enabled: Boolean) {
        isRunning = enabled
        try {
            enhancer?.enabled = enabled
        } catch (e: Exception) {
            e.printStackTrace()
        }
        updateNotification(enabled)
    }

    override fun onBind(intent: Intent): IBinder = binder

    override fun onDestroy() {
        try {
            enhancer?.release()
            equalizer?.release()
        } catch (_: Exception) {
        }
        super.onDestroy()
    }

    private fun startForegroundWithNotification() {
        val channelId = "booster_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Loudness Booster",
                NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }
        startForeground(1, buildNotification(channelId, false))
    }

    private fun buildNotification(channelId: String, active: Boolean): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pi = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Loudness Booster")
            .setContentText(if (active) "Amplificacion activa" else "En espera")
            .setSmallIcon(android.R.drawable.ic_lock_silent_mode_off)
            .setContentIntent(pi)
            .setOngoing(true)
            .build()
    }

    private fun updateNotification(active: Boolean) {
        val mgr = getSystemService(NotificationManager::class.java)
        mgr.notify(1, buildNotification("booster_channel", active))
    }
}
