package com.medrevpatient.mobile.app.ux.main.medication

import android.content.Context
import com.medrevpatient.mobile.app.data.source.remote.repository.ApiRepository
import com.medrevpatient.mobile.app.navigation.NavigationAction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class GetMedicationUiStateUseCase
@Inject constructor(
    private val apiRepository: ApiRepository,
) {
    private val messageUiDataState = MutableStateFlow(MedicationUiDataState())

    operator fun invoke(
        context: Context,
        @Suppress("UnusedPrivateProperty")
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
    ): MedicationUiState {
        return MedicationUiState(
            medicationUiDataState = messageUiDataState,
            event = {
                chatUiEvent(
                    context = context,
                    event = it,
                    coroutineScope = coroutineScope,
                    navigate = navigate
                )
            }
        )
    }

    private fun chatUiEvent(
        context: Context,
        event: MedicationUiEvent,
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit
    ) {
        when (event) {

            else -> {}
        }
    }

}