package com.medrevpatient.mobile.app.ux.container.editProfile

import android.content.Context
import android.util.Log
import co.touchlab.kermit.Logger
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.data.source.Constants
import com.medrevpatient.mobile.app.data.source.local.datastore.AppPreferenceDataStore
import com.medrevpatient.mobile.app.data.source.remote.helper.NetworkResult
import com.medrevpatient.mobile.app.data.source.remote.repository.ApiRepository
import com.medrevpatient.mobile.app.domain.validation.ValidationUseCase
import com.medrevpatient.mobile.app.model.domain.response.auth.UserAuthResponse
import com.medrevpatient.mobile.app.navigation.NavigationAction
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
        coroutineScope.launch{
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
                        profileImage = it.profileImage ?: "",
                        firstName = it.firstName ?: "",
                        dateSelected = formatDate(it.dateOfBirth),
                        lastName = it.lastName ?: "",
                        height = it.height?:"",
                        weight = it.weight?:"",
                        bmi = it.bmi?:"",
                        allergies = it.knownAllergies?:"",
                        medicalConditions = it.medicalConditions?:"",
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

                    )
                }
            }
            is EditProfileUiEvent.FirstNameValueChange -> {
                editProfileDataFlow.update { state ->
                    state.copy(
                        firstName = event.firstName,
                        firstNameErrorMsg = validationUseCase.emptyFieldValidation(
                            event.firstName,
                            "Please enter your first name"
                        ).errorMsg
                    )
                }
            }
            is EditProfileUiEvent.LastNameValueChange -> {
                editProfileDataFlow.update { state ->
                    state.copy(
                        lastName = event.lastName,
                        lastNameErrorMsg = validationUseCase.emptyFieldValidation(
                            event.lastName,
                            "Please enter your last name"
                        ).errorMsg
                    )
                }
            }

            is EditProfileUiEvent.OnClickOfDate -> {
                Log.d("TAG", "Date picker selected: ${event.date}")
                val formattedDate = convertDateToDisplayFormat(event.date)
                editProfileDataFlow.update { state ->
                    state.copy(
                        dateSelected = formattedDate,
                    )
                }

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
                        val hasError = listOf(
                            firstNameValidationResult,
                            lastNameValidationResult,
                            ).any { !it.isSuccess }
                        // ���� **Update all error messages in one go**
                        editProfileDataFlow.update { state ->
                            state.copy(
                                firstNameErrorMsg = firstNameValidationResult.errorMsg,
                                lastNameErrorMsg = lastNameValidationResult.errorMsg
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

            // Medical Information events
            is EditProfileUiEvent.HeightValueChange -> {
                editProfileDataFlow.update { state ->
                    state.copy(
                        height = event.height,
                    )
                }
            }

            is EditProfileUiEvent.WeightValueChange -> {
                editProfileDataFlow.update { state ->
                    state.copy(
                        weight = event.weight,
                    )
                }
            }

            is EditProfileUiEvent.AllergiesValueChange -> {
                editProfileDataFlow.update { state ->
                    state.copy(
                        allergies = event.allergies,
                    )
                }
            }

            is EditProfileUiEvent.MedicalConditionsValueChange -> {
                editProfileDataFlow.update { state ->
                    state.copy(
                        medicalConditions = event.medicalConditions,
                    )
                }
            }






            is EditProfileUiEvent.BmiValueChange -> {
                editProfileDataFlow.update { state ->
                    state.copy(
                        bmi = event.bmi
                    )
                }
            }
        }
    }





    private fun callEditProfileApi(
        coroutineScope: CoroutineScope,
        navigation: (NavigationAction) -> Unit
    ) {
        val map: HashMap<String, RequestBody> = hashMapOf()

        if (editProfileDataFlow.value.firstName.isNotBlank()) map[Constants.EditProfile.FIRST_NAME] =
            editProfileDataFlow.value.firstName.toRequestBody("multipart/form-data".toMediaTypeOrNull())

        if (editProfileDataFlow.value.lastName.isNotBlank()) map[Constants.EditProfile.LAST_NAME] =
            editProfileDataFlow.value.lastName.toRequestBody("multipart/form-data".toMediaTypeOrNull())

        if (editProfileDataFlow.value.email.isNotBlank()) map[Constants.EditProfile.EMAIL] =
            editProfileDataFlow.value.email.toRequestBody("multipart/form-data".toMediaTypeOrNull())

        if (editProfileDataFlow.value.dateSelected.isNotBlank()) {
            // Convert display date to API format (yyyy-MM-dd)
            val apiDate = convertDisplayFormatToApiFormat(editProfileDataFlow.value.dateSelected)
            Log.d("TAG", "API date format: $apiDate")

            map[Constants.EditProfile.DATE_OF_BIRTH] =
                apiDate.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        }
        if (editProfileDataFlow.value.height.isNotBlank()) {
            val heightValue = editProfileDataFlow.value.height.trim()
            // Validate that height is a valid decimal number
            if (isValidDecimal(heightValue)) {
                map[Constants.EditProfile.HEIGHT] = heightValue.toRequestBody("multipart/form-data".toMediaTypeOrNull())
            } else {
                Log.e("TAG", "Invalid height value: $heightValue")
            }
        }

        if (editProfileDataFlow.value.weight.isNotBlank()) {
            val weightValue = editProfileDataFlow.value.weight.trim()
            // Validate that weight is a valid decimal number
            if (isValidDecimal(weightValue)) {
                map[Constants.EditProfile.WEIGHT] = weightValue.toRequestBody("multipart/form-data".toMediaTypeOrNull())
            } else {
                Log.e("TAG", "Invalid weight value: $weightValue")
            }
        }


        if (editProfileDataFlow.value.allergies.isNotBlank()) map[Constants.EditProfile.KNOWN_ALLERGIES] =
            editProfileDataFlow.value.allergies.toRequestBody("multipart/form-data".toMediaTypeOrNull())

        if (editProfileDataFlow.value.medicalConditions.isNotBlank()) map[Constants.EditProfile.CURRENT_MEDICATIONS] =
            editProfileDataFlow.value.medicalConditions.toRequestBody("multipart/form-data".toMediaTypeOrNull())

        // Debug logging for all values being sent to API
        Log.d("TAG", "API Request Values:")
        map.forEach { (key, value) ->
            Log.d("TAG", "$key: ${value.toString()}")
        }


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

}

