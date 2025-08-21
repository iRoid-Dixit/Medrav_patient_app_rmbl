package com.medrevpatient.mobile.app.ux.main.myprogress

import android.content.Context
import com.medrevpatient.mobile.app.data.source.remote.dto.MyProgress
import com.medrevpatient.mobile.app.data.source.remote.helper.NetworkResult
import com.medrevpatient.mobile.app.data.source.remote.repository.ApiRepository
import com.medrevpatient.mobile.app.data.source.remote.repository.RequestBodyMaker
import com.medrevpatient.mobile.app.domain.response.ApiResponse
import com.medrevpatient.mobile.app.domain.validation.ValidationResult
import com.medrevpatient.mobile.app.domain.validation.ValidationUseCase
import com.medrevpatient.mobile.app.navigation.NavRoute
import com.medrevpatient.mobile.app.ui.base.BaseViewModel
import com.medrevpatient.mobile.app.ux.main.component.convertHHMMtoSS
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
import java.io.File
import javax.inject.Inject

@HiltViewModel
class MyProgressViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val apiRepository: ApiRepository,
    private val requestBody: RequestBodyMaker,
    private val validationUseCase: ValidationUseCase
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(MyProgressUiState())
    val uiState: StateFlow<MyProgressUiState> = _uiState.asStateFlow()

    init {
        fetchInitials()
    }

    fun event(event: MyProgressUiEvent) {
        when (event) {
            is MyProgressUiEvent.NavigateTo -> {
                navigate(event.route)
            }

            is MyProgressUiEvent.SheetEvent -> {
                handleSheetEvents(event.event)
            }

            MyProgressUiEvent.Refresh -> fetchInitials()
        }
    }

    private fun handleSheetEvents(event: SheetEvents) {
        when (event) {
            is SheetEvents.AddElementLog -> {
                _uiState.update {
                    it.copy(
                        isSheetVisible = true,
                        shouldShowSuccessSheet = false,
                        sheetType = SheetType.ADD_ELEMENT_LOG,
                        addElementLog = SheetUiState.AddElementLog(
                            element = event.element,
                            value = event.value,
                            minute = event.minute
                        )
                    )
                }
            }

            is SheetEvents.CreateGoal -> {
                _uiState.update {
                    it.copy(
                        isSheetVisible = true,
                        shouldShowSuccessSheet = false,
                        sheetType = SheetType.CREATE_GOAL,
                        createGoal = SheetUiState.CreateGoal(
                            element = event.element,
                            value = event.value,
                            value2 = event.value2,
                            images = event.images,
                            elementToDisable = uiState.value.myProgress.data?.data?.todayStates?.map { it.element }
                                ?: emptyList()
                        ),
                    )
                }
            }


            is SheetEvents.AddLogToSpecific -> {
                _uiState.update {
                    it.copy(
                        isSheetVisible = true,
                        shouldShowSuccessSheet = false,
                        sheetType = SheetType.ADD_LOG_TO_SPECIFIC_GOAL,
                        addLogToSpecific = SheetUiState.AddLogToSpecific(
                            element = event.element,
                            value = event.value,
                            value2 = event.value2,
                            images = event.images,
                        )
                    )
                }
            }

            is SheetEvents.Negative -> {
                _uiState.update {
                    it.copy(
                        isSheetVisible = false,
                        shouldShowSuccessSheet = false,
                        addElementLog = SheetUiState.AddElementLog(),
                    )
                }
            }

            is SheetEvents.Positive -> {
                handleSheetPositiveEvents(event.sheetType)
            }

            is SheetEvents.NavigateTo -> {
                navigate(event.route)
            }

            is SheetEvents.Success -> {
                handleSuccessType(event.sheetType)
            }

            else -> {}
        }
    }

    private fun handleSuccessType(sheetType: SheetType) {
        when (sheetType) {
            SheetType.CREATE_GOAL, SheetType.ADD_ELEMENT_LOG, SheetType.ADD_LOG_TO_SPECIFIC_GOAL -> {
                _uiState.update {
                    it.copy(
                        shouldShowSuccessSheet = false,
                        isSheetVisible = false,
                        createGoal = SheetUiState.CreateGoal(),
                        addElementLog = SheetUiState.AddElementLog(),
                        addLogToSpecific = SheetUiState.AddLogToSpecific()
                    )
                }
                fetchInitials()
            }

            else -> {}
        }
    }

    private fun handleSheetPositiveEvents(event: SheetType) {
        when (event) {
            SheetType.CREATE_GOAL -> {
                createGoal()
            }

            SheetType.ADD_ELEMENT_LOG -> {
                addLogs(
                    element = uiState.value.addElementLog.element,
                    value = uiState.value.addElementLog.value,
                    value2 = uiState.value.addElementLog.minute,
                    images = emptyList()
                )
            }

            SheetType.ADD_LOG_TO_SPECIFIC_GOAL -> {
                addLogs(
                    element = uiState.value.addLogToSpecific.element,
                    value = uiState.value.addLogToSpecific.value,
                    images = uiState.value.addLogToSpecific.images,
                    value2 = uiState.value.addLogToSpecific.value2
                )
            }

            else -> {}
        }
    }

    private fun fetchInitials() {
        viewModelScope.launch {
            apiRepository.myProgress().collect { networkResult ->
                withContext(Dispatchers.Main) {
                    _uiState.update {
                        it.copy(myProgress = networkResult)
                    }
                }
            }
        }
    }

    private fun addLogs(
        element: String,
        value: String,
        value2: String,
        images: List<File> = emptyList()
    ) {

        val validate = valueValidations(value, value2)

        if (validate.any { !it.isSuccess }) {
            val errorMsg = validate.find { !it.isSuccess }?.errorMsg
            updateAddElementLogs { copy(isError = errorMsg) }
            updateLogToSpecific { copy(isError = errorMsg) }
            Toast(context, errorMsg ?: "something went wrong")
            return
        }

        viewModelScope.launch {

            val (body, image) = requestBody.addLogRequestBody(
                goalType = element.getTypeByElement().toString(),
                value = convertHHMMtoSS(element, value, value2).toString(),
                imageFiles = images,
            )

            apiRepository.addLog(body = body, images = image).collect { networkResult ->
                withContext(Dispatchers.Main) {
                    when (networkResult) {
                        is NetworkResult.Error -> {
                            Toast(context, networkResult.message ?: "unknown error")
                            updateAddElementLogs { copy(isLoading = false) }
                            updateLogToSpecific { copy(isLoading = false) }
                        }

                        is NetworkResult.Loading -> {
                            updateAddElementLogs { copy(isLoading = true, isError = null) }
                            updateLogToSpecific { copy(isLoading = true, isError = null) }
                        }

                        is NetworkResult.Success -> {
                            _uiState.update {
                                it.copy(
                                    shouldShowSuccessSheet = true,
                                    addElementLog = SheetUiState.AddElementLog(),
                                    addLogToSpecific = SheetUiState.AddLogToSpecific()
                                )
                            }
                        }
                    }
                }
            }
        }
    }


    private fun createGoal() {
        val element = uiState.value.createGoal.element
        val value = uiState.value.createGoal.value
        val value2 = uiState.value.createGoal.value2

        val validate = valueValidations(value, value2)

        if (!validate.any { it.isSuccess }) {
            val errorMsg = validate.find { !it.isSuccess }?.errorMsg
            _uiState.update {
                it.copy(
                    createGoal = it.createGoal.copy(isError = errorMsg)
                )
            }
            return
        }

        viewModelScope.launch {

            val (body, imageParts) = requestBody.addLogRequestBody(
                goalType = element.getTypeByElement().toString(),
                value = convertHHMMtoSS(element, value, value2).toString(),
                imageFiles = uiState.value.createGoal.images
            )

            apiRepository.createGoal(body = body, images = imageParts).collect { networkResult ->
                withContext(Dispatchers.Main) {
                    when (networkResult) {
                        is NetworkResult.Error -> {
                            Toast(context, networkResult.message ?: "unknown error")
                            _uiState.update {
                                it.copy(
                                    createGoal = it.createGoal.copy(isLoading = false)
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
                            _uiState.update {
                                it.copy(
                                    shouldShowSuccessSheet = true,
                                    createGoal = it.createGoal.copy(
                                        isSuccess = "Goal Created Successfully",
                                        isError = null
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }


    private fun updateAddElementLogs(update: SheetUiState.AddElementLog.() -> SheetUiState.AddElementLog) {
        _uiState.update {
            it.copy(
                addElementLog = it.addElementLog.update()
            )
        }
    }

    private fun updateLogToSpecific(update: SheetUiState.AddLogToSpecific.() -> SheetUiState.AddLogToSpecific) {
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


data class MyProgressUiState(
    val isLoading: Boolean = true,
    val isError: String? = null,
    val isSheetVisible: Boolean = false,
    val shouldShowSuccessSheet: Boolean = false,
    val myProgress: NetworkResult<ApiResponse<MyProgress>> = NetworkResult.Loading(),
    val sheetType: SheetType = SheetType.CREATE_GOAL,
    val addElementLog: SheetUiState.AddElementLog = SheetUiState().addElementLog,
    val addLogToSpecific: SheetUiState.AddLogToSpecific = SheetUiState().addLogToSpecific,
    val createGoal: SheetUiState.CreateGoal = SheetUiState().createGoal,
)

typealias ProgressEvent = (MyProgressUiEvent) -> Unit

sealed interface MyProgressUiEvent {
    data class NavigateTo(val route: NavRoute) : MyProgressUiEvent
    data class SheetEvent(val event: SheetEvents) : MyProgressUiEvent
    object Refresh : MyProgressUiEvent
}
