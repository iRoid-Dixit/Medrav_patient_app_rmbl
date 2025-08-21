package com.medrevpatient.mobile.app.data.source.remote.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.google.gson.JsonObject
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
import com.medrevpatient.mobile.app.data.source.remote.pagging.CommentsPagingSource
import com.medrevpatient.mobile.app.data.source.remote.pagging.CommunityPostPagingSource
import com.medrevpatient.mobile.app.data.source.remote.pagging.NotesPagingSource
import com.medrevpatient.mobile.app.data.source.remote.pagging.OnDemandClassesPagingSource
import com.medrevpatient.mobile.app.data.source.remote.pagging.ProgramPagingSource
import com.medrevpatient.mobile.app.data.source.remote.pagging.ProgramWorkoutRecipesSearchPagingSource
import com.medrevpatient.mobile.app.data.source.remote.pagging.RecipesSearchPagingSource
import com.medrevpatient.mobile.app.data.source.remote.pagging.ViewGoalPagingSource
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
import com.medrevpatient.mobile.app.utils.Constants
import com.medrevpatient.mobile.app.utils.Constants.Paging.CATCH_SIZE
import com.medrevpatient.mobile.app.utils.Constants.Paging.PER_PAGE
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject


class ApiRepositoryImpl @Inject constructor(
    private val apiServices: ApiServices
) : ApiRepository, ApiCallHelper() {

    override fun signUp(signUpReq: SignUpReq): Flow<NetworkResult<ApiResponse<CommonMessageResponse>>> =
        lazyCall {
            apiServices.signUp(signUpReq)
        }

    override fun verifyOTP(verifyOTPReq: VerifyOTPReq): Flow<NetworkResult<ApiResponse<AuthResponse>>> =
        lazyCall {
            apiServices.verifyOTP(verifyOTPReq)
        }

    override fun sendOTP(sendOTPReq: SendOTPReq): Flow<NetworkResult<ApiResponse<Any>>> = lazyCall {
        apiServices.sendOTP(sendOTPReq)
    }

    override fun login(loginReq: LoginReq): Flow<NetworkResult<ApiResponse<AuthResponse>>> =
        lazyCall {
            apiServices.login(loginReq)
        }

    override fun changePassword(changePasswordReq: ResetPasswordReq): Flow<NetworkResult<ApiResponse<Any>>> =
        lazyCall {
            apiServices.changePassword(changePasswordReq)
        }

    override fun takeUserDetails(
        data: HashMap<String, RequestBody>,
        profileImage: MultipartBody.Part?
    ): Flow<NetworkResult<ApiResponse<AuthResponse>>> = lazyCall {
        apiServices.takeUserDetails(data, profileImage)
    }

    override fun logout(): Flow<NetworkResult<ApiResponse<Any>>> = lazyCall {
        apiServices.logout()
    }

    override fun deleteAccount(): Flow<NetworkResult<ApiResponse<Any>>> = lazyCall {
        apiServices.deleteAccount()
    }

    override fun updateUserDetails(
        data: HashMap<String, RequestBody>,
        profileImage: MultipartBody.Part?
    ): Flow<NetworkResult<ApiResponse<AuthResponse>>> = lazyCall {
        apiServices.updateUserDetails(data, profileImage)
    }

    override fun updateEmail(email: Map<String, String>): Flow<NetworkResult<ApiResponse<Any>>> =
        lazyCall {
            apiServices.updateEmail(email)
        }

    override fun verifyOTPForUpdateEmail(verifyOTPReq: VerifyOTPForUpdateReq): Flow<NetworkResult<ApiResponse<AuthResponse>>> =
        lazyCall {
            apiServices.verifyOTPForUpdateEmail(verifyOTPReq)
        }

    override fun updatePassword(updatePasswordReq: UpdatePasswordReq): Flow<NetworkResult<ApiResponse<Any>>> =
        lazyCall {
            apiServices.updatePassword(updatePasswordReq)
        }

    override fun getHomeScreenData(): Flow<NetworkResult<ApiResponse<HomeScreenResponse>>> =
        lazyCall {
            apiServices.getHomeData()
        }

    override fun getCalendar(
        startDate: String,
        endDate: String
    ): Flow<NetworkResult<ApiListResponse<CalendarResponse>>> = lazyCall {
        apiServices.getCalendar(startDate, endDate)
    }

    override fun getProgramsGoalsForReminder(): Flow<NetworkResult<ApiListResponse<SubscribedProgramGoal>>> =
        lazyCall {
            apiServices.getProgramsGoalsForReminder()
        }

    override fun createUpdateReminder(data: CreateUpdateReminderReq): Flow<NetworkResult<ApiResponse<SubscribedProgramGoal>>> =
        lazyCall {
            apiServices.createUpdateReminder(data)
        }

    override fun getAllCommunityPosts(): Flow<PagingData<CommunityPosts>> =
        Pager(
            config = PagingConfig(
                pageSize = PER_PAGE,
                maxSize = CATCH_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                CommunityPostPagingSource(
                    apiServices = apiServices,
                    type = Constants.Paging.TYPE_ALL_POST
                )
            }
        ).flow

    override fun getAllComments(postId: String): Flow<PagingData<Comments>> =
        Pager(
            config = PagingConfig(
                pageSize = PER_PAGE,
                maxSize = CATCH_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                CommentsPagingSource(
                    postId = postId,
                    apiServices = apiServices
                )
            }
        ).flow

    override fun commentOnPost(
        postId: String,
        comment: Map<String, String>
    ): Flow<NetworkResult<ApiResponse<Comments>>> = lazyCall {
        apiServices.commentOnPost(postId, comment)
    }

    override fun getMyPosts(): Flow<PagingData<CommunityPosts>> =
        Pager(
            config = PagingConfig(
                pageSize = PER_PAGE,
                maxSize = CATCH_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                CommunityPostPagingSource(
                    apiServices = apiServices,
                    type = Constants.Paging.TYPE_MY_POST
                )
            }
        ).flow


    override fun likePost(postId: String): Flow<NetworkResult<ApiResponse<Any>>> = lazyCall {
        apiServices.likePost(postId)
    }

    override fun createPost(
        data: HashMap<String, RequestBody>,
        images: List<MultipartBody.Part?>?
    ): Flow<NetworkResult<ApiResponse<CommunityPosts>>> = lazyCall {
        apiServices.createPost(data, images)
    }

    override fun acceptCommunityGuideline(): Flow<NetworkResult<ApiResponse<AuthResponse>>> =
        lazyCall {
            apiServices.acceptCommunityGuideLine()
        }

    override fun deletePost(postId: String): Flow<NetworkResult<ApiResponse<Any>>> = lazyCall {
        apiServices.deletePost(postId)
    }

    override fun deletePostImage(imageId: String): Flow<NetworkResult<ApiResponse<Any>>> =
        lazyCall {
            apiServices.deletePostImage(imageId)
        }

    override fun editPost(
        postId: String,
        data: HashMap<String, RequestBody>,
        images: List<MultipartBody.Part?>?
    ): Flow<NetworkResult<ApiResponse<CommunityPosts>>> = lazyCall {
        apiServices.editPost(postId, data, images)
    }

    //Program And Workout screen
    override fun getPrograms(
        type: Int
    ): Flow<PagingData<Program>> =
        Pager(
            config = PagingConfig(
                pageSize = PER_PAGE,
                maxSize = CATCH_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { ProgramPagingSource(apiServices = apiServices, type = type) }
        ).flow


    override fun completeRestDay(json: JsonObject): Flow<NetworkResult<ApiResponse<CompleteRestDay>>> =
        lazyCall { apiServices.completeRestDay(json) }

    override fun getSearchedProgram(
        keyword: String,
    ): Flow<PagingData<ProgramWorkoutAndRecipesSearch>> =
        Pager(
            config = PagingConfig(
                pageSize = PER_PAGE,
                maxSize = CATCH_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { ProgramWorkoutRecipesSearchPagingSource(keyword, apiServices) }
        ).flow

    override fun getOnDemandClasses(
        type: Int
    ): Flow<PagingData<OnDemandClasses>> = Pager(
        config = PagingConfig(
            pageSize = PER_PAGE,
            maxSize = CATCH_SIZE,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            OnDemandClassesPagingSource(
                apiServices = apiServices,
                type = type
            )
        }
    ).flow

    override fun getSearchedRecipes(
        keyword: String,
        type: Int,
        filterTag: String,
        apiPagingCallBack: APIPagingCallBack<List<String>>
    ): Flow<PagingData<Recipes.Data>> = Pager(
        config = PagingConfig(
            pageSize = PER_PAGE,
            maxSize = CATCH_SIZE,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            RecipesSearchPagingSource(
                keyword = keyword,
                filterTag,
                type,
                apiServices,
                apiCallBack = apiPagingCallBack
            )
        }
    ).flow

    override fun getProgram(id: String): Flow<NetworkResult<ApiResponse<Program>>> =
        lazyCall { apiServices.getProgram(programId = id) }

    override fun getStrengthLog(): Flow<NetworkResult<ApiListResponse<StrengthLog>>> =
        lazyCall { apiServices.getStrengthLog() }

    override fun getStrengthLogExercises(): Flow<NetworkResult<ApiListResponse<StrengthLogExercises>>> =
        lazyCall { apiServices.getStrengthLogExercises() }

    override fun pin(pinRequest: PinRequest): Flow<NetworkResult<CommonMessageResponse>> =
        lazyCall { apiServices.pin(pinRequest) }

    override fun getRecipe(id: String): Flow<NetworkResult<ApiResponse<Recipe>>> =
        lazyCall { apiServices.recipe(id) }

    override fun addStrengthLog(body: JsonObject): Flow<NetworkResult<ApiResponse<StrengthLog>>> =
        lazyCall { apiServices.addStrengthLog(body) }

    override fun getDayExercises(
        day: String,
        programId: String
    ): Flow<NetworkResult<ApiResponse<DayExercises>>> =
        lazyCall { apiServices.getDayExercises(day, programId) }

    override fun completeExercise(
        exerciseId: String,
        timeSpend: JsonObject
    ): Flow<NetworkResult<ApiResponse<ExerciseCompleted>>> =
        lazyCall { apiServices.completeExercise(exerciseId, timeSpend) }

    override fun getForMe(): Flow<NetworkResult<ApiResponse<ForMe>>> =
        lazyCall { apiServices.getForMe() }


    override fun getLastWeekStats(): Flow<NetworkResult<ApiListResponse<LastWeekStats>>> =
        lazyCall { apiServices.getLastWeekStats() }

    override fun getTodayStats(): Flow<NetworkResult<ApiListResponse<TodayStats>>> =
        lazyCall { apiServices.getTodayStats() }

    override fun getGoals(): Flow<NetworkResult<ApiListResponse<Goal>>> =
        lazyCall { apiServices.getGoals() }

    override fun addLog(
        body: HashMap<String, RequestBody>,
        images: List<MultipartBody.Part>
    ): Flow<NetworkResult<ApiResponse<Goal>>> =
        lazyCall { apiServices.addLog(body, images) }

    override fun updateGoal(goalId: String, body: JsonObject): Flow<NetworkResult<String>> =
        lazyCall { apiServices.updateGoal(goalId, body) }

    override fun deleteGoal(goalId: String): Flow<NetworkResult<String>> =
        lazyCall { apiServices.deleteGoal(goalId) }


    override fun getGoal(
        goalId: String,
        apiPagingCallBack: APIPagingCallBack<ViewGoal>
    ): Flow<PagingData<ViewGoal.Data.Log>> {
        return Pager(
            config = PagingConfig(
                pageSize = PER_PAGE,
                maxSize = CATCH_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                ViewGoalPagingSource(
                    goalId = goalId,
                    apiServices = apiServices,
                    apiPagingCallBack = apiPagingCallBack
                )
            }
        ).flow
    }

    override fun createGoal(
        body: HashMap<String, RequestBody>,
        images: List<MultipartBody.Part>
    ): Flow<NetworkResult<String>> =
        lazyCall { apiServices.createGoal(body, images) }

    override fun myProgress(): Flow<NetworkResult<ApiResponse<MyProgress>>> =
        lazyCall { apiServices.myProgress() }

    override fun getNotes(): Flow<PagingData<Note>> {
        return Pager(
            config = PagingConfig(
                pageSize = PER_PAGE,
                maxSize = CATCH_SIZE,
            ),
            pagingSourceFactory = {
                NotesPagingSource(
                    apiServices = apiServices,
                )
            }
        ).flow
    }

    override fun createNote(note: JsonObject): Flow<NetworkResult<ApiResponse<Note>>> =
        lazyCall { apiServices.createNote(note) }

    override fun deleteNote(noteId: String): Flow<NetworkResult<String>> = lazyCall {
        apiServices.deleteNote(noteId)
    }

    override fun updateNote(noteId: String, note: JsonObject): Flow<NetworkResult<String>> =
        lazyCall {
            apiServices.updateNote(noteId, note)
        }

    override fun registerPush(data: RegisterForPushRequest): Flow<NetworkResult<ApiResponse<CommonMessageResponse>>> =
        lazyCall {
            apiServices.registerForPush(data)
        }

    override fun reportPost(data: ReportPostRequest): Flow<NetworkResult<ApiResponse<CommonMessageResponse>>> =
        lazyCall {
            apiServices.reportPost(data)
        }
}