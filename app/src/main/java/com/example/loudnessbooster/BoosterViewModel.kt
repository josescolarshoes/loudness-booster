package com.example.loudnessbooster

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.IBinder
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class BoosterViewModel(app: Application) : AndroidViewModel(app) {

    private var service: BoosterService? = null
    private var bound = false

    private val _boostPercent = MutableStateFlow(0f)
    val boostPercent: StateFlow<Float> = _boostPercent

    private val _isEnabled = MutableStateFlow(false)
    val isEnabled: StateFlow<Boolean> = _isEnabled

    val gainMb: Float get() = _boostPercent.value * 15f

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, binder: IBinder) {
            service = (binder as BoosterService.LocalBinder).getService()
            bound = true
            _isEnabled.value = service?.isRunning ?: false
            _boostPercent.value = service?.currentPercent ?: 0f
        }

        override fun onServiceDisconnected(name: ComponentName) {
            bound = false
            service = null
        }
    }

    fun bindService(context: Context) {
        val intent = Intent(context, BoosterService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    fun unbindService(context: Context) {
        if (bound) {
            context.unbindService(connection)
            bound = false
        }
    }

    fun setBoost(percent: Float) {
        val clamped = percent.coerceIn(0f, 100f)
        _boostPercent.value = clamped
        service?.setGain(clamped)
    }

    fun toggleEnabled() {
        val next = !_isEnabled.value
        _isEnabled.value = next
        service?.setEnabled(next)
    }

    fun setBandLevel(band: Short, gainMb: Int) {
        service?.setBandLevel(band, gainMb)
    }
}
