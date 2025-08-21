package com.medrevpatient.mobile.app.ux.startup.onboarding.onBoardDataFlow

import android.net.Uri
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class OnBoardDataUiState(
    //Events
    val onNextClick: () -> Unit = {},
    val onPrevClick: () -> Unit = {},
    val onSkipClick: () -> Unit = {},
    val event: (OnBoardDataUiEvent) -> Unit = {},
    val currentStepCounter: StateFlow<Int> = MutableStateFlow(0),
    val isLoading: StateFlow<Boolean> = MutableStateFlow(false),
    val age: StateFlow<String> = MutableStateFlow(""),
    val height: StateFlow<Int> = MutableStateFlow(-1),
    val heightInches: StateFlow<Int> = MutableStateFlow(-1),
    val weight: StateFlow<Int> = MutableStateFlow(-1),
    val bodyType: StateFlow<Int> = MutableStateFlow(-1),
    val energyLevel: StateFlow<Int> = MutableStateFlow(-1),
    val lifeStyle: StateFlow<Int> = MutableStateFlow(-1),
    val fitnessLevel: StateFlow<Int> = MutableStateFlow(-1),
    val goals: StateFlow<Int> = MutableStateFlow(-1),
    val profileImage: StateFlow<Uri> = MutableStateFlow(Uri.EMPTY),
    val showPermissionDialog: StateFlow<Boolean> = MutableStateFlow(false),
    val onShowPermissionDialog: (Boolean) -> Unit = {},
)

sealed interface OnBoardDataUiEvent {
    data class AgeValueChange(val age: String) : OnBoardDataUiEvent
    data class HeightValueChange(val height: Int) : OnBoardDataUiEvent
    data class HeightInchValueChange(val heightInches: Int) : OnBoardDataUiEvent
    data class WeightValueChange(val weight: Int) : OnBoardDataUiEvent
    data class BodyTypeValueChange(val bodyType: Int) : OnBoardDataUiEvent
    data class EnergyLevelValueChange(val energyLevel: Int) : OnBoardDataUiEvent
    data class LifeStyleValueChange(val lifeStyle: Int) : OnBoardDataUiEvent
    data class FitnessLevelValueChange(val fitnessLevel: Int) : OnBoardDataUiEvent
    data class GoalsValueChange(val goals: Int) : OnBoardDataUiEvent
    data class ProfileImage(val file: Uri) : OnBoardDataUiEvent
}