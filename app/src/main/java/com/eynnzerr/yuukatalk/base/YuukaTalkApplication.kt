package com.eynnzerr.yuukatalk.base

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Intent
import com.eynnzerr.yuukatalk.CrashActivity
import com.eynnzerr.yuukatalk.utils.VersionUtils
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

@HiltAndroidApp
class YuukaTalkApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

        Thread.setDefaultUncaughtExceptionHandler { _, e ->
            e.printStackTrace()
            startActivity(
                Intent(this, CrashActivity::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .apply {
                        putExtra("msg", "version: ${VersionUtils.getLocalVersion()}\n" + e.stackTraceToString())
                    }
            )
        }
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
        lateinit var applicationScope: CoroutineScope
    }
}