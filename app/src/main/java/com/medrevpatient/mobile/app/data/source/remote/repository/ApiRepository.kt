package com.medrevpatient.mobile.app.data.source.remote.repository

import androidx.paging.PagingData
import com.medrevpatient.mobile.app.data.source.remote.helper.NetworkResult
import com.medrevpatient.mobile.app.data.source.remote.paging.ApiCallback
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
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface ApiRepository {
    /** Authentication flow apis */
    fun doLogin(signInRequest: LogInRequest): Flow<NetworkResult<ApiResponse<UserAuthResponse>>>


    fun doSignUp(signUpReq: SignUpReq): Flow<NetworkResult<ApiResponse<UserAuthResponse>>>

    fun verifyOTP(verifyOTPReq: VerifyOTPReq): Flow<NetworkResult<ApiResponse<UserAuthResponse>>>

    fun forgetPassword(forgetPasswordReq: ForgetPasswordReq): Flow<NetworkResult<ApiResponse<UserAuthResponse>>>

    fun resetPassword(resetPasswordReq: ResetPasswordReq): Flow<NetworkResult<ApiResponse<UserAuthResponse>>>

    fun resendOtpOTP(sendOTPReq: ResendOTPReq): Flow<NetworkResult<ApiResponse<Any>>>

    fun doLogout(): Flow<NetworkResult<ApiResponse<UserAuthResponse>>>

    fun deleteAccount(): Flow<NetworkResult<ApiResponse<UserAuthResponse>>>

    /**
     * single image delete
     * */
    fun singlePostDelete(singlePostReq: SinglePostReq): Flow<NetworkResult<ApiResponse<LegacyPostResponse>>>

    /**
     * changePassword Screen
     * */
    fun doChangePassword(changePasswordReq: ChangePasswordReq): Flow<NetworkResult<ApiResponse<UserAuthResponse>>>


    /**
     * rejectNotification
     * */
    fun acceptRejectNotification(
        type: String,
        tribeId: String
    ): Flow<NetworkResult<ApiResponse<MessageResponse>>>


    /**
     * contactUs Screen
     * */
    fun doContactUs(contactUsReq: ContactUsReq): Flow<NetworkResult<ApiResponse<UserAuthResponse>>>

    /**
     * faq Question Screen
     * */


    fun getFaqQuestion(): Flow<PagingData<FAQQuestionResponse>>


    /**
     * Reflection Question Screen
     * */


    fun getReflection(): Flow<PagingData<FAQQuestionResponse>>

    /**
     * main village page
     * */


    fun getMainVillage(): Flow<PagingData<LegacyPostResponse>>

    /**
     * deleteLegacyPost
     * */


    fun deleteLegacyPost(postId: String): Flow<NetworkResult<ApiResponse<MessageResponse>>>

    /**
     * legacy post Screen
     * */
    fun getLegacyPost(type:Int): Flow<PagingData<LegacyPostResponse>>

    /**
     * friend information
     * */

    fun getFriendProfile(
        userId: String,
        page: Int
    ): Flow<NetworkResult<ApiResponse<FriendInfoResponse>>>


    /**
     * tribe inner circle List
     * */
    fun getInnerCircleTribe(type: Int): Flow<PagingData<LegacyPostResponse>>

    /**
     * post details Screen
     * */
    fun getPostDetails(postId:String): Flow<NetworkResult<ApiResponse<LegacyPostResponse>>>

    fun likeDislike(postId:String): Flow<NetworkResult<ApiResponse<LegacyPostResponse>>>

    /**
     * get block list
     * */
    fun getBlockList(type: String): Flow<PagingData<BlockUserResponse>>

    /**
     * edit profile Screen
     * */
    fun editProfileDetails(req: Map<String, RequestBody>, profileImage: MultipartBody.Part?): Flow<NetworkResult<ApiResponse<UserAuthResponse>>>

    /**
     * create inner circle and tribe Screen
     * */
    fun createInnerCircleAndTribe(
        req: Map<String, RequestBody>,
        profileImage: MultipartBody.Part?
    ): Flow<NetworkResult<ApiResponse<TribeResponse>>>

    fun addGroupMember(
        req: Map<String, RequestBody>,
        profileImage: MultipartBody.Part?
    ): Flow<NetworkResult<ApiResponse<MessageResponse>>>


    fun updateGroupMember(
        req: Map<String, RequestBody>,
        profileImage: MultipartBody.Part?
    ): Flow<NetworkResult<ApiResponse<MessageResponse>>>





    /**
     * group details Update
     * */
    fun groupDetailsUpdate(
        req: Map<String, RequestBody>,
        profileImage: MultipartBody.Part?
    ): Flow<NetworkResult<ApiResponse<MessageResponse>>>

    /**
     * notification on off
     * */
    fun notificationOnOff(): Flow<NetworkResult<ApiResponse<UserAuthResponse>>>

    /**
     * public private on off
     * */
    fun publicPrivateProfileOnOff(): Flow<NetworkResult<ApiResponse<UserAuthResponse>>>

    /**
     *add Advertisement
     * */
    fun addAdvertisement(
        req: Map<String, RequestBody>,
        photo: MultipartBody.Part?
    ): Flow<NetworkResult<ApiResponse<AdvertisementResponse>>>

    /**
     *update Advertisement
     * */

    fun updateAdvertisement(
        req: Map<String, RequestBody>,
        photo: MultipartBody.Part?
    ): Flow<NetworkResult<ApiResponse<AdvertisementResponse>>>


    fun createLegacyPost(
        req: Map<String, RequestBody>,
        media: List<MultipartBody.Part?>
    ): Flow<NetworkResult<ApiResponse<LegacyPostResponse>>>

    fun updateLegacyPost(
        req: Map<String, RequestBody>,
    ): Flow<NetworkResult<ApiResponse<LegacyPostResponse>>>

    fun addImageToLegacyPost(
        req: Map<String, RequestBody>,
        media: List<MultipartBody.Part?>
    ): Flow<NetworkResult<ApiResponseNew<AddImageLegacyPostResponse>>>

    fun editProfileDetailsWithOutImage(req: Map<String, RequestBody>): Flow<NetworkResult<ApiResponse<UserAuthResponse>>>

    /**
     * comment  Screen
     * */

    fun getCommentList(postId:String,apiCallback: ApiCallback?): Flow<PagingData<CommentResponse>>

    /**
     * get notification
     * */
    fun getNotification(): Flow<PagingData<NotificationResponse>>


    /**
     * get messageList
     * */
    fun getMessage(): Flow<PagingData<MessageTabResponse>>

    /**
     * get Advertisements
     * */

    fun getAdvertisements(): Flow<PagingData<AdvertisementResponse>>


    /**
     * ge homet Advertisements
     * */

    fun getHomeAdvertisements(): Flow<PagingData<AdvertisementResponse>>

    /**
     * add comment  Screen
     * */

    fun addComment(postId: String,addCommentReq: AddCommentReq): Flow<NetworkResult<ApiResponse<CommentResponse>>>


    /**
     * get message Screen
     * */

    fun getChatMessage(
        type: String,
        receiverId: String?,
        groupId: String?,
        page: Int,
    ): Flow<NetworkResult<ApiResponseNew<ChatResponse>>>


    /**
     * inner circle and tribe block and leave
     * */

    fun innerCircleTribeBlockAndLeave(
        type: String,
        userId: String,
        tribeId: String?
    ): Flow<NetworkResult<ApiResponse<UnblockResponse>>>

    fun reportUserAndPost(reportUserPostReq: ReportUserPostReq): Flow<NetworkResult<ApiResponse<LegacyPostResponse>>>

    fun markedAudioAsPlayed(date: String): Flow<NetworkResult<ApiResponse<Any>>>

    /**
     * add and remove group member
     * */
    fun addRemoveGroupMember(groupMemberRequest: GroupMemberRequest): Flow<NetworkResult<ApiResponse<UnblockResponse>>>


    /** Home Screen */
    fun getHomeScreenData(): Flow<NetworkResult<ApiResponse<HomeScreenResponse>>>

    /** user storage */
    fun userStorage(): Flow<NetworkResult<ApiResponse<StorageResponse>>>

    fun addMemberData(addTribeMemberReq: AddMemberRequest): Flow<NetworkResult<ApiResponse<MessageResponse>>>

    /** image position change  */
    fun imagePositionChange(imagePositionReq: ImagePositionReq): Flow<NetworkResult<ApiResponse<MessageResponse>>>

    fun getArchiveScreenData(month: Int, year: Int): Flow<NetworkResult<ApiResponse<ArchiveScreenResponse>>>
    /**
     * Paging Request
     * */


    fun getTribeList(type:Int): Flow<PagingData<TribeResponse>>


    fun getTribeMemberList(tribeId:String): Flow<PagingData<MemberResponse>>
    fun getAllPeopleList(
        searchName: String,
        tribeId: String
    ): Flow<PagingData<SearchPeopleResponse>>

    /**
     * add Group Member
     * */


    fun getAddGroupMember(
        type: String,
        name: String?,
        groupId: String,

        ): Flow<PagingData<SearchPeopleResponse>>

    /**
     * Group Member
     * */

    fun getGroupMember(
        groupId: String,
        name: String
    ): Flow<PagingData<SearchPeopleResponse>>


    /**
     * Manage profile
     * */
    fun updateProfile(updateProfileReq: UpdateProfileReq): Flow<NetworkResult<ApiResponse<UserAuthResponse>>>
    /**
     * Feed back
     * */
    fun submitFeedback(feedbackReq: FeedbackReq): Flow<NetworkResult<ApiResponse<Any>>>
    /**
     * Push notification token store
     * */
    fun storeFCMToken(tokenStoreReq: TokenStoreReq): Flow<NetworkResult<ApiResponse<Any>>>

    /**
     * Subscription
     * */
    fun storePurchaseInfo(subscriptionInfoReq: SubscriptionInfoReq): Flow<NetworkResult<ApiResponse<SubscriptionResponse>>>

    /**
     * Advertisement Subscription
     * */
    fun storeAdvertisementInfo(subscriptionInfoReq: SubscriptionInfoReq): Flow<NetworkResult<ApiResponse<SubscriptionResponse>>>

    /**
     * Check Subscription
     * */
    fun checkSubscription(checkSubscriptionReq: CheckSubscriptionReq): Flow<NetworkResult<ApiResponse<SubscriptionResponse>>>

    /**
     * Check Subscription
     * */
    fun getAdvertisementView(advertisementId: String): Flow<NetworkResult<ApiResponse<AdvertisementResponse>>>

    /**
     * Get Terms and Conditions
     * */
    fun getTermsAndConditions(type: String): Flow<NetworkResult<ApiResponse<TermsResponse>>>

    /**
     * App Update
     * */
    fun checkAppUpdate(appUpdateRequest: AppUpdateRequest): Flow<NetworkResult<ApiResponse<AppUpdateResponse>>>

}