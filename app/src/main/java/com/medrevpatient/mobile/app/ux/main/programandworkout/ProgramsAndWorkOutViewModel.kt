package com.medrevpatient.mobile.app.ux.main.programandworkout

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.google.gson.JsonObject
import com.medrevpatient.mobile.app.data.source.remote.dto.Program
import com.medrevpatient.mobile.app.data.source.remote.dto.ProgramWorkoutAndRecipesSearch
import com.medrevpatient.mobile.app.data.source.remote.dto.StrengthLog
import com.medrevpatient.mobile.app.data.source.remote.dto.StrengthLogExercises
import com.medrevpatient.mobile.app.data.source.remote.helper.NetworkResult
import com.medrevpatient.mobile.app.data.source.remote.repository.ApiRepository
import com.medrevpatient.mobile.app.domain.response.ApiListResponse
import com.medrevpatient.mobile.app.domain.validation.ValidationUseCase
import com.medrevpatient.mobile.app.navigation.NavRoute
import com.medrevpatient.mobile.app.navigation.NavigationAction
import com.medrevpatient.mobile.app.ui.base.BaseViewModel
import com.medrevpatient.mobile.app.utils.DateTimeUtils
import com.medrevpatient.mobile.app.utils.Debouncing
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ProgramsAndWorkOutViewModel
@Inject constructor(
    @ApplicationContext private val ctx: Context,
    private val validationUseCase: ValidationUseCase,
    private val repository: ApiRepository,
    private val debouncing: Debouncing,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(ProgramAndWorkOutUiState())
    val uiState: StateFlow<ProgramAndWorkOutUiState> = _uiState.asStateFlow()

    private val addLogErrors = MutableList<String?>(5) { null }

    private val isForME: Int =
        savedStateHandle[com.medrevpatient.mobile.app.navigation.RouteMaker.Keys.IS_FOR_ME] ?: 1
    val isFromNotification: Boolean =
        savedStateHandle[com.medrevpatient.mobile.app.navigation.RouteMaker.Keys.IS_FROM_NOTIFICATION]
            ?: false

    val programs = repository.getPrograms(type = isForME).cachedIn(viewModelScope).take(5)

    init {
        getStrengthLog()
        _uiState.update {
            it.copy(isFromNotification = isFromNotification)
        }
    }


    fun event(event: ProgramsAndWorkOutUiEvent) {
        when (event) {
            is ProgramsAndWorkOutUiEvent.NavigateTo -> {
                navigate(NavigationAction.Navigate(event.navRoute))
            }

            is ProgramsAndWorkOutUiEvent.Search -> {
                _uiState.update {
                    it.copy(searchKeyword = event.keyword)
                }
                searchPrograms(event.keyword)

            }

            ProgramsAndWorkOutUiEvent.ClearSearch -> {
                _uiState.update {
                    it.copy(
                        searchKeyword = "",
                        searchResult = repository.getSearchedProgram("").cachedIn(viewModelScope)
                    )
                }
            }

            ProgramsAndWorkOutUiEvent.ResetState -> {
                _uiState.update {
                    it.copy(message = null)
                }
            }

            ProgramsAndWorkOutUiEvent.AddStrengthLog -> {
                checkValidationAndAddLog()
            }

            is ProgramsAndWorkOutUiEvent.date -> {

                event.date ?: return

                val error =
                    validationUseCase.isEmptyStringValidation(event.date.toString()).errorMsg

                addLogErrors[1] = error

                Timber.d("date : ${event.date}")

                updateAddLog {
                    copy(
                        date = event.date,
                        formatedDate = DateTimeUtils.formatUTCToDateTime(
                            event.date / 1000,
                            "dd-MM-yyyy"
                        ),
                        errors = addLogErrors
                    )
                }
            }

            is ProgramsAndWorkOutUiEvent.exerciseId -> {
                val error = validationUseCase.isEmptyStringValidation(event.id).errorMsg
                addLogErrors[0] = error

                updateAddLog {
                    copy(
                        exercisesId = event.id,
                        errors = addLogErrors
                    )
                }
            }

            is ProgramsAndWorkOutUiEvent.lb -> {
                val error = validationUseCase.isStringIntegerValidation(event.lb).errorMsg
                addLogErrors[3] = error

                updateAddLog {
                    copy(
                        lb = event.lb,
                        errors = addLogErrors
                    )
                }
            }

            is ProgramsAndWorkOutUiEvent.reps -> {
                val error = validationUseCase.isStringIntegerValidation(event.reps).errorMsg
                addLogErrors[4] = error

                updateAddLog {
                    copy(
                        reps = event.reps,
                        errors = addLogErrors
                    )
                }
            }

            is ProgramsAndWorkOutUiEvent.set -> {
                val error = validationUseCase.isStringIntegerValidation(event.sets).errorMsg
                addLogErrors[2] = error

                updateAddLog {
                    copy(
                        sets = event.sets,
                        errors = addLogErrors
                    )
                }
            }

            ProgramsAndWorkOutUiEvent.RetryFetchingExercise -> {
                viewModelScope.launch {
                    getStrengthLogExercises()
                }

            }

            ProgramsAndWorkOutUiEvent.RetryFetchingSLogs -> getStrengthLog()
            ProgramsAndWorkOutUiEvent.ResetAddLogInputs -> {
                _uiState.update {
                    it.copy(
                        addLog = it.addLog.copy(
                            isLoading = false,
                            exercisesId = "",
                            date = 0L,
                            sets = "",
                            lb = "",
                            reps = "",
                            errors = emptyList()
                        )
                    )
                }
            }
        }
    }

    private fun checkValidationAndAddLog() {
        val addLog = _uiState.value.addLog

        val hasError = listOf(
            validationUseCase.isEmptyStringValidation(
                addLog.exercisesId,
                "Please select an exercise"
            ),
            validationUseCase.isEmptyStringValidation(
                addLog.date?.toString() ?: "",
                message = "Please select a date"
            ),
            validationUseCase.isStringIntegerValidation(addLog.sets),
            validationUseCase.isStringIntegerValidation(addLog.lb),
            validationUseCase.isStringIntegerValidation(addLog.reps)
        )

        if (hasError.any { !it.isSuccess }) {

            val errors = hasError.map { if (!it.isSuccess) it.errorMsg else null }

            Timber.d("Date Log ${addLog.date}")

            Timber.d("errors: api : $errors")

            updateAddLog {
                copy(errors = errors)
            }
            return
        }

        val body = JsonObject().apply {
            addProperty("date", addLog.date?.div(1000))
            addProperty("sets", addLog.sets.toInt())
            addProperty("lb", addLog.lb.toInt())
            addProperty("reps", addLog.reps.toInt())
            addProperty("exercisesId", addLog.exercisesId)
        }

        viewModelScope.launch {
            repository.addStrengthLog(body).collect { network ->
                withContext(Dispatchers.Main) {
                    when (network) {
                        is NetworkResult.Error -> {
                            _uiState.update {
                                it.copy(
                                    message = it.message,
                                    addLog = it.addLog.copy(isLoading = false)
                                )
                            }
                        }

                        is NetworkResult.Loading -> {
                            _uiState.update {
                                it.copy(
                                    message = null,
                                    addLog = it.addLog.copy(isLoading = true)
                                )
                            }
                        }

                        is NetworkResult.Success -> {
                            network.data?.data?.let { nData ->
                                _uiState.update {
                                    it.copy(
                                        strengthLog = NetworkResult.Success(
                                            ApiListResponse(
                                                data = it.strengthLog.data?.data?.plus(nData)
                                                    ?: listOf(nData)
                                            )
                                        ),
                                        addLog = ProgramAndWorkOutUiState.AddStrengthLog(exercises = it.addLog.exercises)
                                    )
                                }
                            }

                            Toast(ctx, "Log added successfully")
                        }
                    }
                }
            }
        }
    }

    private fun updateAddLog(update: ProgramAndWorkOutUiState.AddStrengthLog.() -> ProgramAndWorkOutUiState.AddStrengthLog) {
        _uiState.update {
            it.copy(addLog = it.addLog.update())
        }
    }

    private fun searchPrograms(keyword: String) {
        debouncing.debounce(delay = 500) {
            _uiState.update {
                it.copy(
                    searchResult = repository.getSearchedProgram(keyword).cachedIn(viewModelScope)
                )
            }
        }
    }

    private fun getStrengthLog() {
        viewModelScope.launch(Dispatchers.IO) {

            val logs = async {
                repository.getStrengthLog().collect { network ->
                    withContext(Dispatchers.Main) {
                        _uiState.update {
                            it.copy(strengthLog = network)
                        }
                    }
                }
            }

            val getStrengthLog = async { getStrengthLogExercises() }

            awaitAll(logs, getStrengthLog)
        }
    }

    private suspend fun getStrengthLogExercises() {
        repository.getStrengthLogExercises().collect { network ->
            withContext(Dispatchers.Main) {
                _uiState.update {
                    it.copy(addLog = it.addLog.copy(exercises = network))
                }
            }
        }
    }
}

