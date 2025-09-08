package com.medrevpatient.mobile.app.data.source.remote.repository
import com.medrevpatient.mobile.app.data.source.remote.EndPoints
import com.medrevpatient.mobile.app.model.domain.request.CheckSubscriptionReq
import com.medrevpatient.mobile.app.model.domain.request.FeedbackReq
import com.medrevpatient.mobile.app.model.domain.request.SubscriptionInfoReq
import com.medrevpatient.mobile.app.model.domain.request.TokenStoreReq
import com.medrevpatient.mobile.app.model.domain.request.addMember.AddMemberRequest
import com.medrevpatient.mobile.app.model.domain.request.addMember.GroupMemberRequest
import com.medrevpatient.mobile.app.model.domain.request.authReq.AppUpdateRequest
import com.medrevpatient.mobile.app.model.domain.request.authReq.ForgetPasswordReq
import com.medrevpatient.mobile.app.model.domain.request.authReq.LogoutReq
import com.medrevpatient.mobile.app.model.domain.request.authReq.ResetPasswordReq
import com.medrevpatient.mobile.app.model.domain.request.authReq.ResendOTPReq
import com.medrevpatient.mobile.app.model.domain.request.authReq.LogInRequest
import com.medrevpatient.mobile.app.model.domain.request.authReq.SignUpReq
import com.medrevpatient.mobile.app.model.domain.request.authReq.UpdateProfileReq
import com.medrevpatient.mobile.app.model.domain.request.authReq.VerifyOTPReq
import com.medrevpatient.mobile.app.model.domain.request.bmi.BmiCalculateRequest
import com.medrevpatient.mobile.app.model.domain.request.imagePostionReq.ImagePositionReq
import com.medrevpatient.mobile.app.model.domain.request.mainReq.AddCommentReq
import com.medrevpatient.mobile.app.model.domain.request.mainReq.ChangePasswordReq
import com.medrevpatient.mobile.app.model.domain.request.mainReq.ContactUsReq
import com.medrevpatient.mobile.app.model.domain.request.mainReq.deletePost.SinglePostReq
import com.medrevpatient.mobile.app.model.domain.request.report.ReportUserPostReq
import com.medrevpatient.mobile.app.model.domain.response.ApiResponse
import com.medrevpatient.mobile.app.model.domain.response.ApiResponseNew
import com.medrevpatient.mobile.app.model.domain.response.TermsResponse
import com.medrevpatient.mobile.app.model.domain.response.advertisement.AdvertisementResponse
import com.medrevpatient.mobile.app.model.domain.response.archive.ArchiveScreenResponse
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
import com.medrevpatient.mobile.app.model.domain.response.home.HomeScreenResponse
import com.medrevpatient.mobile.app.model.domain.response.message.MessageResponse
import com.medrevpatient.mobile.app.model.domain.response.notification.NotificationResponse
import com.medrevpatient.mobile.app.model.domain.response.searchPeople.SearchPeopleResponse
import com.medrevpatient.mobile.app.model.domain.response.subscription.SubscriptionResponse
import com.medrevpatient.mobile.app.model.domain.response.tribe.MemberResponse
import com.medrevpatient.mobile.app.model.domain.response.tribe.TribeResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Query

interface ApiServices {

    @POST(EndPoints.Auth.LOG_IN)
    suspend fun doLoginIn(
        @Body logInRequest: LogInRequest
    ): Response<ApiResponse<UserAuthResponse>>

    @POST(EndPoints.Auth.SIGN_UP)
    suspend fun doSignUp(
        @Body signUpReq: SignUpReq
    ): Response<ApiResponse<UserAuthResponse>>

    @POST(EndPoints.Auth.OTP_VERIFY)
    suspend fun verifyOTP(
        @Body verifyOTPReq: VerifyOTPReq
    ): Response<ApiResponse<UserAuthResponse>>

