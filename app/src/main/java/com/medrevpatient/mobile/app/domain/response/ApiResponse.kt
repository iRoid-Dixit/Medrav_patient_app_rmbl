package com.medrevpatient.mobile.app.domain.response

import com.google.gson.annotations.SerializedName

/**
 * Use for all normal response
 * **/
open class ApiResponse<T>(
    @SerializedName("data")
    var data: T? = null,

    @SerializedName("message")
    val message: String = "",

    @SerializedName("status")
    val status: Int = 0,

    @SerializedName("error")
    val errorMsg: String = "",

    @SerializedName("errors")
    var apiErrors: ApiErrors? = null
)

open class ApiListResponse<T>(
    @SerializedName("data")
    var data: List<T> = emptyList(),

    @SerializedName("message")
    val message: String = "",

    @SerializedName("status")
    val status: Int = 0,
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
open class ApiResponseNew<T>(
    @SerializedName("data")
    val data: List<T> = emptyList(),
    @SerializedName("message")
    val message: String = "",
    @SerializedName("meta")
    val meta: Meta = Meta(),
    @SerializedName("status")
    val status: Int = 0,
    @SerializedName("filterTags")
    val filterTags: List<String> = emptyList()
)


data class Meta(
    @SerializedName("currentPage")
    val currentPage: Int = 0,
    @SerializedName("lastPage")
    val lastPage: Int = 0,
    @SerializedName("perPage")
    val perPage: Int = 0,
    @SerializedName("total")
    val total: Int = 0
)
