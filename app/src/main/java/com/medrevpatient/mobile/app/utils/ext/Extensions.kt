package com.medrevpatient.mobile.app.utils.ext

import android.content.Context
import android.content.ContextWrapper
import androidx.activity.ComponentActivity
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.SavedStateHandle
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import okhttp3.ResponseBody
import org.json.JSONObject
import java.util.Locale
import kotlin.experimental.ExperimentalTypeInference


fun String.extractNumberString(): String? {
    return "\\d+".toRegex().find(this)?.value
}


fun String.removeNumberString(): String? {
    return "\\D+".toRegex().find(this)?.value
}

inline fun <reified T> T.toJsonString(): String? {
    return Gson().toJson(this)
}

inline fun <reified T> String.fromJsonString(): T? {
    return try {
        val type = object : TypeToken<T>() {}.type
        Gson().fromJson(this, type)
    } catch (e: Exception) {
        null
    }
}


fun ResponseBody?.extractError(): String {
    return try {
        if (this != null) JSONObject(
            this.charStream().readText()
        ).getString("message") else "something went wrong!"
    } catch (e: Exception) {
        this?.string() ?: "something went wrong!"
    }
}


fun Context.requireActivity(): ComponentActivity = when (this) {
    is ComponentActivity -> this
    is ContextWrapper -> baseContext.requireActivity()
    else -> error("No Activity Found")
}


fun String.makeUpperCase(b: Boolean): String {
    return if (b) this.uppercase(Locale.getDefault()) else this
}

//---------------------Flow Extension-------------------
/**
 * Invokes [stateIn] with the recommended settings
 */
fun <T> Flow<T>.stateInDefault(coroutineScope: CoroutineScope, initialValue: T): StateFlow<T> =
    this.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000L),
        initialValue = initialValue,
    )

/**
 * Invokes [shareIn] with the recommended settings
 */
fun <T> Flow<T>.shareInDefaults(coroutineScope: CoroutineScope): SharedFlow<T> = this.shareIn(
    coroutineScope,
    SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000L),
    replay = 1
)

/**
 * Applies a [map] transform to a flow, only emitting new values when the value is distinct.
 */
@OptIn(ExperimentalTypeInference::class)
inline fun <T, R> Flow<T>.mapDistinct(@BuilderInference crossinline transform: suspend (value: T) -> R) =
    this.map(transform).distinctUntilChanged()

//----------------------SaveStateHandler--------------

@VisibleForTesting
fun createSaveStateErrorMessage(key: String) = "Missing SavedState value for Key: $key"

fun SavedStateHandle.requireString(key: String): String =
    requireNotNull(get<String>(key)) { createSaveStateErrorMessage(key) }

fun SavedStateHandle.requireInt(key: String): Int =
    requireNotNull(get<Int>(key)) { createSaveStateErrorMessage(key) }


inline fun Boolean.ifTrue(block: (Boolean) -> Unit) {
    if (this) {
        block(true)
    }
}
