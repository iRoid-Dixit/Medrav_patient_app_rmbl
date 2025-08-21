package com.medrevpatient.mobile.app.ux.main.programandworkout

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.google.gson.JsonObject
import com.medrevpatient.mobile.app.data.source.remote.dto.DayExercises
import com.medrevpatient.mobile.app.data.source.remote.dto.ExerciseCompleted
import com.medrevpatient.mobile.app.data.source.remote.helper.NetworkResult
import com.medrevpatient.mobile.app.data.source.remote.repository.ApiRepository
import com.medrevpatient.mobile.app.domain.response.ApiResponse
import com.medrevpatient.mobile.app.navigation.NavRoute
import com.medrevpatient.mobile.app.navigation.PopResultKeyValue
import com.medrevpatient.mobile.app.navigation.RouteMaker.Keys.DAY
import com.medrevpatient.mobile.app.navigation.RouteMaker.Keys.ID
import com.medrevpatient.mobile.app.ui.base.BaseViewModel
import com.medrevpatient.mobile.app.utils.DateTimeUtils.formatSecondsDuration
import com.medrevpatient.mobile.app.utils.ext.ifTrue
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import kotlin.math.abs

@HiltViewModel
class TodayRoutineViewModel @Inject constructor(
    @ApplicationContext private val ctx: Context,
    savedStateHandle: SavedStateHandle,
    private val repository: ApiRepository,
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(TodayRoutineUiState())
    val uiState: StateFlow<TodayRoutineUiState> = _uiState.asStateFlow()

    val day = requireNotNull(savedStateHandle.get<String>(DAY))
    val programId = requireNotNull(savedStateHandle.get<String>(ID))
    private var isAllCompleted: Boolean = false

    private val player by lazy {
        ExoPlayer.Builder(ctx).build().apply {
            repeatMode = Player.REPEAT_MODE_ALL // Set repeat mode
        }
    }

    private var timerJob: Job? = null
    private var currentTime: Long = 0

    init {
        _uiState.update {
            it.copy(
                day = day,
            )
        }
        getTodayRoutine(day, programId)
    }

    fun event(event: TodayRoutineUIEvent) {
        when (event) {
            is TodayRoutineUIEvent.NavigateTo -> {
                navigate(event.route)
            }

            is TodayRoutineUIEvent.StartWorkOut -> {
                currentTime = sumDuration()
                _uiState.update {
                    it.copy(
                        shouldShowStartWorkOutScreen = true,
                        workOutUiState = it.workOutUiState.copy(
                            id = event.exercises.id,
                            isInPreviewMode = event.inPreviewMode,
                            isExerciseCompleted = event.exercises.isCompleted,
                            titleValue = Pair(event.exercises.name, event.exercises.bulletPoints),
                            gif = event.exercises.gif,
                            player = updatePlayer(url = event.exercises.gif),
                            timerValue = currentTime.formatSecondsDuration()
                        )
                    )
                }

                event.inPreviewMode.not().ifTrue { startCounter() }
            }

            is TodayRoutineUIEvent.WorkoutUIEvent.Complete -> {
                completeExercise(id = uiState.value.workOutUiState.id, currentTime)
            }

            TodayRoutineUIEvent.WorkoutUIEvent.CloseScreen -> {
                resetState()
            }

            TodayRoutineUIEvent.WorkoutUIEvent.StartStop -> {
                if (uiState.value.workOutUiState.isTimerRunning) {
                    stopCounter()
                } else {
                    startCounter()
                }
            }

            TodayRoutineUIEvent.CloseCongratulationScreen -> {
                _uiState.update {
                    it.copy(
                        shouldShowStartWorkOutScreen = false,
                        shouldShowCongratulationsScreen = false,
                        congratulationsScreenUiState = null
                    )
                }
            }

            TodayRoutineUIEvent.BackPress -> {
                if (isAllCompleted) {
                    popBackStackWithResult(
                        resultValues = listOf(
                            PopResultKeyValue(
                                com.medrevpatient.mobile.app.navigation.RouteMaker.Keys.REFRESH, true
                            )
                        )
                    )
                } else {
                    popBackStack()
                }
            }

            TodayRoutineUIEvent.Refresh -> getTodayRoutine(day, programId)
        }
    }

    private fun completeExercise(id: String, timeSpend: Long) {
        if (uiState.value.workOutUiState.isExerciseCompleted) {
            return
        }

        stopCounter()

        viewModelScope.launch {
            val absoluteTime = abs(sumDuration() - timeSpend)
            val body = JsonObject().apply {
                addProperty("timeToTaken", absoluteTime)
            }

            repository.completeExercise(exerciseId = id, timeSpend = body).collect { network ->

                withContext(Dispatchers.Main) {

                    when (network) {
                        is NetworkResult.Error -> {
                            Toast(context = ctx, message = network.message ?: "unknown error")
                            updateWorkoutScreenUiState {
                                copy(isLoading = false)
                            }
                        }

                        is NetworkResult.Loading -> {
                            updateWorkoutScreenUiState {
                                copy(
                                    isLoading = true
                                )
                            }
                        }

                        is NetworkResult.Success -> {

                            val updatedExercise =
                                uiState.value.todayRoutine.data?.data?.exercises?.map {
                                    if (it.id == id) {
                                        it.copy(
                                            isCompleted = true, spendTime = absoluteTime
                                        )
                                    } else {
                                        it
                                    }
                                } ?: emptyList()

                            Timber.d("$updatedExercise")


                            val updatedDayExercises = uiState.value.todayRoutine.data?.data?.copy(
                                exercises = updatedExercise
                            ) ?: DayExercises()


                            // Wrap the updated DayExercises inside ApiResponse
                            val updatedApiResponse = ApiResponse(data = updatedDayExercises)

                            Timber.d("$updatedApiResponse")

                            isAllCompleted = network.data?.data?.history?.isCompleted ?: false

                            _uiState.update {
                                it.copy(
                                    shouldShowStartWorkOutScreen = false,
                                    shouldShowCongratulationsScreen = isAllCompleted,
                                    todayRoutine = NetworkResult.Success(updatedApiResponse),
                                    congratulationsScreenUiState = network.data,
                                    workOutUiState = it.workOutUiState.copy(
                                        isExerciseCompleted = true, isLoading = false
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }


    private fun getTodayRoutine(day: String, programId: String) {
        viewModelScope.launch {
            repository.getDayExercises(day, programId).collect { network ->
                _uiState.update {
                    it.copy(
                        todayRoutine = network
                    )
                }
            }
        }
    }


    private fun updateWorkoutScreenUiState(workoutScreenUiState: TodayRoutineUiState.WorkoutScreenUiState.() -> TodayRoutineUiState.WorkoutScreenUiState) {
        _uiState.update {
            it.copy(
                workOutUiState = it.workOutUiState.workoutScreenUiState()
            )
        }
    }

    private fun sumDuration(): Long {
        return uiState.value.todayRoutine.data?.data?.exercises?.sumOf { it.spendTime } ?: 0
    }


    fun startCounter(onTick: (Long) -> Unit = {}) {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                currentTime++
                updateWorkoutScreenUiState {
                    copy(
                        isTimerRunning = true, timerValue = currentTime.formatSecondsDuration()
                    )
                }
                onTick(currentTime)
                delay(1000)
            }
        }
    }

    private fun stopCounter() {
        timerJob?.cancel()
        updateWorkoutScreenUiState {
            copy(
                isTimerRunning = false
            )
        }
    }

    private fun resetState() {
        timerJob?.cancel()
        currentTime = 0

        _uiState.update {
            it.copy(
                shouldShowStartWorkOutScreen = false,
                shouldShowCongratulationsScreen = false,
                workOutUiState = TodayRoutineUiState.WorkoutScreenUiState()
            )
        }
    }

    private fun updatePlayer(url: String): ExoPlayer {
        return player.apply {
            setMediaItem(MediaItem.fromUri(url))
            prepare()
            playWhenReady = true // Auto-play video
        }
    }


    override fun onCleared() {
        super.onCleared()
        player.apply {
            stop()
            release()
        }
        resetState()
    }
}

typealias TodayRoutineEvent = (TodayRoutineUIEvent) -> Unit

sealed interface TodayRoutineUIEvent {
    data class NavigateTo(val route: NavRoute) : TodayRoutineUIEvent
    data class StartWorkOut(
        val exercises: DayExercises.Exercise, val inPreviewMode: Boolean = false
    ) : TodayRoutineUIEvent

    object CloseCongratulationScreen : TodayRoutineUIEvent

    object WorkoutUIEvent {
        object StartStop : TodayRoutineUIEvent
        object Complete : TodayRoutineUIEvent
        object CloseScreen : TodayRoutineUIEvent
    }

    object BackPress : TodayRoutineUIEvent
    object Refresh : TodayRoutineUIEvent
}

data class TodayRoutineUiState(
    val day: String = "",
    val todayRoutine: NetworkResult<ApiResponse<DayExercises>> = NetworkResult.Loading(),
    val shouldShowStartWorkOutScreen: Boolean = false,
    val shouldShowCongratulationsScreen: Boolean = false,
    val workOutUiState: WorkoutScreenUiState = WorkoutScreenUiState(),
    val congratulationsScreenUiState: ApiResponse<ExerciseCompleted>? = null
) {
    data class WorkoutScreenUiState(
        val id: String = "",
        val player: ExoPlayer? = null,
        val isLoading: Boolean = false,
        val isInPreviewMode: Boolean = false,
        val isExerciseCompleted: Boolean = false,
        val isTimerRunning: Boolean = false,
        val timerValue: String = "00:00",
        val titleValue: Pair<String, List<String>> = Pair("", emptyList()),
        val gif: String = "",
    )
}