package com.medrevpatient.mobile.app.ux.main.profile

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.data.source.local.datastore.LocalManager
import com.medrevpatient.mobile.app.data.source.remote.helper.NetworkResult
import com.medrevpatient.mobile.app.data.source.remote.repository.ApiRepository
import com.medrevpatient.mobile.app.domain.request.UpdatePasswordReq
import com.medrevpatient.mobile.app.domain.request.UpdateProfileReq
import com.medrevpatient.mobile.app.domain.request.VerifyOTPForUpdateReq
import com.medrevpatient.mobile.app.domain.response.AuthResponse
import com.medrevpatient.mobile.app.domain.validation.ValidationUseCase
import com.medrevpatient.mobile.app.navigation.NavRoute
import com.medrevpatient.mobile.app.navigation.NavigationAction
import com.medrevpatient.mobile.app.navigation.RouteMaker
import com.medrevpatient.mobile.app.navigation.ViewModelNav
import com.medrevpatient.mobile.app.navigation.ViewModelNavImpl
import com.medrevpatient.mobile.app.utils.AppUtils
import com.medrevpatient.mobile.app.utils.Constants
import com.medrevpatient.mobile.app.ux.startup.StartupActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel
@Inject constructor(
    @ApplicationContext private val context: Context,
    private val validationUseCase: ValidationUseCase,
    private val apiRepository: ApiRepository,
    private val localManager: LocalManager
) : ViewModel(), ViewModelNav by ViewModelNavImpl() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()
    private var job: Job? = null

    init {
        getUserData()
    }

    private fun callCounter(coroutineScope: CoroutineScope) {
        job?.cancel()
        _uiState.update {
            it.copy(counter = 60)
        }
        job = coroutineScope.launch {
            decreaseCounter()
        }
    }

    private suspend fun decreaseCounter() {
        while (uiState.value.counter > 0) {
            delay(1000)
            _uiState.update {
                it.copy(counter = it.counter - 1)
            }
        }
    }

    fun event(event: ProfileUiEvent) {
        when (event) {
            is ProfileUiEvent.ShowAccountSettingsDialog -> {
                _uiState.update {
                    it.copy(showAccountSettingsDialog = event.value)
                }
            }

            is ProfileUiEvent.ShowLogoutDialog -> {
                _uiState.update {
                    it.copy(showLogoutDialog = event.value)
                }
            }

            is ProfileUiEvent.ShowDeleteAccountDialog -> {
                _uiState.update {
                    it.copy(showDeleteAccountDialog = event.value)
                }
            }

            is ProfileUiEvent.PerformLogoutClick -> {
                callLogoutApi()
            }

            is ProfileUiEvent.PerformDeleteAccountClick -> {
                callDeleteAccountApi()
            }

            is ProfileUiEvent.ShowEditProfileDialog -> {
                _uiState.update {
                    it.copy(showEditProfileDialog = event.value)
                }
            }

            is ProfileUiEvent.ShowUpdatePictureDialog -> {
                _uiState.update {
                    it.copy(showUpdatePictureDialog = event.value)
                }
            }

            is ProfileUiEvent.StoreData -> {
                when (event.value.isFrom) {
                    context.getString(R.string.update_picture) -> {
                        _uiState.update {
                            it.copy(profileImage = event.value.profileImage)
                        }
                    }

                    context.getString(R.string.update_name) -> {
                        _uiState.update {
                            it.copy(firstName = event.value.firstName, lastName = event.value.lastName)
                        }
                    }
                }
            }

            is ProfileUiEvent.ShowPermissionDialog -> {
                _uiState.update {
                    it.copy(showPermissionDialog = event.value)
                }
            }

            is ProfileUiEvent.OpenDialogFor -> {
                _uiState.update {
                    it.copy(openDialogFor = event.value)
                }
            }

            is ProfileUiEvent.PerformUpdateClick -> {
                when (event.value.isFrom) {
                    context.getString(R.string.update_email) -> {
                        callUpdateEmailApi(viewModelScope)
                    }

                    context.getString(R.string.update_password) -> {
                        callUpdatePasswordApi()
                    }

                    else -> {
                        callUserUpdateApi(viewModelScope, context, event.value.isFrom)
                    }
                }

            }

            is ProfileUiEvent.EmailValueChange -> {
                _uiState.update { state ->
                    state.copy(
                        email = event.email,
                        emailErrorMsg = validationUseCase.emailValidation(event.email).errorMsg
                    )
                }
            }

            is ProfileUiEvent.FirstNameValueChange -> {
                _uiState.update { state ->
                    state.copy(
                        firstName = event.firstName,
                        firstNameErrorMsg = validationUseCase.emptyFieldValidation(
                            fieldValue = event.firstName,
                            errorMsg = context.getString(R.string.empty_first_name)
                        ).errorMsg
                    )
                }
            }

            is ProfileUiEvent.LastNameValueChange -> {
                _uiState.update { state ->
                    state.copy(
                        lastName = event.lastname,
                        lastNameErrorMsg = validationUseCase.emptyFieldValidation(
                            fieldValue = event.lastname,
                            errorMsg = context.getString(R.string.empty_last_name)
                        ).errorMsg
                    )
                }
            }

            is ProfileUiEvent.PasswordValueChange -> {
                _uiState.update { state ->
                    state.copy(
                        password = event.password,
                        passwordErrorMsg = validationUseCase.emptyFieldValidation(
                            fieldValue = event.password,
                            errorMsg = context.getString(R.string.empty_password)
                        ).errorMsg
                    )
                }
            }

            is ProfileUiEvent.ConfirmPasswordValueChange -> {
                _uiState.update { state ->
                    state.copy(
                        confirmPassword = event.confirmPassword,
                        passwordNotMatch = if (validationUseCase.emptyFieldValidation(
                                fieldValue = event.confirmPassword,
                                errorMsg = context.getString(R.string.empty_confirm_password)
                            ).isSuccess
                        ) {
                            if (state.newPassword != event.confirmPassword) {
                                context.getString(R.string.password_mismatch)
                            } else {
                                null
                            }
                        } else {
                            context.getString(R.string.empty_confirm_password)
                        }
                    )
                }
            }

            is ProfileUiEvent.ShowOtpVerificationDialog -> {
                _uiState.update {
                    it.copy(showOtpVerificationDialog = event.value)
                }
            }

            is ProfileUiEvent.OnOTPValueInsert -> {
                _uiState.update { state ->
                    state.copy(
                        otp = event.value,
                        errorMsg = null
                    )
                }
            }

            is ProfileUiEvent.PerformVerifyOtp -> {
                //call verify otp API
                callVerifyOtpApi(viewModelScope)
            }

            is ProfileUiEvent.PerformResendOTP -> {
                callUpdateEmailApi(viewModelScope)
            }

            is ProfileUiEvent.ShowUpdateSuccessDialog -> {
                _uiState.update {
                    it.copy(showUpdateSuccessDialog = event.value)
                }
            }

            is ProfileUiEvent.NewPasswordValueChange -> {
                _uiState.update { state ->
                    state.copy(
                        newPassword = event.password,
                        newPasswordErrorMsg = validationUseCase.emptyFieldValidation(
                            fieldValue = event.password,
                            errorMsg = context.getString(R.string.empty_password)
                        ).errorMsg
                    )
                }
            }

            is ProfileUiEvent.ShowManagePersonaliseDialog -> {
                _uiState.update {
                    it.copy(showManagePersonaliseDialog = event.value)
                }
            }

            is ProfileUiEvent.ShowUpdatePersonaliseDialog -> {
                _uiState.update {
                    it.copy(showUpdatePersonaliseDialog = event.value)
                }
            }

            is ProfileUiEvent.ManagePersonaliseFor -> {
                _uiState.update {
                    it.copy(managePersonaliseFor = event.value)
                }
            }

            is ProfileUiEvent.AgeValueChange -> {
                _uiState.update {
                    it.copy(age = event.age)
                }
            }

            is ProfileUiEvent.HeightValueChange -> {
                _uiState.update {
                    it.copy(height = event.height)
                }
            }

            is ProfileUiEvent.HeightInchValueChange -> {
                _uiState.update {
                    it.copy(heightInches = event.heightInches)
                }
            }

            is ProfileUiEvent.WeightValueChange -> {
                _uiState.update {
                    it.copy(weight = event.weight)
                }
            }

            is ProfileUiEvent.BodyTypeValueChange -> {
                _uiState.update {
                    it.copy(bodyType = event.bodyType)
                }
            }

            is ProfileUiEvent.EnergyLevelValueChange -> {
                _uiState.update {
                    it.copy(energyLevel = event.energyLevel)
                }
            }

            is ProfileUiEvent.LifeStyleValueChange -> {
                _uiState.update {
                    it.copy(lifeStyle = event.lifeStyle)
                }
            }

            is ProfileUiEvent.FitnessLevelValueChange -> {
                _uiState.update {
                    it.copy(fitnessLevel = event.fitnessLevel)
                }
            }

            is ProfileUiEvent.GoalsValueChange -> {
                _uiState.update {
                    it.copy(goals = event.goals)
                }
            }

            is ProfileUiEvent.IsNewProfileImageAdded -> {
                _uiState.update {
                    it.copy(isNewProfileImageAdded = event.value)
                }
            }

            is ProfileUiEvent.ResetErrorMsgPassword -> {
                _uiState.update {
                    it.copy(
                        passwordErrorMsg = null,
                        newPasswordErrorMsg = null,
                        emptyCPErrorMsf = null,
                        passwordNotMatch = null
                    )
                }
            }
        }
    }

    fun getUserData() {
        viewModelScope.launch {
            val response = localManager.retrieveUserData()
            if (response != null) {
                _uiState.update { it.copy(userData = response) }
                _uiState.update { it.copy(firstName = response.firstName ?: "") }
                _uiState.update { it.copy(lastName = response.lastName ?: "") }
                _uiState.update { it.copy(email = response.email ?: "") }
                _uiState.update { it.copy(age = AppUtils.formatTimeStampForBirthDate(response.birthDate ?: 0)) }
                _uiState.update { it.copy(height = response.heightInFeet ?: -1) }
                _uiState.update { it.copy(heightInches = response.heightInInch ?: -1) }
                _uiState.update { it.copy(weight = response.weight ?: -1) }
                _uiState.update { it.copy(bodyType = response.bodyType ?: -1) }
                _uiState.update { it.copy(energyLevel = response.energyLevel ?: -1) }
                _uiState.update { it.copy(lifeStyle = response.lifeStyle ?: -1) }
                _uiState.update { it.copy(fitnessLevel = response.fitnessLevel ?: -1) }
                _uiState.update { it.copy(goals = response.goals ?: -1) }
            }
        }
    }

    private fun callLogoutApi() {
        viewModelScope.launch {
            apiRepository.logout().collect {
                when (it) {
                    is NetworkResult.Error -> {
                        showOrHideLoader(false)
                        AppUtils.Toast(context, it.message ?: "Something went wrong!").show()
                    }

                    is NetworkResult.Loading -> {
                        showOrHideLoader(true)
                    }

                    is NetworkResult.Success -> {
                        _uiState.update { it1 ->
                            it1.copy(showLogoutDialog = false)
                        }
                        showOrHideLoader(false)
                        AppUtils.Toast(context, it.data?.message ?: "Success").show()
                        localManager.clearStorage()
                        val intent = Intent(context, StartupActivity::class.java)
                        val screen = RouteMaker.SignInRoute.routeDefinition.value
                        intent.putExtra(Constants.IntentKeys.NEED_TO_OPEN, screen)
                        navigate(NavigationAction.NavigateIntent(intent = intent, finishCurrentActivity = true))
                    }
                }
            }
        }
    }

    private fun callDeleteAccountApi() {
        viewModelScope.launch {
            apiRepository.deleteAccount().collect {
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
                        _uiState.update { it1 ->
                            it1.copy(showDeleteAccountDialog = false)
                        }
                        AppUtils.Toast(context, it.data?.message ?: "Success").show()
                        localManager.clearStorage()
                        val intent = Intent(context, StartupActivity::class.java)
                        val screen = RouteMaker.SignupRoute.routeDefinition.value
                        intent.putExtra(Constants.IntentKeys.NEED_TO_OPEN, screen)
                        navigate(NavigationAction.NavigateIntent(intent = intent, finishCurrentActivity = true))
                    }
                }
            }
        }
    }

    private fun callUserUpdateApi(coroutineScope: CoroutineScope, context: Context, isFrom: String) {
        coroutineScope.launch {
            //Sign up API call
            val req = HashMap<String, RequestBody>()
            var doc: okhttp3.MultipartBody.Part? = null
            when (isFrom) {
                context.getString(R.string.update_picture) -> {
                    doc = if (uiState.value.profileImage != null) {
                        val fileExtension = uiState.value.profileImage?.let { AppUtils.getFileExtensionFromUri(context, it) }
                        val file = uiState.value.profileImage?.let { AppUtils.getFileFromUri(context, it) }
                        fileExtension?.let { AppUtils.createMultipartBodyForFile(file, Constants.RequestParams.PROFILE_IMAGE, imeType = it) }
                    } else {
                        null
                    }
                }

                context.getString(R.string.update_name) -> {
                    req[Constants.RequestParams.FIRST_NAME] = uiState.value.firstName.toRequestBody("multipart/form-data".toMediaTypeOrNull())
                    req[Constants.RequestParams.LAST_NAME] = uiState.value.lastName.toRequestBody("multipart/form-data".toMediaTypeOrNull())
                }

                context.getString(R.string.update_email) -> {
                    req[Constants.RequestParams.EMAIL] = uiState.value.email.toRequestBody("multipart/form-data".toMediaTypeOrNull())
                }

                context.getString(R.string.age) -> {
                    req[Constants.RequestParams.BIRTH_DATE] = uiState.value.age.toRequestBody("multipart/form-data".toMediaTypeOrNull())
                }

                context.getString(R.string.height) -> {
                    if (uiState.value.height != -1 && uiState.value.height != 1) req[Constants.RequestParams.HEIGHT_IN_FEET] = uiState.value.height.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
                    if (uiState.value.heightInches != -1 && uiState.value.heightInches != 0) req[Constants.RequestParams.HEIGHT_IN_INCH] = uiState.value.heightInches.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
                }

                context.getString(R.string.weight) -> {
                    if (uiState.value.weight != -1 && uiState.value.weight != 0) req[Constants.RequestParams.WEIGHT] = uiState.value.weight.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
                }

                context.getString(R.string.energy_level) -> {
                    if (uiState.value.energyLevel != -1) req[Constants.RequestParams.ENERGY_LEVEL] = uiState.value.energyLevel.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
                }

                context.getString(R.string.fitness_level) -> {
                    if (uiState.value.fitnessLevel != -1) req[Constants.RequestParams.FITNESS_LEVEL] = uiState.value.fitnessLevel.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
                }

                context.getString(R.string.goals) -> {
                    if (uiState.value.goals != -1) req[Constants.RequestParams.GOALS] = uiState.value.goals.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
                }
            }

            apiRepository.updateUserDetails(req, doc).collect {
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
                        //AppUtils.Toast(context, it.data?.message ?: "Success").show()
                        storeUserData(coroutineScope, it.data?.data, isFrom)
                        _uiState.update { it1 ->
                            it1.copy(
                                showUpdatePictureDialog = false,
                                showUpdateSuccessDialog = isFrom != context.getString(R.string.update_picture)
                            )
                        }
                    }
                }
            }
        }
    }

    private fun callUpdateEmailApi(coroutineScope: CoroutineScope) {
        viewModelScope.launch {
            val req = HashMap<String, String>()
            req["email"] = uiState.value.email
            apiRepository.updateEmail(req).collect {
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
                        if (!uiState.value.showOtpVerificationDialog) {
                            _uiState.update { it1 ->
                                it1.copy(showOtpVerificationDialog = true, showUpdatePictureDialog = false)
                            }
                        }
                        callCounter(coroutineScope)
                    }
                }
            }
        }
    }

    private fun callVerifyOtpApi(coroutineScope: CoroutineScope) {
        val otpState = uiState.value
        validationUseCase.apply {
            val otpHasError =
                emptyFieldValidation(otpState.otp, context.getString(R.string.empty_otp))
            if (!otpHasError.isSuccess) {
                _uiState.update { state ->
                    state.copy(errorMsg = otpHasError.errorMsg)
                }
                return
            }
        }
        viewModelScope.launch {
            val verifyOTPReq = VerifyOTPForUpdateReq(
                email = uiState.value.email,
                otp = otpState.otp.toInt(),
            )
            apiRepository.verifyOTPForUpdateEmail(verifyOTPReq).collect {
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
                        AppUtils.Toast(context, it.data?.message ?: "Success").show()
                        storeUserData(
                            coroutineScope = coroutineScope,
                            userAuthResponse = it.data?.data,
                            isFrom = ""
                        )
                        _uiState.update { it1 ->
                            it1.copy(showOtpVerificationDialog = false)
                        }
                        _uiState.update { it1 ->
                            it1.copy(showUpdateSuccessDialog = true)
                        }
                    }
                }
            }
        }
    }

    private fun callUpdatePasswordApi() {
        viewModelScope.launch {
            val req = UpdatePasswordReq(
                oldPassword = uiState.value.password,
                newPassword = uiState.value.newPassword,
                confirmPassword = uiState.value.confirmPassword
            )
            apiRepository.updatePassword(req).collect {
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
                        AppUtils.Toast(context, it.data?.message ?: "Success").show()
                        _uiState.update { it1 ->
                            it1.copy(showUpdateSuccessDialog = true, showUpdatePictureDialog = false)
                        }
                    }
                }
            }
        }
    }

    private fun storeUserData(coroutineScope: CoroutineScope, userAuthResponse: AuthResponse?, isFrom: String) {
        coroutineScope.launch {
            userAuthResponse?.let { data ->
                val tokenData = data.auth
                localManager.saveUserData(data)
                if (tokenData != null) {
                    localManager.saveUserTokenData(tokenData)
                }
            }
            if (isFrom == context.getString(R.string.update_picture)) getUserData()
        }
    }

    private fun showOrHideLoader(isLoading: Boolean) {
        _uiState.update { state ->
            state.copy(
                isLoading = isLoading
            )
        }
    }

    fun navigate(navRoute: NavRoute) {
        navigate(NavigationAction.Navigate(navRoute))
    }
}

