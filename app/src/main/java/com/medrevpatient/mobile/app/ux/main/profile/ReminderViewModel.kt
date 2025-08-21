package com.medrevpatient.mobile.app.ux.main.profile

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.data.source.local.datastore.LocalManager
import com.medrevpatient.mobile.app.data.source.remote.helper.NetworkResult
import com.medrevpatient.mobile.app.data.source.remote.repository.ApiRepository
import com.medrevpatient.mobile.app.domain.request.CreateUpdateReminderReq
import com.medrevpatient.mobile.app.domain.response.ApiListResponse
import com.medrevpatient.mobile.app.domain.response.SubscribedProgramGoal
import com.medrevpatient.mobile.app.navigation.NavRoute
import com.medrevpatient.mobile.app.navigation.NavigationAction
import com.medrevpatient.mobile.app.navigation.ViewModelNav
import com.medrevpatient.mobile.app.navigation.ViewModelNavImpl
import com.medrevpatient.mobile.app.utils.AppUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class ReminderViewModel
@Inject constructor(
    @ApplicationContext private val context: Context,
    private val apiRepository: ApiRepository,
    private val localManager: LocalManager
) : ViewModel(), ViewModelNav by ViewModelNavImpl() {

    private val _uiState = MutableStateFlow(ReminderUiState())
    val uiState = _uiState.asStateFlow()

    init {
        getProgramsGoalsAPI()
        _uiState.update {
            it.copy(isNeedToUpdate = false)
        }
    }

    fun event(event: ReminderUiEvent) {
        when (event) {
            is ReminderUiEvent.DisplayReminderValues -> {
                uiState.value.repeatFrequency.clear()
                _uiState.update {
                    it.copy(
                        selectedItemReminderTime = event.reminderTime, isNeedToUpdate = event.isNeedToUpdate, repeatFrequency = event.repeatFrequency,
                        programGoalId = event.id
                    )
                }
            }

            ReminderUiEvent.PerformUpdateReminderClick -> {
                callCreateUpdateReminderApi()
            }

            ReminderUiEvent.PerformCancelClick -> {
                popBackStack()
            }

            is ReminderUiEvent.UpdateTimer -> {
                _uiState.update {
                    it.copy(selectedItemReminderTime = event.time)
                }
            }

            is ReminderUiEvent.UpdateRepeatFrequency -> {
                _uiState.update {
                    it.copy(repeatFrequency = event.repeatFrequency)
                }
            }
        }
    }


    private fun getProgramsGoalsAPI() {
        viewModelScope.launch {
            apiRepository.getProgramsGoalsForReminder().collect { network ->
                _uiState.update {
                    it.copy(programGoalsList = network)
                }
            }
        }
    }

    private fun callCreateUpdateReminderApi() {
        when {
            uiState.value.programGoalId.isEmpty() -> {
                AppUtils.Toast(context, context.getString(R.string.warn_reminder_program)).show()
                return
            }

            uiState.value.repeatFrequency.isEmpty() -> {
                AppUtils.Toast(context, context.getString(R.string.warn_reminder_frequency)).show()
                return
            }
        }
        viewModelScope.launch {
            val req = CreateUpdateReminderReq(
                programGoalId = uiState.value.programGoalId,
                reminderTime = uiState.value.selectedItemReminderTime.toString(),
                repeatFrequency = uiState.value.repeatFrequency
            )

            apiRepository.createUpdateReminder(req).collect {
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
                        val updatedGoal = it.data?.data // Assuming this contains the updated goal object
                        if (updatedGoal != null) {
                            _uiState.update { state ->
                                val updatedList = when (val currentList = state.programGoalsList) {
                                    is NetworkResult.Success -> {
                                        val modifiedGoals = currentList.data?.data?.map { goal ->
                                            if (goal.id == updatedGoal.programGoal) updatedGoal else goal
                                        }
                                        NetworkResult.Success(ApiListResponse(modifiedGoals as List<SubscribedProgramGoal>))
                                    }
                                    else -> currentList
                                }
                                state.copy(programGoalsList = updatedList)
                            }
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


    fun navigate(navRoute: NavRoute) {
        navigate(NavigationAction.Navigate(navRoute))
    }
}

data class ReminderUiState(
    val programGoalsList: NetworkResult<ApiListResponse<SubscribedProgramGoal>> = NetworkResult.Loading(),
    val selectedItemReminderTime: LocalTime = LocalTime.now(),
    val isNeedToUpdate: Boolean = false,
    val repeatFrequency: ArrayList<Int> = arrayListOf(),
    val isLoading: Boolean = false,
    val programGoalId: String = ""
)

sealed interface ReminderUiEvent {
    data class DisplayReminderValues(val reminderTime: LocalTime, val isNeedToUpdate: Boolean, val repeatFrequency: ArrayList<Int>, val id: String) : ReminderUiEvent
    data object PerformUpdateReminderClick : ReminderUiEvent
    data object PerformCancelClick : ReminderUiEvent
    data class UpdateTimer(val time: LocalTime) : ReminderUiEvent
    data class UpdateRepeatFrequency(val repeatFrequency: ArrayList<Int>) : ReminderUiEvent
}