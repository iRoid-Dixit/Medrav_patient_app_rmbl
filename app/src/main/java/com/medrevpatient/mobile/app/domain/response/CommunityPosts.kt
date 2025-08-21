package com.medrevpatient.mobile.app.domain.response


import android.net.Uri
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CommunityPosts(
    @SerializedName("comments")
    var comments: Int? = null,
    @SerializedName("content")
    val content: String? = null,
    @SerializedName("createdAt")
    val createdAt: Long? = null,
    @SerializedName("_id")
    val id: String? = null,
    @SerializedName("images")
    val images: ArrayList<PostImages> = arrayListOf(),
    @SerializedName("likes")
    var likes: Int? = null,
    @SerializedName("user")
    val user: User? = null,
    @SerializedName("isVerified")
    val isVerified: Boolean = false,
    @SerializedName("isLike")
    var isLike: Boolean = false,
) : Serializable

data class PostImages(
    @SerializedName("_id")
    val id: String? = null,
    @SerializedName("url")
    val url: String? = null
) : Serializable

data class LocalPostImages(
    @SerializedName("_id")
    val id: String? = null,
    @SerializedName("url")
    val url: Uri? = null
)

data class User(
    @SerializedName("fullName")
    val fullName: String? = null,
    @SerializedName("_id")
    val id: String? = null,
    @SerializedName("profileImage")
    val profileImage: String? = null
) : Serializable

data class Comments(
    @SerializedName("createdAt")
    val createdAt: Long? = null,
    @SerializedName("_id")
    val id: String? = null,
    @SerializedName("text")
    val text: String? = null,
    @SerializedName("user")
    val user: User? = null
) : Serializable
