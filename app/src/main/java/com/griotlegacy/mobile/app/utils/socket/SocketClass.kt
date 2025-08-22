package com.griotlegacy.mobile.app.utils.socket

import android.util.Log
import co.touchlab.kermit.Logger
import com.griotlegacy.mobile.app.data.source.Constants
import io.socket.client.IO

object SocketClass {
    private const val TAG = "SocketClass"
    private var mSocket: io.socket.client.Socket? = null

    private fun initSocket(token: String) {
        try {
            if (mSocket == null) {
                val options = IO.Options().apply {
                    forceNew = true
                    reconnection = true
                    extraHeaders = mapOf("Authorization" to listOf(token))
                }
                mSocket = IO.socket(Constants.Socket.SOCKET_URL, options)
            }
        } catch (e: Exception) {
            loggerE("initSocket: ${e.localizedMessage}")
        }
    }

    fun connectSocket(token: String) {
        try {
            if (token != null) {
                initSocket(token)
                mSocket?.let {
                    loggerE("socket status: ${it.connected()}")
                    if (!it.connected()) {
                        loggerE("socket connected")
                        it.connect()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getSocket(token: String): io.socket.client.Socket? {
        if (mSocket == null) {
            initSocket(token)
        }
        return mSocket
    }

    fun disConnectSocket() {
        mSocket?.let {
            if (it.connected()) {
                Logger.e("Socket disconnected called")
                it.disconnect()
            }
        }
    }

    fun loggerE(msg: String) {
        Log.e(TAG, msg)
    }

    /*
    *
    * Socket url: http://134.209.145.75:3020

On socket connection
Update user's online status
emit method: <b>UpdateStatusToOnline</b>
Request parameter: {senderId}
Response Parameters: { senderId, isOnline }
--> Server will emit with method <b>statusOnline</b> with requested data

Android : here we called below method
                 Emit method :  UpdateStatusToOnline //here we request something to server.
                 Listen [listener on/off ] method :  statusOnline //in this we get callback of our request.

Create Room
emit method: <b>createRoom</b>
Request parameter: { senderId, receiverId, chatType }
Response Parameters: roomId
--> Server will emit with method <b>roomConnected</b> with requested data

Android : here we called below method
                 Emit method :  createRoom //here we request something to server.
                 Listen [listener on/off ] method :  roomConnected //in this we get callback of our request.

Send Message
emit method: <b>sendMessage</b>
Request parameter: { message, type: 1, senderId, receiverId, roomId, chatType }
Response Parameters: { message, type: 1, senderId, receiverId, roomId, chatId, chatType }
--> Server will emit with method <b>newMessage</b> with requested data if opponent user is connected
Message types (1= Normal Message, 2=image 3= Audio 4=>Zoom, etc.)
=> On newMessage
--> Acknowledgement to server back with method <b>ReadMessage</b>

Android : here we called below method
                 Emit method :  sendMessage //here we request sendMessage to server,
                                ReadMessage // After getting success response in new message we acknowledgement to server like message read.
                 Listen [listener on/off ] method :  newMessage //in this we get callback of our request.

Get opponent user's online status
emit method: getOnlineStatus
Parameters: { receiverId: receiverId }
--> Socket will Acknowledgement back with statusOnline method with data isOnline status and lastSeen status

Android : here we called below method
                 Emit method :  getOnlineStatus //here we request something to server.
                 Listen [listener on/off ] method :  statusOnline //in this we get callback of our request.
                 *
                 *
                 *
                 * */
}