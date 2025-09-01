package com.medrevpatient.mobile.app.ux.startup.auth.register

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.data.source.Constants
import com.medrevpatient.mobile.app.data.source.local.datastore.AppPreferenceDataStore
import com.medrevpatient.mobile.app.data.source.remote.helper.NetworkResult
import com.medrevpatient.mobile.app.data.source.remote.repository.ApiRepository
import com.medrevpatient.mobile.app.domain.validation.ValidationResult
import com.medrevpatient.mobile.app.domain.validation.ValidationUseCase
import com.medrevpatient.mobile.app.model.domain.request.authReq.SignUpReq
import com.medrevpatient.mobile.app.model.domain.response.auth.Auth
import com.medrevpatient.mobile.app.model.domain.response.auth.UserAuthResponse
import com.medrevpatient.mobile.app.navigation.NavigationAction
import com.medrevpatient.mobile.app.navigation.NavigationAction.*

import com.medrevpatient.mobile.app.utils.AppUtils
import com.medrevpatient.mobile.app.utils.AppUtils.showErrorMessage
import com.medrevpatient.mobile.app.utils.AppUtils.showSuccessMessage
import com.medrevpatient.mobile.app.utils.AppUtils.showWaringMessage
import com.medrevpatient.mobile.app.utils.connection.NetworkMonitor
import com.medrevpatient.mobile.app.ux.container.ContainerActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
class GetRegisterUiStateUseCase
@Inject constructor(
    private val validationUseCase: ValidationUseCase,
    private val apiRepository: ApiRepository,
    private val appPreferenceDataStore: AppPreferenceDataStore,
    private val networkMonitor: NetworkMonitor
) {
    private val isOffline = MutableStateFlow(false)
    private val registerUiDataState = MutableStateFlow(RegisterUiDataState())
    private lateinit var context: Context
    operator fun invoke(
        context: Context,
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
    ): RegisterUiState {
        coroutineScope.launch {
            networkMonitor.isOnline.map(Boolean::not).stateIn(
                scope = coroutineScope,
                started = WhileSubscribed(5_000),
                initialValue = false,
            ).collect {
                isOffline.value = it
            }
        }

        return RegisterUiState(
            registerUiDataState = registerUiDataState,

            event = {
                authEvent(
                    event = it,
                    context = context,
                    navigate = navigate,
                    coroutineScope = coroutineScope
                )
            }
        )
    }
    private fun authEvent(
        event: RegisterUiEvent,
        context: Context,
        navigate: (NavigationAction) -> Unit,
        coroutineScope: CoroutineScope
    ) {
        when (event) {
            is RegisterUiEvent.NameValueChange -> {
                registerUiDataState.update { state ->
                    state.copy(
                        name = event.name,
                        nameErrorMsg = validationUseCase.emptyFieldValidation(
                            event.name,
                            context.getString(R.string.please_enter_your_full_name)
                        ).errorMsg
                    )
                }
            }
            is RegisterUiEvent.EmailValueChange -> {
                registerUiDataState.update { state ->
                    state.copy(
                        email = event.email,
                        emailErrorMsg = validationUseCase.emailValidation(
                            emailAddress = event.email,
                            context = context
                        ).errorMsg
                    )
                }
            }

            is RegisterUiEvent.PhoneNumberValueChange -> {
                registerUiDataState.update { state ->
                    state.copy(
                        phoneNumber = event.phoneNumber,
                        phoneNumberErrorMsg = phoneNumberValidation(
                            event.phoneNumber,
                            context
                        ).errorMsg
                    )

                }
            }

            is RegisterUiEvent.PasswordValueChange -> {
                registerUiDataState.update { state ->
                    state.copy(
                        password = event.password,
                        passwordErrorMsg = validationUseCase.passwordValidation(
                            password = event.password,
                            context = context
                        ).errorMsg
                    )

                }
            }
            is RegisterUiEvent.ConfirmPasswordValueChange -> {
                registerUiDataState.update { state ->
                    state.copy(
                        confirmPassword = event.confirmPassword,
                        confirmPasswordErrorMsg = confirmPasswordValidation(
                            registerUiDataState.value.password,
                            event.confirmPassword,
                            context
                        ).errorMsg
                    )

                }
            }
            is RegisterUiEvent.DoSignIn -> {
                navigate(Pop())
            }
            is RegisterUiEvent.OnClickOfDate -> {
                registerUiDataState.update { state ->
                    state.copy(
                        dateSelected = event.date,
                        dateOfBirthValidationMsg = dateOfBirthValidation(
                            event.date,
                            context
                        ).errorMsg
                    )
                }

            }

            is RegisterUiEvent.RoleDropDownExpanded -> {
                registerUiDataState.update { state ->
                    state.copy(
                        selectGender = event.selectGender,
                        selectGanderErrorMsg = genderValidation(
                            event.selectGender,
                            context = context
                        ).errorMsg
                    )
                }
            }
            is RegisterUiEvent.DoSignUp -> {
                if (!isOffline.value) {
                    validationUseCase.apply {
                        val nameValidationResult = emptyFieldValidation(
                            registerUiDataState.value.name,
                            context.getString(R.string.please_enter_your_full_name)
                        )
                        val emailValidationResult =
                            emailValidation(registerUiDataState.value.email, context = context)
                        val phoneValidationResult = phoneNumberValidation(
                            registerUiDataState.value.phoneNumber,
                            context
                        )
                        val passwordValidationResult =
                            passwordValidation(registerUiDataState.value.password, context)
                        val confirmPasswordResult = confirmPasswordValidation(
                            registerUiDataState.value.password,
                            registerUiDataState.value.confirmPassword,
                            context
                        )
                        val dobValidationResult = dateOfBirthValidation(
                            registerUiDataState.value.dateSelected,
                            context
                        )
                        val genderValidationResult =
                            genderValidation(registerUiDataState.value.selectGender, context)
                        val termsValidationResult = termsAndConditionsValidation(
                            registerUiDataState.value.isTermAndConditionChecked,
                            context
                        )
                        val hasError = listOf(
                            nameValidationResult,
                            emailValidationResult,
                            phoneValidationResult,
                            passwordValidationResult,
                            confirmPasswordResult,
                            dobValidationResult,
                            genderValidationResult,
                            termsValidationResult
                        ).any { !it.isSuccess }
                        // 🔹 **Update all error messages in one go**
                        registerUiDataState.update { state ->
                            state.copy(
                                nameErrorMsg = nameValidationResult.errorMsg,
                                emailErrorMsg = emailValidationResult.errorMsg,
                                phoneNumberErrorMsg = phoneValidationResult.errorMsg,
                                passwordErrorMsg = passwordValidationResult.errorMsg,
                                confirmPasswordErrorMsg = confirmPasswordResult.errorMsg,
                                dateOfBirthValidationMsg = dobValidationResult.errorMsg,
                                selectGanderErrorMsg = genderValidationResult.errorMsg,
                                isTermAndConditionCheckedErrorMsg = termsValidationResult.errorMsg
                            )
                        }
                        if (hasError) return //  Stop if any validation failed
                    }
                    doUserSignUp(context = this.context, coroutineScope = coroutineScope, navigate = navigate)
                    // navigate(NavigationAction.Navigate(VerifyOtpRoute.createRoute(email = registerUiDataState.value.email?.trim()?:"", screenName = Constants.AppScreen.REGISTER_SCREEN)))
                } else {
                    showWaringMessage(
                        this.context,
                        context.getString(R.string.please_check_your_internet_connection_first)
                    )
                }
            }
            is RegisterUiEvent.OnCheckedChange -> {
                registerUiDataState.update { state ->
                    state.copy(
                        isTermAndConditionChecked = event.isChecked,
                        isTermAndConditionCheckedErrorMsg = termsAndConditionsValidation(
                            event.isChecked,
                            context
                        ).errorMsg
                    )
                }
            }
            is RegisterUiEvent.GetContext -> {
                this.context = event.context
            }
            RegisterUiEvent.PrivacyPolicy -> {
                callGetTermsAPI(coroutineScope, "2", navigate, screenName = Constants.AppScreen.PRIVACY_POLICY_SCREEN)
            }
            RegisterUiEvent.TermAndCondition -> {
                callGetTermsAPI(coroutineScope, "3", navigate, screenName = Constants.AppScreen.TERM_AND_CONDITION_SCREEN)
            }
        }
    }
    private fun callGetTermsAPI(coroutineScope: CoroutineScope, type: String, navigate: (NavigationAction) -> Unit, screenName: String) {
        coroutineScope.launch {
            apiRepository.getTermsAndConditions(type).collect { result ->
                when (result) {
                    is NetworkResult.Error -> {
                        showErrorMessage(context, result.message ?: "Something went wrong!")
                        showOrHideLoader(false)
                    }
                    is NetworkResult.Loading -> {
                        showOrHideLoader(true)
                    }
                    is NetworkResult.Success -> {
                        showOrHideLoader(false)
                        navigateToContainerScreens(
                            context = context,
                            navigate = navigate,
                            screenName = screenName,
                            uri = result.data?.data?.url ?: ""
                        )
                    }
                    is NetworkResult.UnAuthenticated -> {
                        showOrHideLoader(false)
                        showErrorMessage(context, result.message ?: "Something went wrong!")
                    }
                }
            }
        }
    }
    private fun navigateToContainerScreens(
        context: Context,
        navigate: (NavigationAction) -> Unit,
        screenName: String,
        uri: String = ""
    ) {
        val bundle = Bundle()
        val intent = Intent(context, ContainerActivity::class.java)
        bundle.putString(Constants.BundleKey.URL, uri)
        intent.putExtra(Constants.IS_COME_FOR, screenName)
        intent.putExtra(Constants.IS_FORM, bundle)
        navigate(
            NavigateIntent(
                intent = intent,
                finishCurrentActivity = false
            )
        )
    }

    private fun doUserSignUp(coroutineScope: CoroutineScope, context: Context, navigate: (NavigationAction) -> Unit) {
        val genderValue = when (registerUiDataState.value.selectGender) {
            context.getString(R.string.male) -> Constants.Gender.MALE.value
            context.getString(R.string.female) -> Constants.Gender.FEMALE.value
            else -> Constants.Gender.NON_BINARY.value
        }
        coroutineScope.launch {
            val signUpReq = SignUpReq(
                name = registerUiDataState.value.name,
                email = registerUiDataState.value.email,
                mobileNumber = registerUiDataState.value.phoneNumber,
                dateOfBirth = AppUtils.convertDateToTimestamp(registerUiDataState.value.dateSelected),
                gender = genderValue,
                password = registerUiDataState.value.password,
                confirmPassword = registerUiDataState.value.confirmPassword
            )
            apiRepository.doSignUp(signUpReq).collect {
                when (it) {
                    is NetworkResult.Error -> {
                        showErrorMessage(context = context,it.message?:"Something went wrong!")
                        showOrHideLoader(false)
                    }
                    is NetworkResult.Loading -> {
                        showOrHideLoader(true)
                    }
                    is NetworkResult.Success -> {
                        showOrHideLoader(false)
                        showSuccessMessage(context = context,it.data?.message?:"")
                        storeResponseToDataStore(coroutineScope = coroutineScope, navigate = navigate, userAuthResponseData = it.data?.data)

                    }
                    is NetworkResult.UnAuthenticated -> {
                        showOrHideLoader(false)
                        showErrorMessage(context = context,it.message?:"Something went wrong!")
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

    private fun genderValidation(gender: String?, context: Context): ValidationResult {
        return ValidationResult(
            isSuccess = !gender.isNullOrBlank(),
            errorMsg = if (gender.isNullOrBlank()) context.getString(R.string.please_select_your_gender) else null
        )
    }

    private fun confirmPasswordValidation(
        password: String,
        confirmPassword: String,
        context: Context
    ): ValidationResult {
        return ValidationResult(
            isSuccess = confirmPassword.isNotBlank() && confirmPassword == password,
            errorMsg = when {
                confirmPassword.isBlank() -> context.getString(R.string.please_confirm_your_password)
                confirmPassword != password -> context.getString(R.string.passwords_do_not_match)
                else -> null
            }
        )
    }

    private fun termsAndConditionsValidation(
        isChecked: Boolean,
        context: Context
    ): ValidationResult {
        return ValidationResult(
            isSuccess = isChecked,
            errorMsg = if (!isChecked) context.getString(R.string.you_must_accept_the_terms_and_conditions_to_continue) else null
        )
    }

    private fun storeResponseToDataStore(
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
        userAuthResponseData: UserAuthResponse?
    ) {

    }
    private fun showOrHideLoader(showLoader: Boolean) {
        registerUiDataState.update { state ->
            state.copy(
                showLoader = showLoader
            )
        }
    }
    private fun dateOfBirthValidation(dob: String, context: Context): ValidationResult {
        return ValidationResult(
            isSuccess = dob.isNotBlank(),
            errorMsg = if (dob.isBlank()) context.getString(R.string.please_enter_your_date_of_birth) else null
        )
    }


}