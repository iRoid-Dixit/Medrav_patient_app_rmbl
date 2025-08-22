package com.griotlegacy.mobile.app.ux.container.createTribeOrInnerCircle

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.griotlegacy.mobile.app.R
import com.griotlegacy.mobile.app.data.source.Constants
import com.griotlegacy.mobile.app.navigation.HandleNavigation
import com.griotlegacy.mobile.app.navigation.scaffold.AppScaffold
import com.griotlegacy.mobile.app.ui.compose.common.AppButtonComponent
import com.griotlegacy.mobile.app.ui.compose.common.AppInputTextField
import com.griotlegacy.mobile.app.ui.compose.common.DateSelectComponent
import com.griotlegacy.mobile.app.ui.compose.common.DropdownField
import com.griotlegacy.mobile.app.ui.compose.common.TopBarComponent
import com.griotlegacy.mobile.app.ui.compose.common.dialog.CameraGalleryDialog
import com.griotlegacy.mobile.app.ui.compose.common.dialog.PermissionDialog
import com.griotlegacy.mobile.app.ui.compose.common.loader.CustomLoader
import com.griotlegacy.mobile.app.ui.compose.common.permission.PhotoPickerManager
import com.griotlegacy.mobile.app.ui.theme.AppThemeColor
import com.griotlegacy.mobile.app.ui.theme.Mercury
import com.griotlegacy.mobile.app.ui.theme.Scorpion

