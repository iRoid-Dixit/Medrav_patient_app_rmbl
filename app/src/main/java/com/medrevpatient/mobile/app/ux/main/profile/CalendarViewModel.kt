package com.medrevpatient.mobile.app.ux.main.profile

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medrevpatient.mobile.app.data.source.local.datastore.LocalManager
import com.medrevpatient.mobile.app.data.source.remote.helper.NetworkResult
import com.medrevpatient.mobile.app.data.source.remote.repository.ApiRepository
import com.medrevpatient.mobile.app.domain.response.CalendarResponse
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
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel
@Inject constructor(
    @ApplicationContext private val context: Context,
    private val apiRepository: ApiRepository,
    private val localManager: LocalManager
) : ViewModel(), ViewModelNav by ViewModelNavImpl() {

    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState = _uiState.asStateFlow()

    fun navigate(navRoute: NavRoute) {
        navigate(NavigationAction.Navigate(navRoute))
    }

    init {
        val startDate = AppUtils.getStartDateOfMonth(LocalDate.now().monthValue, LocalDate.now().year)
        val endDate = AppUtils.getEndDateOfMonth(LocalDate.now().monthValue, LocalDate.now().year)
        callCalendarApi(startDate, endDate)
    }

    fun event(event: CalendarUiEvent) {
        when (event) {
            is CalendarUiEvent.PerformNextPreviousMonthClick -> {
                val startDate = AppUtils.getStartDateOfMonth(event.month, event.year)
                val endDate = AppUtils.getEndDateOfMonth(event.month, event.year)
                Timber.d("startDate: $startDate, endDate: $endDate")
                callCalendarApi(startDate, endDate)
            }
        }
    }

    private fun callCalendarApi(startDate: String, endDate: String) {
        viewModelScope.launch {
            apiRepository.getCalendar(startDate = startDate, endDate = endDate).collect {
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
                        _uiState.update { state ->
                            state.copy(
                                calendarData = it.data?.data ?: emptyList(), currentMonth = AppUtils.getMonthFromDate(startDate),
                                currentYear = AppUtils.getYearFromDate(startDate)
                            )
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
}

data class CalendarUiState(
    val isLoading: Boolean = false,
    val calendarData: List<CalendarResponse> = emptyList(),
    val currentMonth: Int = LocalDate.now().monthValue,
    val currentYear: Int = LocalDate.now().year,
    val updateUI : Boolean = false
)

sealed class CalendarUiEvent {
    data class PerformNextPreviousMonthClick(val month: Int, val year: Int) : CalendarUiEvent()
}