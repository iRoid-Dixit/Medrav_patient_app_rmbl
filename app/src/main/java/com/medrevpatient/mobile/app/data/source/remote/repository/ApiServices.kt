package com.medrevpatient.mobile.app.data.source.remote.repository

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
import com.medrevpatient.mobile.app.domain.response.ApiResponseNew
import com.medrevpatient.mobile.app.domain.response.AuthResponse
import com.medrevpatient.mobile.app.domain.response.CalendarResponse
import com.medrevpatient.mobile.app.domain.response.Comments
import com.medrevpatient.mobile.app.domain.response.CommonMessageResponse
import com.medrevpatient.mobile.app.domain.response.CommunityPosts
import com.medrevpatient.mobile.app.domain.response.HomeScreenResponse
import com.medrevpatient.mobile.app.domain.response.SubscribedProgramGoal
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Path
import retrofit2.http.Query
import java.time.Instant

interface ApiServices {
    /* Auth and Profile Section*/
    @POST(EndPoints.Auth.SIGN_UP)
    suspend fun signUp(@Body signUpReq: SignUpReq): Response<ApiResponse<CommonMessageResponse>>

    @POST(EndPoints.Auth.OTP_VERIFY)
    suspend fun verifyOTP(@Body verifyOTPReq: VerifyOTPReq): Response<ApiResponse<AuthResponse>>

    @POST(EndPoints.Auth.SEND_OTP)
    suspend fun sendOTP(@Body sendOTPReq: SendOTPReq): Response<ApiResponse<Any>>

    @POST(EndPoints.Auth.SIGN_IN)
    suspend fun login(@Body signInReq: LoginReq): Response<ApiResponse<AuthResponse>>

    @POST(EndPoints.Auth.CHANGE_PASSWORD)
    suspend fun changePassword(@Body changePasswordReq: ResetPasswordReq): Response<ApiResponse<Any>>

    @Multipart
    @POST(EndPoints.Auth.TAKE_USER_DETAILS)
    suspend fun takeUserDetails(
        @PartMap onBoardingReq: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part profileImage: MultipartBody.Part?
    ): Response<ApiResponse<AuthResponse>>

    @POST(EndPoints.Auth.LOG_OUT)
    suspend fun logout(): Response<ApiResponse<Any>>

    @DELETE(EndPoints.Auth.DELETE_ACCOUNT)
    suspend fun deleteAccount(): Response<ApiResponse<Any>>

    @Multipart
    @PUT(EndPoints.Auth.UPDATE_USER_DETAILS)
    suspend fun updateUserDetails(
        @PartMap onBoardingReq: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part profileImage: MultipartBody.Part?
    ): Response<ApiResponse<AuthResponse>>

    @POST(EndPoints.Auth.UPDATE_EMAIL)
    suspend fun updateEmail(@Body email: Map<String, String>): Response<ApiResponse<Any>>

    @POST(EndPoints.Auth.VERIFY_OTP_FOR_UPDATE_EMAIL)
    suspend fun verifyOTPForUpdateEmail(@Body verifyOTPReq: VerifyOTPForUpdateReq): Response<ApiResponse<AuthResponse>>

    @PUT(EndPoints.Auth.UPDATE_PASSWORD)
    suspend fun updatePassword(@Body updatePasswordReq: UpdatePasswordReq): Response<ApiResponse<Any>>

    @GET(EndPoints.Home.HOME)
    suspend fun getHomeData(): Response<ApiResponse<HomeScreenResponse>>

    @GET(EndPoints.Auth.CALENDAR)
    suspend fun getCalendar(
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String
    ): Response<ApiListResponse<CalendarResponse>>

    @GET(EndPoints.Auth.GET_PROGRAMS_GOALS_FOR_REMINDER)
    suspend fun getProgramsGoalsForReminder(): Response<ApiListResponse<SubscribedProgramGoal>>

