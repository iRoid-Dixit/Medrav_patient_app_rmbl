package com.medrevpatient.mobile.app.ux.container.editProfile

import android.content.Context
import android.util.Log
import androidx.compose.material3.ExperimentalMaterial3Api
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.data.source.Constants
import com.medrevpatient.mobile.app.data.source.local.datastore.AppPreferenceDataStore
import com.medrevpatient.mobile.app.data.source.remote.helper.NetworkResult
import com.medrevpatient.mobile.app.data.source.remote.repository.ApiRepository
import com.medrevpatient.mobile.app.domain.validation.ValidationResult
import com.medrevpatient.mobile.app.domain.validation.ValidationUseCase
import com.medrevpatient.mobile.app.model.domain.request.authReq.ResendOTPReq
import com.medrevpatient.mobile.app.model.domain.request.authReq.VerifyOTPReq
import com.medrevpatient.mobile.app.model.domain.response.auth.UserAuthResponse
import com.medrevpatient.mobile.app.navigation.NavigationAction
import com.medrevpatient.mobile.app.utils.AppUtils.createMultipartBody
import com.medrevpatient.mobile.app.utils.AppUtils.showErrorMessage
import com.medrevpatient.mobile.app.utils.AppUtils.showSuccessMessage
import com.medrevpatient.mobile.app.utils.AppUtils.showWarningMessage

