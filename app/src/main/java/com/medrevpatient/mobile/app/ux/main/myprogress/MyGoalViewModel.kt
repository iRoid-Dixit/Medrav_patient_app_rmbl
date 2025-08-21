package com.medrevpatient.mobile.app.ux.main.myprogress

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.paging.cachedIn
import com.google.gson.JsonObject
import com.medrevpatient.mobile.app.data.source.remote.dto.Goal
import com.medrevpatient.mobile.app.data.source.remote.dto.ViewGoal
import com.medrevpatient.mobile.app.data.source.remote.helper.NetworkResult
import com.medrevpatient.mobile.app.data.source.remote.repository.APIPagingCallBack
import com.medrevpatient.mobile.app.data.source.remote.repository.ApiRepository
import com.medrevpatient.mobile.app.data.source.remote.repository.RequestBodyMaker
import com.medrevpatient.mobile.app.domain.response.ApiListResponse
import com.medrevpatient.mobile.app.domain.validation.ValidationResult
import com.medrevpatient.mobile.app.domain.validation.ValidationUseCase
import com.medrevpatient.mobile.app.navigation.NavRoute
import com.medrevpatient.mobile.app.navigation.PopResultKeyValue
import com.medrevpatient.mobile.app.navigation.RouteMaker
import com.medrevpatient.mobile.app.ui.base.BaseViewModel
import com.medrevpatient.mobile.app.ux.main.component.convertHHMMtoSS
import com.medrevpatient.mobile.app.ux.main.component.convertToNumType
import com.medrevpatient.mobile.app.ux.main.component.decimalHourSplitter
import com.medrevpatient.mobile.app.ux.main.component.formatLogValue
import com.medrevpatient.mobile.app.ux.main.component.getTypeByElement
import com.medrevpatient.mobile.app.ux.main.myprogress.sheetcontent.component.SheetEvents
import com.medrevpatient.mobile.app.ux.main.myprogress.sheetcontent.component.SheetType
import com.medrevpatient.mobile.app.ux.main.myprogress.sheetcontent.component.SheetUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class MyGoalViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private val apiRepository: ApiRepository,
    private val requestBody: RequestBodyMaker,
    savedStateHandle: SavedStateHandle,
    private val validationUseCase: ValidationUseCase
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(MyGoalUiState())
    val uiState: StateFlow<MyGoalUiState> = _uiState.asStateFlow()

    private var isDataChanged: Boolean = false

    private val goalId: String =
        savedStateHandle[RouteMaker.Keys.ID] ?: ""
    private var fetchedGoals: List<Goal> =
        emptyList()
    val isFromNotification: Boolean =
        savedStateHandle[RouteMaker.Keys.IS_FROM_NOTIFICATION] ?: false

    init {
        if (goalId.isEmpty()) fetchGoals()
        _uiState.update {
            it.copy(isFromNotification = isFromNotification)
        }
    }

    val goal = apiRepository.getGoal(
        goalId = goalId,
        apiPagingCallBack = object : APIPagingCallBack<ViewGoal>() {
            override fun onSuccess(response: ViewGoal) {
                _uiState.update {
                    it.copy(viewGoal = response)
                }
            }
        }).cachedIn(viewModelScope)

    fun event(event: MyGoalsUiEvent) {
        when (event) {

            is MyGoalsUiEvent.NavigateTo -> {
                navigate(event.route)
            }

            is MyGoalsUiEvent.SheetEvent -> {
                sheetEvent(event.event)
            }

            MyGoalsUiEvent.Refresh -> fetchGoals()
            MyGoalsUiEvent.Back -> {
                if (isDataChanged) {
                    Timber.d("popBackStackWithResult")
                    popBackStackWithResult(
                        resultValues = listOf(
                            PopResultKeyValue(
                                key = RouteMaker.Keys.REFRESH_MY_PROGRESS,
                                value = true
                            )
                        )
                    )
                } else {
                    popBackStack()
                }
            }
        }
    }

    private fun sheetEvent(event: SheetEvents) {
        when (event) {
            is SheetEvents.AddLogToSpecific -> {
                _uiState.update {
                    it.copy(
                        isSheetVisible = true,
                        sheetType = SheetType.ADD_LOG_TO_SPECIFIC_GOAL,
                        addLogToSpecific = SheetUiState.AddLogToSpecific(
                            element = event.element,
                            value = event.value,
                            value2 = event.value2,
                            images = event.images
                        )
                    )
                }
            }

            is SheetEvents.CreateGoal -> {
                _uiState.update { state ->
                    state.copy(
                        isSheetVisible = true,
                        sheetType = SheetType.CREATE_GOAL,
                        createGoal = SheetUiState.CreateGoal(
                            elementToDisable = fetchedGoals.map { it.element },
                            element = event.element,
                            value = event.value,
                            value2 = event.value2,
                            images = event.images
                        ),
                    )
                }
            }

            is SheetEvents.DeleteGoal -> {
                _uiState.update {
                    it.copy(
                        isSheetVisible = true,
                        sheetType = SheetType.DELETE_GOAL,
                        deleteGoal = SheetUiState.DeleteGoal(
                            id = event.id,
                            element = event.element,
                            value = event.value
                        )
                    )
                }
            }

            is SheetEvents.EditGoal -> {
                _uiState.update {
                    it.copy(
                        isSheetVisible = true,
                        sheetType = SheetType.EDIT_GOAL,
                        editGoal = SheetUiState.EditGoal(
                            element = event.element,
                            value = event.value,
                            value2 = event.value2,
                            id = event.id,
                            images = event.images
                        )
                    )
                }
            }

            is SheetEvents.OptionMenu -> {

                val element = uiState.value.viewGoal.data.goal.element
                val value = uiState.value.viewGoal.data.goal.goalValue
                val elementType = element.getTypeByElement()

                _uiState.update {
                    it.copy(
                        isSheetVisible = true,
                        sheetType = event.sheetType,
                        editGoal = SheetUiState.EditGoal(
                            id = uiState.value.viewGoal.data.goal.id,
                            element = element,
                            value = value.decimalHourSplitter(elementType).first,
                            value2 = value.decimalHourSplitter(elementType).second,
                            images = emptyList()
                        ),
                        deleteGoal = SheetUiState.DeleteGoal(
                            id = uiState.value.viewGoal.data.goal.id,
                            element = element,
                            value = value.formatLogValue(element.getTypeByElement())
                        )
                    )
                }
            }

            SheetEvents.Negative -> {
                _uiState.update {
                    it.copy(isSheetVisible = false, shouldShowSuccessSheet = false)
                }
            }

            is SheetEvents.Positive -> {
                handlePositiveClick(event.sheetType)
            }

            is SheetEvents.Success -> {
                handleSuccessClick(event.sheetType)
            }

            is SheetEvents.NavigateTo -> {
                navigate(event.route)
            }

            else -> {}
        }
    }

    private fun handleSuccessClick(sheetType: SheetType) {
        when (sheetType) {
            SheetType.DELETE_GOAL -> {
                _uiState.update {
                    it.copy(
                        isSheetVisible = false,
                        shouldShowSuccessSheet = false
                    )
                }
                fetchGoals()
            }

            SheetType.CREATE_GOAL -> {
                _uiState.update {
                    it.copy(
                        isSheetVisible = false,
                        shouldShowSuccessSheet = false
                    )
                }
                fetchGoals()
            }

            else -> {}
        }
    }

    private fun handlePositiveClick(sheetType: SheetType) {
        when (sheetType) {
            SheetType.CREATE_GOAL -> {
                createGoal(uiState.value.createGoal)
            }

            SheetType.EDIT_GOAL -> {
                updateGoal()
            }

            SheetType.DELETE_GOAL -> {
                deleteGoal(uiState.value.deleteGoal)
            }

            SheetType.ADD_LOG_TO_SPECIFIC_GOAL -> {
                addLogs()
            }

            else -> {}
        }
    }

    private fun fetchGoals() {
        viewModelScope.launch {
            apiRepository.getGoals().collect { networkResult ->
                withContext(Dispatchers.Main) {
                    _uiState.update {
                        it.copy(
                            goals = networkResult
                        )
                    }
                    if (networkResult is NetworkResult.Success) {
                        fetchedGoals = networkResult.data?.data ?: emptyList()
                    }
                }
            }
        }

    }

    private fun createGoal(createGoal: SheetUiState.CreateGoal) {

        val element = createGoal.element
        val value = createGoal.value
        val value2 = createGoal.value2

        val validate = valueValidations(value, value2)

        if (!validate.any { it.isSuccess }) {
            val errorMsg = validate.find { !it.isSuccess }?.errorMsg
            Toast(context, errorMsg ?: "unknown error")
            return
        }

        val (body, imageParts) = requestBody.addLogRequestBody(
            goalType = element.getTypeByElement().toString(),
            value = convertHHMMtoSS(element, value, value2).toString(),
            imageFiles = uiState.value.createGoal.images
        )

        viewModelScope.launch {
            apiRepository.createGoal(body, imageParts).collect { network ->
                withContext(Dispatchers.Main) {
                    when (network) {
                        is NetworkResult.Error -> {
                            Toast(context, network.message ?: "unknown error")
                            _uiState.update {
                                it.copy(
                                    createGoal = it.createGoal.copy(
                                        isLoading = false,
                                        isError = network.message
                                    )
                                )
                            }
                        }

                        is NetworkResult.Loading -> {
                            _uiState.update {
                                it.copy(
                                    createGoal = it.createGoal.copy(
                                        isLoading = true,
                                        isError = null
                                    )
                                )
                            }
                        }

                        is NetworkResult.Success -> {
                            isDataChanged = true
                            _uiState.update {
                                it.copy(
                                    goals = it.goals,
                                    shouldShowSuccessSheet = true,
                                    createGoal = SheetUiState.CreateGoal()
                                )
                            }
                        }
                    }
                }
            }
        }

    }


    private fun deleteGoal(deleteGoal: SheetUiState.DeleteGoal) {
        viewModelScope.launch {

            apiRepository.deleteGoal(goalId = deleteGoal.id).collect { network ->
                when (network) {
                    is NetworkResult.Error -> {
                        Toast(context, network.message ?: "unknown error")
                        _uiState.update {
                            it.copy(
                                editGoal = it.editGoal.copy(
                                    isLoading = false,
                                    isError = network.message
                                )
                            )
                        }
                    }

                    is NetworkResult.Loading -> {
                        _uiState.update {
                            it.copy(
                                editGoal = it.editGoal.copy(isLoading = true, isError = null)
                            )
                        }
                    }

                    is NetworkResult.Success -> {
                        isDataChanged = true
                        _uiState.update {
                            it.copy(
                                goals = it.goals,
                                shouldShowSuccessSheet = true,
                                editGoal = SheetUiState.EditGoal()
                            )
                        }
                    }
                }
            }
        }
    }

    private fun updateGoal() {

        uiState.value.editGoal.apply {

            val validate = valueValidations(value, value2)

            if (!validate.any { it.isSuccess }) {

                val errorMsg = validate.find { !it.isSuccess }?.errorMsg
                Toast(message = errorMsg ?: "unknown error", context = context)

                return
            }

            viewModelScope.launch {

                val body = JsonObject().apply {
                    addProperty(
                        "value",
                        convertHHMMtoSS(element, value, value2).toString()
                    )
                }

                apiRepository.updateGoal(goalId = id, body = body).collect { network ->
                    withContext(Dispatchers.Main) {
                        when (network) {
                            is NetworkResult.Error -> {
                                Toast(context, network.message ?: "unknown error")
                                _uiState.update {
                                    it.copy(
                                        editGoal = it.editGoal.copy(
                                            isLoading = false,
                                            isError = network.message
                                        )
                                    )
                                }
                            }

                            is NetworkResult.Loading -> {
                                _uiState.update {
                                    it.copy(
                                        editGoal = it.editGoal.copy(
                                            isLoading = true,
                                            isError = null
                                        )
                                    )
                                }
                            }

                            is NetworkResult.Success -> {
                                isDataChanged = true
                                _uiState.update {
                                    it.copy(
                                        shouldShowSuccessSheet = true,
                                        editGoal = SheetUiState.EditGoal()
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun addLogs() {
        val element = uiState.value.addLogToSpecific.element
        val value = uiState.value.addLogToSpecific.value
        val value2 = uiState.value.addLogToSpecific.value2
        val image = uiState.value.addLogToSpecific.images

        val validate = valueValidations(element, value)

        if (!validate.any { it.isSuccess }) {

            val errorMsg = validate.find { !it.isSuccess }?.errorMsg
            updateAddLogToSpecific { copy(isError = errorMsg) }
            Toast(context, errorMsg ?: "unknown error")
            return
        }

        viewModelScope.launch {

            val (body, images) = requestBody.addLogRequestBody(
                goalType = element.getTypeByElement().toString(),
                value = convertHHMMtoSS(element, value, value2).toString(),
                imageFiles = image
            )

            Timber.d("element ${element.getTypeByElement()} value ${value.convertToNumType(element)}")

            apiRepository.addLog(body = body, images = images).collect { networkResult ->
                withContext(Dispatchers.Main) {
                    when (networkResult) {
                        is NetworkResult.Error -> {
                            Toast(context, networkResult.message ?: "unknown error")
                            updateAddLogToSpecific { copy(isLoading = false) }
                        }

                        is NetworkResult.Loading -> {
                            updateAddLogToSpecific { copy(isLoading = true, isError = null) }
                        }

                        is NetworkResult.Success -> {
                            isDataChanged = true
                            _uiState.update {
                                it.copy(
                                    shouldShowSuccessSheet = true,
                                    addLogToSpecific = SheetUiState.AddLogToSpecific()
                                )
                            }
                        }
                    }
                }
            }
        }
    }


    private fun updateAddLogToSpecific(update: SheetUiState.AddLogToSpecific.() -> SheetUiState.AddLogToSpecific) {
        _uiState.update {
            it.copy(
                addLogToSpecific = it.addLogToSpecific.update()
            )
        }
    }

    private fun valueValidations(value: String, value2: String): List<ValidationResult> {
        return listOf(
            validationUseCase.zeroValidation(value),
            validationUseCase.isStringIntegerValidation(value),
            if (value2.isNotEmpty()) validationUseCase.isStringIntegerValidation(value2) else ValidationResult(
                isSuccess = true
            )
        )
    }
}

typealias MyGoalsEvent = (MyGoalsUiEvent) -> Unit

sealed interface MyGoalsUiEvent {
    data class NavigateTo(val route: NavRoute) : MyGoalsUiEvent
    data class SheetEvent(val event: SheetEvents) : MyGoalsUiEvent
    object Refresh : MyGoalsUiEvent
    object Back : MyGoalsUiEvent
}

data class MyGoalUiState(
    val isSheetVisible: Boolean = false,
    val goals: NetworkResult<ApiListResponse<Goal>> = NetworkResult.Loading(),
    val viewGoal: ViewGoal = ViewGoal(),
    val shouldShowSuccessSheet: Boolean = false,
    val createGoal: SheetUiState.CreateGoal = SheetUiState().createGoal,
    val editGoal: SheetUiState.EditGoal = SheetUiState().editGoal,
    val deleteGoal: SheetUiState.DeleteGoal = SheetUiState().deleteGoal,
    val addLogToSpecific: SheetUiState.AddLogToSpecific = SheetUiState().addLogToSpecific,
    val sheetType: SheetType = SheetType.CREATE_GOAL,
    val isFromNotification: Boolean = false
)