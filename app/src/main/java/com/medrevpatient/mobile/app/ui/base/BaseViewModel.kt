package com.medrevpatient.mobile.app.ui.base

import android.content.Context
import androidx.lifecycle.ViewModel
import com.medrevpatient.mobile.app.navigation.ViewModelNav
import com.medrevpatient.mobile.app.navigation.ViewModelNavImpl
import com.medrevpatient.mobile.app.utils.AppUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

open class BaseViewModel : ViewModel(), ViewModelNav by ViewModelNavImpl() {
    val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun Toast(context: Context, message: String, duration: Int = 0) {
        return AppUtils.Toast(context, message, duration).show()
    }
}