import com.medrevpatient.mobile.app.utils.connection.NetworkMonitor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
    private var countdownJob: Job? = null
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
            val userData = appPreferenceDataStore.getUserData()
            Log.d("TAG", "dateOfBirth timestamp: ${userData?.dateOfBirth}")
            userData?.dateOfBirth?.let { timestamp ->
                Log.d("TAG", "Formatted date: ${formatDate(timestamp)}")
                Log.d("TAG", "Date in milliseconds: ${timestamp * 1000}")
            }
        }
        coroutineScope.launch {
            userData = appPreferenceDataStore.getUserData()
            userData?.let {
                editProfileDataFlow.update { profileUiDataState ->
                    profileUiDataState.copy(
                        email = it.email ?: "",
                        originalEmail = it.email ?: "",
                        profileImage = it.profileImage ?: "",
                        originalProfileImage = it.profileImage ?: "",
                        firstName = it.firstName ?: "",
                        originalFirstName = it.firstName ?: "",
                        dateSelected = formatDate(it.dateOfBirth),
                        originalDateSelected = formatDate(it.dateOfBirth),
                        bmiCategory = it.bmiCategory ?: 0,
                        lastName = it.lastName ?: "",
                        originalLastName = it.lastName ?: "",
                        height = it.height ?: "",
                        originalHeight = it.height ?: "",
                        weight = it.weight ?: "",
                        originalWeight = it.weight ?: "",
                        bmi = it.bmi ?: "",
                        allergies = it.knownAllergies ?: "",
                        originalAllergies = it.knownAllergies ?: "",
                        medicalConditions = it.medicalConditions ?: "",
                        originalMedicalConditions = it.medicalConditions ?: "",
                    )
                }
                // Check form changes after initial data load
                checkFormChanges()
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

    @OptIn(ExperimentalMaterial3Api::class)
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
                val trimmedEmail = event.email.trim()
                val emailValidationResult = validationUseCase.emailValidation(
                    emailAddress = trimmedEmail,
                    context = context
                )
                val isEmailChanged = trimmedEmail != editProfileDataFlow.value.originalEmail

                editProfileDataFlow.update { state ->
                    state.copy(
                        email = trimmedEmail,
                        emailErrorMsg = emailValidationResult.errorMsg,
                        isEmailChanged = isEmailChanged,
                        isEmailValid = emailValidationResult.isSuccess && isEmailChanged
                    )
                }
                checkFormChanges()
            }

            is EditProfileUiEvent.FirstNameValueChange -> {
                editProfileDataFlow.update { state ->
                    state.copy(
                        firstName = event.firstName.trim(),
                        firstNameErrorMsg = validationUseCase.emptyFieldValidation(
                            event.firstName.trim(),
                            "Please enter your first name"
                        ).errorMsg
                    )
                }
                checkFormChanges()
            }

            is EditProfileUiEvent.LastNameValueChange -> {
                editProfileDataFlow.update { state ->
                    state.copy(
                        lastName = event.lastName.trim(),
                        lastNameErrorMsg = validationUseCase.emptyFieldValidation(
                            event.lastName.trim(),
                            "Please enter your last name"
                        ).errorMsg
                    )
                }
                checkFormChanges()
            }

            is EditProfileUiEvent.OnClickOfDate -> {
                Log.d("TAG", "Date picker selected: ${event.date}")
                val formattedDate = convertDateToDisplayFormat(event.date)
                editProfileDataFlow.update { state ->
                    state.copy(
                        dateSelected = formattedDate,
                    )
                }
                checkFormChanges()
            }

            EditProfileUiEvent.UpdateClick -> {
                if (!isOffline.value) {
                    validationUseCase.apply {
                        val firstNameValidationResult = emptyFieldValidation(
                            editProfileDataFlow.value.firstName,
                            "Please enter your firstName."
                        )
                        val lastNameValidationResult = emptyFieldValidation(
                            editProfileDataFlow.value.lastName,
                            "Please enter your lastName."
                        )
                        val emailValidationResult =
                            emailValidation(editProfileDataFlow.value.email, context = context)

                        val heightValidationResult = heightValidation(
                            editProfileDataFlow.value.height,
                            context = context
                        )
                        val weightValidationResult = weightValidation(
                            editProfileDataFlow.value.weight,
                            context = context
                        )
                        // Check if email has been changed and needs verification
                        val isEmailChanged = editProfileDataFlow.value.isEmailChanged
                        val isEmailVerified = !isEmailChanged || editProfileDataFlow.value.verifySheetVisible == false

                        val hasError = listOf(
                            firstNameValidationResult,
                            lastNameValidationResult,
                            emailValidationResult,
                            heightValidationResult,
                            weightValidationResult
                        ).any { !it.isSuccess }

                        // If email is changed but not verified, show error and return
                        if (isEmailChanged && !isEmailVerified) {
                            editProfileDataFlow.update { state ->
                                state.copy(
                                    firstNameErrorMsg = firstNameValidationResult.errorMsg,
                                    lastNameErrorMsg = lastNameValidationResult.errorMsg,
                                    emailErrorMsg = "Please verify your email address before updating",
                                    heightErrorMsg = heightValidationResult.errorMsg,
                                    weightErrorMsg = weightValidationResult.errorMsg
                                )
                            }
                            return
                        }
                        // ���� **Update all error messages in one go**
                        editProfileDataFlow.update { state ->
                            state.copy(
                                firstNameErrorMsg = firstNameValidationResult.errorMsg,
                                lastNameErrorMsg = lastNameValidationResult.errorMsg,
                                emailErrorMsg = emailValidationResult.errorMsg,
                                heightErrorMsg = heightValidationResult.errorMsg,
                                weightErrorMsg = weightValidationResult.errorMsg
                            )
                        }
                        if (hasError) return
                        callEditProfileApi(coroutineScope = coroutineScope, navigation = navigate)
                    }

                } else {
                    showWarningMessage(
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
                checkFormChanges()
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
            // Medical Information events
            is EditProfileUiEvent.HeightValueChange -> {
                val trimmedHeight = event.height.trim()
                val heightValidationResult = validationUseCase.heightValidation(
                    height = trimmedHeight,
                    context = context
                )
                editProfileDataFlow.update { state ->
                    state.copy(
                        height = trimmedHeight,
                        heightErrorMsg = heightValidationResult.errorMsg
                    )
                }
                checkFormChanges()
            }

            is EditProfileUiEvent.WeightValueChange -> {
                val trimmedWeight = event.weight.trim()
                val weightValidationResult = validationUseCase.weightValidation(
                    weight = trimmedWeight,
                    context = context
                )
                editProfileDataFlow.update { state ->
                    state.copy(
                        weight = trimmedWeight,
                        weightErrorMsg = weightValidationResult.errorMsg
                    )
                }
                checkFormChanges()
            }

            is EditProfileUiEvent.AllergiesValueChange -> {
                editProfileDataFlow.update { state ->
                    state.copy(
                        allergies = event.allergies.trim(),
                    )
                }
                checkFormChanges()
            }

            is EditProfileUiEvent.MedicalConditionsValueChange -> {
                editProfileDataFlow.update { state ->
                    state.copy(
                        medicalConditions = event.medicalConditions.trim(),
                    )
                }
                checkFormChanges()
                Log.d("TAG", "contactUsUiEvent: ${event.medicalConditions}")
            }

            is EditProfileUiEvent.BmiValueChange -> {
                editProfileDataFlow.update { state ->
                    state.copy(
                        bmi = event.bmi
                    )
                }
            }

            is EditProfileUiEvent.VerifySheetVisibility -> {
                editProfileDataFlow.update { state ->
                    state.copy(
                        verifySheetVisible = event.isVisible
                    )
                }
            }

            is EditProfileUiEvent.VerifyClick -> {
                if (!isOffline.value) {
                    validationUseCase.apply {
                        val otpValidationResult = otpValidation(
                            editProfileDataFlow.value.otpValue,
                            context = context
                        )

                        val hasErrorOtp = !otpValidationResult.isSuccess
                        editProfileDataFlow.update { state ->
                            state.copy(
                                otpErrorMsg = otpValidationResult.errorMsg
                            )
                        }
                        if (hasErrorOtp) {
                            return
                        }

                        doUserVerifyEmailIn(
                            coroutineScope = coroutineScope,
                            event
                        )
                        // callEditProfileApi(coroutineScope = coroutineScope, navigation = navigate)
                    }
                } else {
                    showWarningMessage(
                        context,
                        context.getString(R.string.please_check_your_internet_connection_first)
                    )
                }
            }

            is EditProfileUiEvent.OtpValueChange -> {
                editProfileDataFlow.update { state ->
                    state.copy(
                        otpValue = event.otp,
                        otpErrorMsg = otpValidation(event.otp, context = context).errorMsg
                    )
                }
            }

            EditProfileUiEvent.ResendCode -> {
                doUserEmailVerifyIn(
                    coroutineScope = coroutineScope,

                    )
            }

            EditProfileUiEvent.VerifyEmailClick -> {
                /*editProfileDataFlow.update {state->
                    state.copy(
                        verifySheetVisible=true
                    )
                }*/
                doUserEmailVerifyIn(
                    coroutineScope = coroutineScope,

                    )
            }

            is EditProfileUiEvent.EditEmailClick -> {
                event.scope.launch {
                    event.sheetState.hide()

                }.invokeOnCompletion {
                    editProfileDataFlow.update { state ->
                        state.copy(
                            verifySheetVisible = false,
                            isEmailChanged = true, // Mark email as changed again when editing
                            otpValue = "", // Clear OTP value
                            otpErrorMsg = null // Clear OTP error
                        )
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    private fun doUserVerifyEmailIn(
        coroutineScope: CoroutineScope,
        event: EditProfileUiEvent.VerifyClick,


        ) {
        coroutineScope.launch {
            val otpVerifyReq = VerifyOTPReq(
                newEmail = editProfileDataFlow.value.email,
                otp = editProfileDataFlow.value.otpValue,
                otpType = Constants.OTPType.EMAIL_UPDATE
            )
            apiRepository.verifyOTP(otpVerifyReq).collect {
                when (it) {
                    is NetworkResult.Error -> {
                        showErrorMessage(context = context, it.message ?: "Something went wrong!")
                        editProfileDataFlow.update { state ->
                            state.copy(
                                otpValue = ""
                            )

                        }
                        showOrHideProceedButtonLoader(false)
                    }

                    is NetworkResult.Loading -> {
                        showOrHideProceedButtonLoader(true)
                    }

                    is NetworkResult.Success -> {
                        showOrHideProceedButtonLoader(false)
                        coroutineScope.launch {
                            it.data?.data?.let {
                                appPreferenceDataStore.setIsProfilePicUpdated(true)
                                appPreferenceDataStore.saveUserData(it)
                            }
                        }
                        showSuccessMessage(context = context, it.data?.message ?: "")
                        event.scope.launch {
                            event.sheetState.hide()

                        }.invokeOnCompletion {
                            editProfileDataFlow.update { state ->
                                state.copy(
                                    verifySheetVisible = false
                                )
                            }
                        }
                    }

                    is NetworkResult.UnAuthenticated -> {
                        showOrHideProceedButtonLoader(false)
                        showErrorMessage(context = context, it.message ?: "Something went wrong!")
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    private fun doUserEmailVerifyIn(
        coroutineScope: CoroutineScope,
    ) {
        coroutineScope.launch {
            val resendOtpReq = ResendOTPReq(
                newEmail = editProfileDataFlow.value.email,
                otpType = Constants.OTPType.EMAIL_UPDATE
            )
            apiRepository.resendOtpOTP(resendOtpReq).collect {
                when (it) {
                    is NetworkResult.Error -> {
                        showErrorMessage(context = context, it.message ?: "Something went wrong!")
                        showOrHideResendButtonLoader(false)
                    }

                    is NetworkResult.Loading -> {
                        showOrHideResendButtonLoader(true)
                    }

                    is NetworkResult.Success -> {
                        showOrHideResendButtonLoader(false)
                        startCountdown(coroutineScope)
                        editProfileDataFlow.update { state ->
                            state.copy(
                                verifySheetVisible = true,
                                otpValue = ""
                            )

                        }
                        showSuccessMessage(context = context, it.data?.message ?: "")

                    }
                    is NetworkResult.UnAuthenticated -> {
                        showOrHideResendButtonLoader(false)
                        showErrorMessage(context = context, it.message ?: "Something went wrong!")
                    }
                }
            }
        }
    }

    private fun showOrHideProceedButtonLoader(isLoading: Boolean) {
        editProfileDataFlow.update { state ->
            state.copy(
                isVerifyButtonLoading = isLoading
            )
        }
    }

    private fun showOrHideResendButtonLoader(isLoading: Boolean) {
        editProfileDataFlow.update { state ->
            state.copy(
                isResendButtonLoading = isLoading
            )
        }
    }

    private fun startCountdown(coroutineScope: CoroutineScope) {
        countdownJob?.cancel()
        countdownJob = coroutineScope.launch(Dispatchers.IO) {
            editProfileDataFlow.value = editProfileDataFlow.value.copy(
                isResendVisible = false
            )
            for (n in 60 downTo 0) {
                withContext(Dispatchers.Main) {
                    editProfileDataFlow.value = editProfileDataFlow.value.copy(
                        remainingTimeFlow = String.format(Locale.getDefault(), "00:%02d", n)
                    )
                }
                delay(1000)
            }
            withContext(Dispatchers.Main) {
                editProfileDataFlow.value = editProfileDataFlow.value.copy(
                    remainingTimeFlow = "00:00",
                    isResendVisible = true
                )
            }
        }
    }
    /*@OptIn(ExperimentalMaterial3Api::class)
    private fun doUserResendOtpIn(
        coroutineScope: CoroutineScope,
    ) {
        coroutineScope.launch {
            val resendOtpReq = ResendOTPReq(
                email = .value.resendEmail,
                otpType = Constants.OTPType.FORGET_PASSWORD
            )
            apiRepository.resendOtpOTP(resendOtpReq).collect {
                when (it) {
                    is NetworkResult.Error -> {
                        showErrorMessage(context = context, it.message ?: "Something went wrong!")
                        showOrHideResendButtonLoader(false)
                    }

                    is NetworkResult.Loading -> {
                        showOrHideResendButtonLoader(true)
                    }

                    is NetworkResult.Success -> {
                        showOrHideResendButtonLoader(false)
                        startCountdown(coroutineScope)
                        showSuccessMessage(context = context, it.data?.message ?: "")

                    }

                    is NetworkResult.UnAuthenticated -> {
                        showOrHideResendButtonLoader(false)
                        showErrorMessage(context = context, it.message ?: "Something went wrong!")
                    }
                }
            }
        }
    }*/

    private fun otpValidation(otp: String?, context: Context): ValidationResult {
        return ValidationResult(
            isSuccess = !otp.isNullOrBlank() && otp.length == 4,
            errorMsg = when {
                otp.isNullOrBlank() -> context.getString(R.string.please_provide_otp_for_verification)
                otp.length != 4 -> context.getString(R.string.the_otp_filed_must_be_4_digits)
                else -> null
            }
        )
    }

    private fun callEditProfileApi(
        coroutineScope: CoroutineScope,
        navigation: (NavigationAction) -> Unit
    ) {
        val map: HashMap<String, RequestBody> = hashMapOf()

        // Only add fields that have non-empty values
        if (editProfileDataFlow.value.firstName.isNotBlank()) {
            map[Constants.EditProfile.FIRST_NAME] =
                editProfileDataFlow.value.firstName.trim().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        }
        if (editProfileDataFlow.value.lastName.isNotBlank()) {
            map[Constants.EditProfile.LAST_NAME] =
                editProfileDataFlow.value.lastName.trim().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        }
        if (editProfileDataFlow.value.email.isNotBlank()) {
            map[Constants.EditProfile.EMAIL] =
                editProfileDataFlow.value.email.trim().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        }
        if (editProfileDataFlow.value.email.isNotBlank()) {
            map[Constants.EditProfile.EMAIL] =
                editProfileDataFlow.value.email.trim().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        }

        if (editProfileDataFlow.value.dateSelected.isNotBlank()) {
            // Convert display date to API format (yyyy-MM-dd)
            val apiDate = convertDisplayFormatToApiFormat(editProfileDataFlow.value.dateSelected)
            if (apiDate.isNotBlank()) {
                map[Constants.EditProfile.DATE_OF_BIRTH] =
                    apiDate.toRequestBody("multipart/form-data".toMediaTypeOrNull())
            }
        }

        if (editProfileDataFlow.value.height.isNotBlank()) {
            val heightValue = editProfileDataFlow.value.height.trim()
            map[Constants.EditProfile.HEIGHT] = heightValue.toRequestBody("multipart/form-data".toMediaTypeOrNull())

        } else {
            map[Constants.EditProfile.HEIGHT] = "".toRequestBody("multipart/form-data".toMediaTypeOrNull())

        }

        if (editProfileDataFlow.value.weight.isNotBlank()) {
            val weightValue = editProfileDataFlow.value.weight.trim()
            map[Constants.EditProfile.WEIGHT] = weightValue.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        } else {
            map[Constants.EditProfile.WEIGHT] = "".toRequestBody("multipart/form-data".toMediaTypeOrNull())
        }

        if (editProfileDataFlow.value.allergies.isNotBlank()) {
            map[Constants.EditProfile.KNOWN_ALLERGIES] =
                editProfileDataFlow.value.allergies.trim().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        } else {
            map[Constants.EditProfile.KNOWN_ALLERGIES] =
                "".trim().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        }
        if (editProfileDataFlow.value.medicalConditions.isNotBlank()) {
            map[Constants.EditProfile.CURRENT_MEDICATIONS] =
                editProfileDataFlow.value.medicalConditions.trim().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        } else {
            map[Constants.EditProfile.CURRENT_MEDICATIONS] =
                "".trim().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        }


        val profileImage: MultipartBody.Part? =
            editProfileDataFlow.value.profileImage.let { imagePath ->
                if (imagePath.isNotEmpty()) {
                    if (imagePath.startsWith("http://") || imagePath.startsWith("https://")) {
                        Log.d("TAG", "Profile image is a URL, skipping multipart upload: $imagePath")
                        null
                    } else {
                        Log.d("TAG", "Profile image is a local file, creating multipart body: $imagePath")
                        val profileImageFile = File(imagePath)
                        createMultipartBody(profileImageFile, Constants.EditProfile.PROFILE_IMAGE)
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
                            Log.d("TAG", "callEditProfileApi: ${it.message}")
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

    private fun showOrHideLoader(showLoader: Boolean) {
        editProfileDataFlow.update { state ->
            state.copy(
                showLoader = showLoader
            )
        }
    }

    private fun formatDate(timestamp: Long?): String {
        return if (timestamp != null && timestamp > 0) {
            try {
                val outputFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH)
                // Convert seconds to milliseconds by multiplying by 1000
                val date = Date(timestamp * 1000)
                outputFormat.format(date)
            } catch (e: Exception) {
                ""
            }
        } else {
            ""
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
                // Format to "yyyy-MM-dd" format for API
                val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
                outputFormat.format(date)
            } else {
                dateString // If parsing failed
            }
        } catch (e: Exception) {
            Log.e("TAG", "Error converting display format to API format: $dateString", e)
            dateString // Fallback to original string
        }
    }

    private fun convertDisplayDateToTimestamp(dateString: String): Long {
        return try {
            if (dateString.isBlank()) return 0L

            // Parse the date from "MMMM dd, yyyy" format
            val inputFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH)
            val date = inputFormat.parse(dateString)

            if (date != null) {
                // Convert to timestamp in seconds (not milliseconds)
                date.time / 1000
            } else {
                0L
            }
        } catch (e: Exception) {
            Log.e("TAG", "Error converting display date to timestamp: $dateString", e)
            0L
        }
    }

    private fun isValidDecimal(value: String): Boolean {
        return try {
            if (value.isBlank()) return false
            // Check if it's a valid decimal number (supports both integer and decimal formats)
            val regex = Regex("^\\d+(\\.\\d+)?$")
            if (regex.matches(value)) {
                // Additional check: ensure it can be parsed as a valid decimal
                value.toDoubleOrNull() != null
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e("TAG", "Error validating decimal value: $value", e)
            false
        }
    }

    private fun checkFormChanges() {
        val currentState = editProfileDataFlow.value
        val hasChanges = currentState.firstName != currentState.originalFirstName ||
                currentState.lastName != currentState.originalLastName ||
                currentState.email != currentState.originalEmail ||
                currentState.dateSelected != currentState.originalDateSelected ||
                currentState.profileImage != currentState.originalProfileImage ||
                currentState.height != currentState.originalHeight ||
                currentState.weight != currentState.originalWeight ||
                currentState.allergies != currentState.originalAllergies ||
                currentState.medicalConditions != currentState.originalMedicalConditions

        editProfileDataFlow.update { state ->
            state.copy(isFormChanged = hasChanges)
        }
    }

}