    @POST(EndPoints.Auth.CREATE_UPDATE_REMINDER)
    suspend fun createUpdateReminder(@Body req: CreateUpdateReminderReq): Response<ApiResponse<SubscribedProgramGoal>>

    /* Community Section*/
    @GET(EndPoints.Community.GET_ALL_POSTS)
    suspend fun getAllCommunityPost(
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Response<ApiResponseNew<CommunityPosts>>

    @GET(EndPoints.Community.GET_COMMENTS)
    suspend fun getAllComments(
        @Path("postId") postId: String,
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Response<ApiResponseNew<Comments>>

    @POST(EndPoints.Community.COMMENT_ON_POST)
    suspend fun commentOnPost(
        @Path("postId") postId: String,
        @Body comment: Map<String, String>
    ): Response<ApiResponse<Comments>>

    @GET(EndPoints.Community.GET_MY_POST)
    suspend fun getMyPost(
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Response<ApiResponseNew<CommunityPosts>>

    @POST(EndPoints.Community.LIKE_POST)
    suspend fun likePost(@Path("postId") postId: String): Response<ApiResponse<Any>>

    @Multipart
    @POST(EndPoints.Community.CREATE_POST)
    suspend fun createPost(
        @PartMap req: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part images: List<MultipartBody.Part?>?
    ): Response<ApiResponse<CommunityPosts>>

    @POST(EndPoints.Community.ACCEPT_COMMUNITY_GUIDE_LINE)
    suspend fun acceptCommunityGuideLine(): Response<ApiResponse<AuthResponse>>

    @DELETE(EndPoints.Community.COMMUNITY_DELETE_POST)
    suspend fun deletePost(@Path("postId") postId: String): Response<ApiResponse<Any>>

    @DELETE(EndPoints.Community.COMMUNITY_DELETE_POST_IMAGE)
    suspend fun deletePostImage(@Path("imageId") imageId: String): Response<ApiResponse<Any>>

    @Multipart
    @PUT(EndPoints.Community.COMMUNITY_EDIT_POST)
    suspend fun editPost(
        @Path("postId") postId: String,
        @PartMap req: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part images: List<MultipartBody.Part?>?
    ): Response<ApiResponse<CommunityPosts>>

    /* Program and Progress Section*/
    @GET(EndPoints.Main.PROGRAMS)
    suspend fun getPrograms(
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("type") type: Int
    ): Response<ApiResponseNew<Program>>

    @GET(EndPoints.Main.SEARCH_PROGRAMS_AND_RECIPES)
    suspend fun searchProgramWorkoutAndRecipes(
        @Query("searchString") keyword: String,
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Response<ApiResponseNew<ProgramWorkoutAndRecipesSearch>>


    @GET(EndPoints.Main.PROGRAM_BY_ID)
    suspend fun getProgram(@Path("programId") programId: String): Response<ApiResponse<Program>>

    @GET(EndPoints.Main.STRENGTH_LOG)
    suspend fun getStrengthLog(): Response<ApiListResponse<StrengthLog>>

    @GET(EndPoints.Main.STRENGTH_LOG_EXERCISES)
    suspend fun getStrengthLogExercises(): Response<ApiListResponse<StrengthLogExercises>>

    @GET(EndPoints.Main.ON_DEMAND_CLASSES)
    suspend fun getOnDemandClasses(
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("type") type: Int
    ): Response<ApiListResponse<OnDemandClasses>>


    @GET(EndPoints.Main.SEARCH_RECIPE)
    suspend fun searchRecipes(
        @Query("searchString") keyword: String,
        @Query("filter") filter: String,
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("type") type: Int
    ): Response<Recipes>

    @POST(EndPoints.Main.PIN)
    suspend fun pin(@Body pinRequest: PinRequest): Response<CommonMessageResponse>

    @GET(EndPoints.Main.RECIPE)
    suspend fun recipe(@Path("recipeId") recipeId: String): Response<ApiResponse<Recipe>>

    @POST(EndPoints.Main.ADD_STRENGTH_LOG)
    suspend fun addStrengthLog(@Body body: JsonObject): Response<ApiResponse<StrengthLog>>

    @GET(EndPoints.Main.DAY_EXERCISES)
    suspend fun getDayExercises(
        @Path("day") day: String,
        @Path("programId") programId: String
    ): Response<ApiResponse<DayExercises>>

    @POST(EndPoints.Main.EXERCISE_COMPLETED)
    suspend fun completeExercise(
        @Path("exerciseId") exerciseId: String,
        @Body timeSpend: JsonObject
    ): Response<ApiResponse<ExerciseCompleted>>


    @GET(EndPoints.Main.FOR_ME)
    suspend fun getForMe(): Response<ApiResponse<ForMe>>


    @GET(EndPoints.Main.LAST_WEEK_STATS)
    suspend fun getLastWeekStats(): Response<ApiListResponse<LastWeekStats>>

    @GET(EndPoints.Main.TODAY_STATS)
    suspend fun getTodayStats(): Response<ApiListResponse<TodayStats>>

    @GET(EndPoints.Main.GOALS)
    suspend fun getGoals(): Response<ApiListResponse<Goal>>

    @Multipart
    @POST(EndPoints.Main.ADD_LOG)
    suspend fun addLog(
        @PartMap request: HashMap<String, @JvmSuppressWildcards RequestBody>,
        @Part images: List<MultipartBody.Part>
    ): Response<ApiResponse<Goal>>

    @Multipart
    @POST(EndPoints.Main.CREATE_GOAL)
    suspend fun createGoal(
        @PartMap request: HashMap<String, @JvmSuppressWildcards RequestBody>,
        @Part images: List<MultipartBody.Part>
    ): Response<String>

    @PUT(EndPoints.Main.UPDATE_GOAL)
    suspend fun updateGoal(@Path("goalId") goalId: String, @Body body: JsonObject): Response<String>

    @DELETE(EndPoints.Main.DELETE_GOAL)
    suspend fun deleteGoal(@Path("goalId") goalId: String): Response<String>

    @GET(EndPoints.Main.VIEW_GOAL)
    suspend fun viewGoal(
        @Path("goalId") goalId: String,
        @Query("page") page: Int,
        @Query("limit") limit: Int,
    ): Response<ViewGoal>


    @GET(EndPoints.Main.MY_PROGRESS)
    suspend fun myProgress(
        @Query("utcDate") currentUtcDate: Long = Instant.now().toEpochMilli()
    ): Response<ApiResponse<MyProgress>>

    @POST(EndPoints.Auth.REGISTER_PUSH)
    suspend fun registerForPush(@Body request: RegisterForPushRequest): Response<ApiResponse<CommonMessageResponse>>

    @POST(EndPoints.Community.COMMUNITY_REPORT_POST)
    suspend fun reportPost(@Body request: ReportPostRequest): Response<ApiResponse<CommonMessageResponse>>

    @GET(EndPoints.Main.GET_NOTES)
    suspend fun getNotes(
        @Query("page") page: Int,
        @Query("limit") limit: Int,
    ): Response<ApiListResponse<Note>>


    @POST(EndPoints.Main.CREATE_NOTE)
    suspend fun createNote(@Body note: JsonObject): Response<ApiResponse<Note>>

    @DELETE(EndPoints.Main.DELETE_NOTE)
    suspend fun deleteNote(@Path("notesId") noteId: String): Response<String>

    @PUT(EndPoints.Main.UPDATE_NOTE)
    suspend fun updateNote(
        @Path("notesId") noteId: String,
        @Body note: JsonObject
    ): Response<String>

    @POST(EndPoints.Main.COMPLETE_REST_DAY)
    suspend fun completeRestDay(@Body body: JsonObject): Response<ApiResponse<CompleteRestDay>>
}
