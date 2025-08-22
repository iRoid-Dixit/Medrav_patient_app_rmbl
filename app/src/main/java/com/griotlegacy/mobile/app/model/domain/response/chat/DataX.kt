package com.griotlegacy.mobile.app.model.domain.response.chat

data class DataX(
    val _id: String,
    val createdAt: Long,
    val groupId: String,
    val message: String,
    val name: String,
    val profileImage: String,
    val senderId: String
)