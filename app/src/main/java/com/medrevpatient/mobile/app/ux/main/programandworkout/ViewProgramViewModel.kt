package com.medrevpatient.mobile.app.ux.main.programandworkout

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import com.google.gson.JsonObject
import com.medrevpatient.mobile.app.data.source.remote.dto.CompleteRestDay
import com.medrevpatient.mobile.app.data.source.remote.dto.PinRequest
import com.medrevpatient.mobile.app.data.source.remote.dto.Program
import com.medrevpatient.mobile.app.data.source.remote.helper.NetworkResult
import com.medrevpatient.mobile.app.data.source.remote.repository.ApiRepository
import com.medrevpatient.mobile.app.domain.response.ApiResponse
import com.medrevpatient.mobile.app.navigation.NavRoute
import com.medrevpatient.mobile.app.navigation.RouteMaker
import com.medrevpatient.mobile.app.navigation.RouteMaker.Keys.ID
import com.medrevpatient.mobile.app.ui.base.BaseViewModel
import com.medrevpatient.mobile.app.utils.ext.ifTrue
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ViewProgramViewModel @Inject constructor(
    private val repository: ApiRepository,
    savedStateHandle: SavedStateHandle,
    @ApplicationContext private val ctx: Context
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(ViewProgramUiState())
    val uiState: StateFlow<ViewProgramUiState> = _uiState.asStateFlow()

    private val programId = requireNotNull(savedStateHandle.get<String>(ID))

    init {
        getProgram(programId)
    }

    fun event(event: ViewProgramUIEvent) {
        when (event) {
            is ViewProgramUIEvent.NavigateTo -> {
                navigate(event.route)
            }

            ViewProgramUIEvent.Refresh -> {
                getProgram(programId)
            }

            ViewProgramUIEvent.TogglePinProgram -> {
                pinRecipe(programId)
            }

            is ViewProgramUIEvent.SkipRestDay -> {
                skipRestDay(event.day, event.programId)
            }
        }
    }

    private fun getProgram(id: String) {
        viewModelScope.launch {
            repository.getProgram(id).collect { network ->
                _uiState.update {
                    it.copy(
                        program = network,
                        isPin = network is NetworkResult.Success && network.data?.data?.isPin ?: false
                    )
                }
            }
        }
    }

    private fun pinRecipe(id: String) {
        viewModelScope.launch {
            repository.pin(
                pinRequest = PinRequest(
                    type = 1,
                    itemId = id
                )
            ).collect { network ->
                withContext(Dispatchers.Main) {

                    when (network) {
                        is NetworkResult.Error -> {
                            Toast(
                                context = ctx,
                                message = network.message ?: "something went wrong"
                            )
                            _uiState.update {
                                it.copy(
                                    isLoading = true,
                                )
                            }
                        }

                        is NetworkResult.Loading -> {
                            _uiState.update {
                                it.copy(isLoading = true)
                            }
                        }

                        is NetworkResult.Success -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    isPin = !uiState.value.isPin
                                )
                            }
                        }
                    }
                }
            }
        }
    }


    private fun skipRestDay(day: String, programId: String) {

        if (day.isEmpty() || programId.isEmpty()) {
            Toast(context = ctx, "Empty Values")
            return
        }

        val body = JsonObject().apply {
            addProperty("day", day)
            addProperty("programId", programId)
        }

        viewModelScope.launch {
            repository.completeRestDay(body).collect { network ->
                withContext(Dispatchers.Main) {
                    when (network) {
                        is NetworkResult.Error -> {
                            Toast(
                                ctx,
                                network.message ?: network.data?.message ?: "something went wrong"
                            )
                            _uiState.update { it.copy(isLoading = false) }
                        }

                        is NetworkResult.Loading -> {
                            _uiState.update { it.copy(isLoading = true) }
                        }

                        is NetworkResult.Success -> {

                            val completeMark = network.data?.data ?: CompleteRestDay()

                            val updatedDayDistribution =
                                uiState.value.program.data?.data?.dayDistribution?.map { dayItem ->
                                    if (dayItem.day.toString() == day) {
                                        dayItem.copy(
                                            completedAt = completeMark.updatedAt,
                                            isDayCompleted = true
                                        )
                                    } else {
                                        dayItem
                                    }
                                } ?: emptyList()

                            val updatedProgram =
                                uiState.value.program.data?.data?.copy(dayDistribution = updatedDayDistribution)

                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    program = NetworkResult.Success(ApiResponse(data = updatedProgram))
                                )
                            }

                            val nextDay = day.toInt() + 1
                            updatedDayDistribution.find { it.day == nextDay }?.isRestDay?.not()
                                ?.ifTrue {
                                    navigate(
                                        RouteMaker.TodayRoutine.createRoute(
                                            programId,
                                            day = nextDay.toString()
                                        )
                                    )
                                }
                        }
                    }
                }
            }
        }
    }
}


typealias ViewProgramEvent = (ViewProgramUIEvent) -> Unit

sealed interface ViewProgramUIEvent {
    data class NavigateTo(val route: NavRoute) : ViewProgramUIEvent
    object Refresh : ViewProgramUIEvent
    object TogglePinProgram : ViewProgramUIEvent
    data class SkipRestDay(val day: String, val programId: String) : ViewProgramUIEvent
}

data class ViewProgramUiState(
    val program: NetworkResult<ApiResponse<Program>> = NetworkResult.Loading(),
    val isLoading: Boolean = false,
    val isPin: Boolean = false,
)