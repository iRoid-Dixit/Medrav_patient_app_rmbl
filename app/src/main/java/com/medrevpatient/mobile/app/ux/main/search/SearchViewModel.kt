package com.medrevpatient.mobile.app.ux.main.search

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medrevpatient.mobile.app.navigation.ViewModelNav
import com.medrevpatient.mobile.app.navigation.ViewModelNavImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class SearchViewModel
@Inject constructor(
    @ApplicationContext context: Context,
    getSearchUiStateUseCase: GetSearchUiStateUseCase
) : ViewModel(), ViewModelNav by ViewModelNavImpl() {
    val uiState: SearchUiState =
        getSearchUiStateUseCase(context = context, coroutineScope = viewModelScope) { navigate(it) }
}