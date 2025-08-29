
package com.medrevpatient.mobile.app.ux.main.medication

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class MedicationUiState(
    //data
    val medicationUiDataState: StateFlow<MedicationUiDataState?> = MutableStateFlow(null),
    val event: (MedicationUiEvent) -> Unit = {}
)

data class MedicationUiDataState(

    val isStoringPurchaseInfo: Boolean = false,

)

sealed interface MedicationUiEvent {


}