    @POST(EndPoints.Container.RESET_PASSWORD)
    suspend fun resetPassword(
        @Body resetPasswordReq: ResetPasswordReq
    ): Response<ApiResponse<UserAuthResponse>>

    @POST(EndPoints.Auth.FORGET_PASSWORD)
    suspend fun forgetPassword(
        @Body forgetPasswordReq: ForgetPasswordReq
    ): Response<ApiResponse<UserAuthResponse>>

    @POST(EndPoints.Auth.RESEND_OTP)
    suspend fun resendOtp(
        @Body resendOTPReq: ResendOTPReq
    ): Response<ApiResponse<Any>>

    @POST(EndPoints.Container.CHANGE_PASSWORD)
    suspend fun changePassword(
        @Body changePasswordReq: ChangePasswordReq
    ): Response<ApiResponse<UserAuthResponse>>

    @POST(EndPoints.Container.CONTACT_US)
    suspend fun contactUs(
        @Body contactUs: ContactUsReq
    ): Response<ApiResponse<UserAuthResponse>>

    @GET(EndPoints.Container.FQA_QUESTION)
    suspend fun getFAQQuestion(
        @Query("page") page: Int,
        @Query("perPage") perPage: Int
    ): Response<ApiResponseNew<FAQQuestionResponse>>


    @PUT(EndPoints.Container.INNER_CIRCLE_BLOCK_AND_LEAVE)
    suspend fun innerCircleTribeBlockAndLeave(
        @Query("type") type: String,
        @Query("userId") userId: String,
        @Query("tribeId") tribeId: String?
    ): Response<ApiResponse<UnblockResponse>>


    @GET(EndPoints.Container.MAIN_VILLAGE_PAGE)
    suspend fun getMainVillagePage(
        @Query("page") page: Int,
        @Query("perPage") perPage: Int
    ): Response<ApiResponseNew<LegacyPostResponse>>


    @GET(EndPoints.Container.LEGACY_POST)
    suspend fun getLegacyPost(
        @Query("type") type: Int,
        @Query("page") page: Int,
        @Query("perPage") perPage: Int,
    ): Response<ApiResponseNew<LegacyPostResponse>>

    @GET(EndPoints.Container.TRIBE_INNER_CIRCLE)
    suspend fun getTribeInnerCircleList(
        @Query("type") type: Int,
        @Query("page") page: Int,
        @Query("perPage") perPage: Int,
    ): Response<ApiResponseNew<LegacyPostResponse>>

    @GET(EndPoints.Container.POST_DETAILS)
    suspend fun getPostDetails(
        @Query("postId") postId: String,
    ): Response<ApiResponse<LegacyPostResponse>>

    @POST(EndPoints.Container.REPORT_POST_AND_USER)
    suspend fun reportUserAndPost(
        @Body reportUserPostReq: ReportUserPostReq
    ): Response<ApiResponse<LegacyPostResponse>>

    @POST(EndPoints.Container.ADD_COMMENT)
    suspend fun addComment(
        @Query("postId") postId: String,
        @Body addComment: AddCommentReq
    ): Response<ApiResponse<CommentResponse>>

    @PUT(EndPoints.Container.EDIT_PROFILE)
    @Multipart
    suspend fun updateProfileDetails(
        @PartMap req: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part profileImage: MultipartBody.Part?
    ): Response<ApiResponse<UserAuthResponse>>

    @POST(EndPoints.Container.CREATE_INNER_CIRCLE_AND_TRIBE)
    @Multipart
    suspend fun createInnerCircleAndTribe(
        @PartMap req: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part profileImage: MultipartBody.Part?
    ): Response<ApiResponse<TribeResponse>>

    @POST(EndPoints.Container.ADD_GROUP_MEMBER)
    @Multipart
    suspend fun addGroupMember(
        @PartMap req: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part profileImage: MultipartBody.Part?
    ): Response<ApiResponse<MessageResponse>>

