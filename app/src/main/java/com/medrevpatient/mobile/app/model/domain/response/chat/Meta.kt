package com.medrevpatient.mobile.app.model.domain.response.chat

data class Meta(
    val currentPage: Int,
    val lastPage: Int,
    val perPage: Int,
    val total: Int
)