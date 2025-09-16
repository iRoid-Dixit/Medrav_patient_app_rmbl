package com.medrevpatient.mobile.app.data.source.remote.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.medrevpatient.mobile.app.data.source.remote.helper.NetworkResult
import com.medrevpatient.mobile.app.data.source.remote.paging.AddGroupMemberPagingSource
import com.medrevpatient.mobile.app.data.source.remote.paging.AdvertisementPagingSource
import com.medrevpatient.mobile.app.data.source.remote.paging.AllPeoplePagingSource
import com.medrevpatient.mobile.app.data.source.remote.paging.ApiCallback
import com.medrevpatient.mobile.app.data.source.remote.paging.AppointmentPagingSource
import com.medrevpatient.mobile.app.data.source.remote.paging.BlockMemberPagingSource
import com.medrevpatient.mobile.app.data.source.remote.paging.CommentListPagingSource
import com.medrevpatient.mobile.app.data.source.remote.paging.FAQQuestionPagingSource
import com.medrevpatient.mobile.app.data.source.remote.paging.GroupMemberPagingSource
import com.medrevpatient.mobile.app.data.source.remote.paging.HomeAdvertisementPagingSource
import com.medrevpatient.mobile.app.data.source.remote.paging.InnerCircleTribePagingSource
import com.medrevpatient.mobile.app.data.source.remote.paging.LegacyPostPagingSource
import com.medrevpatient.mobile.app.data.source.remote.paging.LegacyReflectionQuestionPagingSource
import com.medrevpatient.mobile.app.data.source.remote.paging.MainVillagePagingSource
import com.medrevpatient.mobile.app.data.source.remote.paging.MessagePagingSource
import com.medrevpatient.mobile.app.data.source.remote.paging.NotificationPagingSource
import com.medrevpatient.mobile.app.data.source.remote.paging.TribeMembersPagingSource
import com.medrevpatient.mobile.app.data.source.remote.paging.TribePagingSource
import com.medrevpatient.mobile.app.model.domain.request.CheckSubscriptionReq
import com.medrevpatient.mobile.app.model.domain.request.FeedbackReq
import com.medrevpatient.mobile.app.model.domain.request.SubscriptionInfoReq
import com.medrevpatient.mobile.app.model.domain.request.TokenStoreReq
import com.medrevpatient.mobile.app.model.domain.request.addMember.AddMemberRequest
import com.medrevpatient.mobile.app.model.domain.request.addMember.GroupMemberRequest
import com.medrevpatient.mobile.app.model.domain.request.appointment.AvailableSlotsRequest
import com.medrevpatient.mobile.app.model.domain.request.authReq.AppUpdateRequest
import com.medrevpatient.mobile.app.model.domain.request.authReq.ForgetPasswordReq
import com.medrevpatient.mobile.app.model.domain.request.authReq.ResetPasswordReq
import com.medrevpatient.mobile.app.model.domain.request.authReq.ResendOTPReq
import com.medrevpatient.mobile.app.model.domain.request.authReq.LogInRequest
import com.medrevpatient.mobile.app.model.domain.request.authReq.SignUpReq
import com.medrevpatient.mobile.app.model.domain.request.authReq.UpdateProfileReq
import com.medrevpatient.mobile.app.model.domain.request.authReq.VerifyOTPReq
import com.medrevpatient.mobile.app.model.domain.request.bmi.BmiCalculateRequest
import com.medrevpatient.mobile.app.model.domain.request.imagePostionReq.ImagePositionReq
import com.medrevpatient.mobile.app.model.domain.request.sideEffect.SideEffectAnswerRequest
import com.medrevpatient.mobile.app.model.domain.request.dietChallenge.DietChallengeSubmitRequest
import com.medrevpatient.mobile.app.model.domain.request.mainReq.AddCommentReq
import com.medrevpatient.mobile.app.model.domain.request.mainReq.ChangePasswordReq
import com.medrevpatient.mobile.app.model.domain.request.mainReq.ContactUsReq
import com.medrevpatient.mobile.app.model.domain.request.mainReq.deletePost.SinglePostReq
import com.medrevpatient.mobile.app.model.domain.request.report.ReportUserPostReq
import com.medrevpatient.mobile.app.model.domain.response.ApiResponse
import com.medrevpatient.mobile.app.model.domain.response.ApiResponseNew
import com.medrevpatient.mobile.app.model.domain.response.TermsResponse
import com.medrevpatient.mobile.app.model.domain.response.advertisement.AdvertisementResponse
import com.medrevpatient.mobile.app.model.domain.response.appointment.AppointmentResponse
import com.medrevpatient.mobile.app.model.domain.response.appointment.AvailableSlotsData
import com.medrevpatient.mobile.app.model.domain.response.auth.AppUpdateResponse
import com.medrevpatient.mobile.app.model.domain.response.auth.UserAuthResponse
import com.medrevpatient.mobile.app.model.domain.response.bmi.BmiCalculateResponse
import com.medrevpatient.mobile.app.model.domain.response.block.BlockUserResponse
import com.medrevpatient.mobile.app.model.domain.response.block.UnblockResponse
import com.medrevpatient.mobile.app.model.domain.response.chat.ChatResponse
import com.medrevpatient.mobile.app.model.domain.response.chat.MessageTabResponse
import com.medrevpatient.mobile.app.model.domain.response.container.comment.CommentResponse
import com.medrevpatient.mobile.app.model.domain.response.container.faqQuestion.FAQQuestionResponse
import com.medrevpatient.mobile.app.model.domain.response.container.friendInfo.FriendInfoResponse
import com.medrevpatient.mobile.app.model.domain.response.container.legacyPost.AddImageLegacyPostResponse
import com.medrevpatient.mobile.app.model.domain.response.container.legacyPost.LegacyPostResponse
import com.medrevpatient.mobile.app.model.domain.response.container.storege.StorageResponse
import com.medrevpatient.mobile.app.model.domain.response.dietChallenge.DietChallengeResponse
import com.medrevpatient.mobile.app.model.domain.response.home.HomeScreenData
import com.medrevpatient.mobile.app.model.domain.response.weightTracker.WeightTrackerResponse
import com.medrevpatient.mobile.app.model.domain.response.message.MessageResponse
import com.medrevpatient.mobile.app.model.domain.response.notification.NotificationResponse
import com.medrevpatient.mobile.app.model.domain.response.searchPeople.SearchPeopleResponse
import com.medrevpatient.mobile.app.model.domain.response.sideEffect.SideEffectQuestion
import com.medrevpatient.mobile.app.model.domain.response.subscription.SubscriptionResponse
import com.medrevpatient.mobile.app.model.domain.response.tribe.MemberResponse
import com.medrevpatient.mobile.app.model.domain.response.tribe.TribeResponse
import com.medrevpatient.mobile.app.utils.ext.extractError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okio.IOException
import retrofit2.HttpException
import retrofit2.http.Part
import javax.inject.Inject