    @PUT(EndPoints.Container.GROUP_DETAILS_UPDATE)
    @Multipart
    suspend fun updateGroupMember(
        @PartMap req: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part profileImage: MultipartBody.Part?
    ): Response<ApiResponse<MessageResponse>>


    @PUT(EndPoints.Main.NOTIFICATION_OFF_ON)
    suspend fun notificationOnOff(): Response<ApiResponse<UserAuthResponse>>

    @PUT(EndPoints.Container.PUBLIC_PRIVATE_PROFILE)
    suspend fun publicPrivateProfile(): Response<ApiResponse<UserAuthResponse>>


    @POST(EndPoints.Container.GROUP_DETAILS_UPDATE)
    @Multipart
    suspend fun groupDetailsUpdate(
        @PartMap req: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part profileImage: MultipartBody.Part?
    ): Response<ApiResponse<MessageResponse>>


    @POST(EndPoints.Container.LEGACY_POST_CREATE)
    @Multipart
    suspend fun legacyCreatePost(
        @PartMap req: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part media: List<MultipartBody.Part?>
    ): Response<ApiResponse<LegacyPostResponse>>

    @PUT(EndPoints.Container.UPDATE_LEGACY_POST)
    @Multipart
    suspend fun updateLegacyPost(
        @PartMap req: Map<String, @JvmSuppressWildcards RequestBody>,
    ): Response<ApiResponse<LegacyPostResponse>>

    @PUT(EndPoints.Container.ADD_IMAGE_TO_LEGACY_POST)
    @Multipart
    suspend fun addImageToLegacyPost(
        @PartMap req: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part media: List<MultipartBody.Part?>
    ): Response<ApiResponseNew<AddImageLegacyPostResponse>>

    @PUT(EndPoints.Container.EDIT_PROFILE)
    @Multipart
    suspend fun updateProfileDetailsWithOutImage(
        @PartMap req: Map<String, @JvmSuppressWildcards RequestBody>,
    ): Response<ApiResponse<UserAuthResponse>>


    @POST(EndPoints.Auth.LOGOUT)
    suspend fun doLogOut(
    ): Response<ApiResponse<UserAuthResponse>>

    //Delete account
    @DELETE(EndPoints.Auth.ACCOUNT_DELETE)
    suspend fun deleteAccount(): Response<ApiResponse<UserAuthResponse>>

    //home screen
    @GET(EndPoints.Main.HOME)
    suspend fun getHomeScreenData(): Response<ApiResponse<HomeScreenResponse>>

    @GET(EndPoints.Container.GET_NOTIFICATION)
    suspend fun getNotification(
        @Query("perPage") perPage: Int,
        @Query("page") page: Int
    ): Response<ApiResponseNew<NotificationResponse>>


    @GET(EndPoints.Container.GET_MESSAGE)
    suspend fun getMessage(
        @Query("perPage") perPage: Int,
        @Query("page") page: Int
    ): Response<ApiResponseNew<MessageTabResponse>>

    //single post delete
    @HTTP(method = "DELETE", path = EndPoints.Auth.SINGLE_POST_DELETE, hasBody = true)
    suspend fun singlePostDelete(@Body singlePostReq: SinglePostReq): Response<ApiResponse<LegacyPostResponse>>

    @HTTP(method = "DELETE", path = EndPoints.Auth.DELETE_LEGACY_POST, hasBody = true)
    suspend fun deleteLegacyPost(@Query("postId") postId: String): Response<ApiResponse<MessageResponse>>

    @PUT(EndPoints.Container.NOTIFICATION_ACCEPT_AND_REJECT)
    suspend fun acceptRejectNotification(
        @Query("type") type: String,
        @Query("tribeId") tribeId: String
    ): Response<ApiResponse<MessageResponse>>

    //home screen
    @GET(EndPoints.Container.USER_STORAGE)
    suspend fun userStorage(): Response<ApiResponse<StorageResponse>>

