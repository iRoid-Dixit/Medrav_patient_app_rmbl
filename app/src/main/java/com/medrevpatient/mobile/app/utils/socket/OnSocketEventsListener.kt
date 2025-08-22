package com.medrevpatient.mobile.app.utils.socket

import org.json.JSONObject

interface OnSocketEventsListener {
    //    fun onRoomConnected():String
    fun onRoomConnected(roomID: String) {}
    fun onNewMessageReceived(data: JSONObject) {}
    fun onUpdateStatusToOnline(data: JSONObject) {}
}