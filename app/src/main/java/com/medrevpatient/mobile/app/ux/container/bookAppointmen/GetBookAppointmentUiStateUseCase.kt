package com.medrevpatient.mobile.app.ux.container.bookAppointmen

import android.content.Context
import com.medrevpatient.mobile.app.data.source.remote.helper.NetworkResult
import com.medrevpatient.mobile.app.data.source.remote.repository.ApiRepository
import com.medrevpatient.mobile.app.domain.validation.ValidationResult
import com.medrevpatient.mobile.app.domain.validation.ValidationUseCase
import com.medrevpatient.mobile.app.model.domain.request.appointment.AvailableSlotsRequest
import com.medrevpatient.mobile.app.navigation.NavigationAction
import com.medrevpatient.mobile.app.navigation.NavigationAction.*
import com.medrevpatient.mobile.app.utils.connection.NetworkMonitor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.utils.AppUtils

class GetBookAppointmentUiStateUseCase
@Inject constructor(
    private val networkMonitor: NetworkMonitor,
    private val validationUseCase: ValidationUseCase,
    private val apiRepository: ApiRepository,
) {
    private val isOffline = MutableStateFlow(false)
    private lateinit var context: Context
    private val bookAppointmentUiDataFlow = MutableStateFlow(BookAppointmentData())
    operator fun invoke(
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
    ): BookAppointmentUiState {
        coroutineScope.launch {
            networkMonitor.isOnline.map(Boolean::not).stateIn(
                scope = coroutineScope,
                started = WhileSubscribed(5_000),
                initialValue = false,
            ).collect {
                isOffline.value = it
            }
        }
        return BookAppointmentUiState(
            bookAppointmentUiDataFlow = bookAppointmentUiDataFlow,
            event = { appointmentEvent ->
                handleEvent(
                    event = appointmentEvent,
                    navigate = navigate,
                    coroutineScope = coroutineScope
                )
            }
        )
    }
    private fun handleEvent(
        event: BookAppointmentUiEvent,
        navigate: (NavigationAction) -> Unit,
        coroutineScope: CoroutineScope
    ) {
        when (event) {
            is BookAppointmentUiEvent.GetContext -> {
                this.context = event.context
            }
            is BookAppointmentUiEvent.SelectDate -> {
                bookAppointmentUiDataFlow.update { it.copy(selectedDate = event.date) }
                // Fetch available slots when date is selected
                fetchAvailableSlots(event.date, getTimePeriodNumber(bookAppointmentUiDataFlow.value.selectedTimePeriod), coroutineScope)
            }
            is BookAppointmentUiEvent.SelectTime -> {
                bookAppointmentUiDataFlow.update { it.copy(selectedTime = event.time) }
            }
            is BookAppointmentUiEvent.SelectTimePeriod -> {
                bookAppointmentUiDataFlow.update { it.copy(selectedTimePeriod = event.period) }
                // Fetch available slots when time period changes
                if (bookAppointmentUiDataFlow.value.selectedDate.isNotBlank()) {
                    fetchAvailableSlots(bookAppointmentUiDataFlow.value.selectedDate, getTimePeriodNumber(event.period), coroutineScope)
                }
            }
            is BookAppointmentUiEvent.UpdateNotes -> {
                bookAppointmentUiDataFlow.update { it.copy(additionalNotes = event.notes) }
            }
            is BookAppointmentUiEvent.BookAppointmentSheetVisibility -> {
                bookAppointmentUiDataFlow.update { state ->
                    state.copy(isDatePickerVisible = event.isVisible)
                }
            }
            is BookAppointmentUiEvent.ToggleTimePeriodDropdown -> {
                bookAppointmentUiDataFlow.update { it.copy(isTimePeriodDropdownExpanded = event.isExpanded) }
            }
            is BookAppointmentUiEvent.ConfirmBooking -> {
                if (!isOffline.value) {
                    validationUseCase.apply {
                        val dateValidationResult = dateOfBirthValidation(
                            bookAppointmentUiDataFlow.value.selectedDate,
                            context
                        )
                        val patientCategoryValidationResult =
                            genderValidation(bookAppointmentUiDataFlow.value.selectCategory, context)
                        val hasError = listOf(
                            dateValidationResult,
                            patientCategoryValidationResult,
                        ).any { !it.isSuccess }
                        bookAppointmentUiDataFlow.update { state ->
                            state.copy(
                                selectCategoryErrorMsg = dateValidationResult.errorMsg,
                                selectedDateErrorFlow =patientCategoryValidationResult.errorMsg,
                            )
                        }
                        if (hasError) return
                    }
                 // navigate(NavigationAction.Navigate(VerifyOtpRoute.createRoute(email = registerUiDataState.value.email?.trim()?:"", screenName = Constants.AppScreen.REGISTER_SCREEN)))
                } else {
                   AppUtils.showWarningMessage(
                        this.context,
                        context.getString(R.string.please_check_your_internet_connection_first)
                    )
                }
            }
            is BookAppointmentUiEvent.RoleDropDownExpanded -> {
                bookAppointmentUiDataFlow.update { state ->
                    state.copy(
                        selectCategory = event.selectGender,
                        selectCategoryErrorMsg = genderValidation(
                            event.selectGender,
                            context = context
                        ).errorMsg
                    )
                }
            }
            is BookAppointmentUiEvent.FetchAvailableSlots -> {
                fetchAvailableSlots(event.date, event.timePeriod, coroutineScope)
            }
        }
    }
    private fun genderValidation(gender: String?, context: Context): ValidationResult {
        return ValidationResult(
            isSuccess = !gender.isNullOrBlank(),
            errorMsg = if (gender.isNullOrBlank()) context.getString(R.string.please_select_your_category) else null
        )
    }
    private fun dateOfBirthValidation(dob: String, context: Context): ValidationResult {
        return ValidationResult(
            isSuccess = dob.isNotBlank(),
            errorMsg = if (dob.isBlank()) context.getString(R.string.please_enter_your_date_of_birth) else null
        )
    }

    private fun fetchAvailableSlots(date: String, timePeriod: Int, coroutineScope: CoroutineScope) {
        if (date.isBlank()) return
        
        bookAppointmentUiDataFlow.update { it.copy(isLoadingSlots = true, slotsError = null) }
        
        val request = AvailableSlotsRequest(date = date, timePeriod = timePeriod)
        
        coroutineScope.launch {
            apiRepository.getAvailableSlots(request)
                .catch { exception ->
                    bookAppointmentUiDataFlow.update { 
                        it.copy(
                            isLoadingSlots = false,
                            slotsError = exception.message ?: "Failed to fetch available slots"
                        )
                    }
                }
                .collect { result ->
                    when (result) {
                        is NetworkResult.Loading -> {
                            bookAppointmentUiDataFlow.update { it.copy(isLoadingSlots = true) }
                        }
                        is NetworkResult.Success -> {
                            val availableSlots = result.data?.availableSlots
                            val availableTimeSlots = availableSlots
                                ?.filter { it.isAvailable }
                                ?.map { formatTimeSlot(it.time) }
                                ?: emptyList()
                            val unavailableTimeSlots = availableSlots
                                ?.filter { !it.isAvailable }
                                ?.map { formatTimeSlot(it.time) }
                                ?: emptyList()
                            
                            bookAppointmentUiDataFlow.update { 
                                it.copy(
                                    isLoadingSlots = false,
                                    availableTimeSlots = availableTimeSlots,
                                    unavailableTimeSlots = unavailableTimeSlots,
                                    slotsError = null
                                )
                            }
                        }
                        is NetworkResult.Error -> {
                            bookAppointmentUiDataFlow.update { 
                                it.copy(
                                    isLoadingSlots = false,
                                    slotsError = result.message ?: "Failed to fetch available slots"
                                )
                            }
                        }
                        is NetworkResult.UnAuthenticated -> {
                            bookAppointmentUiDataFlow.update { 
                                it.copy(
                                    isLoadingSlots = false,
                                    slotsError = "Authentication required"
                                )
                            }
                        }
                    }
                }
        }
    }

    private fun getTimePeriodNumber(period: String): Int {
        return when (period.lowercase()) {
            "morning" -> 1
            "afternoon" -> 2
            "evening" -> 3
            "full day" -> 4
            else -> 1
        }
    }

    private fun formatTimeSlot(time: String): String {
        // Convert "08:00" to "08:00 AM" format
        return try {
            val parts = time.split(":")
            val hour = parts[0].toInt()
            val minute = parts[1]
            
            when {
                hour == 0 -> "12:$minute AM"
                hour < 12 -> "$time AM"
                hour == 12 -> "$time PM"
                else -> "${hour - 12}:$minute PM"
            }
        } catch (e: Exception) {
            time // Return original if parsing fails
        }
    }
}
