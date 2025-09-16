package com.medrevpatient.mobile.app.ux.container.bookAppointmen

import android.content.Context
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.medrevpatient.mobile.app.model.domain.request.authReq.ForgetPasswordReq
import com.medrevpatient.mobile.app.utils.AppUtils
import com.medrevpatient.mobile.app.utils.AppUtils.showErrorMessage
import com.medrevpatient.mobile.app.utils.AppUtils.showSuccessMessage
import com.medrevpatient.mobile.app.ux.startup.auth.login.LoginUiEvent

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
                fetchAvailableSlots(coroutineScope = coroutineScope)
            }
            is BookAppointmentUiEvent.SelectTime -> {
                bookAppointmentUiDataFlow.update { it.copy(selectedTime = event.time) }
            }
            is BookAppointmentUiEvent.SelectTimePeriod -> {
                bookAppointmentUiDataFlow.update { it.copy(selectedTimePeriod = event.period) }
                if (bookAppointmentUiDataFlow.value.selectedDate.isNotBlank()) {
                    fetchAvailableSlots(coroutineScope = coroutineScope)
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

    @OptIn(ExperimentalMaterial3Api::class)
    private fun fetchAvailableSlots(
        coroutineScope: CoroutineScope,
    ) {
        coroutineScope.launch {
            val availableSlotsRequest = AvailableSlotsRequest(
                date = convertDateToApiFormat(bookAppointmentUiDataFlow.value.selectedDate),
                timePeriod = getTimePeriodNumber(bookAppointmentUiDataFlow.value.selectedTimePeriod)
            )
            // Set loading state
            bookAppointmentUiDataFlow.update { state ->
                state.copy(isLoadingSlots = true, slotsError = null)
            }
            apiRepository.getAvailableSlots(availableSlotsRequest).collect {
                when (it) {
                    is NetworkResult.Error -> {
                        showOrHideLoader(false)
                        showErrorMessage(context = context, it.message ?: "Something went wrong!")
                    }
                    is NetworkResult.Loading -> {
                        showOrHideLoader(true)
                    }
                    is NetworkResult.Success -> {
                        val availableSlots = it.data?.data?.availableSlots ?: emptyList()
                        val timeSlots = availableSlots.map { slot -> slot.time }
                        showOrHideLoader(false)
                        bookAppointmentUiDataFlow.update { state ->
                            state.copy(
                                availableTimeSlots = timeSlots,
                            )
                        }
                        showSuccessMessage(context = context, it.data?.message ?: "")
                    }
                    is NetworkResult.UnAuthenticated -> {
                        showOrHideLoader(true)
                        showErrorMessage(context = context, it.message ?: "Something went wrong!")
                    }
                }
            }
        }
    }

    private fun showOrHideLoader(showLoader: Boolean) {
        bookAppointmentUiDataFlow.update { state ->
            state.copy(
                isLoadingSlots = showLoader
            )
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

    /**
     * Converts date from dd/MM/yyyy format to YYYY-MM-DD format for API calls
     */
    private fun convertDateToApiFormat(dateString: String): String {
        return try {
            val parts = dateString.split("/")
            if (parts.size == 3) {
                val day = parts[0].padStart(2, '0')
                val month = parts[1].padStart(2, '0')
                val year = parts[2]
                "$year-$month-$day"
            } else {
                dateString // Return original if format is unexpected
            }
        } catch (e: Exception) {
            dateString // Return original if parsing fails
        }
    }
}
