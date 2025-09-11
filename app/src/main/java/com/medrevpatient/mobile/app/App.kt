package com.medrevpatient.mobile.app

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.util.Log
import co.touchlab.kermit.Logger
import com.medrevpatient.mobile.app.data.source.Constants
import com.medrevpatient.mobile.app.data.source.local.datastore.AppPreferenceDataStore
import com.medrevpatient.mobile.app.ux.startup.StartupActivity


import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.runBlocking

@HiltAndroidApp
class App : Application(), Application.ActivityLifecycleCallbacks {

    private val appPreferenceDataStore: AppPreferenceDataStore? = null
    private var accessToken: String = ""

    companion object {
        var instance: App? = null
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        getAccessToken()
        Log.d("TAG", "getAccessToken: ${getAccessToken()}")
    }





    fun getAccessToken(): String {
        try {
            runBlocking {
                val token = appPreferenceDataStore?.getUserAuthData()?.accessToken ?: ""
                accessToken = token
            }
        } catch (e: Exception) {
            Logger.e("Exception getting access token: ${e.message}")
        }
        return accessToken
    }

    fun restartApp() {
        val intent = Intent(this, StartupActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra(Constants.BundleKey.RESET, Constants.BundleKey.RESTART_APP)
        startActivity(intent)
    }

    override fun onActivityCreated(p0: Activity, p1: Bundle?) {
        // Implementation not needed for this use case
    }

    override fun onActivityStarted(p0: Activity) {
        // Implementation not needed for this use case
    }

    override fun onActivityResumed(p0: Activity) {
        // Implementation not needed for this use case
    }

    override fun onActivityPaused(p0: Activity) {
        // Implementation not needed for this use case
    }

    override fun onActivityStopped(p0: Activity) {
        // Implementation not needed for this use case
    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
        // Implementation not needed for this use case
    }

    override fun onActivityDestroyed(p0: Activity) {
        // Implementation not needed for this use case
    }
}
