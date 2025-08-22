package com.medrevpatient.mobile.app.ux.container.postDetails

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medrevpatient.mobile.app.navigation.ViewModelNav
import com.medrevpatient.mobile.app.navigation.ViewModelNavImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class PostDetailsViewModel
@Inject constructor(
    @ApplicationContext context: Context,
    postDetailsUiStateUseCase: PostDetailsUiStateUseCase,
) : ViewModel(), ViewModelNav by ViewModelNavImpl() {
    val uiState: PostDetailsUiState = postDetailsUiStateUseCase(context = context, coroutineScope = viewModelScope) { navigate(it) }
}