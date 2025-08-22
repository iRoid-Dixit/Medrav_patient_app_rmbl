package com.griotlegacy.mobile.app.ux.container.myCircle

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.griotlegacy.mobile.app.navigation.ViewModelNav
import com.griotlegacy.mobile.app.navigation.ViewModelNavImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class MyCircleViewModel
@Inject constructor(
    @ApplicationContext context: Context,
    getMyCircleUiStateUseCase: GetMyCircleUiStateUseCase,
) : ViewModel(), ViewModelNav by ViewModelNavImpl() {
    val uiState: MyCircleUiState = getMyCircleUiStateUseCase(context = context, coroutineScope = viewModelScope) { navigate(it) }
}