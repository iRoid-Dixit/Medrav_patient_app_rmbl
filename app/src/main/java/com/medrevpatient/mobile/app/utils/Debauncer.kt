package com.medrevpatient.mobile.app.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class Debouncing @Inject constructor() {

    private var job: Job? = null

    fun debounce(
        delay: Long = 300,
        action: () -> Unit
    ) {
        job?.cancel()
        job = CoroutineScope(Dispatchers.Main).launch {
            delay(delay)
            action()
        }
    }
}
