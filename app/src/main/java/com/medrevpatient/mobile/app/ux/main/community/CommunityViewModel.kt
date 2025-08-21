package com.medrevpatient.mobile.app.ux.main.community

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.data.source.local.datastore.LocalManager
import com.medrevpatient.mobile.app.data.source.remote.helper.NetworkResult
import com.medrevpatient.mobile.app.data.source.remote.repository.ApiRepository
import com.medrevpatient.mobile.app.domain.request.ReportPostRequest
import com.medrevpatient.mobile.app.domain.response.AuthResponse
import com.medrevpatient.mobile.app.domain.response.Comments
import com.medrevpatient.mobile.app.domain.response.CommunityPosts
import com.medrevpatient.mobile.app.navigation.NavRoute
import com.medrevpatient.mobile.app.navigation.NavigationAction
import com.medrevpatient.mobile.app.navigation.RouteMaker
import com.medrevpatient.mobile.app.navigation.ViewModelNav
import com.medrevpatient.mobile.app.navigation.ViewModelNavImpl
import com.medrevpatient.mobile.app.utils.AppUtils
import com.medrevpatient.mobile.app.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

@HiltViewModel
class CommunityViewModel
@Inject constructor(
    @ApplicationContext private val context: Context,
    private val localManager: LocalManager,
    private val apiRepository: ApiRepository
) : ViewModel(), ViewModelNav by ViewModelNavImpl() {

    private val _uiState = MutableStateFlow(CommunityUiState())
    val uiState = _uiState.asStateFlow()
    var communityPosts: Flow<PagingData<CommunityPosts>> = MutableStateFlow(PagingData.empty())

    init {
        getAcceptedGuidelineValue()
    }

    //val communityPosts = repository.getAllCommunityPosts().cachedIn(viewModelScope)
    var getComments: Flow<PagingData<Comments>> = MutableStateFlow(PagingData.empty())

    fun event(event: CommunityUiEvent) {
        when (event) {
            is CommunityUiEvent.PerformExploreClick -> {
                navigate(NavigationAction.Navigate(RouteMaker.CommunityDisclaimer.createRoute()))
            }

            is CommunityUiEvent.ShowMenuDialog -> {
                _uiState.update {
                    it.copy(showMenuOptionsSheet = event.value)
                }
            }

            is CommunityUiEvent.ShowCreatePostDialog -> {
                _uiState.update {
                    it.copy(showCreatePostSheet = event.value)
                }
            }

            is CommunityUiEvent.ShowPostPreviewDialog -> {
                _uiState.update {
                    it.copy(showPostPreviewSheet = event.value)
                }
            }

            is CommunityUiEvent.IsCommentsDialogOpen -> {
                if (event.postId != "") getComments = apiRepository.getAllComments(event.postId).cachedIn(viewModelScope)
                _uiState.update {
                    it.copy(callGetCommentsAPI = event.value, showCommentsSheet = event.value, postId = event.postId)
                }
            }

            is CommunityUiEvent.PerformSendCommentClick -> {
                callPostCommentApi(postId = event.postId)
            }

            is CommunityUiEvent.OnCommentPostValueChange -> {
                _uiState.update {
                    it.copy(commentToPost = event.value)
                }
            }

            is CommunityUiEvent.PostImages -> {
                _uiState.update {
                    it.copy(postImages = event.files)
                }
            }

            is CommunityUiEvent.PerformLikePostClick -> {
                callLikePostApi(postId = event.postId)
            }

            is CommunityUiEvent.OnPostContentValueChange -> {
                _uiState.update {
                    it.copy(postContentValue = event.value)
                }
            }

            is CommunityUiEvent.PerformCreatePostClick -> {
                callCreatePostApi()
            }

            is CommunityUiEvent.NavigateToMyPosts -> {
                navigate(NavigationAction.Navigate(RouteMaker.MyPosts.createRoute()))
            }

            is CommunityUiEvent.ShowReportPostDialog -> {
                _uiState.update { it.copy(showReportPostSheet = event.value, postId = event.postId) }
            }

            is CommunityUiEvent.OnReportPostValueChange -> {
                _uiState.update { it.copy(reportPostContentValue = event.value) }
            }

            is CommunityUiEvent.OnReportPostReasonSelection -> {
                _uiState.update { it.copy(reportPostReason = event.value) }
            }

            is CommunityUiEvent.PerformReportPostClick -> {
                callReportPostApi()
            }

            is CommunityUiEvent.IsUserBlockedByAdmin -> {
                _uiState.update { it.copy(isUserBlockedByAdmin = event.value) }
            }
        }
    }

    fun navigate(navRoute: NavRoute) {
        navigate(NavigationAction.Navigate(navRoute))
    }

    fun getAcceptedGuidelineValue() {
        viewModelScope.launch {
            val userData = localManager.retrieveUserData()
            if (userData != null) {
                val isGuidelinesAccepted = userData.isCommunityGuidelineAccepted
                _uiState.update { it.copy(isCommunityGuidelinesAccepted = isGuidelinesAccepted, userData = userData) }
                communityPosts = apiRepository.getAllCommunityPosts().cachedIn(viewModelScope)
            }
        }
    }

    private fun showOrHideLoader(isLoading: Boolean) {
        _uiState.update { state ->
            state.copy(
                isLoading = isLoading
            )
        }
    }

    private fun callPostCommentApi(postId: String) {
        viewModelScope.launch {
            if (uiState.value.commentToPost.isEmpty()) {
                AppUtils.Toast(context, context.getString(R.string.comment_empty_error)).show()
                return@launch
            }
            val req: HashMap<String, String> = HashMap()
            req[Constants.RequestParams.COMMENT_TEXT] = uiState.value.commentToPost
            apiRepository.commentOnPost(postId = postId, comment = req).collect {
                when (it) {
                    is NetworkResult.Error -> {
                        showOrHideLoader(false)
                        AppUtils.Toast(context, it.message ?: "Something went wrong!").show()
                    }

                    is NetworkResult.Loading -> {
                        showOrHideLoader(true)
                    }

                    is NetworkResult.Success -> {
                        showOrHideLoader(false)
                        AppUtils.Toast(context, it.data?.message ?: "Success").show()
                        getComments = apiRepository.getAllComments(uiState.value.postId).cachedIn(viewModelScope)
                        communityPosts = apiRepository.getAllCommunityPosts().cachedIn(viewModelScope)
                        event(CommunityUiEvent.OnCommentPostValueChange(""))
                    }
                }
            }
        }
    }

    private fun callLikePostApi(postId: String) {
        viewModelScope.launch {
            apiRepository.likePost(postId = postId).collect {
                when (it) {
                    is NetworkResult.Error -> {
                        //showOrHideLoader(false)
                        AppUtils.Toast(context, it.message ?: "Something went wrong!").show()
                    }

                    is NetworkResult.Loading -> {
                        //showOrHideLoader(true)
                        _uiState.update { it1 ->
                            it1.copy(isUpdateLike = true)
                        }

                    }

                    is NetworkResult.Success -> {
                        //showOrHideLoader(false)
                        _uiState.update { it1 ->
                            it1.copy(isUpdateLike = false)
                        }
                    }
                }
            }
        }
    }

    private fun callCreatePostApi() {
        viewModelScope.launch {
            val req = HashMap<String, RequestBody>()
            if (uiState.value.postContentValue.isNotEmpty()) req[Constants.RequestParams.CONTENT] = uiState.value.postContentValue.toRequestBody("multipart/form-data".toMediaTypeOrNull())

            val postImageFiles = if (uiState.value.postImages.isNotEmpty()) AppUtils.createMultipartBody(uiState.value.postImages, Constants.RequestParams.IMAGES, context) else null

            apiRepository.createPost(req, postImageFiles).collect {
                when (it) {
                    is NetworkResult.Error -> {
                        showOrHideLoader(false)
                        AppUtils.Toast(context, it.message ?: "Something went wrong!").show()
                    }

                    is NetworkResult.Loading -> {
                        showOrHideLoader(true)
                    }

                    is NetworkResult.Success -> {
                        showOrHideLoader(false)
                        AppUtils.Toast(context, it.data?.message ?: "Success").show()
                        communityPosts = apiRepository.getAllCommunityPosts().cachedIn(viewModelScope)
                        _uiState.update { it1 ->
                            it1.copy(showPostPreviewSheet = false, showCreatePostSheet = false, showMenuOptionsSheet = false)
                        }
                        event(CommunityUiEvent.OnPostContentValueChange(""))
                    }
                }
            }
        }
    }

    private fun callReportPostApi() {
        viewModelScope.launch {
            if (uiState.value.reportPostReason == 0) {
                AppUtils.Toast(context, context.getString(R.string.select_reason_error)).show()
                return@launch
            } else if (uiState.value.reportPostReason == 9 && uiState.value.reportPostContentValue.isEmpty()) {
                AppUtils.Toast(context, context.getString(R.string.report_content_empty_error)).show()
                return@launch
            }
            val req = ReportPostRequest(postId = uiState.value.postId, reason = uiState.value.reportPostReason, otherReasonText = uiState.value.reportPostContentValue)

            apiRepository.reportPost(req).collect {
                when (it) {
                    is NetworkResult.Error -> {
                        showOrHideLoader(false)
                        AppUtils.Toast(context, it.message ?: "Something went wrong!").show()
                    }

                    is NetworkResult.Loading -> {
                        showOrHideLoader(true)
                    }

                    is NetworkResult.Success -> {
                        showOrHideLoader(false)
                        AppUtils.Toast(context, it.data?.message ?: "Success").show()
                        _uiState.update { it1 -> it1.copy(showReportPostSheet = false) }
                    }
                }
            }
        }
    }
}

