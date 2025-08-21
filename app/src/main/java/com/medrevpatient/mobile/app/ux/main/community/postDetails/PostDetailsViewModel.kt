package com.medrevpatient.mobile.app.ux.main.community.postDetails

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.google.gson.Gson
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.data.source.remote.helper.NetworkResult
import com.medrevpatient.mobile.app.data.source.remote.repository.ApiRepository
import com.medrevpatient.mobile.app.domain.response.Comments
import com.medrevpatient.mobile.app.domain.response.CommunityPosts
import com.medrevpatient.mobile.app.domain.response.LocalPostImages
import com.medrevpatient.mobile.app.navigation.NavRoute
import com.medrevpatient.mobile.app.navigation.NavigationAction
import com.medrevpatient.mobile.app.navigation.PopResultKeyValue
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
class PostDetailsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    savedStateHandle: SavedStateHandle,
    private val repository: ApiRepository
) : ViewModel(), ViewModelNav by ViewModelNavImpl() {

    private val _uiState = MutableStateFlow(PostDetailsUiState())
    val uiState = _uiState.asStateFlow()

    private val postDataString = requireNotNull(savedStateHandle.get<String>(RouteMaker.Keys.POST_DATA))
    private val postData = Gson().fromJson(postDataString, CommunityPosts::class.java)
    var getComments: Flow<PagingData<Comments>> = MutableStateFlow(PagingData.empty())

    init {
        _uiState.update { it.copy(postData = postData) }
    }

    fun event(event: PostDetailsUiEvent) {
        when (event) {
            is PostDetailsUiEvent.NavigateTo -> {
                navigate(NavigationAction.Navigate(event.navRoute))
            }

            is PostDetailsUiEvent.PostImages -> {
                _uiState.update {
                    it.copy(postImages = event.files)
                }
            }

            is PostDetailsUiEvent.OnPostContentValueChange -> {
                _uiState.update {
                    it.copy(postContentValue = event.value)
                }
            }

            is PostDetailsUiEvent.ShowEditPostDialog -> {
                _uiState.update {
                    it.copy(showEditPostDialog = event.value, postId = event.postId, showMenuOptionsSheet = false)
                }
            }

            is PostDetailsUiEvent.ClearImageList -> {
                _uiState.update {
                    it.copy(postImages = arrayListOf())
                }
            }

            is PostDetailsUiEvent.PerformUpdatePostClick -> {
                callEditPostApi()
            }

            is PostDetailsUiEvent.ShowMenuDialog -> {
                _uiState.update {
                    it.copy(showMenuOptionsSheet = event.value)
                }
            }

            is PostDetailsUiEvent.ShowDeletePostDialog -> {
                _uiState.update {
                    it.copy(showDeletePostDialog = event.value, postId = event.postId)
                }
            }

            is PostDetailsUiEvent.ShowDeletePostSuccessDialog -> {
                _uiState.update {
                    it.copy(showDeletePostSuccessDialog = event.value)
                }
            }

            is PostDetailsUiEvent.PerformSeeMyPostsClick -> {
                _uiState.update {
                    it.copy(showDeletePostSuccessDialog = false)
                }
                this@PostDetailsViewModel.popBackStackWithResult(resultValues = listOf(PopResultKeyValue(RouteMaker.Keys.REFRESH, true)))
            }

            is PostDetailsUiEvent.DeletePost -> {
                callDeletePostApi()
            }

            is PostDetailsUiEvent.IsCommentsDialogOpen -> {
                if (event.postId != "") getComments = repository.getAllComments(event.postId).cachedIn(viewModelScope)
                _uiState.update {
                    it.copy(callGetCommentsAPI = event.value, showCommentsSheet = event.value, postId = event.postId)
                }
            }

            is PostDetailsUiEvent.PerformSendCommentClick -> {
                callPostCommentApi(postId = event.postId)
            }

            is PostDetailsUiEvent.OnCommentPostValueChange -> {
                _uiState.update {
                    it.copy(commentToPost = event.value)
                }
            }

            is PostDetailsUiEvent.PerformLikePostClick -> {
                callLikePostApi(postId = event.postId)
            }

            is PostDetailsUiEvent.StoreAPIContentValue -> {
                _uiState.update {
                    it.copy(postContentFromAPIValue = event.value)
                }
            }

            is PostDetailsUiEvent.ManageDeleteImages -> {
                _uiState.update {
                    val updatedDeleteImages = ArrayList(it.deleteImages)
                    updatedDeleteImages.add(event.id)
                    it.copy(deleteImages = updatedDeleteImages)
                }
            }

            is PostDetailsUiEvent.EnableUpdateButton -> {
                _uiState.update { it.copy(enableUpdateButton = event.value) }
            }
        }
    }

    private fun callEditPostApi() {
        viewModelScope.launch {
            val req = HashMap<String, RequestBody>()
            if (uiState.value.postContentValue.isNotEmpty()) req[Constants.RequestParams.CONTENT] = uiState.value.postContentValue.toRequestBody("multipart/form-data".toMediaTypeOrNull())
            if (uiState.value.deleteImages.isNotEmpty()) req[Constants.RequestParams.DELETE_IMAGES] = Gson().toJson(uiState.value.deleteImages).toRequestBody("multipart/form-data".toMediaTypeOrNull())

            val postImageFiles = if (uiState.value.postImages.isNotEmpty()) AppUtils.createMultipartBody(uiState.value.postImages.map { it.url }, Constants.RequestParams.IMAGES, context) else null

            repository.editPost(uiState.value.postId, req, postImageFiles).collect {
                when (it) {
                    is NetworkResult.Error -> {
                        showOrHideLoader(false)
                        AppUtils.Toast(context, it.message ?: "Something went wrong!")
                    }

                    is NetworkResult.Loading -> {
                        showOrHideLoader(true)
                    }

                    is NetworkResult.Success -> {
                        showOrHideLoader(false)
                        AppUtils.Toast(context, it.data?.message ?: "Post updated successfully!")
                        _uiState.update { it1 ->
                            it1.copy(
                                showEditPostDialog = false,
                                postContentValue = "",
                                postImages = arrayListOf(),
                                postData = it.data?.data ?: CommunityPosts(),
                                enableUpdateButton = false,
                                isAnythingChanged = true
                            )
                        }
                    }
                }
            }
        }
    }

    private fun callDeletePostApi() {
        viewModelScope.launch {
            repository.deletePost(postId = uiState.value.postId).collect {
                when (it) {
                    is NetworkResult.Error -> {
                        showOrHideLoader(false)
                        AppUtils.Toast(context, it.message ?: "Something went wrong!")
                    }

                    is NetworkResult.Loading -> {
                        showOrHideLoader(true)
                    }

                    is NetworkResult.Success -> {
                        showOrHideLoader(false)
                        _uiState.update { it1 ->
                            it1.copy(showDeletePostSuccessDialog = true, showDeletePostDialog = false)
                        }
                    }
                }
            }
        }
    }

    /*fun callDeletePostImageApi(imageId: String) {
        viewModelScope.launch {
            repository.deletePostImage(imageId = imageId).collect {
                when (it) {
                    is NetworkResult.Error -> {
                        showOrHideLoader(false)
                        AppUtils.Toast(context, it.message ?: "Something went wrong!")
                    }

                    is NetworkResult.Loading -> {
                        showOrHideLoader(true)
                    }

                    is NetworkResult.Success -> {
                        showOrHideLoader(false)
                        _uiState.update { it1 ->
                            it1.copy(isFromDeleteImage = true)
                        }
                    }
                }
            }
        }
    }*/

    private fun callPostCommentApi(postId: String) {
        viewModelScope.launch {
            if (uiState.value.commentToPost.isEmpty()) {
                AppUtils.Toast(context, context.getString(R.string.comment_empty_error))
                return@launch
            }
            val req: HashMap<String, String> = HashMap()
            req[Constants.RequestParams.COMMENT_TEXT] = uiState.value.commentToPost
            repository.commentOnPost(postId = postId, comment = req).collect {
                when (it) {
                    is NetworkResult.Error -> {
                        showOrHideLoader(false)
                        AppUtils.Toast(context, it.message ?: "Something went wrong!")
                    }

                    is NetworkResult.Loading -> {
                        showOrHideLoader(true)
                    }

                    is NetworkResult.Success -> {
                        showOrHideLoader(false)
                        AppUtils.Toast(context, it.data?.message ?: "Success")
                        getComments = repository.getAllComments(uiState.value.postId).cachedIn(viewModelScope)
                        event(PostDetailsUiEvent.OnCommentPostValueChange(""))
                        _uiState.update { it1 -> it1.copy(postData = it1.postData.copy(comments = it1.postData.comments?.plus(1)), isAnythingChanged = true) }
                    }
                }
            }
        }
    }

    private fun callLikePostApi(postId: String) {
        viewModelScope.launch {
            repository.likePost(postId = postId).collect {
                when (it) {
                    is NetworkResult.Error -> {
                        AppUtils.Toast(context, it.message ?: "Something went wrong!").show()
                    }

                    is NetworkResult.Loading -> {}

                    is NetworkResult.Success -> {
                        _uiState.update { it1 ->
                            it1.copy(
                                postData = it1.postData.copy(
                                    isLike = !it1.postData.isLike,
                                    likes = if (it1.postData.isLike) it1.postData.likes?.minus(1) else it1.postData.likes?.plus(1),
                                ), isAnythingChanged = true
                            )
                        }
                    }
                }
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
}

data class PostDetailsUiState(
    val postData: CommunityPosts = CommunityPosts(),
    val showDeletePostDialog: Boolean = false,
    val showDeletePostSuccessDialog: Boolean = false,
    val postId: String = "",
    val onShowPermissionDialog: (Boolean) -> Unit = {},
    val showPermissionDialog: StateFlow<Boolean> = MutableStateFlow(false),
    val postImages: List<LocalPostImages> = arrayListOf(),
    val postContentValue: String = "",
    val showEditPostDialog: Boolean = false,
    val showMenuOptionsSheet: Boolean = false,
    val isLoading: Boolean = false,
    val showCommentsSheet: Boolean = false,
    val callGetCommentsAPI: Boolean = false,
    val commentToPost: String = "",
    val updatedCommentCount: Int = 0,
    val postContentFromAPIValue: String = "",
    val deleteImages: ArrayList<String> = arrayListOf(),
    val enableUpdateButton: Boolean = false,
    val isAnythingChanged: Boolean = false

)

sealed interface PostDetailsUiEvent {
    data class NavigateTo(val navRoute: NavRoute) : PostDetailsUiEvent
    data class PostImages(val files: List<LocalPostImages>) : PostDetailsUiEvent
    data class OnPostContentValueChange(val value: String) : PostDetailsUiEvent
    data class ShowEditPostDialog(val value: Boolean, val postId: String) : PostDetailsUiEvent
    data class ShowMenuDialog(val value: Boolean) : PostDetailsUiEvent
    data object ClearImageList : PostDetailsUiEvent
    data object PerformUpdatePostClick : PostDetailsUiEvent
    data class ShowDeletePostDialog(val value: Boolean, val postId: String) : PostDetailsUiEvent
    data class ShowDeletePostSuccessDialog(val value: Boolean) : PostDetailsUiEvent
    data object PerformSeeMyPostsClick : PostDetailsUiEvent
    data class DeletePost(val postId: String) : PostDetailsUiEvent
    data class IsCommentsDialogOpen(val value: Boolean, val postId: String) : PostDetailsUiEvent
    data class OnCommentPostValueChange(val value: String) : PostDetailsUiEvent
    data class PerformSendCommentClick(val postId: String) : PostDetailsUiEvent
    data class PerformLikePostClick(val postId: String) : PostDetailsUiEvent //Like post
    data class StoreAPIContentValue(val value: String) : PostDetailsUiEvent
    data class ManageDeleteImages(val id: String) : PostDetailsUiEvent
    data class EnableUpdateButton(val value: Boolean) : PostDetailsUiEvent
}