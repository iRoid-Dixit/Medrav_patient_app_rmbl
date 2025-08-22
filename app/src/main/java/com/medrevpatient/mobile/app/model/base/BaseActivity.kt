package com.medrevpatient.mobile.app.model.base

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModel
import com.app.musicplayerdemo.service.TAG
import com.medrevpatient.mobile.app.App
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.data.source.Constants
import com.medrevpatient.mobile.app.utils.AppUtils.isScreenLocked
import com.medrevpatient.mobile.app.utils.socket.OnSocketEventsListener
import com.medrevpatient.mobile.app.utils.socket.SocketClass
import com.medrevpatient.mobile.app.utils.socket.SocketClass.loggerE
import io.socket.emitter.Emitter
import org.json.JSONObject

abstract class BaseActivity<VM : ViewModel> : ComponentActivity() {
    // Generic ViewModel for any activity that extends BaseActivity
    protected abstract val viewModel: VM
    private var onSocketEventsListener: OnSocketEventsListener? = null
    private var accessToken: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        accessToken = App.instance?.getAccessToken() ?: ""
        setTheme(R.style.AppTheme)
        fitSystemWindow(true)
        // Hook for additional startup logic in child activities
        onStartup()
    }
    override fun onResume() {
        super.onResume()
        SocketClass.connectSocket(accessToken)
        Log.d(TAG, "onResume: $accessToken")
        if (!isScreenLocked()) {
            initSocketListener()
        }
    }
    override fun onPause() {
        super.onPause()
        if (!isScreenLocked()) {
            destroySocketListeners()
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        destroySocketListeners()
    }
    private fun initSocketListener() {
        SocketClass.getSocket(accessToken)?.let {
            Log.d(TAG, "initSocketListener: $accessToken")
            Log.e("TAG", "initSocketListener: connect socket listener ")
            it.on(io.socket.client.Socket.EVENT_CONNECT, onConnected)
            it.on(io.socket.client.Socket.EVENT_CONNECT, onConnected)
            it.on(io.socket.client.Socket.EVENT_CONNECT_ERROR, onConnectError)
            it.on(io.socket.client.Socket.EVENT_DISCONNECT, onDisconnected)
            it.on(Constants.Socket.ROOM_CONNECT, onRoomConnected)
            it.on(Constants.Socket.NEW_MESSAGE, onNewMessage)
            it.on(Constants.Socket.STATUS_ONLINE, updateStatusToOnline)
        }
    }
    private fun destroySocketListeners() {
        SocketClass.getSocket(accessToken)?.let {
            loggerE("destroy socket listener")
            Log.e(TAG, "destroySocketListeners: ")
            it.off(io.socket.client.Socket.EVENT_CONNECT, onConnected)
            it.off(io.socket.client.Socket.EVENT_CONNECT_ERROR, onConnectError)
            it.off(io.socket.client.Socket.EVENT_DISCONNECT, onDisconnected)
            it.off(Constants.Socket.ROOM_CONNECT, onRoomConnected)
            it.off(Constants.Socket.NEW_MESSAGE, onNewMessage)
        }
    }
    fun initSocketListener(socketEventsListener: OnSocketEventsListener) {
        this.onSocketEventsListener = socketEventsListener
    }
    //listener object
    private val onConnected = Emitter.Listener {
        SocketClass.getSocket(accessToken)?.let { it1 ->
            Log.e("TAG", "initSocketListener>>>>>: ${it1.connected()}")
            //            if (it1.connected()) {
            //                it1.emit(Constants.Socket.UPDATE_STATUS_TO_ONLINE, updateStatusToOnline())
            //            }
        }
    }
    private val onConnectError = Emitter.Listener { args ->
        loggerE("onConnectError: ${args[0]}")
    }
    private val onDisconnected = Emitter.Listener { args ->
        Log.e("TAG", "disConnectSocket: disconnect")
        Log.d("TAG", "socketDisconnect${args[0]}: ")
        SocketClass.getSocket(accessToken)?.let { it1 ->
            Log.e("TAG", "disConnectSocket: disconnect: ${it1.connected()}")
            it1.disconnect()
        }
    }
    private val onRoomConnected = Emitter.Listener { args ->
        loggerE("Socket onRoomCreated")
        val roomId: String? = when (val arg = args.getOrNull(0)) {
            is String -> arg
            is Int -> arg.toString()
            is Double -> arg.toInt().toString()
            is JSONObject -> arg.optString("roomId", null)
            else -> {
                loggerE("Unexpected roomId type: ${arg?.javaClass?.name}, value: $arg")
                null
            }
        }

        if (roomId != null) {
            Log.e(ContentValues.TAG, "RoomId: $roomId")
            onSocketEventsListener?.onRoomConnected(roomId)
        } else {
            Log.e(ContentValues.TAG, "Invalid RoomId type")
        }
    }
    private var onNewMessage = Emitter.Listener { args ->
        runOnUiThread {
            val data = args[0] as JSONObject
            loggerE("Socket onNewMessage run:  $data")
            onSocketEventsListener?.onNewMessageReceived(data)
        }
    }
    private var updateStatusToOnline = Emitter.Listener { args ->
        runOnUiThread {
            val data = args[0] as JSONObject
            loggerE("Socket UpdateStatusToOnline run:  $data")
            onSocketEventsListener?.onUpdateStatusToOnline(data)
        }
    }
    private val onRoomDisconnected = Emitter.Listener { args ->
        runOnUiThread {
            loggerE("Socket room disconnected")
        }
    }
    /*private fun updateStatusToOnline(): JSONObject {
        val statusObject = JSONObject()
        //        statusObject.put("senderId", userId)
        Log.e("updateStatusToOnline ", statusObject.toString())
        return statusObject
    }*/

    // Optional method for activities to implement startup operations
    open fun onStartup() {
        // Default startup operation if needed
    }

    open fun fitSystemWindow(fitToSystem: Boolean) {
        WindowCompat.setDecorFitsSystemWindows(window, fitToSystem)
    }
}