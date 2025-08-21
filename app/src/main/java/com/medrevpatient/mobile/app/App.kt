package com.medrevpatient.mobile.app

import android.app.Application
import android.content.Intent
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.medrevpatient.mobile.app.data.source.local.datastore.LocalManager
import com.medrevpatient.mobile.app.utils.AppUtils
import com.medrevpatient.mobile.app.ux.startup.StartupActivity
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import kotlin.system.exitProcess

@HiltAndroidApp
class App : Application(), Configuration.Provider {


    @Inject
    lateinit var localManager: LocalManager

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    var deviceId: String? = null
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        Timber.plant(Timber.DebugTree())
        deviceId = AppUtils.getDeviceId(this)
        Timber.d("Device ID:$deviceId")
    }

    fun unAuthorizedAction() {
        CoroutineScope(Dispatchers.IO).launch {
            localManager.clearStorage()
            withContext(Dispatchers.Main) {
                restartApp()
            }
        }
    }

    private fun restartApp() {
        Intent(this, StartupActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or
                    Intent.FLAG_ACTIVITY_NEW_TASK
            putExtra("reset", true)
            startActivity(this)
            exitProcess(0)
        }
    }

    companion object {
        var instance: App? = null
            private set
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

}
