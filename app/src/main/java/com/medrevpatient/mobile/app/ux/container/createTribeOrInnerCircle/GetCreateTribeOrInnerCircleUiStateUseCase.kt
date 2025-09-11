package com.medrevpatient.mobile.app.ux.container.createTribeOrInnerCircle
import android.content.Context
import android.util.Log
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.data.source.Constants
import com.medrevpatient.mobile.app.data.source.remote.helper.NetworkResult
import com.medrevpatient.mobile.app.data.source.remote.repository.ApiRepository
import com.medrevpatient.mobile.app.domain.validation.ValidationResult
import com.medrevpatient.mobile.app.domain.validation.ValidationUseCase
import com.medrevpatient.mobile.app.model.domain.response.chat.MessageTabResponse
import com.medrevpatient.mobile.app.navigation.NavigationAction
import com.medrevpatient.mobile.app.utils.AppUtils.createMultipartBody
import com.medrevpatient.mobile.app.utils.AppUtils.showErrorMessage
import com.medrevpatient.mobile.app.utils.AppUtils.showSuccessMessage
import com.medrevpatient.mobile.app.utils.AppUtils.showWarningMessage
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
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

class GetCreateTribeOrInnerCircleUiStateUseCase
@Inject constructor(
    private val validationUseCase: ValidationUseCase,
    private val networkMonitor: NetworkMonitor,
    private val apiRepository: ApiRepository,
) {
    private val isOffline = MutableStateFlow(false)
    private lateinit var context: Context
    private val isNewImage = MutableStateFlow(false)
    private val createTribeOrInnerCircleDataFlow =
        MutableStateFlow(CreateTribeOrInnerCircleDataState())

    operator fun invoke(
        context: Context,
        coroutineScope: CoroutineScope,
        screen: String,
        messageData: String,
        navigate: (NavigationAction) -> Unit,
    ): CreateTribeOrInnerCircleUiState {
        coroutineScope.launch {
            networkMonitor.isOnline.map(Boolean::not).stateIn(
                scope = coroutineScope,
                started = WhileSubscribed(5_000),
                initialValue = false,
            ).collect {
                isOffline.value = it
            }
        }
        createTribeOrInnerCircleDataFlow.update {
            try {
                val data: MessageTabResponse? = if (messageData.isNotBlank()) {
                    Gson().fromJson(messageData, MessageTabResponse::class.java)
                } else {
                    null
                }
                it.copy(
                    messageResponse = data
                )
            } catch (e: Exception) {
                Log.e(
                    "GetCreateTribeOrInnerCircleUiStateUseCase",
                    "Error parsing messageData: ${e.message}"
                )
                it.copy(
                    messageResponse = null
                )
            }
        }
        createTribeOrInnerCircleDataFlow.update { state ->
            state.copy(
                screen = screen
            )
        }

        coroutineScope.launch {
            createTribeOrInnerCircleDataFlow.value.messageResponse?.let {
                isNewImage.value = false // This is an existing image from URL
                createTribeOrInnerCircleDataFlow.update { profileUiDataState ->
                    profileUiDataState.copy(
                        circleName = it.groupName ?: "",
                        profileImage = it.groupImage ?: ""
                    )
                }
            }
        }
        return CreateTribeOrInnerCircleUiState(
            createTribeOrInnerCircleDataFlow = createTribeOrInnerCircleDataFlow,
            event = { aboutUsEvent ->
                createTribeOrInnerCircleUiEvent(
                    event = aboutUsEvent,
                    context = context,
                    navigate = navigate,
                    coroutineScope = coroutineScope
                )
            }
        )
    }

    private fun createTribeOrInnerCircleUiEvent(
        event: CreateTribeOrInnerCircleUiEvent,
        context: Context,
        navigate: (NavigationAction) -> Unit,
        coroutineScope: CoroutineScope
    ) {
        when (event) {
            CreateTribeOrInnerCircleUiEvent.BackClick -> {
                if (createTribeOrInnerCircleDataFlow.value.screen == Constants.AppScreen.CREATE_INNER_CIRCLE_OR_TRIBE) {
                    navigate(NavigationAction.Pop())
                } else {
                    navigate(NavigationAction.PopIntent)
                }
            }

            is CreateTribeOrInnerCircleUiEvent.GetContext -> {
                this.context = event.context
            }

            is CreateTribeOrInnerCircleUiEvent.OnAddMemberClick -> {

            }

            is CreateTribeOrInnerCircleUiEvent.CircleNameValueChange -> {
                createTribeOrInnerCircleDataFlow.update { state ->
                    state.copy(
                        circleName = event.circleName,
                        circleErrorMsg = validationUseCase.emptyFieldValidation(
                            event.circleName,
                            context.getString(R.string.please_enter_your_circle_name)
                        ).errorMsg
                    )
                }
            }

            is CreateTribeOrInnerCircleUiEvent.GroupTypeDropDownExpanded -> {
                createTribeOrInnerCircleDataFlow.update { state ->
                    val errorMsg =
                        if (createTribeOrInnerCircleDataFlow.value.screen == Constants.AppScreen.CREATE_INNER_CIRCLE_OR_TRIBE) {
                            groupValidation(event.groupType, context).errorMsg
                        } else {
                            null
                        }
                    state.copy(
                        groupSelect = event.groupType,
                        groupSelectErrorMsg = errorMsg
                    )
                }
            }

            CreateTribeOrInnerCircleUiEvent.SubmitClick -> {
                if (!isOffline.value) {
                    validationUseCase.apply {
                        val circleNameValidationResult = emptyFieldValidation(
                            createTribeOrInnerCircleDataFlow.value.circleName,
                            context.getString(R.string.please_enter_your_circle_name)
                        )

                        val selectCircleValidationResult =
                            if (createTribeOrInnerCircleDataFlow.value.screen == Constants.AppScreen.CREATE_INNER_CIRCLE_OR_TRIBE) {
                                groupValidation(
                                    createTribeOrInnerCircleDataFlow.value.groupSelect,
                                    context
                                )
                            } else {
                                ValidationResult(isSuccess = true, errorMsg = null)
                            }

                        val memberValidationResult =
                            if (createTribeOrInnerCircleDataFlow.value.messageResponse == null) {
                                memberValidation(
                                    createTribeOrInnerCircleDataFlow.value.memberList,
                                    context
                                )
                            } else {
                                ValidationResult(
                                    isSuccess = true,
                                    errorMsg = null
                                ) // No validation needed
                            }


                        /*  val memberValidationResult =
                              memberValidation(
                              createTribeOrInnerCircleDataFlow.value.memberList,
                              context
                          )*/

                        val profileValidationResult = emptyFieldValidation(
                            createTribeOrInnerCircleDataFlow.value.profileImage,
                            context.getString(R.string.please_select_your_profile)
                        )

                        val hasError = listOf(
                            circleNameValidationResult,
                            selectCircleValidationResult,
                            memberValidationResult,
                            profileValidationResult  // Include profile validation in hasError
                        ).any { !it.isSuccess }

                        // 🔹 **Update all error messages in one go**
                        createTribeOrInnerCircleDataFlow.update { state ->
                            state.copy(
                                circleErrorMsg = circleNameValidationResult.errorMsg,
                                groupSelectErrorMsg = if (createTribeOrInnerCircleDataFlow.value.screen == Constants.AppScreen.CREATE_INNER_CIRCLE_OR_TRIBE) selectCircleValidationResult.errorMsg else null,
                                memberValidationMsg = memberValidationResult.errorMsg
                            )
                        }
                        if (!profileValidationResult.isSuccess) {
                            showWarningMessage(
                                this@GetCreateTribeOrInnerCircleUiStateUseCase.context,
                                profileValidationResult.errorMsg ?: ""
                            )
                        }
                        if (hasError) return //  Stop if any validation failed

                        if (createTribeOrInnerCircleDataFlow.value.screen == Constants.AppScreen.CREATE_INNER_CIRCLE_OR_TRIBE) {
                            callCreateInnerCircleAndTribeApi(
                                coroutineScope = coroutineScope,
                                navigation = navigate
                            )
                        } else {
                            if (createTribeOrInnerCircleDataFlow.value.messageResponse == null) {
                                callAddGroupMember(
                                    coroutineScope = coroutineScope,
                                    navigation = navigate
                                )
                            } else {
                                groupDetailsUpdate(
                                    coroutineScope = coroutineScope,
                                    navigation = navigate
                                )
                            }
                        }

                        /* callCreateInnerCircleAndTribeApi(
                             coroutineScope = coroutineScope,
                             navigation = navigate
                         )*/
                    }

                } else {
                    showWarningMessage(
                        this.context,
                        context.getString(R.string.please_check_your_internet_connection_first)
                    )
                }
            }

            is CreateTribeOrInnerCircleUiEvent.ProfileValueChange -> {
                isNewImage.value = true
                createTribeOrInnerCircleDataFlow.update { state ->
                    isNewImage.value = true
                    state.copy(
                        profileImage = event.profile
                    )
                }

            }

            is CreateTribeOrInnerCircleUiEvent.ShowDialog -> {
                createTribeOrInnerCircleDataFlow.update { state ->
                    state.copy(
                        showDialog = event.show
                    )
                }
            }

            is CreateTribeOrInnerCircleUiEvent.ShowPermissionDialog -> {
                createTribeOrInnerCircleDataFlow.update { state ->
                    state.copy(
                        showPermissionDialog = event.show
                    )
                }
            }

            is CreateTribeOrInnerCircleUiEvent.MemberList -> {
                createTribeOrInnerCircleDataFlow.update { state ->
                    state.copy(
                        memberList = event.member,
                        memberValidationMsg = if (event.shouldValidate && event.member.isBlank())
                            context.getString(R.string.select_at_least_one_member)
                        else null
                    )
                }
                Log.d("TAG", "createTribeOrInnerCircleUiEvent: ${event.member}")
            }
        }
    }

    private fun memberValidation(member: String, context: Context): ValidationResult {
        return ValidationResult(
            isSuccess = member.isNotBlank(),
            errorMsg = if (member.isBlank()) context.getString(R.string.select_at_least_one_member) else null
        )
    }

    private fun groupValidation(gender: String?, context: Context): ValidationResult {
        return ValidationResult(
            isSuccess = !gender.isNullOrBlank(),
            errorMsg = if (gender.isNullOrBlank()) context.getString(R.string.please_select_your_group_circle) else null
        )
    }

    private fun showOrHideLoader(showLoader: Boolean) {
        createTribeOrInnerCircleDataFlow.update { state ->
            state.copy(
                showLoader = showLoader
            )
        }
    }


    private fun callCreateInnerCircleAndTribeApi(
        coroutineScope: CoroutineScope,
        navigation: (NavigationAction) -> Unit
    ) {

        val typeValue =
            if (createTribeOrInnerCircleDataFlow.value.groupSelect == context.getString(R.string.tribe)) {
                1
            } else {
                2
            }
        val memberList = try {
            val type = object : TypeToken<List<String>>() {}.type
            val members: List<String> = Gson().fromJson(
                createTribeOrInnerCircleDataFlow.value.memberList,
                type
            )
            members.joinToString(",")
        } catch (e: Exception) {
            Log.e("TAG", "Error parsing member list", e)
            ""
        }
        val map: HashMap<String, RequestBody> = hashMapOf()
        map[Constants.CreateInnerCircleAndTribe.NAME] =
            createTribeOrInnerCircleDataFlow.value.circleName.toRequestBody("multipart/form-data".toMediaTypeOrNull())

        map[Constants.CreateInnerCircleAndTribe.TYPE] =
            typeValue.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())

        map[Constants.CreateInnerCircleAndTribe.MEMBER] =
            memberList.toRequestBody("multipart/form-data".toMediaTypeOrNull())

        Log.d("TAG", "memberList: ${createTribeOrInnerCircleDataFlow.value.memberList}")

        val profileImageFile = File(createTribeOrInnerCircleDataFlow.value.profileImage)
        val profileImage =
            createMultipartBody(profileImageFile, Constants.CreateInnerCircleAndTribe.IMAGE)


        coroutineScope.launch {
            apiRepository.createInnerCircleAndTribe(map, profileImage).collect {
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
                        navigation(NavigationAction.Pop())
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


    private fun callAddGroupMember(
        coroutineScope: CoroutineScope,
        navigation: (NavigationAction) -> Unit
    ) {

        val memberList = try {
            val type = object : TypeToken<List<String>>() {}.type
            val members: List<String> = Gson().fromJson(
                createTribeOrInnerCircleDataFlow.value.memberList,
                type
            )
            members.joinToString(",")
        } catch (e: Exception) {
            Log.e("TAG", "Error parsing member list", e)
            ""
        }
        val map: HashMap<String, RequestBody> = hashMapOf()
        map[Constants.AddMember.GROUP_NAME] =
            createTribeOrInnerCircleDataFlow.value.circleName.toRequestBody("multipart/form-data".toMediaTypeOrNull())

        map[Constants.AddMember.MEMBER] =
            memberList.toRequestBody("multipart/form-data".toMediaTypeOrNull())


        Log.d("TAG", "memberList: ${createTribeOrInnerCircleDataFlow.value.memberList}")

        val profileImageFile = File(createTribeOrInnerCircleDataFlow.value.profileImage)
        val profileImage =
            createMultipartBody(profileImageFile, Constants.AddMember.GROUP_IMAGE)

        coroutineScope.launch {
            apiRepository.addGroupMember(map, profileImage).collect {
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
                        coroutineScope.launch {
                            delay(1000) // Adjust delay if needed
                            navigation(NavigationAction.PopIntent)
                        }

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

    private fun groupDetailsUpdate(
        coroutineScope: CoroutineScope,
        navigation: (NavigationAction) -> Unit
    ) {

        val map: HashMap<String, RequestBody> = hashMapOf()
        map[Constants.AddMember.GROUP_NAME] =
            createTribeOrInnerCircleDataFlow.value.circleName.toRequestBody("multipart/form-data".toMediaTypeOrNull())

        map[Constants.AddMember.GROUP_ID] =
            createTribeOrInnerCircleDataFlow.value.messageResponse?.groupId?.toRequestBody("multipart/form-data".toMediaTypeOrNull())
                ?: "".toRequestBody("multipart/form-data".toMediaTypeOrNull())


        val profileImagePath = createTribeOrInnerCircleDataFlow.value.profileImage
        val profileImageFile = if (isNewImage.value) {
            File(profileImagePath) // Only create a File object if it's a new image
        } else {
            null // Skip creating a File object for URLs
        }

        val profileImage = profileImageFile?.let {
            createMultipartBody(it, Constants.AddMember.GROUP_IMAGE)
        }

        coroutineScope.launch {
            apiRepository.updateGroupMember(map, profileImage).collect {
                when (it) {
                    is NetworkResult.Error -> {
                        Log.d("TAG", "groupDetailsUpdate: ${it.message}")
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
                        coroutineScope.launch {
                            delay(1000) // Adjust delay if needed
                            navigation(NavigationAction.PopIntent)
                        }

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


