package com.medrevpatient.mobile.app.ux.startup.onboarding.onBoardDataFlow

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.data.source.local.datastore.LocalManager
import com.medrevpatient.mobile.app.data.source.remote.helper.NetworkResult
import com.medrevpatient.mobile.app.data.source.remote.repository.ApiRepository
import com.medrevpatient.mobile.app.domain.response.AuthResponse
import com.medrevpatient.mobile.app.navigation.NavigationAction
import com.medrevpatient.mobile.app.navigation.RouteMaker
import com.medrevpatient.mobile.app.utils.AppUtils
import com.medrevpatient.mobile.app.utils.Constants
import com.medrevpatient.mobile.app.utils.connection.NetworkMonitor
import com.medrevpatient.mobile.app.ux.main.MainActivity
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import timber.log.Timber
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class GetOnBoardDataUiStateUseCase
@Inject constructor(
    private val localManager: LocalManager,
    private val networkMonitor: NetworkMonitor,
    private val apiRepository: ApiRepository,
) {

    private val currentStepCounter = MutableStateFlow(0)
    private val _state = MutableStateFlow(OnBoardDataUiState())
    private val ageValue = MutableStateFlow("")
    private val heightValue = MutableStateFlow(-1)
    private val heightInchesValue = MutableStateFlow(-1)
    private val weightValue = MutableStateFlow(-1)
    private val bodyTypeValue = MutableStateFlow(-1)
    private val energyLevelValue = MutableStateFlow(-1)
    private val lifeStyleValue = MutableStateFlow(-1)
    private val fitnessLevelValue = MutableStateFlow(-1)
    private val goalsValue = MutableStateFlow(-1)
    private val isOffline = MutableStateFlow(false)
    private val isLoading = MutableStateFlow(false)
    private val profileImage = MutableStateFlow(Uri.EMPTY)
    private val showPermissionDialog = MutableStateFlow(false)
    //private val isSkipClicked = MutableStateFlow(false)

    operator fun invoke(
        context: Context,
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
    ): OnBoardDataUiState {
        coroutineScope.launch {
            networkMonitor.isOnline.map(Boolean::not).stateIn(
                scope = coroutineScope,
                started = WhileSubscribed(5_000),
                initialValue = false,
            ).collect {
                isOffline.value = it
            }
        }

        return OnBoardDataUiState(
            onNextClick = {
                navigateToNext(navigate, context, coroutineScope)
            },
            onPrevClick = {
                navigateToPrev(navigate)
            },
            onSkipClick = {
                //isSkipClicked.value = true
                if (currentStepCounter.value == 6) {
                    //val intent = Intent(context, MainActivity::class.java)
                    //navigate(NavigationAction.NavigateIntent(intent = intent, finishCurrentActivity = true))
                    goalsValue.value = -1
                    if (!isOffline.value) {
                        callOnBoardAPI(navigate, coroutineScope, context)
                    } else {
                        AppUtils.Toast(context, context.getString(R.string.internet_connection)).show()
                    }
                } else {
                    currentStepCounter.value++
                }
            },
            currentStepCounter = currentStepCounter,
            event = { onBoardUiEvent ->
                onBoardEvent(event = onBoardUiEvent)
            },
            isLoading = isLoading,
            age = ageValue,
            height = heightValue,
            heightInches = heightInchesValue,
            weight = weightValue,
            bodyType = bodyTypeValue,
            energyLevel = energyLevelValue,
            lifeStyle = lifeStyleValue,
            fitnessLevel = fitnessLevelValue,
            goals = goalsValue,
            profileImage = profileImage,
            showPermissionDialog = showPermissionDialog,
            onShowPermissionDialog = {
                showPermissionDialog.value = it
            },
        )
    }

    private fun onBoardEvent(event: OnBoardDataUiEvent) {
        when (event) {
            is OnBoardDataUiEvent.AgeValueChange -> {
                ageValue.value = event.age
                _state.update {
                    it.copy(age = ageValue)
                }
            }

            is OnBoardDataUiEvent.HeightValueChange -> {
                heightValue.value = event.height
                _state.update {
                    it.copy(height = heightValue)
                }
            }

            is OnBoardDataUiEvent.HeightInchValueChange -> {
                heightInchesValue.value = event.heightInches
                _state.update {
                    it.copy(heightInches = heightInchesValue)
                }
            }

            is OnBoardDataUiEvent.WeightValueChange -> {
                weightValue.value = event.weight
                _state.update {
                    it.copy(weight = weightValue)
                }
            }

            is OnBoardDataUiEvent.BodyTypeValueChange -> {
                bodyTypeValue.value = event.bodyType
                _state.update {
                    it.copy(bodyType = bodyTypeValue)
                }

            }

            is OnBoardDataUiEvent.EnergyLevelValueChange -> {
                energyLevelValue.value = event.energyLevel
                _state.update {
                    it.copy(energyLevel = energyLevelValue)
                }
            }

            is OnBoardDataUiEvent.LifeStyleValueChange -> {
                lifeStyleValue.value = event.lifeStyle
                _state.update {
                    it.copy(lifeStyle = lifeStyleValue)
                }
            }

            is OnBoardDataUiEvent.FitnessLevelValueChange -> {
                fitnessLevelValue.value = event.fitnessLevel
                _state.update {
                    it.copy(fitnessLevel = fitnessLevelValue)
                }
            }

            is OnBoardDataUiEvent.GoalsValueChange -> {
                goalsValue.value = event.goals
                _state.update {
                    it.copy(goals = goalsValue)
                }
            }

            is OnBoardDataUiEvent.ProfileImage -> {
                profileImage.value = event.file
                _state.update {
                    it.copy(profileImage = profileImage)
                }
            }
        }
    }

    private fun navigateToNext(navigate: (NavigationAction) -> Unit, context: Context, coroutineScope: CoroutineScope) {
        if (currentStepCounter.value == 6) {
            if (goalsValue.value != -1) {
                if (!isOffline.value) {
                    callOnBoardAPI(navigate, coroutineScope, context)
                } else {
                    AppUtils.Toast(context, context.getString(R.string.internet_connection)).show()
                }

            } else {
                Toasty.warning(context, context.getString(R.string.empty_goals), Toast.LENGTH_SHORT, false).show()
            }
        } else {
            when (currentStepCounter.value) {
                0 -> {//upload a picture
                    //isSkipClicked.value = false
                    if (profileImage.value != Uri.EMPTY) {
                        currentStepCounter.value++
                        Timber.e("Picture uploaded ${profileImage.value}")
                    } else {
                        Toasty.warning(context, context.getString(R.string.empty_picture), Toast.LENGTH_SHORT, false).show()
                    }
                }

                1 -> { //age
                    if (ageValue.value.isNotEmpty()) {
                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                        val inputDate = LocalDate.parse(ageValue.value, formatter)
                        val isFutureDate = inputDate.isAfter(LocalDate.now())
                        if (isFutureDate.not()) {
                            currentStepCounter.value++
                        } else {
                            Toasty.warning(context, context.getString(R.string.age_should_not_be_future), Toast.LENGTH_SHORT, false).show()
                        }
                    } else {
                        Toasty.warning(context, context.getString(R.string.empty_age), Toast.LENGTH_SHORT, false).show()
                    }
                }

                2 -> { // Save height
                    if (heightValue.value != -1) {
                        currentStepCounter.value++
                    } else {
                        Toasty.warning(context, context.getString(R.string.empty_height), Toast.LENGTH_SHORT, false).show()
                    }
                }

                3 -> { // Save weight
                    if (weightValue.value != -1) {
                        currentStepCounter.value++
                    } else {
                        Toasty.warning(context, context.getString(R.string.empty_weight), Toast.LENGTH_SHORT, false).show()
                    }
                }

                /*4 -> { // Save body type
                    if (bodyTypeValue.value != 0) {
                        currentStepCounter.value++
                    } else {
                        Toasty.warning(context, context.getString(R.string.empty_body_type), Toast.LENGTH_SHORT, false).show()
                    }
                }*/

                4 -> { // Save energy level
                    if (energyLevelValue.value != -1) {
                        currentStepCounter.value++
                    } else {
                        Toasty.warning(context, context.getString(R.string.empty_energy_level), Toast.LENGTH_SHORT, false).show()
                    }
                }

                /*6 -> { // Save life style
                    if (lifeStyleValue.value != 0) {
                        currentStepCounter.value++
                    } else {
                        Toasty.warning(context, context.getString(R.string.empty_lifestyle), Toast.LENGTH_SHORT, false).show()
                    }
                }*/

                5 -> { // Save fitness level
                    if (fitnessLevelValue.value != -1) {
                        currentStepCounter.value++
                    } else {
                        Toasty.warning(context, context.getString(R.string.empty_fitness_level), Toast.LENGTH_SHORT, false).show()
                    }
                }
            }
        }
    }

    private fun navigateToPrev(nav: (NavigationAction) -> Unit) {
        if (currentStepCounter.value == 0) {
            nav(NavigationAction.Navigate(RouteMaker.OnboardingRoute.createRoute()))
        } /*else if (currentStepCounter.value == 1 && isSkipClicked.value) {
            nav(NavigationAction.Navigate(RouteMaker.OnboardingRoute.createRoute()))
        }*/ else {
            currentStepCounter.value--
        }
    }

    private fun callOnBoardAPI(navigate: (NavigationAction) -> Unit, coroutineScope: CoroutineScope, context: Context) {
        coroutineScope.launch {
            //Sign up API call
            val req = HashMap<String, RequestBody>()
            if (ageValue.value != "") req[Constants.RequestParams.BIRTH_DATE] = ageValue.value.toRequestBody("multipart/form-data".toMediaTypeOrNull())
            if (heightValue.value != -1 && heightValue.value != 1) req[Constants.RequestParams.HEIGHT_IN_FEET] = heightValue.value.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
            if (heightInchesValue.value != -1 && heightInchesValue.value != 0) req[Constants.RequestParams.HEIGHT_IN_INCH] = heightInchesValue.value.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
            if (weightValue.value != -1 || weightValue.value != 0) req[Constants.RequestParams.WEIGHT] = weightValue.value.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
            //req["bodyType"] = bodyTypeValue.value.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
            if (energyLevelValue.value != -1) req[Constants.RequestParams.ENERGY_LEVEL] = energyLevelValue.value.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
            //req["lifestyle"] = lifeStyleValue.value.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
            if (fitnessLevelValue.value != -1) req[Constants.RequestParams.FITNESS_LEVEL] = fitnessLevelValue.value.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
            if (goalsValue.value != -1) req[Constants.RequestParams.GOALS] = goalsValue.value.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())

            val doc = if (profileImage.value != null) {
                val fileExtension = AppUtils.getFileExtensionFromUri(context, profileImage.value)
                val file = AppUtils.getFileFromUri(context, profileImage.value)
                fileExtension?.let { AppUtils.createMultipartBodyForFile(file, Constants.RequestParams.PROFILE_IMAGE, imeType = it) }
            } else {
                null
            }
            apiRepository.takeUserDetails(req, doc).collect {
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
                        storeUserData(coroutineScope, it.data?.data)
                        val intent = Intent(context, MainActivity::class.java)
                        navigate(NavigationAction.NavigateIntent(intent = intent, finishCurrentActivity = true))
                    }
                }
            }
        }
    }

    private fun storeUserData(coroutineScope: CoroutineScope, userAuthResponse: AuthResponse?) {
        coroutineScope.launch {
            userAuthResponse?.let { data ->
                val tokenData = data.auth
                localManager.saveUserData(data)
                if (tokenData != null) {
                    localManager.saveUserTokenData(tokenData)
                }
            }
        }
    }

    private fun showOrHideLoader(isLoading: Boolean) {
        this.isLoading.value = isLoading
        _state.update {
            it.copy(isLoading = this.isLoading)
        }
    }
}