data class CommunityUiState(
    val event: (CommunityUiEvent) -> Unit = {},
    val userData: AuthResponse = AuthResponse(),
    val isCommunityGuidelinesAccepted: Boolean = false,
    val showMenuOptionsSheet: Boolean = false,
    val showCreatePostSheet: Boolean = false,
    val showPostPreviewSheet: Boolean = false,
    val showCommentsSheet: Boolean = false,
    val showReportPostSheet: Boolean = false,
    val callGetCommentsAPI: Boolean = false,
    val isLoading: Boolean = false,
    val isUpdateLike: Boolean = false,
    val commentToPost: String = "",
    val postId: String = "",
    val onShowPermissionDialog: (Boolean) -> Unit = {},
    val showPermissionDialog: StateFlow<Boolean> = MutableStateFlow(false),
    val postImages: List<Uri> = arrayListOf(),
    val postContentValue: String = "",
    val reportPostContentValue: String = "",
    val reportPostReason: Int = 0,
    val isUserBlockedByAdmin: Boolean = false
)

sealed interface CommunityUiEvent {
    data object PerformExploreClick : CommunityUiEvent
    data class PerformSendCommentClick(val postId: String) : CommunityUiEvent
    data class PerformLikePostClick(val postId: String) : CommunityUiEvent
    data object PerformCreatePostClick : CommunityUiEvent
    data class ShowMenuDialog(val value: Boolean) : CommunityUiEvent
    data class ShowCreatePostDialog(val value: Boolean) : CommunityUiEvent
    data class ShowPostPreviewDialog(val value: Boolean) : CommunityUiEvent
    data class ShowReportPostDialog(val value: Boolean, val postId: String) : CommunityUiEvent
    data class IsCommentsDialogOpen(val value: Boolean, val postId: String) : CommunityUiEvent
    data class OnCommentPostValueChange(val value: String) : CommunityUiEvent
    data class OnPostContentValueChange(val value: String) : CommunityUiEvent
    data class OnReportPostValueChange(val value: String) : CommunityUiEvent
    data class OnReportPostReasonSelection(val value: Int) : CommunityUiEvent
    data class PostImages(val files: List<Uri>) : CommunityUiEvent
    data object NavigateToMyPosts : CommunityUiEvent
    data object PerformReportPostClick : CommunityUiEvent
    data class IsUserBlockedByAdmin(val value: Boolean) : CommunityUiEvent
}