    @GET(EndPoints.Friends.BLOCK_MEMBER)
    suspend fun getBlockedMemberList(
        @Query("type") type: String,
        @Query("perPage") perPage: Int,
        @Query("page") page: Int,
    ): Response<ApiResponseNew<BlockUserResponse>>



    @GET(EndPoints.Container.COMMENT_LIST)
    suspend fun getCommentList(
        @Query("postId") postId: String,
        @Query("page") page: Int,
        @Query("perPage") perPage: Int
    ): Response<ApiResponseNew<CommentResponse>>


    @GET(EndPoints.Container.FRIEND_INFORMATION)
    suspend fun getFriendProfile(
        @Query("userId") userId: String,
        @Query("page") page: Int,
        @Query("perPage") perPage: Int
    ): Response<ApiResponse<FriendInfoResponse>>


    @POST(EndPoints.Container.ADD_ADVERTISEMENT)
    @Multipart
    suspend fun addAdvertisement(
        @PartMap req: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part photo: MultipartBody.Part?
    ): Response<ApiResponse<AdvertisementResponse>>

    @PUT(EndPoints.Container.UPDATE_ADVERTISEMENT)
    @Multipart
    suspend fun updateAdvertisement(
        @PartMap req: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part photo: MultipartBody.Part?
    ): Response<ApiResponse<AdvertisementResponse>>


    @GET(EndPoints.Container.GET_CHAT_MESSAGE)
    suspend fun getChatMessage(
        @Query("type") type: String,
        @Query("receiverId") receiverId: String?,
        @Query("groupId") groupId: String?,
        @Query("perPage") perPage: Int,
        @Query("page") page: Int
    ): Response<ApiResponseNew<ChatResponse>>

    @POST(EndPoints.Container.LIKE_DISLIKE)
    suspend fun likeDislikeAPICall(
        @Query("postId") postId: String,
    ): Response<ApiResponse<LegacyPostResponse>>

    @GET(EndPoints.Container.ADVERTISEMENT)
    suspend fun getAdvertisements(
        @Query("perPage") perPage: Int,
        @Query("page") page: Int
    ): Response<ApiResponseNew<AdvertisementResponse>>


    @GET(EndPoints.Container.HOME_ADVERTISEMENT)
    suspend fun getHomeAdvertisements(
        @Query("perPage") perPage: Int,
        @Query("page") page: Int
    ): Response<ApiResponseNew<AdvertisementResponse>>


    @GET(EndPoints.Main.MARK_AS_PLAYED)
    suspend fun markedAudioAsPlayed(@Query("date") date: String): Response<ApiResponse<Any>>

    //Archive screen
    @GET(EndPoints.Archive.ARCHIVE)
    suspend fun getArchiveScreenData(
        @Query("month") month: Int,
        @Query("year") year: Int
    ): Response<ApiResponse<ArchiveScreenResponse>>


    @GET(EndPoints.Friends.LIST_INNER_CIRCLE_AND_TRIBE)
    suspend fun getListInnerCircleAndTribe(
        @Query("type") type: Int,
        @Query("page") page: Int,
        @Query("perPage") perPage: Int
    ): Response<ApiResponseNew<TribeResponse>>

    @GET(EndPoints.Friends.SEARCH_FRIENDS)
    suspend fun getAllPeopleList(
        @Query("name") name: String,
        @Query("tribeId") tribeId: String,
        @Query("page") page: Int,
        @Query("perPage") perPage: Int
    ): Response<ApiResponseNew<SearchPeopleResponse>>


    @GET(EndPoints.Friends.ADD_GROUP_MEMBER)
    suspend fun getAddGroupMemberList(
        @Query("type") type: String,
        @Query("name") name: String?,
        @Query("groupId") groupId: String,
        @Query("perPage") perPage: Int,
        @Query("page") page: Int
    ): Response<ApiResponseNew<SearchPeopleResponse>>