typealias ProgramAndWorkOutEvent = (ProgramsAndWorkOutUiEvent) -> Unit

sealed interface ProgramsAndWorkOutUiEvent {
    data class NavigateTo(val navRoute: NavRoute) : ProgramsAndWorkOutUiEvent
    data class Search(val keyword: String) : ProgramsAndWorkOutUiEvent
    object ClearSearch : ProgramsAndWorkOutUiEvent
    object AddStrengthLog : ProgramsAndWorkOutUiEvent
    object ResetState : ProgramsAndWorkOutUiEvent
    object RetryFetchingExercise : ProgramsAndWorkOutUiEvent
    object RetryFetchingSLogs : ProgramsAndWorkOutUiEvent
    object ResetAddLogInputs : ProgramsAndWorkOutUiEvent
    data class exerciseId(val id: String) : ProgramsAndWorkOutUiEvent
    data class date(val date: Long?) : ProgramsAndWorkOutUiEvent
    data class set(val sets: String) : ProgramsAndWorkOutUiEvent
    data class lb(val lb: String) : ProgramsAndWorkOutUiEvent
    data class reps(val reps: String) : ProgramsAndWorkOutUiEvent
}

data class ProgramAndWorkOutUiState(
    val isLoading: Boolean = false,
    val message: String? = null,
    val searchKeyword: String = "",
    val searchResult: Flow<PagingData<ProgramWorkoutAndRecipesSearch>> = MutableStateFlow(PagingData.empty()),
    val strengthLog: NetworkResult<ApiListResponse<StrengthLog>> = NetworkResult.Loading(),
    val programs: List<Program> = emptyList(),
    val addLog: AddStrengthLog = AddStrengthLog(),
    val isFromNotification: Boolean = false
) {

    data class AddStrengthLog( //Keep this in same order
        val isLoading: Boolean = false,
        val exercises: NetworkResult<ApiListResponse<StrengthLogExercises>> = NetworkResult.Loading(),
        val exercisesId: String = "",
        val date: Long? = null,
        val formatedDate: String = "",
        val sets: String = "",
        val lb: String = "",
        val reps: String = "",
        val errors: List<String?> = listOf(null, null, null, null, null)
    ) {
        val getExerciseById: String
            get() = exercises.data?.data?.find { it.id == exercisesId }?.name ?: ""
    }

}