package com.griotlegacy.mobile.app.model.domain.response.searchPeople


import com.google.gson.annotations.SerializedName

data class SearchPeopleResponse(
    @SerializedName("_id")
    val id: String? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("isOnline")
    val isOnline: Boolean? = null,
    @SerializedName("lastSeen")
    val lastSeen: Any? = null,
    @SerializedName("profileImage")
    val profileImage: Any? = null,
    @SerializedName("isAdmin")
    val isAdmin: Any? = null


)