    @GET(EndPoints.Friends.GROUP_MEMBER)
    suspend fun getGroupMember(
        @Query("groupId") groupId: String,
        @Query("name") name: String,
        @Query("perPage") perPage: Int,
        @Query("page") page: Int
    ): Response<ApiResponseNew<SearchPeopleResponse>>

    @GET(EndPoints.Friends.MEMBERS_INNER_CIRCLE_AND_TRIBE)
    suspend fun getMemberInnerCircleAndTribe(
        @Query("tribeId") tribeId: String,
        @Query("perPage") perPage: Int,
        @Query("page") page: Int,
    ): Response<ApiResponseNew<MemberResponse>>


    @PUT(EndPoints.Friends.ADD_MEMBERS_INNER_CIRCLE_AND_TRIBE)
    suspend fun addMembersInnerCircleAndTribe(
        @Body addMemberRequest: AddMemberRequest
    ): Response<ApiResponse<MessageResponse>>


    @PUT(EndPoints.Container.REMOVE_ADD_GROUP_MEMBER)
    suspend fun addRemoveGroupMember(
        @Body groupMemberRequest: GroupMemberRequest
    ): Response<ApiResponse<UnblockResponse>>

    @PUT(EndPoints.Container.IMAGE_POSITION_CHANGE)
    suspend fun imagePositionChange(
        @Body imagePositionReq: ImagePositionReq
    ): Response<ApiResponse<MessageResponse>>


    //Manage profile
    @POST(EndPoints.Auth.USER_UPDATE)
    suspend fun updateProfile(
        @Body updateProfileReq: UpdateProfileReq
    ): Response<ApiResponse<UserAuthResponse>>

    @POST(EndPoints.Support.FEEDBACK)
    suspend fun submitFeedback(
        @Body feedbackReq: FeedbackReq
    ): Response<ApiResponse<Any>>


    @POST(EndPoints.Notification.FCM_TOKEN)
    suspend fun storeFCMToken(
        @Body tokenStoreReq: TokenStoreReq
    ): Response<ApiResponse<Any>>

    //Subscription
    @POST(EndPoints.Subscription.SUBSCRIBE)
    suspend fun storePurchaseInfo(
        @Body subscriptionInfoReq: SubscriptionInfoReq
    ): Response<ApiResponse<SubscriptionResponse>>


    //Advertisement
    @POST(EndPoints.Subscription.ADVERTISEMENT_SUBSCRIPTION)
    suspend fun storeAdvertisementInfo(
        @Body subscriptionInfoReq: SubscriptionInfoReq
    ): Response<ApiResponse<SubscriptionResponse>>

    //Check Subscription
    @POST(EndPoints.Subscription.CHECK_SUBSCRIPTION)
    suspend fun checkSubscription(
        @Body checkSubscriptionReq: CheckSubscriptionReq
    ): Response<ApiResponse<SubscriptionResponse>>

    @GET(EndPoints.Container.LEGACY_REFLECTION)
    suspend fun legacyReflection(
        @Query("page") page: Int,
        @Query("perPage") perPage: Int
    ): Response<ApiResponseNew<FAQQuestionResponse>>

    @GET(EndPoints.Container.ADVERTISEMENT_VIEW)
    suspend fun getAdvertisementView(
        @Query("advertisementId") advertisementId: String,
    ): Response<ApiResponse<AdvertisementResponse>>

    @GET(EndPoints.Container.USER_TERMS)
    suspend fun getTermsAndConditions(
        @Query("type") type: String,
    ): Response<ApiResponse<TermsResponse>>

    @POST(EndPoints.Auth.FORCE_UPDATE)
    suspend fun checkAppUpdate(
        @Body appUpdateReq: AppUpdateRequest
    ): Response<ApiResponse<AppUpdateResponse>>

    @POST(EndPoints.Auth.BMI_CALCULATE)
    suspend fun calculateBmi(
        @Body bmiRequest: BmiCalculateRequest
    ): Response<ApiResponse<BmiCalculateResponse>>

}