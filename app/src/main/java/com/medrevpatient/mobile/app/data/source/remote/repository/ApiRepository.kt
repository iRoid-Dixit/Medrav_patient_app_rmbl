package com.medrevpatient.mobile.app.data.source.remote.repository

import androidx.paging.PagingData
import com.google.gson.JsonObject
import com.medrevpatient.mobile.app.data.source.remote.EndPoints
import com.medrevpatient.mobile.app.data.source.remote.dto.CompleteRestDay
import com.medrevpatient.mobile.app.data.source.remote.dto.DayExercises
import com.medrevpatient.mobile.app.data.source.remote.dto.ExerciseCompleted
import com.medrevpatient.mobile.app.data.source.remote.dto.ForMe
import com.medrevpatient.mobile.app.data.source.remote.dto.Goal
import com.medrevpatient.mobile.app.data.source.remote.dto.LastWeekStats
import com.medrevpatient.mobile.app.data.source.remote.dto.MyProgress
import com.medrevpatient.mobile.app.data.source.remote.dto.Note
import com.medrevpatient.mobile.app.data.source.remote.dto.OnDemandClasses
import com.medrevpatient.mobile.app.data.source.remote.dto.PinRequest
import com.medrevpatient.mobile.app.data.source.remote.dto.Program
import com.medrevpatient.mobile.app.data.source.remote.dto.ProgramWorkoutAndRecipesSearch
import com.medrevpatient.mobile.app.data.source.remote.dto.Recipe
import com.medrevpatient.mobile.app.data.source.remote.dto.Recipes
import com.medrevpatient.mobile.app.data.source.remote.dto.StrengthLog
import com.medrevpatient.mobile.app.data.source.remote.dto.StrengthLogExercises
import com.medrevpatient.mobile.app.data.source.remote.dto.TodayStats
import com.medrevpatient.mobile.app.data.source.remote.dto.ViewGoal
import com.medrevpatient.mobile.app.data.source.remote.helper.NetworkResult
import com.medrevpatient.mobile.app.domain.request.CreateUpdateReminderReq
import com.medrevpatient.mobile.app.domain.request.LoginReq
import com.medrevpatient.mobile.app.domain.request.RegisterForPushRequest
import com.medrevpatient.mobile.app.domain.request.ReportPostRequest
import com.medrevpatient.mobile.app.domain.request.ResetPasswordReq
import com.medrevpatient.mobile.app.domain.request.SendOTPReq
import com.medrevpatient.mobile.app.domain.request.SignUpReq
import com.medrevpatient.mobile.app.domain.request.UpdatePasswordReq
import com.medrevpatient.mobile.app.domain.request.VerifyOTPForUpdateReq
import com.medrevpatient.mobile.app.domain.request.VerifyOTPReq
import com.medrevpatient.mobile.app.domain.response.ApiListResponse
import com.medrevpatient.mobile.app.domain.response.ApiResponse
import com.medrevpatient.mobile.app.domain.response.AuthResponse
import com.medrevpatient.mobile.app.domain.response.CalendarResponse
import com.medrevpatient.mobile.app.domain.response.Comments
import com.medrevpatient.mobile.app.domain.response.CommonMessageResponse
import com.medrevpatient.mobile.app.domain.response.CommunityPosts
import com.medrevpatient.mobile.app.domain.response.HomeScreenResponse
import com.medrevpatient.mobile.app.domain.response.SubscribedProgramGoal
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface ApiRepository {
    fun signUp(signUpReq: SignUpReq): Flow<NetworkResult<ApiResponse<CommonMessageResponse>>>
    fun verifyOTP(verifyOTPReq: VerifyOTPReq): Flow<NetworkResult<ApiResponse<AuthResponse>>>
    fun sendOTP(sendOTPReq: SendOTPReq): Flow<NetworkResult<ApiResponse<Any>>>
    fun login(loginReq: LoginReq): Flow<NetworkResult<ApiResponse<AuthResponse>>>
    fun changePassword(changePasswordReq: ResetPasswordReq): Flow<NetworkResult<ApiResponse<Any>>>
    fun takeUserDetails(
        data: HashMap<String, RequestBody>,
        profileImage: MultipartBody.Part?
    ): Flow<NetworkResult<ApiResponse<AuthResponse>>>

    fun logout(): Flow<NetworkResult<ApiResponse<Any>>>
    fun deleteAccount(): Flow<NetworkResult<ApiResponse<Any>>>
    fun updateUserDetails(
        data: HashMap<String, RequestBody>,
        profileImage: MultipartBody.Part?
    ): Flow<NetworkResult<ApiResponse<AuthResponse>>>

    fun updateEmail(email: Map<String, String>): Flow<NetworkResult<ApiResponse<Any>>>
    fun verifyOTPForUpdateEmail(verifyOTPReq: VerifyOTPForUpdateReq): Flow<NetworkResult<ApiResponse<AuthResponse>>>
    fun updatePassword(updatePasswordReq: UpdatePasswordReq): Flow<NetworkResult<ApiResponse<Any>>>
    fun getHomeScreenData(): Flow<NetworkResult<ApiResponse<HomeScreenResponse>>>
    fun getCalendar(
        startDate: String,
        endDate: String
    ): Flow<NetworkResult<ApiListResponse<CalendarResponse>>>

    fun getProgramsGoalsForReminder(): Flow<NetworkResult<ApiListResponse<SubscribedProgramGoal>>>
    fun createUpdateReminder(data: CreateUpdateReminderReq): Flow<NetworkResult<ApiResponse<SubscribedProgramGoal>>>
    fun getAllCommunityPosts(): Flow<PagingData<CommunityPosts>>
    fun getAllComments(postId: String): Flow<PagingData<Comments>>
    fun commentOnPost(
        postId: String,
        comment: Map<String, String>
    ): Flow<NetworkResult<ApiResponse<Comments>>>

    fun getMyPosts(): Flow<PagingData<CommunityPosts>>
    fun likePost(postId: String): Flow<NetworkResult<ApiResponse<Any>>>
    fun createPost(
        data: HashMap<String, RequestBody>,
        images: List<MultipartBody.Part?>?
    ): Flow<NetworkResult<ApiResponse<CommunityPosts>>>

    fun acceptCommunityGuideline(): Flow<NetworkResult<ApiResponse<AuthResponse>>>
    fun deletePost(postId: String): Flow<NetworkResult<ApiResponse<Any>>>
    fun deletePostImage(imageId: String): Flow<NetworkResult<ApiResponse<Any>>>
    fun editPost(
        postId: String,
        data: HashMap<String, RequestBody>,
        images: List<MultipartBody.Part?>?
    ): Flow<NetworkResult<ApiResponse<CommunityPosts>>>

    //Main
    fun getPrograms(type: Int = EndPoints.ResultType.FOR_GENERAL.value): Flow<PagingData<Program>>
    fun getSearchedProgram(keyword: String): Flow<PagingData<ProgramWorkoutAndRecipesSearch>>
    fun getProgram(id: String): Flow<NetworkResult<ApiResponse<Program>>>
    fun getStrengthLog(): Flow<NetworkResult<ApiListResponse<StrengthLog>>>
    fun getStrengthLogExercises(): Flow<NetworkResult<ApiListResponse<StrengthLogExercises>>>
    fun getOnDemandClasses(type: Int = EndPoints.ResultType.FOR_GENERAL.value): Flow<PagingData<OnDemandClasses>>
    fun getSearchedRecipes(
        keyword: String = "",
        type: Int = EndPoints.ResultType.FOR_GENERAL.value,
        filterTag: String = "",
        apiPagingCallBack: APIPagingCallBack<List<String>>
    ): Flow<PagingData<Recipes.Data>>

    fun getRecipe(id: String): Flow<NetworkResult<ApiResponse<Recipe>>>
    fun pin(pinRequest: PinRequest): Flow<NetworkResult<CommonMessageResponse>>
    fun addStrengthLog(body: JsonObject): Flow<NetworkResult<ApiResponse<StrengthLog>>>
    fun getDayExercises(
        day: String,
        programId: String
    ): Flow<NetworkResult<ApiResponse<DayExercises>>>

    fun completeExercise(
        exerciseId: String,
        timeSpend: JsonObject
    ): Flow<NetworkResult<ApiResponse<ExerciseCompleted>>>

    fun completeRestDay(
        json: JsonObject
    ): Flow<NetworkResult<ApiResponse<CompleteRestDay>>>

    fun getForMe(): Flow<NetworkResult<ApiResponse<ForMe>>>


    //MY_PROGRESS
    fun getLastWeekStats(): Flow<NetworkResult<ApiListResponse<LastWeekStats>>>
    fun getTodayStats(): Flow<NetworkResult<ApiListResponse<TodayStats>>>
    fun getGoals(): Flow<NetworkResult<ApiListResponse<Goal>>>
    fun addLog(
        body: HashMap<String, RequestBody>,
        images: List<MultipartBody.Part>
    ): Flow<NetworkResult<ApiResponse<Goal>>>

    fun updateGoal(goalId: String, body: JsonObject): Flow<NetworkResult<String>>
    fun deleteGoal(goalId: String): Flow<NetworkResult<String>>

    fun getGoal(
        goalId: String,
        apiPagingCallBack: APIPagingCallBack<ViewGoal>
    ): Flow<PagingData<ViewGoal.Data.Log>>

    fun createGoal(
        body: HashMap<String, RequestBody>,
        images: List<MultipartBody.Part>
    ): Flow<NetworkResult<String>>

    fun myProgress(): Flow<NetworkResult<ApiResponse<MyProgress>>>

    //Notes
    fun getNotes(): Flow<PagingData<Note>>
    fun createNote(note: JsonObject): Flow<NetworkResult<ApiResponse<Note>>>
    fun deleteNote(noteId: String): Flow<NetworkResult<String>>
    fun updateNote(noteId: String, note: JsonObject): Flow<NetworkResult<String>>
    fun registerPush(data: RegisterForPushRequest): Flow<NetworkResult<ApiResponse<CommonMessageResponse>>>
    fun reportPost(data: ReportPostRequest): Flow<NetworkResult<ApiResponse<CommonMessageResponse>>>
}