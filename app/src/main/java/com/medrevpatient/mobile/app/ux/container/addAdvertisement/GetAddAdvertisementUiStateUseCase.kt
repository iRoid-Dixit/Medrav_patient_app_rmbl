package com.medrevpatient.mobile.app.ux.container.addAdvertisement

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.data.source.Constants
import com.medrevpatient.mobile.app.data.source.remote.helper.NetworkResult
import com.medrevpatient.mobile.app.data.source.remote.repository.ApiRepository
import com.medrevpatient.mobile.app.domain.validation.ValidationResult
import com.medrevpatient.mobile.app.domain.validation.ValidationUseCase
import com.medrevpatient.mobile.app.model.domain.response.advertisement.AdvertisementResponse
import com.medrevpatient.mobile.app.navigation.NavigationAction

import com.medrevpatient.mobile.app.utils.AppUtils
import com.medrevpatient.mobile.app.utils.AppUtils.createMultipartBody
import com.medrevpatient.mobile.app.utils.AppUtils.showErrorMessage
import com.medrevpatient.mobile.app.utils.AppUtils.showSuccessMessage
import com.medrevpatient.mobile.app.utils.AppUtils.showWaringMessage
import com.medrevpatient.mobile.app.utils.connection.NetworkMonitor
import com.medrevpatient.mobile.app.ux.container.advertisement.AdvertisementRoute
import com.medrevpatient.mobile.app.ux.container.advertisementSubscription.AdvertisementSubscriptionRoute
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

