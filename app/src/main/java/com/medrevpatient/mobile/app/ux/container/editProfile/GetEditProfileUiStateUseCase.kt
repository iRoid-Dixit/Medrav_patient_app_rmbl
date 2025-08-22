package com.medrevpatient.mobile.app.ux.container.editProfile

import android.content.Context
import android.util.Log
import co.touchlab.kermit.Logger
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.data.source.Constants
import com.medrevpatient.mobile.app.data.source.local.datastore.AppPreferenceDataStore
import com.medrevpatient.mobile.app.data.source.remote.helper.NetworkResult
import com.medrevpatient.mobile.app.data.source.remote.repository.ApiRepository
import com.medrevpatient.mobile.app.domain.validation.ValidationResult
import com.medrevpatient.mobile.app.domain.validation.ValidationUseCase
import com.medrevpatient.mobile.app.model.domain.response.auth.UserAuthResponse
import com.medrevpatient.mobile.app.navigation.NavigationAction
import com.medrevpatient.mobile.app.ui.compose.common.countryCode.CountryCodePickerNew
import com.medrevpatient.mobile.app.ui.compose.common.countryCode.allCountries
import com.medrevpatient.mobile.app.utils.AppUtils
import com.medrevpatient.mobile.app.utils.AppUtils.createMultipartBody
import com.medrevpatient.mobile.app.utils.AppUtils.showErrorMessage
import com.medrevpatient.mobile.app.utils.AppUtils.showSuccessMessage
import com.medrevpatient.mobile.app.utils.AppUtils.showWaringMessage
import com.medrevpatient.mobile.app.utils.connection.NetworkMonitor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class GetEditProfileUiStateUseCase
@Inject constructor(
    private val validationUseCase: ValidationUseCase,
    private val networkMonitor: NetworkMonitor,
    private val apiRepository: ApiRepository,
    private val appPreferenceDataStore: AppPreferenceDataStore,
) {
    private var userData: UserAuthResponse? = null
    private val isOffline = MutableStateFlow(false)
    private lateinit var context: Context
    private val isNewImage = MutableStateFlow(false)
    private val editProfileDataFlow = MutableStateFlow(EditProfileDataState())
    operator fun invoke(
        context: Context,
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
    ): EditProfileUiState {
        coroutineScope.launch {
            networkMonitor.isOnline.map(Boolean::not).stateIn(
                scope = coroutineScope,
                started = WhileSubscribed(5_000),
                initialValue = false,
            ).collect {
                isOffline.value = it
            }
        }

        coroutineScope.launch {
            userData = appPreferenceDataStore.getUserData()
            userData?.let {
                editProfileDataFlow.update { profileUiDataState ->
                    profileUiDataState.copy(
                        name = it.name ?: "",
                        email = it.email ?: "",
                        profileImage = it.profileImage ?: "",
                        phoneNumber = it.mobileNumber ?: "",
                        dateSelected = formatDate(it.dateOfBirth),
                        showCountryCode = allCountries.find { it.cCountryPhoneNoCode == appPreferenceDataStore.getUserData()?.countryCode }?.countryCode
                            ?: "+1",
                        selectGender = Constants.getGenderLabel(
                            it.gender.toString()
                        )
                    )
                }
            }
        }
        return EditProfileUiState(
            editProfileDataFlow = editProfileDataFlow,
            event = { aboutUsEvent ->
                contactUsUiEvent(
                    event = aboutUsEvent,
                    context = context,
                    navigate = navigate,
                    coroutineScope = coroutineScope

                )
            }
        )
    }

    private fun contactUsUiEvent(
        event: EditProfileUiEvent,
        context: Context,
        navigate: (NavigationAction) -> Unit,
        coroutineScope: CoroutineScope
    ) {
        when (event) {
            EditProfileUiEvent.BackClick -> {
                navigate(NavigationAction.PopIntent)
            }

            is EditProfileUiEvent.GetContext -> {
                this.context = event.context
            }

            is EditProfileUiEvent.EmailValueChange -> {
                editProfileDataFlow.update { state ->
                    state.copy(
                        email = event.email,
                        emailErrorMsg = validationUseCase.emailValidation(
                            emailAddress = event.email,
                            context = context
                        ).errorMsg
                    )

                }
            }

            is EditProfileUiEvent.NameValueChange -> {
                editProfileDataFlow.update { state ->
                    state.copy(
                        name = event.name,
                        nameErrorMsg = validationUseCase.emptyFieldValidation(
                            event.name,
                            context.getString(R.string.please_enter_your_full_name)
                        ).errorMsg
                    )
                }

            }

            is EditProfileUiEvent.OnClickOfDate -> {
                // Convert the date from "dd/MM/yyyy" format to "MMMM dd, yyyy" format for display
                Log.d("TAG", "Date picker selected: ${event.date}")
                val formattedDate = convertDateToDisplayFormat(event.date)
                Log.d("TAG", "Converted to display format: $formattedDate")
                editProfileDataFlow.update { state ->
                    state.copy(
                        dateSelected = formattedDate,
                        dateOfBirthValidationMsg = dateOfBirthValidation(
                            formattedDate,
                            context
                        ).errorMsg
                    )
                }

            }

            is EditProfileUiEvent.PhoneNumberValueChange -> {
                editProfileDataFlow.update { state ->
                    state.copy(
                        phoneNumber = event.phoneNumber,
                        phoneNumberErrorMsg = phoneNumberValidation(
                            event.phoneNumber,
                            context
                        ).errorMsg
                    )
                }

            }

            is EditProfileUiEvent.RoleDropDownExpanded -> {
                editProfileDataFlow.update { state ->
                    state.copy(
                        selectGender = event.selectGender,
                        selectGanderErrorMsg = genderValidation(
                            event.selectGender,
                            context = context
                        ).errorMsg
                    )

                }
                Log.d("TAG", "selectGender: ${editProfileDataFlow.value.selectGender}")

            }

            EditProfileUiEvent.ProfileSubmitClick -> {
                if (!isOffline.value) {
                    validationUseCase.apply {
                        val nameValidationResult = emptyFieldValidation(
                            editProfileDataFlow.value.name,
                            context.getString(R.string.please_enter_your_full_name)
                        )
                        val emailValidationResult =
                            emailValidation(editProfileDataFlow.value.email, context = context)
                        val phoneValidationResult = phoneNumberValidation(
                            editProfileDataFlow.value.phoneNumber,
                            context
                        )
                        val dobValidationResult = dateOfBirthValidation(
                            editProfileDataFlow.value.dateSelected,
                            context
                        )
                        val genderValidationResult =
                            genderValidation(editProfileDataFlow.value.selectGender, context)
                        val hasError = listOf(
                            nameValidationResult,
                            emailValidationResult,
                            phoneValidationResult,
                            dobValidationResult,
                            genderValidationResult,

                            ).any { !it.isSuccess }
                        // 🔹 **Update all error messages in one go**
                        editProfileDataFlow.update { state ->
                            state.copy(
                                nameErrorMsg = nameValidationResult.errorMsg,
                                emailErrorMsg = emailValidationResult.errorMsg,
                                phoneNumberErrorMsg = phoneValidationResult.errorMsg,
                                dateOfBirthValidationMsg = dobValidationResult.errorMsg,
                                selectGanderErrorMsg = genderValidationResult.errorMsg,

                                )
                        }
                        if (hasError) return
                    }
                    callEditProfileApi(coroutineScope = coroutineScope, navigation = navigate)

                } else {
                    showWaringMessage(
                        this.context,
                        context.getString(R.string.please_check_your_internet_connection_first)
                    )
                }
            }

            is EditProfileUiEvent.ProfileValueChange -> {
                isNewImage.value = true
                editProfileDataFlow.update { state ->
                    isNewImage.value = true
                    state.copy(
                        profileImage = event.profile
                    )
                }

            }
            is EditProfileUiEvent.ShowDialog -> {
                editProfileDataFlow.update { state ->
                    state.copy(
                        showDialog = event.show
                    )
                }
            }

            is EditProfileUiEvent.ShowPermissionDialog -> {
                editProfileDataFlow.update { state ->
                    state.copy(
                        showPermissionDialog = event.show
                    )
                }
            }

        }
    }

    private fun dateOfBirthValidation(dob: String, context: Context): ValidationResult {
        return ValidationResult(
            isSuccess = dob.isNotBlank(),
            errorMsg = if (dob.isBlank()) context.getString(R.string.please_enter_your_date_of_birth) else null
        )
    }

    private fun genderValidation(gender: String?, context: Context): ValidationResult {
        return ValidationResult(
            isSuccess = !gender.isNullOrBlank(),
            errorMsg = if (gender.isNullOrBlank()) context.getString(R.string.please_select_your_gender) else null
        )
    }

    private fun showOrHideLoader(showLoader: Boolean) {
        editProfileDataFlow.update { state ->
            state.copy(
                showLoader = showLoader
            )
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

    private fun callEditProfileApi(
        coroutineScope: CoroutineScope,
        navigation: (NavigationAction) -> Unit
    ) {
        val genderValue = when (editProfileDataFlow.value.selectGender) {
            context.getString(R.string.male) -> Constants.Gender.MALE.value
            context.getString(R.string.female) -> Constants.Gender.FEMALE.value
            else -> Constants.Gender.NON_BINARY.value
        }
        val map: HashMap<String, RequestBody> = hashMapOf()
        if (editProfileDataFlow.value.name.isNotBlank()) map[Constants.EditProfile.NAME] =
            editProfileDataFlow.value.name.toRequestBody("multipart/form-data".toMediaTypeOrNull())

        if (editProfileDataFlow.value.email.isNotBlank()) map[Constants.EditProfile.EMAIL] =
            editProfileDataFlow.value.email.toRequestBody("multipart/form-data".toMediaTypeOrNull())

        if (editProfileDataFlow.value.phoneNumber.isNotBlank()) map[Constants.EditProfile.MOBILE_NUMBER] =
            editProfileDataFlow.value.phoneNumber.toRequestBody("multipart/form-data".toMediaTypeOrNull())

        if (editProfileDataFlow.value.dateSelected.isNotBlank()) {
            // Convert from display format "MMMM dd, yyyy" back to "dd/MM/yyyy" for API
            Log.d("TAG", "Display format date: ${editProfileDataFlow.value.dateSelected}")
            val apiDate = convertDisplayFormatToApiFormat(editProfileDataFlow.value.dateSelected)
            Log.d("TAG", "API format date: $apiDate")
            map[Constants.EditProfile.DATE_OF_BIRTH] =
                AppUtils.convertDateToTimestamp(apiDate).toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        }

        if (editProfileDataFlow.value.dateSelected.isNotBlank()) map[Constants.EditProfile.COUNTRY_CODE] =
            CountryCodePickerNew.getCountryPhoneCodeNew().toRequestBody("multipart/form-data".toMediaTypeOrNull())


        if (editProfileDataFlow.value.selectGender.isNotBlank()) map[Constants.EditProfile.GENDER] =
            genderValue.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())

        Log.d("TAG", "callEditProfileApi: ${editProfileDataFlow.value.dateSelected}")

        val profileImage: MultipartBody.Part? =
            editProfileDataFlow.value.profileImage.let { imagePath ->
                if (imagePath.isNotEmpty()) {
                    if (imagePath.startsWith("http://") || imagePath.startsWith("https://")) {
                        // It's a URL, don't include in multipart body for update
                        Log.d("TAG", "Profile image is a URL, skipping multipart upload: $imagePath")
                        null
                    } else {
                        // It's a local file path
                        Log.d("TAG", "Profile image is a local file, creating multipart body: $imagePath")
                        val profileImageFile = File(imagePath)
                        createMultipartBody(profileImageFile, Constants.EditProfile.PROFILE)
                    }
                } else {
                    null
                }
            }

        if (isNewImage.value) {
            coroutineScope.launch {
                apiRepository.editProfileDetails(map, profileImage).collect {
                    when (it) {
                        is NetworkResult.Error -> {
                            showErrorMessage(
                                context = context,
                                it.message ?: "Something went wrong!"
                            )
                            showOrHideLoader(false)
                        }

                        is NetworkResult.Loading -> {
                            showOrHideLoader(true)
                        }

                        is NetworkResult.Success -> {
                            showSuccessMessage(context = context, it.data?.message ?: "")
                            showOrHideLoader(false)
                            storeResponseToDataStore(
                                coroutineScope = coroutineScope,
                                navigate = navigation,
                                userAuthResponseData = it.data?.data
                            )
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


        } else {
            coroutineScope.launch {
                apiRepository.editProfileDetailsWithOutImage(map).collect {
                    when (it) {
                        is NetworkResult.Error -> {
                            showErrorMessage(
                                context = context,
                                it.message ?: "Something went wrong!"
                            )
                            showOrHideLoader(false)
                        }

                        is NetworkResult.Loading -> {
                            showOrHideLoader(true)
                        }

                        is NetworkResult.Success -> {
                            showSuccessMessage(context = context, it.data?.message ?: "")
                            storeResponseToDataStore(
                                coroutineScope = coroutineScope,
                                navigate = navigation,
                                userAuthResponseData = it.data?.data
                            )
                            showOrHideLoader(false)
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
    }

    private fun storeResponseToDataStore(
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
        userAuthResponseData: UserAuthResponse?
    ) {
        coroutineScope.launch {
            userAuthResponseData?.let {
                appPreferenceDataStore.setIsProfilePicUpdated(true)
                appPreferenceDataStore.saveUserData(it)
                coroutineScope.launch {
                    delay(1000) // Adjust delay if needed
                    navigate(NavigationAction.PopIntent)
                }
            }
        }

    }

    private fun formatDate(timestamp: Long?): String {
        return if (timestamp != null && timestamp > 0) {
            try {
                val outputFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH)
                val date = Date(timestamp)
                outputFormat.format(date)
            } catch (e: Exception) {
                Logger.e("Return empty string if an error occurs ${e.message}")
                "" // Return empty string if an error occurs
            }
        } else {
            "" // Handle null or invalid timestamps
        }
    }

    private fun convertDateToDisplayFormat(dateString: String): String {
        return try {
            if (dateString.isBlank()) return ""

            // Parse the date from "dd/MM/yyyy" format
            val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
            val date = inputFormat.parse(dateString)

            if (date != null) {
                // Format to "MMMM dd, yyyy" format
                val outputFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH)
                outputFormat.format(date)
            } else {
                dateString // Return original string if parsing failed
            }
        } catch (e: Exception) {
            Log.e("TAG", "Error converting date format: $dateString", e)
            dateString // Return original string if conversion fails
        }
    }

    private fun convertDisplayFormatToApiFormat(dateString: String): String {
        return try {
            if (dateString.isBlank()) return ""

            // Parse the date from "MMMM dd, yyyy" format
            val inputFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH)
            val date = inputFormat.parse(dateString)

            if (date != null) {
                // Format to "dd/MM/yyyy" format for API
                val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
                outputFormat.format(date)
            } else {
                dateString // If parsing failed
            }
        } catch (e: Exception) {
            Log.e("TAG", "Error converting display format to API format: $dateString", e)
            dateString // Fallback to original string
        }
    }

}


