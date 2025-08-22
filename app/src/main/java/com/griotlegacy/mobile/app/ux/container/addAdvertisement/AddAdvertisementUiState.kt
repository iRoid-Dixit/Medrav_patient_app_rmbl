package com.griotlegacy.mobile.app.ux.container.addAdvertisement
import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
data class AddAdvertisementUiState(
    //data
    val addAdvertisementDataFlow: StateFlow<AddAdvertisementDataState?> = MutableStateFlow(null),


    //event
    val event: (AddAdvertisementUiEvent) -> Unit = {}
)

data class AddAdvertisementDataState(
    val showLoader: Boolean = false,
    val advertisementId: String = "", // For edit mode
    val companyName: String = "",
    val companyNameErrorMsg: String? = null,
    val contactPerson: String = "",
    val contactPersonErrorMsg: String? = null,
    val email: String = "",
    val emailErrorMsg: String? = null,
    val mobileNumber: String = "",
    val mobileNumberErrorMsg: String? = null,
    val physicalAddress: String = "",
    val physicalAddressErrorMsg: String? = null,
    val purposeAdvertisement: String = "",
    val purposeAdvertisementErrorMsg: String? = null,
    val description: String = "",
    val descriptionErrorMsg: String? = null,
    val link: String = "",
    val linkErrorMsg: String? = null,
    val title: String = "",
    val titleErrorMsg: String? = null,
    var startDate: String = "",
    val startDateErrorMsg: String? = null,
    var endDate: String = "",
    val endDateErrorMsg: String? = null,
    val defaultCountryCode: String = "",
    val showDialog: Boolean = false,
    val showPermissionDialog: Boolean = false,
    var photo: String = "",
    val photoErrorMsg: String? = null,
    val screen: String = "",
    val rejectReason: String = ""


    )

sealed interface AddAdvertisementUiEvent {
    data class GetContext(val context: Context) : AddAdvertisementUiEvent
    data object BackClick : AddAdvertisementUiEvent
    data class CompanyNameValueChange(val companyName: String) : AddAdvertisementUiEvent
    data class ContactPersonValueChange(val contactPerson: String) : AddAdvertisementUiEvent
    data class EmailValueChange(val email: String) : AddAdvertisementUiEvent
    data class MobileNumberValueChange(val mobileNumber: String) : AddAdvertisementUiEvent
    data class PhysicalAddressValueChange(val physicalAddress: String) : AddAdvertisementUiEvent
    data class PurposeAdvertisementValueChange(val purposeAdvertisement: String) :
        AddAdvertisementUiEvent

    data class DescriptionValueChange(val description: String) : AddAdvertisementUiEvent
    data class LinkValueChange(val link: String) : AddAdvertisementUiEvent
    data class TitleValueChange(val title: String) : AddAdvertisementUiEvent
    data class OnClickOfStartDate(val startDate: String) : AddAdvertisementUiEvent
    data class OnClickOfEndDate(val endDate: String) : AddAdvertisementUiEvent
    data class ShowDialog(val show: Boolean) : AddAdvertisementUiEvent
    data class ShowPermissionDialog(val show: Boolean) : AddAdvertisementUiEvent
    data class ProfileValueChange(val photo: String) : AddAdvertisementUiEvent
    data object AddAdvertisementClick : AddAdvertisementUiEvent

}