class ApiRepositoryImpl @Inject constructor(
    private val apiServices: ApiServices
) : ApiRepository {
    override fun doLogin(signInRequest: LogInRequest): Flow<NetworkResult<ApiResponse<UserAuthResponse>>> =
        flow {
            try {
                val response = apiServices.doLoginIn(signInRequest)

                if (response.isSuccessful && response.body() != null) {
                    emit(NetworkResult.Success(response.body()))
                } else {
                    emit(NetworkResult.Error(response.errorBody().extractError()))
                }

            } catch (e: IOException) {
                // IOException for network failures.
                emit(NetworkResult.Error(e.message))
            } catch (e: HttpException) {
                // HttpException for any non-2xx HTTP status codes.
                if (e.code() == 401) {
                    emit(NetworkResult.UnAuthenticated(e.message))
                } else {
                    emit(NetworkResult.Error(e.message))
                }
            }
        }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
            emit(NetworkResult.Error(cause.message))
        }

    /** Authentication */


    override fun doSignUp(signUpReq: SignUpReq): Flow<NetworkResult<ApiResponse<UserAuthResponse>>> =
        flow {
            try {
                val response = apiServices.doSignUp(signUpReq)

                if (response.isSuccessful && response.body() != null) {
                    emit(NetworkResult.Success(response.body()))
                } else {
                    emit(NetworkResult.Error(response.errorBody().extractError()))
                }

            } catch (e: IOException) {
                // IOException for network failures.
                emit(NetworkResult.Error(e.message))
            } catch (e: HttpException) {
                // HttpException for any non-2xx HTTP status codes.
                if (e.code() == 401) {
                    emit(NetworkResult.UnAuthenticated(e.message))
                } else {
                    emit(NetworkResult.Error(e.message))
                }
            }
        }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
            emit(NetworkResult.Error(cause.message))
        }

    override fun verifyOTP(verifyOTPReq: VerifyOTPReq): Flow<NetworkResult<ApiResponse<UserAuthResponse>>> =
        flow {
            try {
                val response = apiServices.verifyOTP(verifyOTPReq)

                if (response.isSuccessful && response.body() != null) {
                    emit(NetworkResult.Success(response.body()))
                } else {
                    emit(NetworkResult.Error(response.errorBody().extractError()))
                }

            } catch (e: IOException) {
                // IOException for network failures.
                emit(NetworkResult.Error(e.message))
            } catch (e: HttpException) {
                // HttpException for any non-2xx HTTP status codes.
                if (e.code() == 401) {
                    emit(NetworkResult.UnAuthenticated(e.message))
                } else {
                    emit(NetworkResult.Error(e.message))
                }
            }
        }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
            emit(NetworkResult.Error(cause.message))
        }

    override fun forgetPassword(forgetPasswordReq: ForgetPasswordReq): Flow<NetworkResult<ApiResponse<UserAuthResponse>>> =
        flow {
            try {
                val response = apiServices.forgetPassword(forgetPasswordReq)

                if (response.isSuccessful && response.body() != null) {
                    emit(NetworkResult.Success(response.body()))
                } else {
                    emit(NetworkResult.Error(response.errorBody().extractError()))
                }

            } catch (e: IOException) {
                // IOException for network failures.
                emit(NetworkResult.Error(e.message))
            } catch (e: HttpException) {
                // HttpException for any non-2xx HTTP status codes.
                if (e.code() == 401) {
                    emit(NetworkResult.UnAuthenticated(e.message))
                } else {
                    emit(NetworkResult.Error(e.message))
                }
            }
        }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
            emit(NetworkResult.Error(cause.message))
        }

    override fun resetPassword(resetPasswordReq: ResetPasswordReq): Flow<NetworkResult<ApiResponse<UserAuthResponse>>> =
        flow {
            try {
                val response = apiServices.resetPassword(resetPasswordReq)

                if (response.isSuccessful && response.body() != null) {
                    emit(NetworkResult.Success(response.body()))
                } else {
                    emit(NetworkResult.Error(response.errorBody().extractError()))
                }

            } catch (e: IOException) {
                // IOException for network failures.
                emit(NetworkResult.Error(e.message))
            } catch (e: HttpException) {
                // HttpException for any non-2xx HTTP status codes.
                if (e.code() == 401) {
                    emit(NetworkResult.UnAuthenticated(e.message))
                } else {
                    emit(NetworkResult.Error(e.message))
                }
            }
        }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
            emit(NetworkResult.Error(cause.message))
        }

    override fun resendOtpOTP(sendOTPReq: ResendOTPReq): Flow<NetworkResult<ApiResponse<Any>>> = flow {
        try {
            val response = apiServices.resendOtp(sendOTPReq)

            if (response.isSuccessful && response.body() != null) {
                emit(NetworkResult.Success(response.body()))
            } else {
                emit(NetworkResult.Error(response.errorBody().extractError()))
            }

        } catch (e: IOException) {
            // IOException for network failures.
            emit(NetworkResult.Error(e.message))
        } catch (e: HttpException) {
            // HttpException for any non-2xx HTTP status codes.
            if (e.code() == 401) {
                emit(NetworkResult.UnAuthenticated(e.message))
            } else {
                emit(NetworkResult.Error(e.message))
            }
        }
    }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
        emit(NetworkResult.Error(cause.message))
    }

    override fun doLogout(): Flow<NetworkResult<ApiResponse<UserAuthResponse>>> =
        flow {
            try {
                val response = apiServices.doLogOut()

                if (response.isSuccessful && response.body() != null) {
                    emit(NetworkResult.Success(response.body()!!))
                } else {
                    emit(NetworkResult.Error(response.errorBody().extractError()))
                }

            } catch (e: IOException) {
                // IOException for network failures.
                emit(NetworkResult.Error(e.message))
            } catch (e: HttpException) {
                // HttpException for any non-2xx HTTP status codes.
                if (e.code() == 401) {
                    emit(NetworkResult.UnAuthenticated(e.message))
                } else {
                    emit(NetworkResult.Error(e.message))
                }
            }
        }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
            emit(NetworkResult.Error(cause.message))
        }


    override fun deleteAccount(): Flow<NetworkResult<ApiResponse<UserAuthResponse>>> = flow {
        try {
            val response = apiServices.deleteAccount()

            if (response.isSuccessful && response.body() != null) {
                emit(NetworkResult.Success(response.body()!!))
            } else {
                emit(NetworkResult.Error(response.errorBody().extractError()))
            }

        } catch (e: IOException) {
            // IOException for network failures.
            emit(NetworkResult.Error(e.message))
        } catch (e: HttpException) {
            // HttpException for any non-2xx HTTP status codes.
            if (e.code() == 401) {
                emit(NetworkResult.UnAuthenticated(e.message))
            } else {
                emit(NetworkResult.Error(e.message))
            }
        }
    }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
        emit(NetworkResult.Error(cause.message))
    }

    override fun singlePostDelete(singlePostReq: SinglePostReq): Flow<NetworkResult<ApiResponse<LegacyPostResponse>>> =
        flow {
            try {
                val response = apiServices.singlePostDelete(singlePostReq)

                if (response.isSuccessful && response.body() != null) {
                    emit(NetworkResult.Success(response.body()!!))
                } else {
                    emit(NetworkResult.Error(response.errorBody().extractError()))
                }

            } catch (e: IOException) {
                // IOException for network failures.
                emit(NetworkResult.Error(e.message))
            } catch (e: HttpException) {
                // HttpException for any non-2xx HTTP status codes.
                if (e.code() == 401) {
                    emit(NetworkResult.UnAuthenticated(e.message))
                } else {
                    emit(NetworkResult.Error(e.message))
                }
            }
        }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
            emit(NetworkResult.Error(cause.message))
        }

    /** Patient Home Screen */
    override fun getPatientHomeScreenData(): Flow<NetworkResult<ApiResponse<HomeScreenData>>> = flow {
        try {
            val response = apiServices.getPatientHomeScreenData()
            if (response.isSuccessful && response.body() != null) {
                emit(NetworkResult.Success(response.body()!!))
            } else {
                emit(NetworkResult.Error(response.errorBody().extractError()))
            }
        } catch (e: IOException) {
            // IOException for network failures.
            emit(NetworkResult.Error(e.message))
        } catch (e: HttpException) {
            // HttpException for any non-2xx HTTP status codes.
            if (e.code() == 401) {
                emit(NetworkResult.UnAuthenticated(e.message))
            } else {
                emit(NetworkResult.Error(e.message))
            }
        }
    }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
        emit(NetworkResult.Error(cause.message))
    }

    override fun userStorage(): Flow<NetworkResult<ApiResponse<StorageResponse>>> = flow {
        try {
            val response = apiServices.userStorage()

            if (response.isSuccessful && response.body() != null) {
                emit(NetworkResult.Success(response.body()!!))
            } else {
                emit(NetworkResult.Error(response.errorBody().extractError()))
            }

        } catch (e: IOException) {
            // IOException for network failures.
            emit(NetworkResult.Error(e.message))
        } catch (e: HttpException) {
            // HttpException for any non-2xx HTTP status codes.
            if (e.code() == 401) {
                emit(NetworkResult.UnAuthenticated(e.message))
            } else {
                emit(NetworkResult.Error(e.message))
            }
        }
    }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
        emit(NetworkResult.Error(cause.message))
    }

    override fun addMemberData(addMemberReq: AddMemberRequest): Flow<NetworkResult<ApiResponse<MessageResponse>>> =
        flow {
            try {
                val response = apiServices.addMembersInnerCircleAndTribe(addMemberReq)

                if (response.isSuccessful && response.body() != null) {
                    emit(NetworkResult.Success(response.body()!!))
                } else {
                    emit(NetworkResult.Error(response.errorBody().extractError()))
                }

            } catch (e: IOException) {
                // IOException for network failures.
                emit(NetworkResult.Error(e.message))
            } catch (e: HttpException) {
                // HttpException for any non-2xx HTTP status codes.
                if (e.code() == 401) {
                    emit(NetworkResult.UnAuthenticated(e.message))
                } else {
                    emit(NetworkResult.Error(e.message))
                }
            }
        }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
            emit(NetworkResult.Error(cause.message))
        }

    override fun imagePositionChange(imagePositionReq: ImagePositionReq): Flow<NetworkResult<ApiResponse<MessageResponse>>> =
        flow {
            try {
                val response = apiServices.imagePositionChange(imagePositionReq)

                if (response.isSuccessful && response.body() != null) {
                    emit(NetworkResult.Success(response.body()!!))
                } else {
                    emit(NetworkResult.Error(response.errorBody().extractError()))
                }

            } catch (e: IOException) {
                // IOException for network failures.
                emit(NetworkResult.Error(e.message))
            } catch (e: HttpException) {
                // HttpException for any non-2xx HTTP status codes.
                if (e.code() == 401) {
                    emit(NetworkResult.UnAuthenticated(e.message))
                } else {
                    emit(NetworkResult.Error(e.message))
                }
            }
        }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
            emit(NetworkResult.Error(cause.message))
        }


    override fun doChangePassword(changePasswordReq: ChangePasswordReq): Flow<NetworkResult<ApiResponse<UserAuthResponse>>> =
        flow {
            try {
                val response = apiServices.changePassword(changePasswordReq)

                if (response.isSuccessful && response.body() != null) {
                    emit(NetworkResult.Success(response.body()!!))
                } else {
                    emit(NetworkResult.Error(response.errorBody().extractError()))
                }

            } catch (e: IOException) {
                // IOException for network failures.
                emit(NetworkResult.Error(e.message))
            } catch (e: HttpException) {
                // HttpException for any non-2xx HTTP status codes.
                if (e.code() == 401) {
                    emit(NetworkResult.UnAuthenticated(e.message))
                } else {
                    emit(NetworkResult.Error(e.message))
                }
            }
        }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
            emit(NetworkResult.Error(cause.message))
        }

    override fun acceptRejectNotification(
        type: String,
        tribeId: String
    ): Flow<NetworkResult<ApiResponse<MessageResponse>>> = flow {
        try {
            val response =
                apiServices.acceptRejectNotification(type = type, tribeId = tribeId)

            if (response.isSuccessful && response.body() != null) {
                emit(NetworkResult.Success(response.body()!!))
            } else {
                emit(NetworkResult.Error(response.errorBody().extractError()))
            }

        } catch (e: IOException) {
            // IOException for network failures.
            emit(NetworkResult.Error(e.message))
        } catch (e: HttpException) {
            // HttpException for any non-2xx HTTP status codes.
            if (e.code() == 401) {
                emit(NetworkResult.UnAuthenticated(e.message))
            } else {
                emit(NetworkResult.Error(e.message))
            }
        }
    }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
        emit(NetworkResult.Error(cause.message))
    }

    override fun doContactUs(contactUsReq: ContactUsReq): Flow<NetworkResult<ApiResponse<UserAuthResponse>>> =
        flow {
            try {
                val response = apiServices.contactUs(contactUsReq)

                if (response.isSuccessful && response.body() != null) {
                    emit(NetworkResult.Success(response.body()!!))
                } else {
                    emit(NetworkResult.Error(response.errorBody().extractError()))
                }

            } catch (e: IOException) {
                // IOException for network failures.
                emit(NetworkResult.Error(e.message))
            } catch (e: HttpException) {
                // HttpException for any non-2xx HTTP status codes.
                if (e.code() == 401) {
                    emit(NetworkResult.UnAuthenticated(e.message))
                } else {
                    emit(NetworkResult.Error(e.message))
                }
            }
        }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
            emit(NetworkResult.Error(cause.message))
        }

    override fun getFaqQuestion(): Flow<PagingData<FAQQuestionResponse>> = Pager(
        config = PagingConfig(pageSize = 20, maxSize = 100, enablePlaceholders = false),
        pagingSourceFactory = {
            FAQQuestionPagingSource(apiServices)
        }
    ).flow

    override fun getReflection(): Flow<PagingData<FAQQuestionResponse>> = Pager(
        config = PagingConfig(pageSize = 20, maxSize = 100, enablePlaceholders = false),
        pagingSourceFactory = {
            LegacyReflectionQuestionPagingSource(apiServices)
        }
    ).flow

    override fun getMainVillage(): Flow<PagingData<LegacyPostResponse>> = Pager(
        config = PagingConfig(pageSize = 20, maxSize = 100, enablePlaceholders = false),
        pagingSourceFactory = {
            MainVillagePagingSource(apiServices)
        }
    ).flow

    override fun deleteLegacyPost(postId: String): Flow<NetworkResult<ApiResponse<MessageResponse>>> =
        flow {
            try {
                val response = apiServices.deleteLegacyPost(postId = postId)

                if (response.isSuccessful && response.body() != null) {
                    emit(NetworkResult.Success(response.body()!!))
                } else {
                    emit(NetworkResult.Error(response.errorBody().extractError()))
                }

            } catch (e: IOException) {
                // IOException for network failures.
                emit(NetworkResult.Error(e.message))
            } catch (e: HttpException) {
                // HttpException for any non-2xx HTTP status codes.
                if (e.code() == 401) {
                    emit(NetworkResult.UnAuthenticated(e.message))
                } else {
                    emit(NetworkResult.Error(e.message))
                }
            }
        }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
            emit(NetworkResult.Error(cause.message))
        }

    override fun getLegacyPost(type: Int): Flow<PagingData<LegacyPostResponse>> = Pager(
        config = PagingConfig(pageSize = 20, maxSize = 100, enablePlaceholders = false),
        pagingSourceFactory = {
            LegacyPostPagingSource(apiServices, type = type)
        }
    ).flow

    override fun getFriendProfile(
        userId: String,
        page: Int
    ): Flow<NetworkResult<ApiResponse<FriendInfoResponse>>> = flow {
        try {
            val response = apiServices.getFriendProfile(userId = userId, perPage = 200, page = page)

            if (response.isSuccessful && response.body() != null) {
                emit(NetworkResult.Success(response.body()!!))
            } else {
                emit(NetworkResult.Error(response.errorBody().extractError()))
            }

        } catch (e: IOException) {
            // IOException for network failures.
            emit(NetworkResult.Error(e.message))
        } catch (e: HttpException) {
            // HttpException for any non-2xx HTTP status codes.
            if (e.code() == 401) {
                emit(NetworkResult.UnAuthenticated(e.message))
            } else {
                emit(NetworkResult.Error(e.message))
            }
        }
    }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
        emit(NetworkResult.Error(cause.message))
    }


    override fun getInnerCircleTribe(type: Int): Flow<PagingData<LegacyPostResponse>> = Pager(
        config = PagingConfig(pageSize = 20, maxSize = 100, enablePlaceholders = false),
        pagingSourceFactory = {
            InnerCircleTribePagingSource(apiServices, type = type)
        }
    ).flow

    override fun getPostDetails(postId: String): Flow<NetworkResult<ApiResponse<LegacyPostResponse>>> =
        flow {
            try {
                val response = apiServices.getPostDetails(postId)
                if (response.isSuccessful && response.body() != null) {
                    emit(NetworkResult.Success(response.body()!!))
                } else {
                    emit(NetworkResult.Error(response.errorBody().extractError()))
                }

            } catch (e: IOException) {
                // IOException for network failures.
                emit(NetworkResult.Error(e.message))
            } catch (e: HttpException) {
                // HttpException for any non-2xx HTTP status codes.
                if (e.code() == 401) {
                    emit(NetworkResult.UnAuthenticated(e.message))
                } else {
                    emit(NetworkResult.Error(e.message))
                }
            }
        }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
            emit(NetworkResult.Error(cause.message))
        }

    override fun likeDislike(postId: String): Flow<NetworkResult<ApiResponse<LegacyPostResponse>>> =
        flow {
            try {
                val response = apiServices.likeDislikeAPICall(postId)
                if (response.isSuccessful && response.body() != null) {
                    emit(NetworkResult.Success(response.body()!!))
                } else {
                    emit(NetworkResult.Error(response.errorBody().extractError()))
                }

            } catch (e: IOException) {
                // IOException for network failures.
                emit(NetworkResult.Error(e.message))
            } catch (e: HttpException) {
                // HttpException for any non-2xx HTTP status codes.
                if (e.code() == 401) {
                    emit(NetworkResult.UnAuthenticated(e.message))
                } else {
                    emit(NetworkResult.Error(e.message))
                }
            }
        }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
            emit(NetworkResult.Error(cause.message))
        }

    override fun editProfileDetails(
        req: Map<String, RequestBody>,
        profileImage: MultipartBody.Part?
    ): Flow<NetworkResult<ApiResponse<UserAuthResponse>>> = flow {
        try {
            val response = apiServices.updateProfileDetails(req = req, profileImage = profileImage)
            if (response.isSuccessful && response.body() != null) {
                emit(NetworkResult.Success(response.body()!!))
            } else {
                emit(NetworkResult.Error(response.errorBody().extractError()))
            }

        } catch (e: IOException) {
            // IOException for network failures.
            emit(NetworkResult.Error(e.message))
        } catch (e: HttpException) {
            // HttpException for any non-2xx HTTP status codes.
            if (e.code() == 401) {
                emit(NetworkResult.UnAuthenticated(e.message))
            } else {
                emit(NetworkResult.Error(e.message))
            }
        }
    }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
        emit(NetworkResult.Error(cause.message))
    }

    override fun createInnerCircleAndTribe(
        req: Map<String, RequestBody>,
        profileImage: MultipartBody.Part?
    ): Flow<NetworkResult<ApiResponse<TribeResponse>>> = flow {
        try {
            val response = apiServices.createInnerCircleAndTribe(req, profileImage)

            if (response.isSuccessful && response.body() != null) {
                emit(NetworkResult.Success(response.body()!!))
            } else {
                emit(NetworkResult.Error(response.errorBody().extractError()))
            }

        } catch (e: IOException) {
            // IOException for network failures.
            emit(NetworkResult.Error(e.message))
        } catch (e: HttpException) {
            // HttpException for any non-2xx HTTP status codes.
            if (e.code() == 401) {
                emit(NetworkResult.UnAuthenticated(e.message))
            } else {
                emit(NetworkResult.Error(e.message))
            }
        }
    }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
        emit(NetworkResult.Error(cause.message))
    }

    override fun addGroupMember(
        req: Map<String, RequestBody>,
        photo: MultipartBody.Part?
    ): Flow<NetworkResult<ApiResponse<MessageResponse>>> = flow {
        try {
            val response = apiServices.addGroupMember(req = req, profileImage = photo)

            if (response.isSuccessful && response.body() != null) {
                emit(NetworkResult.Success(response.body()!!))
            } else {
                emit(NetworkResult.Error(response.errorBody().extractError()))
            }

        } catch (e: IOException) {
            // IOException for network failures.
            emit(NetworkResult.Error(e.message))
        } catch (e: HttpException) {
            // HttpException for any non-2xx HTTP status codes.
            if (e.code() == 401) {
                emit(NetworkResult.UnAuthenticated(e.message))
            } else {
                emit(NetworkResult.Error(e.message))
            }
        }
    }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
        emit(NetworkResult.Error(cause.message))
    }

    override fun updateGroupMember(
        req: Map<String, RequestBody>,
        profileImage: MultipartBody.Part?
    ): Flow<NetworkResult<ApiResponse<MessageResponse>>> = flow {
        try {
            val response = apiServices.updateGroupMember(req = req, profileImage = profileImage)

            if (response.isSuccessful && response.body() != null) {
                emit(NetworkResult.Success(response.body()!!))
            } else {
                emit(NetworkResult.Error(response.errorBody().extractError()))
            }

        } catch (e: IOException) {
            // IOException for network failures.
            emit(NetworkResult.Error(e.message))
        } catch (e: HttpException) {
            // HttpException for any non-2xx HTTP status codes.
            if (e.code() == 401) {
                emit(NetworkResult.UnAuthenticated(e.message))
            } else {
                emit(NetworkResult.Error(e.message))
            }
        }
    }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
        emit(NetworkResult.Error(cause.message))
    }

    override fun groupDetailsUpdate(
        req: Map<String, RequestBody>,
        profileImage: MultipartBody.Part?
    ): Flow<NetworkResult<ApiResponse<MessageResponse>>> = flow {
        try {
            val response = apiServices.groupDetailsUpdate(req = req, profileImage = profileImage)

            if (response.isSuccessful && response.body() != null) {
                emit(NetworkResult.Success(response.body()!!))
            } else {
                emit(NetworkResult.Error(response.errorBody().extractError()))
            }

        } catch (e: IOException) {
            // IOException for network failures.
            emit(NetworkResult.Error(e.message))
        } catch (e: HttpException) {
            // HttpException for any non-2xx HTTP status codes.
            if (e.code() == 401) {
                emit(NetworkResult.UnAuthenticated(e.message))
            } else {
                emit(NetworkResult.Error(e.message))
            }
        }
    }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
        emit(NetworkResult.Error(cause.message))
    }

    override fun notificationOnOff(): Flow<NetworkResult<ApiResponse<UserAuthResponse>>> = flow {
        try {
            val response = apiServices.notificationOnOff()

            if (response.isSuccessful && response.body() != null) {
                emit(NetworkResult.Success(response.body()!!))
            } else {
                emit(NetworkResult.Error(response.errorBody().extractError()))
            }

        } catch (e: IOException) {
            // IOException for network failures.
            emit(NetworkResult.Error(e.message))
        } catch (e: HttpException) {
            // HttpException for any non-2xx HTTP status codes.
            if (e.code() == 401) {
                emit(NetworkResult.UnAuthenticated(e.message))
            } else {
                emit(NetworkResult.Error(e.message))
            }
        }
    }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
        emit(NetworkResult.Error(cause.message))
    }

    override fun publicPrivateProfileOnOff(): Flow<NetworkResult<ApiResponse<UserAuthResponse>>> =
        flow {
            try {
                val response = apiServices.publicPrivateProfile()

                if (response.isSuccessful && response.body() != null) {
                    emit(NetworkResult.Success(response.body()!!))
                } else {
                    emit(NetworkResult.Error(response.errorBody().extractError()))
                }

            } catch (e: IOException) {
                // IOException for network failures.
                emit(NetworkResult.Error(e.message))
            } catch (e: HttpException) {
                // HttpException for any non-2xx HTTP status codes.
                if (e.code() == 401) {
                    emit(NetworkResult.UnAuthenticated(e.message))
                } else {
                    emit(NetworkResult.Error(e.message))
                }
            }
        }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
            emit(NetworkResult.Error(cause.message))
        }

    override fun addAdvertisement(
        req: Map<String, RequestBody>,
        photo: MultipartBody.Part?
    ): Flow<NetworkResult<ApiResponse<AdvertisementResponse>>> = flow {
        try {
            val response = apiServices.addAdvertisement(req = req, photo = photo)

            if (response.isSuccessful && response.body() != null) {
                emit(NetworkResult.Success(response.body()!!))
            } else {
                emit(NetworkResult.Error(response.errorBody().extractError()))
            }

        } catch (e: IOException) {
            // IOException for network failures.
            emit(NetworkResult.Error(e.message))
        } catch (e: HttpException) {
            // HttpException for any non-2xx HTTP status codes.
            if (e.code() == 401) {
                emit(NetworkResult.UnAuthenticated(e.message))
            } else {
                emit(NetworkResult.Error(e.message))
            }
        }
    }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
        emit(NetworkResult.Error(cause.message))
    }

    override fun updateAdvertisement(req: Map<String, RequestBody>, photo: MultipartBody.Part?): Flow<NetworkResult<ApiResponse<AdvertisementResponse>>> = flow {
        try {
            val response = apiServices.updateAdvertisement(req, photo)
            if (response.isSuccessful && response.body() != null) {
                emit(NetworkResult.Success(response.body()!!))
            } else {
                emit(NetworkResult.Error(response.errorBody().extractError()))
            }

        } catch (e: IOException) {
            // IOException for network failures.
            emit(NetworkResult.Error(e.message))
        } catch (e: HttpException) {
            // HttpException for any non-2xx HTTP status codes.
            if (e.code() == 401) {
                emit(NetworkResult.UnAuthenticated(e.message))
            } else {
                emit(NetworkResult.Error(e.message))
            }
        }
    }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
        emit(NetworkResult.Error(cause.message))
    }


    override fun createLegacyPost(
        req: Map<String, RequestBody>,
        @Part media: List<MultipartBody.Part?>
    ): Flow<NetworkResult<ApiResponse<LegacyPostResponse>>> = flow {
        try {
            val response = apiServices.legacyCreatePost(req, media)

            if (response.isSuccessful && response.body() != null) {
                emit(NetworkResult.Success(response.body()!!))
            } else {
                emit(NetworkResult.Error(response.errorBody().extractError()))
            }

        } catch (e: IOException) {
            // IOException for network failures.
            emit(NetworkResult.Error(e.message))
        } catch (e: HttpException) {
            // HttpException for any non-2xx HTTP status codes.
            if (e.code() == 401) {
                emit(NetworkResult.UnAuthenticated(e.message))
            } else {
                emit(NetworkResult.Error(e.message))
            }
        }
    }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
        emit(NetworkResult.Error(cause.message))
    }

    override fun updateLegacyPost(
        req: Map<String, RequestBody>,
    ): Flow<NetworkResult<ApiResponse<LegacyPostResponse>>> = flow {
        try {
            val response = apiServices.updateLegacyPost(req = req)
            if (response.isSuccessful && response.body() != null) {
                emit(NetworkResult.Success(response.body()!!))
            } else {
                emit(NetworkResult.Error(response.errorBody().extractError()))
            }

        } catch (e: IOException) {
            // IOException for network failures.
            emit(NetworkResult.Error(e.message))
        } catch (e: HttpException) {
            // HttpException for any non-2xx HTTP status codes.
            if (e.code() == 401) {
                emit(NetworkResult.UnAuthenticated(e.message))
            } else {
                emit(NetworkResult.Error(e.message))
            }
        }
    }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
        emit(NetworkResult.Error(cause.message))
    }

    override fun addImageToLegacyPost(
        req: Map<String, RequestBody>,
        media: List<MultipartBody.Part?>
    ): Flow<NetworkResult<ApiResponseNew<AddImageLegacyPostResponse>>> = flow {
        try {
            val response = apiServices.addImageToLegacyPost(req = req, media = media)
            if (response.isSuccessful && response.body() != null) {
                emit(NetworkResult.Success(response.body()!!))
            } else {
                emit(NetworkResult.Error(response.errorBody().extractError()))
            }

        } catch (e: IOException) {
            // IOException for network failures.
            emit(NetworkResult.Error(e.message))
        } catch (e: HttpException) {
            // HttpException for any non-2xx HTTP status codes.
            if (e.code() == 401) {
                emit(NetworkResult.UnAuthenticated(e.message))
            } else {
                emit(NetworkResult.Error(e.message))
            }
        }
    }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
        emit(NetworkResult.Error(cause.message))
    }


    override fun editProfileDetailsWithOutImage(req: Map<String, RequestBody>): Flow<NetworkResult<ApiResponse<UserAuthResponse>>> =
        flow {
            try {
                val response = apiServices.updateProfileDetailsWithOutImage(req)

                if (response.isSuccessful && response.body() != null) {
                    emit(NetworkResult.Success(response.body()!!))
                } else {
                    emit(NetworkResult.Error(response.errorBody().extractError()))
                }

            } catch (e: IOException) {
                // IOException for network failures.
                emit(NetworkResult.Error(e.message))
            } catch (e: HttpException) {
                // HttpException for any non-2xx HTTP status codes.
                if (e.code() == 401) {
                    emit(NetworkResult.UnAuthenticated(e.message))
                } else {
                    emit(NetworkResult.Error(e.message))
                }
            }
        }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
            emit(NetworkResult.Error(cause.message))
        }

    override fun getCommentList(
        postId: String,
        apiCallback: ApiCallback?
    ): Flow<PagingData<CommentResponse>> = Pager(
        config = PagingConfig(pageSize = 20, maxSize = 100, enablePlaceholders = false),
        pagingSourceFactory = {
            CommentListPagingSource(apiServices, postId = postId, apiCallback = apiCallback)
        }
    ).flow

    override fun getNotification(): Flow<PagingData<NotificationResponse>> = Pager(
        config = PagingConfig(pageSize = 20, maxSize = 100, enablePlaceholders = false),
        pagingSourceFactory = {
            NotificationPagingSource(apiService = apiServices)
        }
    ).flow

    override fun getMessage(): Flow<PagingData<MessageTabResponse>> = Pager(
        config = PagingConfig(pageSize = 20, maxSize = 100, enablePlaceholders = false),
        pagingSourceFactory = {
            MessagePagingSource(apiService = apiServices)
        }
    ).flow

    override fun getAdvertisements(): Flow<PagingData<AdvertisementResponse>> = Pager(
        config = PagingConfig(pageSize = 20, maxSize = 100, enablePlaceholders = false),
        pagingSourceFactory = {
            AdvertisementPagingSource(apiService = apiServices)
        }
    ).flow

    override fun getHomeAdvertisements(): Flow<PagingData<AdvertisementResponse>> = Pager(
        config = PagingConfig(pageSize = 20, maxSize = 100, enablePlaceholders = false),
        pagingSourceFactory = {
            HomeAdvertisementPagingSource(apiService = apiServices)
        }
    ).flow


    override fun addComment(
        postId: String,
        addCommentReq: AddCommentReq
    ): Flow<NetworkResult<ApiResponse<CommentResponse>>> = flow {
        try {
            val response = apiServices.addComment(postId = postId, addComment = addCommentReq)
            if (response.isSuccessful && response.body() != null) {
                emit(NetworkResult.Success(response.body()!!))
            } else {
                emit(NetworkResult.Error(response.errorBody().extractError()))
            }

        } catch (e: IOException) {
            // IOException for network failures.
            emit(NetworkResult.Error(e.message))
        } catch (e: HttpException) {
            // HttpException for any non-2xx HTTP status codes.
            if (e.code() == 401) {
                emit(NetworkResult.UnAuthenticated(e.message))
            } else {
                emit(NetworkResult.Error(e.message))
            }
        }
    }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
        emit(NetworkResult.Error(cause.message))
    }

    override fun getChatMessage(
        type: String,
        receiverId: String?,
        groupId: String?,
        page: Int
    ): Flow<NetworkResult<ApiResponseNew<ChatResponse>>> = flow {
        try {
            val response = apiServices.getChatMessage(
                type = type,
                receiverId = receiverId,
                groupId = groupId,
                page = page,
                perPage = 40
            )
            if (response.isSuccessful && response.body() != null) {
                emit(NetworkResult.Success(response.body()!!))
            } else {
                emit(NetworkResult.Error(response.errorBody().extractError()))
            }

        } catch (e: IOException) {
            // IOException for network failures.
            emit(NetworkResult.Error(e.message))
        } catch (e: HttpException) {
            // HttpException for any non-2xx HTTP status codes.
            if (e.code() == 401) {
                emit(NetworkResult.UnAuthenticated(e.message))
            } else {
                emit(NetworkResult.Error(e.message))
            }
        }
    }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
        emit(NetworkResult.Error(cause.message))
    }

    /* override fun getChatMessage(
         type: String,
         receiverId: String?,
         groupId: String?,
         page: Int
     ): Flow<NetworkResult<ApiResponseNew<ChatResponse>>> = flow {
         try {
             val response = apiServices.getChatMessage(type = type, receiverId = receiverId, groupId = groupId, page = page, perPage = 40)
             if (response.isSuccessful && response.body() != null) {
                 emit(NetworkResult.Success(response.body()!!))
             } else {
                 emit(NetworkResult.Error(response.errorBody().extractError()))
             }

         } catch (e: IOException) {
             // IOException for network failures.
             emit(NetworkResult.Error(e.message))
         } catch (e: HttpException) {
             // HttpException for any non-2xx HTTP status codes.
             if (e.code() == 401) {
                 emit(NetworkResult.UnAuthenticated(e.message))
             } else {
                 emit(NetworkResult.Error(e.message))
             }
         }
     }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
         emit(NetworkResult.Error(cause.message))
     }*/

    override fun innerCircleTribeBlockAndLeave(
        type: String,
        userId: String,
        tribeId: String?
    ): Flow<NetworkResult<ApiResponse<UnblockResponse>>> = flow {
        try {
            val response = apiServices.innerCircleTribeBlockAndLeave(
                type = type,
                userId = userId,
                tribeId = tribeId
            )
            if (response.isSuccessful && response.body() != null) {
                emit(NetworkResult.Success(response.body()!!))
            } else {
                emit(NetworkResult.Error(response.errorBody().extractError()))
            }

        } catch (e: IOException) {
            // IOException for network failures.
            emit(NetworkResult.Error(e.message))
        } catch (e: HttpException) {
            // HttpException for any non-2xx HTTP status codes.
            if (e.code() == 401) {
                emit(NetworkResult.UnAuthenticated(e.message))
            } else {
                emit(NetworkResult.Error(e.message))
            }
        }
    }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
        emit(NetworkResult.Error(cause.message))
    }

    override fun reportUserAndPost(reportUserPostReq: ReportUserPostReq): Flow<NetworkResult<ApiResponse<LegacyPostResponse>>> =
        flow {
            try {
                val response = apiServices.reportUserAndPost(reportUserPostReq)

                if (response.isSuccessful && response.body() != null) {
                    emit(NetworkResult.Success(response.body()!!))
                } else {
                    emit(NetworkResult.Error(response.errorBody().extractError()))
                }

            } catch (e: IOException) {
                // IOException for network failures.
                emit(NetworkResult.Error(e.message))
            } catch (e: HttpException) {
                // HttpException for any non-2xx HTTP status codes.
                if (e.code() == 401) {
                    emit(NetworkResult.UnAuthenticated(e.message))
                } else {
                    emit(NetworkResult.Error(e.message))
                }
            }
        }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
            emit(NetworkResult.Error(cause.message))
        }

    override fun markedAudioAsPlayed(date: String): Flow<NetworkResult<ApiResponse<Any>>> = flow {
        try {
            val response = apiServices.markedAudioAsPlayed(date)

            if (response.isSuccessful && response.body() != null) {
                emit(NetworkResult.Success(response.body()!!))
            } else {
                emit(NetworkResult.Error(response.errorBody().extractError()))
            }

        } catch (e: IOException) {
            // IOException for network failures.
            emit(NetworkResult.Error(e.message))
        } catch (e: HttpException) {
            // HttpException for any non-2xx HTTP status codes.
            if (e.code() == 401) {
                emit(NetworkResult.UnAuthenticated(e.message))
            } else {
                emit(NetworkResult.Error(e.message))
            }
        }
    }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
        emit(NetworkResult.Error(cause.message))
    }

    override fun addRemoveGroupMember(groupMemberRequest: GroupMemberRequest): Flow<NetworkResult<ApiResponse<UnblockResponse>>> =
        flow {
            try {
                val response = apiServices.addRemoveGroupMember(groupMemberRequest)

                if (response.isSuccessful && response.body() != null) {
                    emit(NetworkResult.Success(response.body()!!))
                } else {
                    emit(NetworkResult.Error(response.errorBody().extractError()))
                }
            } catch (e: IOException) {
                // IOException for network failures.
                emit(NetworkResult.Error(e.message))
            } catch (e: HttpException) {
                // HttpException for any non-2xx HTTP status codes.
                if (e.code() == 401) {
                    emit(NetworkResult.UnAuthenticated(e.message))
                } else {
                    emit(NetworkResult.Error(e.message))
                }
            }
        }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
            emit(NetworkResult.Error(cause.message))
        }


    override fun updateProfile(updateProfileReq: UpdateProfileReq): Flow<NetworkResult<ApiResponse<UserAuthResponse>>> =
        flow {
            try {
                val response = apiServices.updateProfile(updateProfileReq)

                if (response.isSuccessful && response.body() != null) {
                    emit(NetworkResult.Success(response.body()!!))
                } else {
                    emit(NetworkResult.Error(response.errorBody().extractError()))
                }

            } catch (e: IOException) {
                // IOException for network failures.
                emit(NetworkResult.Error(e.message))
            } catch (e: HttpException) {
                // HttpException for any non-2xx HTTP status codes.
                if (e.code() == 401) {
                    emit(NetworkResult.UnAuthenticated(e.message))
                } else {
                    emit(NetworkResult.Error(e.message))
                }
            }
        }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
            emit(NetworkResult.Error(cause.message))
        }

    override fun submitFeedback(feedbackReq: FeedbackReq): Flow<NetworkResult<ApiResponse<Any>>> =
        flow {
            try {
                val response = apiServices.submitFeedback(feedbackReq)

                if (response.isSuccessful && response.body() != null) {
                    emit(NetworkResult.Success(response.body()!!))
                } else {
                    emit(NetworkResult.Error(response.errorBody().extractError()))
                }

            } catch (e: IOException) {
                // IOException for network failures.
                emit(NetworkResult.Error(e.message))
            } catch (e: HttpException) {
                // HttpException for any non-2xx HTTP status codes.
                if (e.code() == 401) {
                    emit(NetworkResult.UnAuthenticated(e.message))
                } else {
                    emit(NetworkResult.Error(e.message))
                }
            }
        }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
            emit(NetworkResult.Error(cause.message))
        }

    override fun storeFCMToken(tokenStoreReq: TokenStoreReq): Flow<NetworkResult<ApiResponse<Any>>> =
        flow {
            try {
                val response = apiServices.storeFCMToken(tokenStoreReq)

                if (response.isSuccessful && response.body() != null) {
                    emit(NetworkResult.Success(response.body()!!))
                } else {
                    emit(NetworkResult.Error(response.errorBody().extractError()))
                }

            } catch (e: IOException) {
                // IOException for network failures.
                emit(NetworkResult.Error(e.message))
            } catch (e: HttpException) {
                // HttpException for any non-2xx HTTP status codes.
                if (e.code() == 401) {
                    emit(NetworkResult.UnAuthenticated(e.message))
                } else {
                    emit(NetworkResult.Error(e.message))
                }
            }
        }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
            emit(NetworkResult.Error(cause.message))
        }

    override fun storePurchaseInfo(subscriptionInfoReq: SubscriptionInfoReq): Flow<NetworkResult<ApiResponse<SubscriptionResponse>>> =
        flow {
            try {
                val response = apiServices.storePurchaseInfo(subscriptionInfoReq)

                if (response.isSuccessful && response.body() != null) {
                    emit(NetworkResult.Success(response.body()!!))
                } else {
                    emit(NetworkResult.Error(response.errorBody().extractError()))
                }

            } catch (e: IOException) {
                // IOException for network failures.
                emit(NetworkResult.Error(e.message))
            } catch (e: HttpException) {
                // HttpException for any non-2xx HTTP status codes.
                if (e.code() == 401) {
                    emit(NetworkResult.UnAuthenticated(e.message))
                } else {
                    emit(NetworkResult.Error(e.message))
                }
            }
        }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
            emit(NetworkResult.Error(cause.message))
        }

    override fun storeAdvertisementInfo(subscriptionInfoReq: SubscriptionInfoReq): Flow<NetworkResult<ApiResponse<SubscriptionResponse>>> = flow {
        try {
            val response = apiServices.storeAdvertisementInfo(subscriptionInfoReq)

            if (response.isSuccessful && response.body() != null) {
                emit(NetworkResult.Success(response.body()!!))
            } else {
                emit(NetworkResult.Error(response.errorBody().extractError()))
            }
        } catch (e: IOException) {
            // IOException for network failures.
            emit(NetworkResult.Error(e.message))
        } catch (e: HttpException) {
            // HttpException for any non-2xx HTTP status codes.
            if (e.code() == 401) {
                emit(NetworkResult.UnAuthenticated(e.message))
            } else {
                emit(NetworkResult.Error(e.message))
            }
        }
    }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
        emit(NetworkResult.Error(cause.message))
    }

    override fun checkSubscription(checkSubscriptionReq: CheckSubscriptionReq): Flow<NetworkResult<ApiResponse<SubscriptionResponse>>> =
        flow {
            try {
                val response = apiServices.checkSubscription(checkSubscriptionReq)

                if (response.isSuccessful && response.body() != null) {
                    emit(NetworkResult.Success(response.body()!!))
                } else {
                    emit(NetworkResult.Error(response.errorBody().extractError()))
                }
            } catch (e: IOException) {
                // IOException for network failures.
                emit(NetworkResult.Error(e.message))
            } catch (e: HttpException) {
                // HttpException for any non-2xx HTTP status codes.
                if (e.code() == 401) {
                    emit(NetworkResult.UnAuthenticated(e.message))
                } else {
                    emit(NetworkResult.Error(e.message))
                }
            }
        }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
            emit(NetworkResult.Error(cause.message))
        }

    override fun getAdvertisementView(advertisementId: String): Flow<NetworkResult<ApiResponse<AdvertisementResponse>>> = flow {
        try {
            val response = apiServices.getAdvertisementView(advertisementId)

            if (response.isSuccessful && response.body() != null) {
                emit(NetworkResult.Success(response.body()!!))
            } else {
                emit(NetworkResult.Error(response.errorBody().extractError()))
            }
        } catch (e: IOException) {
            // IOException for network failures.
            emit(NetworkResult.Error(e.message))
        } catch (e: HttpException) {
            // HttpException for any non-2xx HTTP status codes.
            if (e.code() == 401) {
                emit(NetworkResult.UnAuthenticated(e.message))
            } else {
                emit(NetworkResult.Error(e.message))
            }
        }
    }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
        emit(NetworkResult.Error(cause.message))
    }


    override fun getTribeList(type: Int): Flow<PagingData<TribeResponse>> = Pager(
        config = PagingConfig(pageSize = 20, maxSize = 100, enablePlaceholders = false),
        pagingSourceFactory = {
            TribePagingSource(type = type, apiServices)
        }
    ).flow

    override fun getTribeMemberList(tribeId: String): Flow<PagingData<MemberResponse>> = Pager(
        config = PagingConfig(pageSize = 20, maxSize = 100, enablePlaceholders = false),
        pagingSourceFactory = {
            TribeMembersPagingSource(tribeId = tribeId, apiServices)
        }
    ).flow

    override fun getAllPeopleList(
        searchName: String,
        tribeId: String
    ): Flow<PagingData<SearchPeopleResponse>> = Pager(
        config = PagingConfig(pageSize = 20, maxSize = 100, enablePlaceholders = false),
        pagingSourceFactory = {
            AllPeoplePagingSource(
                searchName = searchName,
                tribeId = tribeId,
                apiService = apiServices
            )
        }
    ).flow

    override fun getAddGroupMember(
        type: String,
        name: String?,
        groupId: String
    ): Flow<PagingData<SearchPeopleResponse>> = Pager(
        config = PagingConfig(pageSize = 20, maxSize = 100, enablePlaceholders = false),
        pagingSourceFactory = {
            AddGroupMemberPagingSource(
                type = type,
                name = name,
                groupId = groupId,
                apiService = apiServices
            )
        }
    ).flow

    override fun getGroupMember(
        groupId: String,
        name: String
    ): Flow<PagingData<SearchPeopleResponse>> = Pager(
        config = PagingConfig(pageSize = 20, maxSize = 100, enablePlaceholders = false),
        pagingSourceFactory = {
            GroupMemberPagingSource(groupId = groupId, name = name, apiServices)
        }
    ).flow

    override fun getBlockList(type: String): Flow<PagingData<BlockUserResponse>> = Pager(
        config = PagingConfig(pageSize = 20, maxSize = 100, enablePlaceholders = false),
        pagingSourceFactory = {
            BlockMemberPagingSource(type = type, apiServices)
        }
    ).flow

    override fun getTermsAndConditions(type: String): Flow<NetworkResult<ApiResponse<TermsResponse>>> = flow {
        try {
            val response = apiServices.getTermsAndConditions(type)

            if (response.isSuccessful && response.body() != null) {
                emit(NetworkResult.Success(response.body()!!))
            } else {
                emit(NetworkResult.Error(response.errorBody().extractError()))
            }
        } catch (e: IOException) {
            // IOException for network failures.
            emit(NetworkResult.Error(e.message))
        } catch (e: HttpException) {
            // HttpException for any non-2xx HTTP status codes.
            if (e.code() == 401) {
                emit(NetworkResult.UnAuthenticated(e.message))
            } else {
                emit(NetworkResult.Error(e.message))
            }
        }
    }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
        emit(NetworkResult.Error(cause.message))
    }

    override fun checkAppUpdate(appUpdateRequest: AppUpdateRequest): Flow<NetworkResult<ApiResponse<AppUpdateResponse>>> = flow {
        try {
            val response = apiServices.checkAppUpdate(appUpdateRequest)

            if (response.isSuccessful && response.body() != null) {
                emit(NetworkResult.Success(response.body()!!))
            } else {
                emit(NetworkResult.Error(response.errorBody().extractError()))
            }
        } catch (e: IOException) {
            // IOException for network failures.
            emit(NetworkResult.Error(e.message))
        } catch (e: HttpException) {
            // HttpException for any non-2xx HTTP status codes.
            if (e.code() == 401) {
                emit(NetworkResult.UnAuthenticated(e.message))
            } else {
                emit(NetworkResult.Error(e.message))
            }
        }
    }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
        emit(NetworkResult.Error(cause.message))
    }

    override fun calculateBmi(bmiRequest: BmiCalculateRequest): Flow<NetworkResult<ApiResponse<BmiCalculateResponse>>> = flow {
        try {
            val response = apiServices.calculateBmi(bmiRequest)

            if (response.isSuccessful && response.body() != null) {
                emit(NetworkResult.Success(response.body()!!))
            } else {
                emit(NetworkResult.Error(response.errorBody().extractError()))
            }
        } catch (e: IOException) {
            // IOException for network failures.
            emit(NetworkResult.Error(e.message))
        } catch (e: HttpException) {
            // HttpException for any non-2xx HTTP status codes.
            if (e.code() == 401) {
                emit(NetworkResult.UnAuthenticated(e.message))
            } else {
                emit(NetworkResult.Error(e.message))
            }
        }
    }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
        emit(NetworkResult.Error(cause.message))
    }

    override fun getSideEffectQuestions(): Flow<NetworkResult<ApiResponseNew<SideEffectQuestion>>> = flow {
        try {
            val response = apiServices.getSideEffectQuestions()

            if (response.isSuccessful && response.body() != null) {
                emit(NetworkResult.Success(response.body()!!))
            } else {
                emit(NetworkResult.Error(response.errorBody().extractError()))
            }
        } catch (e: IOException) {
            // IOException for network failures.
            emit(NetworkResult.Error(e.message))
        } catch (e: HttpException) {
            // HttpException for any non-2xx HTTP status codes.
            if (e.code() == 401) {
                emit(NetworkResult.UnAuthenticated(e.message))
            } else {
                emit(NetworkResult.Error(e.message))
            }
        }
    }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
        emit(NetworkResult.Error(cause.message))
    }

    override fun submitSideEffectAnswers(sideEffectAnswerRequest: SideEffectAnswerRequest): Flow<NetworkResult<ApiResponse<Any>>> = flow {
        try {
            val response = apiServices.submitSideEffectAnswers(sideEffectAnswerRequest)

            if (response.isSuccessful && response.body() != null) {
                emit(NetworkResult.Success(response.body()!!))
            } else {
                emit(NetworkResult.Error(response.errorBody().extractError()))
            }
        } catch (e: IOException) {
            // IOException for network failures.
            emit(NetworkResult.Error(e.message))
        } catch (e: HttpException) {
            // HttpException for any non-2xx HTTP status codes.
            if (e.code() == 401) {
                emit(NetworkResult.UnAuthenticated(e.message))
            } else {
                emit(NetworkResult.Error(e.message))
            }
        }
    }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
        emit(NetworkResult.Error(cause.message))
    }

    override fun getDietChallenge(): Flow<NetworkResult<ApiResponse<DietChallengeResponse>>> = flow {
        try {
            val response = apiServices.getDietChallenge()

            if (response.isSuccessful && response.body() != null) {
                emit(NetworkResult.Success(response.body()!!))
            } else {
                emit(NetworkResult.Error(response.errorBody().extractError()))
            }
        } catch (e: IOException) {
            // IOException for network failures.
            emit(NetworkResult.Error(e.message))
        } catch (e: HttpException) {
            // HttpException for any non-2xx HTTP status codes.
            if (e.code() == 401) {
                emit(NetworkResult.UnAuthenticated(e.message))
            } else {
                emit(NetworkResult.Error(e.message))
            }
        }
    }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
        emit(NetworkResult.Error(cause.message))
    }

    override fun submitDietChallengeAnswer(request: DietChallengeSubmitRequest): Flow<NetworkResult<ApiResponse<DietChallengeResponse>>> = flow {
        try {
            val response = apiServices.submitDietChallengeAnswer(request)

            if (response.isSuccessful && response.body() != null) {
                emit(NetworkResult.Success(response.body()!!))
            } else {
                emit(NetworkResult.Error(response.errorBody().extractError()))
            }
        } catch (e: IOException) {
            // IOException for network failures.
            emit(NetworkResult.Error(e.message))
        } catch (e: HttpException) {
            // HttpException for any non-2xx HTTP status codes.
            if (e.code() == 401) {
                emit(NetworkResult.UnAuthenticated(e.message))
            } else {
                emit(NetworkResult.Error(e.message))
            }
        }
    }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
        emit(NetworkResult.Error(cause.message))
    }

    override fun getWeightTrackerData(): Flow<NetworkResult<ApiResponse<WeightTrackerResponse>>> = flow {
        try {
            val response = apiServices.getWeightTrackerData()

            if (response.isSuccessful && response.body() != null) {
                emit(NetworkResult.Success(response.body()!!))
            } else {
                emit(NetworkResult.Error(response.errorBody().extractError()))
            }
        } catch (e: IOException) {
            // IOException for network failures.
            emit(NetworkResult.Error(e.message))
        } catch (e: HttpException) {
            // HttpException for any non-2xx HTTP status codes.
            if (e.code() == 401) {
                emit(NetworkResult.UnAuthenticated(e.message))
            } else {
                emit(NetworkResult.Error(e.message))
            }
        }
    }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
        emit(NetworkResult.Error(cause.message))
    }

    override fun getAppointmentData(status: Int?): Flow<PagingData<AppointmentResponse>> = Pager(
        config = PagingConfig(pageSize = 20, maxSize = 100, enablePlaceholders = false),
        pagingSourceFactory = {
            AppointmentPagingSource(status = status, apiServices)
        }
    ).flow

    override fun getAvailableSlots(request: AvailableSlotsRequest): Flow<NetworkResult<ApiResponse<AvailableSlotsData>>> = flow {
        try {
            val response = apiServices.getAvailableSlots(request)

            if (response.isSuccessful && response.body() != null) {
                emit(NetworkResult.Success(response.body()!!))
            } else {
                emit(NetworkResult.Error(response.errorBody().extractError()))
            }
        } catch (e: IOException) {
            // IOException for network failures.
            emit(NetworkResult.Error(e.message))
        } catch (e: HttpException) {
            // HttpException for any non-2xx HTTP status codes.
            if (e.code() == 401) {
                emit(NetworkResult.UnAuthenticated(e.message))
            } else {
                emit(NetworkResult.Error(e.message))
            }
        }
    }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO).catch { cause ->
        emit(NetworkResult.Error(cause.message))
    }


}