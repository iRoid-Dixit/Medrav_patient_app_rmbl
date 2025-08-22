package com.griotlegacy.mobile.app.model.domain.response

import com.google.gson.annotations.SerializedName

/**
 * Use for all normal response
 * **/
open class ApiResponse<Any>(
    @SerializedName("data")
    var data: Any? = null,

    @SerializedName("message")
    val message: String = "",

    @SerializedName("error")
    val errorMsg: String = "",

    @SerializedName("errors")
    var apiErrors: ApiErrors? = null
)

data class ApiErrors(
    val email: ArrayList<String> = ArrayList(),
    val first_name: ArrayList<String> = ArrayList(),
    val last_name: ArrayList<String> = ArrayList(),
    val password: ArrayList<String> = ArrayList(),
    val confirm_password: ArrayList<String> = ArrayList(),
    val phone_number: ArrayList<String> = ArrayList(),
    val role: ArrayList<String> = ArrayList(),
    val username: ArrayList<String> = ArrayList()
)

/**
 * Use for pagination or for listing type response
 * **/
open class ApiResponseNew<Any>(
    @SerializedName("data") val data: List<Any> = listOf(),

    @SerializedName("message")
    val message: String = "",

    @SerializedName("different_licensor")
    val differentLicensor: Int = -1,

    @SerializedName("error")
    val errorMsg: String = "",

    @SerializedName("errors")
    var apiErrors: ApiErrors? = null,

    @SerializedName("links")
    val links: Links,

    @SerializedName("meta")
    val meta: Meta,
)

data class Links(
    @SerializedName("first") val first: String,
    @SerializedName("last") val last: String,
    @SerializedName("next") val next: String,
    @SerializedName("prev") val prev: String
)

data class Meta(
    @SerializedName("current_page") val current_page: Int,
    @SerializedName("from") val from: Int,
    @SerializedName("last_page") val last_page: Int,
    @SerializedName("links") val links: List<Link>,
    @SerializedName("path") val path: String,
    @SerializedName("per_page") val per_page: String,
    @SerializedName("to") val to: Int,
    @SerializedName("total") val total: Int
)

data class Link(
    @SerializedName("active") val active: Boolean,
    @SerializedName("label") val label: String,
    @SerializedName("url") val url: String
)