data class ProfileUiState(
    val event: (ProfileUiEvent) -> Unit = {},
    val userData: AuthResponse = AuthResponse(),
    val showAccountSettingsDialog: Boolean = false,
    val showLogoutDialog: Boolean = false,
    val showDeleteAccountDialog: Boolean = false,
    val showEditProfileDialog: Boolean = false,
    val showUpdatePictureDialog: Boolean = false,
    val showPermissionDialog: Boolean = false,
    val isLoading: Boolean = false,
    val profileImage: Uri? = null,
    val openDialogFor: String = "",
    val email: String = "",
    val emailErrorMsg: String? = null,
    val firstName: String = "",
    val firstNameErrorMsg: String? = null,
    val lastName: String = "",
    val lastNameErrorMsg: String? = null,
    val password: String = "",
    val passwordErrorMsg: String? = null,
    val newPassword: String = "",
    val newPasswordErrorMsg: String? = null,
    val confirmPassword: String = "",
    val emptyCPErrorMsf: String? = null,
    val passwordNotMatch: String? = null,
    val showOtpVerificationDialog: Boolean = false,
    val showUpdateSuccessDialog: Boolean = false,
    val otp: String = "",
    val errorMsg: String? = null,
    val isSuccess: Boolean = false,
    val isOTPResend: Boolean = false,
    val counter: Int = 30,
    val showManagePersonaliseDialog: Boolean = false,
    val showUpdatePersonaliseDialog: Boolean = false,
    val managePersonaliseFor: String = "",
    val age: String = "",
    val height: Int = -1,
    val heightInches: Int = -1,
    val weight: Int = -1,
    val bodyType: Int = -1,
    val energyLevel: Int = -1,
    val lifeStyle: Int = -1,
    val fitnessLevel: Int = -1,
    val goals: Int = -1,
    val isNewProfileImageAdded: Boolean = false
)

