package com.griotlegacy.mobile.app.data.source.remote.repository

import com.griotlegacy.mobile.app.data.source.remote.EndPoints
import com.griotlegacy.mobile.app.model.domain.request.authReq.RefreshTokenReq
import com.griotlegacy.mobile.app.model.domain.response.ApiResponse
import com.griotlegacy.mobile.app.model.domain.response.auth.Token
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface TokenApi {
    @POST(EndPoints.Auth.REFRESH_TOKEN)
    suspend fun getRefreshToken(
        @Body refreshTokenReq: RefreshTokenReq
    ): Response<ApiResponse<Token>>
}