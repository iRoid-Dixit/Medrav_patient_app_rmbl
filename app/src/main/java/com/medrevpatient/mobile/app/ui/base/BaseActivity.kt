package com.medrevpatient.mobile.app.ui.base

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.core.view.WindowCompat
import com.medrevpatient.mobile.app.R

open class BaseActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTheme(R.style.Theme_MedrevPatientApp)
        // Hook for additional startup logic in child activities
        onStartup()
    }

    // Optional method for activities to implement startup operations
    open fun onStartup() {
        // Default startup operation if needed
    }

    open fun fitSystemWindow(fitToSystem: Boolean) {
        WindowCompat.setDecorFitsSystemWindows(window, fitToSystem)
    }
}