class GetAddAdvertisementUiStateUseCase
@Inject constructor(
    private val validationUseCase: ValidationUseCase,
    private val networkMonitor: NetworkMonitor,
    private val apiRepository: ApiRepository,
) {
    private val isOffline = MutableStateFlow(false)
    private lateinit var context: Context
    private val advertisementId = MutableStateFlow("")
    private val advertisementDataFlow = MutableStateFlow(AddAdvertisementDataState())
    operator fun invoke(
        context: Context,
        coroutineScope: CoroutineScope,
        screen: String,
        advertisementData: String,
        navigate: (NavigationAction) -> Unit,
    ): AddAdvertisementUiState {
        Log.d("TAG", "advertisementData: $screen,$advertisementData")
        coroutineScope.launch {
            networkMonitor.isOnline.map(Boolean::not).stateIn(
                scope = coroutineScope,
                started = WhileSubscribed(5_000),
                initialValue = false,
            ).collect {
                isOffline.value = it
            }
        }
        if (screen == Constants.AppScreen.EDIT_ADVERTISEMENT_SCREEN) {
            val data =
                Gson().fromJson(advertisementData, AdvertisementResponse::class.java)
            advertisementId.value = data.id ?: ""
            advertisementDataFlow.update { state ->
                state.copy(
                    companyName = data?.companyName ?: "",
                    contactPerson = data?.contactPerson ?: "",
                    email = data?.email ?: "",
                    mobileNumber = data?.phoneNumber ?: "",
                    physicalAddress = data?.physicalAddress ?: "",
                    purposeAdvertisement = data?.purpose ?: "",
                    description = data?.description ?: "",
                    link = data?.link ?: "",
                    title = data?.title ?: "",
                    startDate = data?.startDate?.let {
                        Log.d("TAG", "Converting startDate timestamp: $it")
                        AppUtils.convertTimestampToDate(it)
                    } ?: "",
                    endDate = data?.endDate?.let {
                        Log.d("TAG", "Converting endDate timestamp: $it")
                        AppUtils.convertTimestampToDate(it)
                    } ?: "",
                    photo = data?.image ?: "",

                    screen = screen,
                    rejectReason = data.rejectReason ?: ""
                )
            }
        } else {
            // Empty form for add mode
            advertisementDataFlow.update { state ->
                state.copy(

                    screen = screen
                )
            }
        }

        return AddAdvertisementUiState(
            addAdvertisementDataFlow = advertisementDataFlow,
            event = { aboutUsEvent ->
                addAdvertisementUiEvent(
                    event = aboutUsEvent,
                    context = context,
                    navigate = navigate,
                    coroutineScope = coroutineScope,
                    screen = screen,


                )
            }
        )
    }

    private fun addAdvertisementUiEvent(
        event: AddAdvertisementUiEvent,
        context: Context,
        navigate: (NavigationAction) -> Unit,
        coroutineScope: CoroutineScope,
        screen: String,
    ) {
        when (event) {
            AddAdvertisementUiEvent.BackClick -> {
                navigate(NavigationAction.Pop())
            }

            is AddAdvertisementUiEvent.GetContext -> {
                this.context = event.context
            }

            is AddAdvertisementUiEvent.CompanyNameValueChange -> {
                advertisementDataFlow.update { state ->
                    state.copy(
                        companyName = event.companyName,
                        companyNameErrorMsg = validationUseCase.emptyFieldValidation(
                            event.companyName,
                            context.getString(R.string.please_enter_your_company_name)
                        ).errorMsg
                    )
                }

            }

            is AddAdvertisementUiEvent.ContactPersonValueChange -> {
                advertisementDataFlow.update { state ->
                    state.copy(
                        contactPerson = event.contactPerson,
                        contactPersonErrorMsg = phoneNumberValidation(
                            event.contactPerson,
                            context
                        ).errorMsg
                    )
                }
            }

            is AddAdvertisementUiEvent.DescriptionValueChange -> {
                advertisementDataFlow.update { state ->
                    state.copy(
                        description = event.description,
                        descriptionErrorMsg = validationUseCase.emptyFieldValidation(
                            event.description,
                            context.getString(R.string.please_enter_your_description)
                        ).errorMsg
                    )
                }
            }

            is AddAdvertisementUiEvent.EmailValueChange -> {
                advertisementDataFlow.update { state ->
                    state.copy(
                        email = event.email,
                        emailErrorMsg = validationUseCase.emailValidation(
                            emailAddress = event.email,
                            context = context
                        ).errorMsg

                    )
                }
            }

            is AddAdvertisementUiEvent.LinkValueChange -> {
                advertisementDataFlow.update { state ->
                    state.copy(
                        link = event.link,
                        linkErrorMsg = linkValidation(event.link, context).errorMsg
                    )
                }
            }

            is AddAdvertisementUiEvent.MobileNumberValueChange -> {
                advertisementDataFlow.update { state ->
                    state.copy(
                        mobileNumber = event.mobileNumber,
                        mobileNumberErrorMsg = phoneNumberValidation(
                            event.mobileNumber,
                            context
                        ).errorMsg
                    )
                }
            }

            is AddAdvertisementUiEvent.PhysicalAddressValueChange -> {
                advertisementDataFlow.update { state ->
                    state.copy(
                        physicalAddress = event.physicalAddress,
                        physicalAddressErrorMsg = validationUseCase.emptyFieldValidation(
                            event.physicalAddress,
                            context.getString(R.string.please_enter_your_physical_address)
                        ).errorMsg
                    )
                }
            }

            is AddAdvertisementUiEvent.PurposeAdvertisementValueChange -> {
                advertisementDataFlow.update { state ->
                    state.copy(
                        purposeAdvertisement = event.purposeAdvertisement,
                        purposeAdvertisementErrorMsg = validationUseCase.emptyFieldValidation(
                            event.purposeAdvertisement,
                            context.getString(R.string.please_enter_your_purpose_of_advertisement)
                        ).errorMsg
                    )
                }
            }

            is AddAdvertisementUiEvent.TitleValueChange -> {
                advertisementDataFlow.update { state ->
                    state.copy(
                        title = event.title,
                        titleErrorMsg = validationUseCase.emptyFieldValidation(
                            event.title,
                            context.getString(R.string.please_enter_your_title)
                        ).errorMsg
                    )
                }
            }

            is AddAdvertisementUiEvent.OnClickOfEndDate -> {
                advertisementDataFlow.update { state ->
                    state.copy(
                        endDate = event.endDate,
                        endDateErrorMsg = dateOfBirthValidation(
                            event.endDate,
                            context
                        ).errorMsg
                    )
                }

            }

            is AddAdvertisementUiEvent.OnClickOfStartDate -> {
                advertisementDataFlow.update { state ->
                    state.copy(
                        startDate = event.startDate,
                        startDateErrorMsg = dateOfBirthValidation(
                            event.startDate,
                            context
                        ).errorMsg
                    )
                }

            }

            is AddAdvertisementUiEvent.ShowDialog -> {
                advertisementDataFlow.update { state ->
                    state.copy(
                        showDialog = event.show
                    )
                }
            }

            is AddAdvertisementUiEvent.ShowPermissionDialog -> {
                advertisementDataFlow.update { state ->
                    state.copy(
                        showPermissionDialog = event.show
                    )
                }
            }

            is AddAdvertisementUiEvent.ProfileValueChange -> {
                advertisementDataFlow.update { state ->
                    state.copy(
                        photo = event.photo,
                        photoErrorMsg = validationUseCase.emptyFieldValidation(
                            event.photo,
                            context.getString(R.string.please_select_your_photo)
                        ).errorMsg
                    )
                }
            }

            AddAdvertisementUiEvent.AddAdvertisementClick -> {
                if (!isOffline.value) {
                    validationUseCase.apply {
                        val companyNameValidationResult = emptyFieldValidation(
                            advertisementDataFlow.value.companyName,
                            context.getString(R.string.please_enter_your_company_name)
                        )


                        val contactPersonValidationResult = phoneNumberValidation(
                            advertisementDataFlow.value.contactPerson,
                            context
                        )
                        val emailValidationResult =
                            emailValidation(advertisementDataFlow.value.email, context = context)


                        val mobileNumberPersonValidationResult = phoneNumberValidation(
                            advertisementDataFlow.value.mobileNumber,
                            context
                        )

                        val physicalAddressValidationResult = emptyFieldValidation(
                            advertisementDataFlow.value.physicalAddress,
                            context.getString(R.string.please_enter_your_physical_address)
                        )

                        val purposeOfAdvertisementValidationResult = emptyFieldValidation(
                            advertisementDataFlow.value.purposeAdvertisement,
                            context.getString(R.string.please_enter_your_purpose_of_advertisement)
                        )

                        val descriptionValidationResult = emptyFieldValidation(
                            advertisementDataFlow.value.description,
                            context.getString(R.string.please_enter_your_description)
                        )

                        val linkValidationResult =
                            linkValidation(advertisementDataFlow.value.link, context)

                        val titleValidationResult = emptyFieldValidation(
                            advertisementDataFlow.value.title,
                            context.getString(R.string.please_enter_your_title)
                        )

                        val startDateValidationResult = dateOfBirthValidation(
                            advertisementDataFlow.value.startDate,
                            context
                        )
                        val endDateValidationResult = dateOfBirthValidation(
                            advertisementDataFlow.value.endDate,
                            context
                        )
                        val photoValidationResult = emptyFieldValidation(
                            advertisementDataFlow.value.photo,
                            context.getString(R.string.please_select_your_photo)
                        )

                        val hasError = listOf(
                            companyNameValidationResult,
                            contactPersonValidationResult,
                            emailValidationResult,
                            mobileNumberPersonValidationResult,
                            physicalAddressValidationResult,
                            purposeOfAdvertisementValidationResult,
                            descriptionValidationResult,
                            linkValidationResult,
                            titleValidationResult,
                            startDateValidationResult,
                            endDateValidationResult,
                            photoValidationResult

                        ).any { !it.isSuccess }
                        // 🔹 **Update all error messages in one go**
                        advertisementDataFlow.update { state ->
                            state.copy(
                                companyNameErrorMsg = companyNameValidationResult.errorMsg,
                                contactPersonErrorMsg = contactPersonValidationResult.errorMsg,
                                emailErrorMsg = emailValidationResult.errorMsg,
                                mobileNumberErrorMsg = mobileNumberPersonValidationResult.errorMsg,
                                physicalAddressErrorMsg = physicalAddressValidationResult.errorMsg,
                                purposeAdvertisementErrorMsg = purposeOfAdvertisementValidationResult.errorMsg,
                                descriptionErrorMsg = descriptionValidationResult.errorMsg,
                                linkErrorMsg = linkValidationResult.errorMsg,
                                titleErrorMsg = titleValidationResult.errorMsg,
                                startDateErrorMsg = startDateValidationResult.errorMsg,
                                endDateErrorMsg = endDateValidationResult.errorMsg,
                                photoErrorMsg = photoValidationResult.errorMsg
                            )
                        }
                        if (hasError) return //  Stop if any validation failed
                    }
                    if (screen == Constants.AppScreen.EDIT_ADVERTISEMENT_SCREEN) {
                        callUpdateAdvertisementAPI(
                            coroutineScope = coroutineScope,
                            navigation = navigate
                        )
                    } else {
                        callAddAdvertisementAPI(coroutineScope = coroutineScope, navigation = navigate)
                    }

                } else {
                    showWaringMessage(
                        this.context,
                        context.getString(R.string.please_check_your_internet_connection_first)
                    )
                }

            }
        }
    }

    private fun callAddAdvertisementAPI(
        coroutineScope: CoroutineScope,
        navigation: (NavigationAction) -> Unit
    ) {

        val map: HashMap<String, RequestBody> = hashMapOf()
        map[Constants.AddAdvertisement.COMPANY_NAME] =
            advertisementDataFlow.value.companyName.toRequestBody("multipart/form-data".toMediaTypeOrNull())

        map[Constants.AddAdvertisement.CONTACT_PERSON] =
            advertisementDataFlow.value.contactPerson.toRequestBody("multipart/form-data".toMediaTypeOrNull())

        map[Constants.AddAdvertisement.EMAIL] =
            advertisementDataFlow.value.email.toRequestBody("multipart/form-data".toMediaTypeOrNull())



        map[Constants.AddAdvertisement.PHONE_NUMBER] =
            advertisementDataFlow.value.mobileNumber.toRequestBody("multipart/form-data".toMediaTypeOrNull())

        map[Constants.AddAdvertisement.PHYSICAL_ADDRESS] =
            advertisementDataFlow.value.physicalAddress.toRequestBody("multipart/form-data".toMediaTypeOrNull())

        map[Constants.AddAdvertisement.PHYSICAL_ADDRESS] =
            advertisementDataFlow.value.physicalAddress.toRequestBody("multipart/form-data".toMediaTypeOrNull())

        map[Constants.AddAdvertisement.PURPOSE] =
            advertisementDataFlow.value.purposeAdvertisement.toRequestBody("multipart/form-data".toMediaTypeOrNull())

        map[Constants.AddAdvertisement.DESCRIPTION] =
            advertisementDataFlow.value.description.toRequestBody("multipart/form-data".toMediaTypeOrNull())

        map[Constants.AddAdvertisement.LINK] =
            advertisementDataFlow.value.link.toRequestBody("multipart/form-data".toMediaTypeOrNull())

        map[Constants.AddAdvertisement.TITLE] =
            advertisementDataFlow.value.title.toRequestBody("multipart/form-data".toMediaTypeOrNull())

        map[Constants.AddAdvertisement.START_DATE] =
            AppUtils.convertDateToTimestamp(advertisementDataFlow.value.startDate).toString()
                .toRequestBody("multipart/form-data".toMediaTypeOrNull())

        map[Constants.AddAdvertisement.END_DATE] =
            AppUtils.convertDateToTimestamp(advertisementDataFlow.value.endDate).toString()
                .toRequestBody("multipart/form-data".toMediaTypeOrNull())

        val profileImageFile = File(advertisementDataFlow.value.photo)
        val photo =
            createMultipartBody(profileImageFile, Constants.AddAdvertisement.IMAGE)

        coroutineScope.launch {
            apiRepository.addAdvertisement(map, photo).collect {
                when (it) {
                    is NetworkResult.Error -> {
                        showOrHideLoader(false)
                        showErrorMessage(
                            context = context,
                            it.message ?: "Something went wrong!"
                        )
                    }

                    is NetworkResult.Loading -> {
                        showOrHideLoader(true)
                    }

                    is NetworkResult.Success -> {
                        showOrHideLoader(false)
                        showSuccessMessage(context = context, it.data?.message ?: "")
                        coroutineScope.launch {
                            delay(1000) // Adjust delay if needed
                            navigation(NavigationAction.Navigate(AdvertisementSubscriptionRoute.createRoute(it.data?.data?.id ?: "")))
                            /* navigation(
                                 NavigationAction.PopWithResult(
                                     resultValues = listOf(
                                         PopResultKeyValue(
                                             "advertisementScreen",
                                             Constants.AppScreen.ADVERTISEMENT_SCREEN
                                         ),

                                         )
                                 )
                             )*/
                        }
                    }

                    is NetworkResult.UnAuthenticated -> {
                        showOrHideLoader(false)
                        showErrorMessage(
                            context = context,
                            it.message ?: "Something went wrong!"
                        )
                    }
                }
            }

        }
    }

    private fun callUpdateAdvertisementAPI(
        coroutineScope: CoroutineScope,
        navigation: (NavigationAction) -> Unit
    ) {
        val map: HashMap<String, RequestBody> = hashMapOf()
        map[Constants.AddAdvertisement.ADVERTISEMENT_ID] =
            advertisementId.value.toRequestBody("multipart/form-data".toMediaTypeOrNull())

        map[Constants.AddAdvertisement.COMPANY_NAME] =
            advertisementDataFlow.value.companyName.toRequestBody("multipart/form-data".toMediaTypeOrNull())

        map[Constants.AddAdvertisement.CONTACT_PERSON] =
            advertisementDataFlow.value.contactPerson.toRequestBody("multipart/form-data".toMediaTypeOrNull())

        map[Constants.AddAdvertisement.EMAIL] =
            advertisementDataFlow.value.email.toRequestBody("multipart/form-data".toMediaTypeOrNull())



        map[Constants.AddAdvertisement.PHONE_NUMBER] =
            advertisementDataFlow.value.mobileNumber.toRequestBody("multipart/form-data".toMediaTypeOrNull())

        map[Constants.AddAdvertisement.PHYSICAL_ADDRESS] =
            advertisementDataFlow.value.physicalAddress.toRequestBody("multipart/form-data".toMediaTypeOrNull())

        map[Constants.AddAdvertisement.PHYSICAL_ADDRESS] =
            advertisementDataFlow.value.physicalAddress.toRequestBody("multipart/form-data".toMediaTypeOrNull())

        map[Constants.AddAdvertisement.PURPOSE] =
            advertisementDataFlow.value.purposeAdvertisement.toRequestBody("multipart/form-data".toMediaTypeOrNull())

        map[Constants.AddAdvertisement.DESCRIPTION] =
            advertisementDataFlow.value.description.toRequestBody("multipart/form-data".toMediaTypeOrNull())

        map[Constants.AddAdvertisement.LINK] =
            advertisementDataFlow.value.link.toRequestBody("multipart/form-data".toMediaTypeOrNull())

        map[Constants.AddAdvertisement.TITLE] =
            advertisementDataFlow.value.title.toRequestBody("multipart/form-data".toMediaTypeOrNull())

        map[Constants.AddAdvertisement.START_DATE] =
            AppUtils.convertDateToTimestamp(advertisementDataFlow.value.startDate).toString()
                .toRequestBody("multipart/form-data".toMediaTypeOrNull())

        map[Constants.AddAdvertisement.END_DATE] =
            AppUtils.convertDateToTimestamp(advertisementDataFlow.value.endDate).toString()
                .toRequestBody("multipart/form-data".toMediaTypeOrNull())

        // Handle photo upload for update - check if it's a URL or local file
        val photo = if (advertisementDataFlow.value.photo.isNotEmpty()) {
            if (advertisementDataFlow.value.photo.startsWith("http://") ||
                advertisementDataFlow.value.photo.startsWith("https://")
            ) {
                // It's a URL, don't include in multipart body for update
                Log.d("TAG", "Photo is a URL, skipping multipart upload: ${advertisementDataFlow.value.photo}")
                null
            } else {
                // It's a local file path
                Log.d("TAG", "Photo is a local file, creating multipart body: ${advertisementDataFlow.value.photo}")
                val profileImageFile = File(advertisementDataFlow.value.photo)
                createMultipartBody(profileImageFile, Constants.AddAdvertisement.IMAGE)
            }
        } else {
            Log.d("TAG", "Photo is empty, skipping multipart upload")
            null
        }

        coroutineScope.launch {
            apiRepository.updateAdvertisement(map, photo).collect {
                when (it) {
                    is NetworkResult.Error -> {
                        showOrHideLoader(false)
                        Log.d("TAG", "callUpdateAdvertisementAPI: ${it.message}")

                        showErrorMessage(
                            context = context,
                            it.message ?: "Something went wrong!"
                        )
                    }

                    is NetworkResult.Loading -> {
                        showOrHideLoader(true)
                    }

                    is NetworkResult.Success -> {
                        showOrHideLoader(false)
                        showSuccessMessage(context = context, it.data?.message ?: "")
                        coroutineScope.launch {
                            delay(1000)
                            navigation(NavigationAction.PopAndNavigate(AdvertisementRoute.createRoute()))
                        }
                    }

                    is NetworkResult.UnAuthenticated -> {
                        showOrHideLoader(false)
                        showErrorMessage(
                            context = context,
                            it.message ?: "Something went wrong!"
                        )
                    }
                }
            }

        }
    }


    private fun phoneNumberValidation(phoneNumber: String, context: Context): ValidationResult {
        val isValidLength = phoneNumber.length in 10..15

        return ValidationResult(
            isSuccess = isValidLength,
            errorMsg = when {
                phoneNumber.isBlank() -> context.getString(R.string.please_enter_a_phone_number)
                !isValidLength -> context.getString(R.string.error_enter_valid_number)
                else -> null
            }
        )
    }

    private fun linkValidation(link: String, context: Context): ValidationResult {
        return ValidationResult(
            isSuccess = link.isBlank() || android.util.Patterns.WEB_URL.matcher(link).matches(),
            errorMsg = when {
                link.isBlank() -> context.getString(R.string.please_enter_your_link) // No error if empty (if you want to make it required, change this)
                !android.util.Patterns.WEB_URL.matcher(link).matches() ->
                    context.getString(R.string.please_enter_the_valid_link)

                else -> null
            }
        )
    }

    private fun dateOfBirthValidation(dob: String, context: Context): ValidationResult {
        return ValidationResult(
            isSuccess = dob.isNotBlank(),
            errorMsg = if (dob.isBlank()) context.getString(R.string.please_enter_your_date_of_birth) else null
        )
    }

    private fun showOrHideLoader(showLoader: Boolean) {
        advertisementDataFlow.update { state ->
            state.copy(
                showLoader = showLoader
            )
        }
    }

}


