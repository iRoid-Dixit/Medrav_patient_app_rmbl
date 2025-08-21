package com.medrevpatient.mobile.app.utils.connection

import kotlinx.coroutines.flow.Flow


interface NetworkMonitor {
    val isOnline: Flow<Boolean>
}