@ExperimentalMaterial3Api
@Composable
fun CreateTribeOrInnerCircleScreen(
    navController: NavController,
    viewModel: CreateTribeOrInnerCircleViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState
    val context = LocalContext.current
    val createTribeOrInnerCircleUiState by uiState.createTribeOrInnerCircleDataFlow.collectAsStateWithLifecycle()
    val navBackStackEntry = navController.currentBackStackEntry
    val memberListJson = navBackStackEntry?.savedStateHandle?.get<String>("memberList")
    LaunchedEffect(memberListJson) {
        uiState.event(
            CreateTribeOrInnerCircleUiEvent.MemberList(
                memberListJson ?: "",
                shouldValidate = false
            )
        )
    }

    uiState.event(CreateTribeOrInnerCircleUiEvent.GetContext(context))
    AppScaffold(
        containerColor = AppThemeColor,
        modifier = Modifier,
        topAppBar = {
            TopBarComponent(
                header = if (createTribeOrInnerCircleUiState?.screen != Constants.AppScreen.CREATE_INNER_CIRCLE_OR_TRIBE) {
                    if (createTribeOrInnerCircleUiState?.messageResponse != null) {
                        "Group info"
                    } else {
                        stringResource(R.string.my_circle)
                    }
                } else {
                    stringResource(R.string.create_circle)
                },
                isBackVisible = true,
                onClick = {
                    uiState.event(CreateTribeOrInnerCircleUiEvent.BackClick)
                },
            )
        },
        navBarData = null
    ) {
        CreateTribeOrInnerCircleScreenContent(uiState, uiState.event)
    }
    if (createTribeOrInnerCircleUiState?.showLoader == true) {
        CustomLoader()
    }
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}
@Composable
private fun CreateTribeOrInnerCircleScreenContent(
    uiState: CreateTribeOrInnerCircleUiState,
    event: (CreateTribeOrInnerCircleUiEvent) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val createTribeOrUiState by uiState.createTribeOrInnerCircleDataFlow.collectAsStateWithLifecycle()
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
            .imePadding()
            .clickable {
                keyboardController?.hide()
            }
            .fillMaxSize(),
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        ProfilePicture(createTribeOrUiState, event)
        Spacer(modifier = Modifier.height(40.dp))
        InnerCircleInputField(createTribeOrUiState, event)
        Spacer(modifier = Modifier.height(35.dp))
        if (createTribeOrUiState?.messageResponse?.isAdmin == null || createTribeOrUiState?.messageResponse?.isAdmin == true) {
            AppButtonComponent(
                onClick = {
                    event(CreateTribeOrInnerCircleUiEvent.SubmitClick)
                },
                modifier = Modifier.fillMaxWidth(),
                text = if (createTribeOrUiState?.screen != Constants.AppScreen.CREATE_INNER_CIRCLE_OR_TRIBE) stringResource(
                    R.string.create
                ) else stringResource(id = R.string.submit),
            )

        }
        Log.d("TAG", "isAdmin:${createTribeOrUiState?.messageResponse?.isAdmin}")
    }
}
@Composable
fun InnerCircleInputField(
    createTribeOrInnerCircleUiState: CreateTribeOrInnerCircleDataState?,
    event: (CreateTribeOrInnerCircleUiEvent) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val memberCount = try {
        val type = object : TypeToken<List<String>>() {}.type
        val members: List<String> =
            Gson().fromJson(createTribeOrInnerCircleUiState?.memberList ?: "[]", type)
        members.size
    } catch (e: Exception) {
        Log.e("TAG", "Error parsing member list", e)
        0 // Default to 0 if parsing fails
    }
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        AppInputTextField(
            value = createTribeOrInnerCircleUiState?.circleName ?: "",
            onValueChange = { event(CreateTribeOrInnerCircleUiEvent.CircleNameValueChange(it)) },
            isLeadingIconVisible = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next,
                capitalization = KeyboardCapitalization.Sentences,
            ),
            isReadOnly = !(createTribeOrInnerCircleUiState?.messageResponse?.isAdmin == null || createTribeOrInnerCircleUiState.messageResponse.isAdmin == true),
            errorMessage = createTribeOrInnerCircleUiState?.circleErrorMsg ?: "",
            header = if (createTribeOrInnerCircleUiState?.screen != Constants.AppScreen.CREATE_INNER_CIRCLE_OR_TRIBE) "Group name" else "Circle Name",
            leadingIcon = if (createTribeOrInnerCircleUiState?.screen != Constants.AppScreen.CREATE_INNER_CIRCLE_OR_TRIBE) R.drawable.ic_app_icon else R.drawable.ic_app_icon,
        )
        if (createTribeOrInnerCircleUiState?.screen == Constants.AppScreen.CREATE_INNER_CIRCLE_OR_TRIBE) {
            DropdownField(
                list = listOf(stringResource(R.string.tribe), stringResource(R.string.innercircle)),
                expanded = expanded,
                selectedRole = createTribeOrInnerCircleUiState.groupSelect,
                onRoleDropDownExpanded = {
                    expanded = it
                },
                placeholder = stringResource(R.string.select_circle),
                errorMessage = createTribeOrInnerCircleUiState.groupSelectErrorMsg,
                onUserRoleValue = {
                    event(
                        CreateTribeOrInnerCircleUiEvent.GroupTypeDropDownExpanded(
                            it
                        )
                    )
                },
            )

        }
        val memberText =
            if (createTribeOrInnerCircleUiState?.screen != Constants.AppScreen.CREATE_INNER_CIRCLE_OR_TRIBE) stringResource(
                id = R.string.add_member
            ) else stringResource(
                R.string.member
            )

        if (createTribeOrInnerCircleUiState?.messageResponse == null) {
            DateSelectComponent(
                value = if (memberCount == 0) "" else "Members (${memberCount})",
                header = memberText,
                leadingIcon = R.drawable.ic_app_icon,
                errorMessage = createTribeOrInnerCircleUiState?.memberValidationMsg ?: "",
                trailingIcon = R.drawable.ic_app_icon,
                endPadding = 10.dp,
                onClick = {
                    event(CreateTribeOrInnerCircleUiEvent.OnAddMemberClick)
                },
            )

        }
    }
}
@Composable
fun ProfilePicture(
    editProfileUiState: CreateTribeOrInnerCircleDataState?,
    event: (CreateTribeOrInnerCircleUiEvent) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalActivity.current
    val startForCameraPermissionResult =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            Log.e(ContentValues.TAG, "Camera Permission ${result.resultCode}")
        }
    val photoPickerManager = remember {
        PhotoPickerManager(
            context = context,
            activityResultRegistry = (lifecycleOwner as ComponentActivity).activityResultRegistry,
            onPhotoPicked = { photoPath ->
                event(CreateTribeOrInnerCircleUiEvent.ProfileValueChange(photoPath))
            },
            onError = {
                event(CreateTribeOrInnerCircleUiEvent.ShowPermissionDialog(true))
            },
            onCameraErrorClick = {
                event(CreateTribeOrInnerCircleUiEvent.ShowDialog(false))
            }
        )
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box {
            Surface(
                onClick = {
                    if (editProfileUiState?.messageResponse?.isAdmin == null || editProfileUiState.messageResponse.isAdmin == true) {
                        event(CreateTribeOrInnerCircleUiEvent.ShowDialog(true))

                    }
                },
                shape = RoundedCornerShape(100),
                modifier = Modifier
                    .size(120.dp),
                border = BorderStroke(2.dp, Scorpion),
            ) {
                AsyncImage(
                    model = editProfileUiState?.profileImage,
                    contentDescription = null,
                    placeholder = painterResource(id = R.drawable.ic_app_icon),
                    error = painterResource(id = R.drawable.ic_app_icon),
                    modifier = Modifier.clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
            if (editProfileUiState?.messageResponse?.isAdmin == null || editProfileUiState.messageResponse.isAdmin == true) {
                Surface(
                    onClick = {
                        event(CreateTribeOrInnerCircleUiEvent.ShowDialog(true))
                    },
                    shape = RoundedCornerShape(100), color = Mercury, shadowElevation = 3.dp,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = (-5).dp, y = (5).dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_app_icon),
                        contentDescription = null,
                        modifier = Modifier.padding(8.dp)
                    )
                }

            }
        }
    }
    if (editProfileUiState?.showDialog == true) {
        CameraGalleryDialog(
            onDismissRequest = { event(CreateTribeOrInnerCircleUiEvent.ShowDialog(false)) },
            galleryText = stringResource(id = R.string.gallery),
            cameraText = stringResource(id = R.string.camera),
            onGalleryClick = {
                event(CreateTribeOrInnerCircleUiEvent.ShowDialog(false))
                photoPickerManager.pickPhotoFromGallery()
            },
            onCameraClick = {
                event(CreateTribeOrInnerCircleUiEvent.ShowDialog(false))
                photoPickerManager.capturePhoto()
            }
        )
    }
    if (editProfileUiState?.showPermissionDialog == true) {
        PermissionDialog(
            onDismissRequest = { event(CreateTribeOrInnerCircleUiEvent.ShowPermissionDialog(false)) },
            title = stringResource(id = R.string.griot_legacy_app),
            description = stringResource(id = R.string.allow_griot_legacy_app_to_access_your_storage_and_camera_while_you_are_using_the_app),
            negativeText = stringResource(id = R.string.cancel),
            positiveText = stringResource(id = R.string.open_setting),
            onPositiveClick = {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uriIntent = Uri.fromParts("package", context.packageName, null)
                intent.data = uriIntent
                startForCameraPermissionResult.launch(intent)
            },
        )
    }
}

@Preview
@Composable
fun CreateTribeOrInnerCircleScreenPreview() {
    val uiState = CreateTribeOrInnerCircleUiState()
    CreateTribeOrInnerCircleScreenContent(uiState = uiState, event = uiState.event)
}