sealed interface ProfileUiEvent {
    data class ShowAccountSettingsDialog(val value: Boolean) : ProfileUiEvent
    data class ShowLogoutDialog(val value: Boolean) : ProfileUiEvent
    data class ShowDeleteAccountDialog(val value: Boolean) : ProfileUiEvent
    data class ShowEditProfileDialog(val value: Boolean) : ProfileUiEvent
    data object PerformLogoutClick : ProfileUiEvent
    data object PerformDeleteAccountClick : ProfileUiEvent
    data class ShowUpdatePictureDialog(val value: Boolean) : ProfileUiEvent
    data class ShowPermissionDialog(val value: Boolean) : ProfileUiEvent
    data class StoreData(val value: UpdateProfileReq) : ProfileUiEvent
    data class OpenDialogFor(val value: String) : ProfileUiEvent
    data class PerformUpdateClick(val value: UpdateProfileReq) : ProfileUiEvent
    data class EmailValueChange(val email: String) : ProfileUiEvent
    data class FirstNameValueChange(val firstName: String) : ProfileUiEvent
    data class LastNameValueChange(val lastname: String) : ProfileUiEvent
    data class PasswordValueChange(val password: String) : ProfileUiEvent
    data class NewPasswordValueChange(val password: String) : ProfileUiEvent
    data class ConfirmPasswordValueChange(val confirmPassword: String) : ProfileUiEvent
    data class ShowOtpVerificationDialog(val value: Boolean) : ProfileUiEvent
    data class OnOTPValueInsert(val value: String) : ProfileUiEvent
    data class ShowUpdateSuccessDialog(val value: Boolean) : ProfileUiEvent
    data class ShowManagePersonaliseDialog(val value: Boolean) : ProfileUiEvent
    data class ShowUpdatePersonaliseDialog(val value: Boolean) : ProfileUiEvent
    data class ManagePersonaliseFor(val value: String) : ProfileUiEvent
    data object PerformVerifyOtp : ProfileUiEvent
    data object PerformResendOTP : ProfileUiEvent
    data class AgeValueChange(val age: String) : ProfileUiEvent
    data class HeightValueChange(val height: Int) : ProfileUiEvent
    data class HeightInchValueChange(val heightInches: Int) : ProfileUiEvent
    data class WeightValueChange(val weight: Int) : ProfileUiEvent
    data class BodyTypeValueChange(val bodyType: Int) : ProfileUiEvent
    data class EnergyLevelValueChange(val energyLevel: Int) : ProfileUiEvent
    data class LifeStyleValueChange(val lifeStyle: Int) : ProfileUiEvent
    data class FitnessLevelValueChange(val fitnessLevel: Int) : ProfileUiEvent
    data class GoalsValueChange(val goals: Int) : ProfileUiEvent
    data class IsNewProfileImageAdded(val value: Boolean) : ProfileUiEvent
    data object ResetErrorMsgPassword : ProfileUiEvent
}