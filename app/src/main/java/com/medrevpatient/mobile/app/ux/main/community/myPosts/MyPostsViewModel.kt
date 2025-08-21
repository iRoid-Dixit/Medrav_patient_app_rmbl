package com.medrevpatient.mobile.app.ux.main.community.myPosts

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.medrevpatient.mobile.app.data.source.remote.helper.NetworkResult
import com.medrevpatient.mobile.app.data.source.remote.repository.ApiRepository
import com.medrevpatient.mobile.app.domain.response.CommunityPosts
import com.medrevpatient.mobile.app.domain.response.LocalPostImages
import com.medrevpatient.mobile.app.navigation.NavRoute
import com.medrevpatient.mobile.app.navigation.NavigationAction
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
class MyPostsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: ApiRepository
) : ViewModel(), ViewModelNav by ViewModelNavImpl() {

    private val _uiState = MutableStateFlow(MyPostsUiState())
    val uiState = _uiState.asStateFlow()
    var myPosts: Flow<PagingData<CommunityPosts>> = MutableStateFlow(PagingData.empty())

    init {
        getMyPosts()
    }

    private fun getMyPosts() {
        myPosts = repository.getMyPosts().cachedIn(viewModelScope)
    }

    fun event(event: MyPostsUiEvent) {
        when (event) {
            is MyPostsUiEvent.NavigateTo -> {
                navigate(NavigationAction.Navigate(event.route))
            }

            is MyPostsUiEvent.ShowDeletePostDialog -> {
                _uiState.update {
                    it.copy(showDeletePostDialog = event.value, postId = event.postId)
                }
            }

            is MyPostsUiEvent.ShowDeletePostSuccessDialog -> {
                _uiState.update {
                    it.copy(showDeletePostSuccessDialog = event.value)
                }
            }

            is MyPostsUiEvent.DeletePost -> {
                callDeletePostApi()
            }

            is MyPostsUiEvent.PerformSeeMyPostsClick -> {
                _uiState.update {
                    it.copy(showDeletePostSuccessDialog = false)
                }
                myPosts = repository.getMyPosts().cachedIn(viewModelScope)
            }

            is MyPostsUiEvent.ShowEditPostDialog -> {
                _uiState.update {
                    it.copy(showEditPostDialog = event.value, postId = event.postId)
                }
            }

            is MyPostsUiEvent.PostImages -> {
                _uiState.update {
                    it.copy(postImages = event.files)
                }
            }

            is MyPostsUiEvent.OnPostContentValueChange -> {
                _uiState.update {
                    it.copy(postContentValue = event.value)
                }
            }

            is MyPostsUiEvent.ClearImageList -> {
                _uiState.update {
                    it.copy(postImages = arrayListOf())
                }
            }

            is MyPostsUiEvent.PerformUpdatePostClick -> {
                callEditPostApi()
            }

            is MyPostsUiEvent.RefreshPostList -> {
                myPosts = repository.getMyPosts().cachedIn(viewModelScope)
            }

            is MyPostsUiEvent.StoreAPIContentValue -> {
                _uiState.update {
                    it.copy(postContentFromAPIValue = event.value)
                }
            }

            is MyPostsUiEvent.ManageDeleteImages -> {
                _uiState.update {
                    val updatedDeleteImages = ArrayList(it.deleteImages)
                    updatedDeleteImages.add(event.id)
                    it.copy(deleteImages = updatedDeleteImages)
                }
            }

            is MyPostsUiEvent.EnableUpdateButton -> {
                _uiState.update { it.copy(enableUpdateButton = event.value) }
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
                            it1.copy(
                                showDeletePostSuccessDialog = true,
                                showDeletePostDialog = false
                            )
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
                            it1.copy(
                                isFromDeleteImage = true
                            )
                        }
                    }
                }
            }
        }
    }*/

    private fun callEditPostApi() {
        viewModelScope.launch {
            val req = HashMap<String, RequestBody>()
            if (uiState.value.postContentValue.isNotEmpty()) req[Constants.RequestParams.CONTENT] = uiState.value.postContentValue.toRequestBody("multipart/form-data".toMediaTypeOrNull())
            if (uiState.value.deleteImages.isNotEmpty()) req[Constants.RequestParams.DELETE_IMAGES] = uiState.value.deleteImages.joinToString(",").toRequestBody("multipart/form-data".toMediaTypeOrNull())

            val postImageFiles = if (uiState.value.postImages.isNotEmpty()) AppUtils.createMultipartBody(uiState.value.postImages.map { it.url }, Constants.RequestParams.IMAGES, context) else arrayListOf()

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
                                enableUpdateButton = false
                            )
                        }
                        myPosts = repository.getMyPosts().cachedIn(viewModelScope)
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

data class MyPostsUiState(
    val showDeletePostDialog: Boolean = false,
    val showDeletePostSuccessDialog: Boolean = false,
    val isLoading: Boolean = false,
    val postId: String = "",
    val showEditPostDialog: Boolean = false,
    val onShowPermissionDialog: (Boolean) -> Unit = {},
    val showPermissionDialog: StateFlow<Boolean> = MutableStateFlow(false),
    val postImages: List<LocalPostImages> = arrayListOf(),
    val postContentValue: String = "",
    val postContentFromAPIValue: String = "",
    val deleteImages: ArrayList<String> = arrayListOf(),
    val enableUpdateButton: Boolean = false
)

sealed interface MyPostsUiEvent {
    data class NavigateTo(val route: NavRoute) : MyPostsUiEvent
    data class ShowDeletePostDialog(val value: Boolean, val postId: String) : MyPostsUiEvent
    data class ShowDeletePostSuccessDialog(val value: Boolean) : MyPostsUiEvent
    data class ShowEditPostDialog(val value: Boolean, val postId: String) : MyPostsUiEvent
    data class DeletePost(val postId: String) : MyPostsUiEvent
    data object PerformSeeMyPostsClick : MyPostsUiEvent
    data class PostImages(val files: List<LocalPostImages>) : MyPostsUiEvent
    data class OnPostContentValueChange(val value: String) : MyPostsUiEvent
    data class StoreAPIContentValue(val value: String) : MyPostsUiEvent
    data object ClearImageList : MyPostsUiEvent
    data object PerformUpdatePostClick : MyPostsUiEvent
    data object RefreshPostList : MyPostsUiEvent
    data class ManageDeleteImages(val id: String) : MyPostsUiEvent
    data class EnableUpdateButton(val value: Boolean) : MyPostsUiEvent
}