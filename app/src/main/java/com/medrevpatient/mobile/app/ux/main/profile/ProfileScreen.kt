package com.medrevpatient.mobile.app.ux.main.profile

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.androidisland.ezpermission.EzPermission
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.domain.request.UpdateProfileReq
import com.medrevpatient.mobile.app.navigation.RouteMaker
import com.medrevpatient.mobile.app.ui.DialogLoader
import com.medrevpatient.mobile.app.ui.HStack
import com.medrevpatient.mobile.app.ui.VStack
import com.medrevpatient.mobile.app.ui.common.AppInputTextField
import com.medrevpatient.mobile.app.ui.common.BasicBottomSheet
import com.medrevpatient.mobile.app.ui.common.IconTitleArrowItem
import com.medrevpatient.mobile.app.ui.common.PermissionDialog
import com.medrevpatient.mobile.app.ui.common.TitleDualFont
import com.medrevpatient.mobile.app.ui.common.VerifiedBottomSheet
import com.medrevpatient.mobile.app.ui.theme.ColorSwansDown
import com.medrevpatient.mobile.app.ui.theme.MineShaft
import com.medrevpatient.mobile.app.ui.theme.MedrevPatientTheme
import com.medrevpatient.mobile.app.ui.theme.black25
import com.medrevpatient.mobile.app.ui.theme.outFit
import com.medrevpatient.mobile.app.ui.theme.white
import com.medrevpatient.mobile.app.utils.AppUtils.noRippleClickable
import com.medrevpatient.mobile.app.ux.main.component.SkaiButton
import com.medrevpatient.mobile.app.ux.main.component.SkaiButtonDefault
import com.medrevpatient.mobile.app.ux.startup.emailVerification.OTPVerificationBottomSheet
import com.medrevpatient.mobile.app.ux.startup.onboarding.onBoardDataFlow.showImagePickerOptions
import com.medrevpatient.mobile.app.ux.startup.onboarding.onBoardDataFlow.uriFromBitmap
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    MedrevPatientTheme {
        ProfileContent(
            modifier = modifier.padding(PaddingValues()),
            viewModel = viewModel,
            uiState = uiState,
            event = viewModel::event
        )
        if (uiState.isLoading) DialogLoader()
    }
}

@Composable
fun ProfileContent(
    modifier: Modifier = Modifier, viewModel: ProfileViewModel, uiState: ProfileUiState,
    event: (ProfileUiEvent) -> Unit
) {
    val context = LocalContext.current
    VStack(
        spaceBy = 0.dp,
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(white)
    ) {
        Image(
            imageVector = ImageVector.vectorResource(id = R.drawable.back),
            contentDescription = null,
            modifier = Modifier
                .padding(18.dp)
                .padding(top = 40.dp)
                .align(Alignment.Start)
                .noRippleClickable {
                    viewModel.popBackStack()
                }
        )
        AsyncImage(
            model = uiState.userData.profileImage,
            placeholder = painterResource(R.drawable.ic_dummy_profile),
            error = painterResource(R.drawable.ic_dummy_profile),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .padding(top = 2.dp)
                .size(140.dp)
                .clip(CircleShape)
        )
        VStack(
            spaceBy = 0.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
                .padding(bottom = 10.dp, top = 40.dp)
                .clip(RoundedCornerShape(8))
                .background(
                    brush = Brush.linearGradient(
                        listOf(
                            white,
                            ColorSwansDown.copy(alpha = 0.7f),
                            ColorSwansDown
                        )
                    )
                )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 10.dp)
            ) {
                Image(
                    imageVector = ImageVector.vectorResource(id = R.drawable.edit_with_bg),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .size(40.dp)
                        .noRippleClickable {
                            /*Toasty
                                .info(context, context.getString(R.string.under_development))
                                .show()*/
                            event(ProfileUiEvent.ShowEditProfileDialog(true))
                        }
                )
                Text(
                    text = uiState.userData.firstName.plus(" ").plus(uiState.userData.lastName),
                    fontSize = 26.sp,
                    color = MineShaft,
                    fontFamily = outFit,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 12.dp)
                )
                Spacer(modifier = Modifier.padding(top = 20.dp))
                val array: ArrayList<Pair<String, Int>> = arrayListOf()
                array.add(Pair(stringResource(R.string.calendar), R.drawable.calendar))
                array.add(Pair(stringResource(R.string.reminder), R.drawable.reminder))
                array.add(Pair(stringResource(R.string.rate_us), R.drawable.rate_us))
                array.add(Pair(stringResource(R.string.terms_conditions), R.drawable.terms___conditions))
                array.add(Pair(stringResource(R.string.account_settings), R.drawable.settings))
                array.forEach {
                    IconTitleArrowItem(
                        text = it.first,
                        icon = it.second,
                        backgroundColor = MineShaft.copy(alpha = 0.02f),
                        onClick = {
                            when (it.first) {
                                context.getString(R.string.calendar) -> {
                                    //Toasty.info(context, context.getString(R.string.under_development)).show()
                                    viewModel.navigate(RouteMaker.Calendar.createRoute())
                                }

                                context.getString(R.string.reminder) -> {
                                    viewModel.navigate(RouteMaker.Reminder.createRoute())
                                    //Toasty.info(context, context.getString(R.string.under_development)).show()
                                }

                                context.getString(R.string.rate_us) -> {
                                    Toasty.info(context, context.getString(R.string.under_development)).show()
                                }

                                context.getString(R.string.terms_conditions) -> {
                                    Toasty.info(context, context.getString(R.string.under_development)).show()
                                }

                                context.getString(R.string.account_settings) -> {
                                    event(ProfileUiEvent.ShowAccountSettingsDialog(true))
                                }
                            }
                        }
                    )
                }
                TitleDualFont(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp, bottom = 15.dp),
                    color = MineShaft,
                    fontWeightBold = FontWeight.Bold,
                    fontWeightRegular = FontWeight.Light,
                    fontSize = 20,
                    fontFamilyBold = outFit,
                    fontFamilyRegular = outFit,
                    titlePart1 = stringResource(id = R.string.skai),
                    titlePart2 = stringResource(id = R.string.fitness)
                )
            }
        }
    }

    if (uiState.showAccountSettingsDialog) {
        AccountSettingsDialog(event = event, uiState = uiState)
    }
    if (uiState.showLogoutDialog) {
        AccountLogoutDialog(event = event, uiState = uiState)
    }
    if (uiState.showDeleteAccountDialog) {
        DeleteAccountDialog(event = event, uiState = uiState)
    }
    if (uiState.showEditProfileDialog) {
        EditProfileDialog(event = event, uiState = uiState, viewModel = viewModel)
    }
    if (uiState.showUpdatePictureDialog) {
        UpdatePictureDialog(event = event, uiState = uiState)
    }
    if (uiState.showOtpVerificationDialog) {
        OtpVerificationDialog(uiState, event)
    }
    if (uiState.showUpdateSuccessDialog) {
        UpdateSuccessDialog(uiState, event, viewModel)
    }
    if (uiState.showManagePersonaliseDialog) {
        ManagePreferenceDialog(uiState = uiState, event = event)
    }
    if (uiState.showUpdatePersonaliseDialog) {
        UpdatePersonaliseDialog(
            uiState = uiState, event = event, title = when (uiState.managePersonaliseFor) {
                stringResource((R.string.age)) -> stringResource(R.string.age)
                stringResource((R.string.height)) -> stringResource(R.string.height)
                stringResource((R.string.weight)) -> stringResource(R.string.weight)
                stringResource((R.string.energy_level)) -> stringResource(R.string.energy_level)
                stringResource((R.string.lifestyle)) -> stringResource(R.string.lifestyle)
                stringResource((R.string.fitness_level)) -> stringResource(R.string.fitness_level)
                stringResource((R.string.goals)) -> stringResource(R.string.goals)
                else -> stringResource(R.string.age)
            }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileDialog(modifier: Modifier = Modifier, event: (ProfileUiEvent) -> Unit, uiState: ProfileUiState, viewModel: ProfileViewModel) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    Column {
        BasicBottomSheet(
            sheetState = { sheetState },
            onDismissRequest = {
                viewModel.getUserData()
                event(ProfileUiEvent.ShowEditProfileDialog(false))
            },
            isSheetVisible = { uiState.showEditProfileDialog },
            title = stringResource(R.string.edit_profile)
        ) {
            EditProfileContent(modifier, event, uiState)
        }
    }
}

@Composable
fun EditProfileContent(modifier: Modifier = Modifier, event: (ProfileUiEvent) -> Unit, uiState: ProfileUiState) {
    VStack(
        0.dp, modifier = modifier
            .fillMaxWidth()
            .background(white)
    ) {
        ProfilePicture(uiState = uiState, event, isFrom = "EditProfileContent")
        val context = LocalContext.current
        val array: ArrayList<Pair<String, Int>> = arrayListOf()
        array.add(Pair(stringResource(R.string.name), R.drawable.edit_profile))
        array.add(Pair(stringResource(R.string.email), R.drawable.email))
        array.add(Pair(stringResource(R.string.update_password), R.drawable.update_password))
        array.add(Pair(stringResource(R.string.manage_personalizes), R.drawable.preferences))
        LazyColumn(
            modifier = Modifier.padding(
                top = 35.dp,
                bottom = 20.dp,
                start = 18.dp,
                end = 18.dp
            )
        ) {
            items(array) {
                IconTitleArrowItem(
                    text = it.first,
                    icon = it.second,
                    backgroundColor = MineShaft.copy(alpha = 0.02f)
                ) {
                    when (it.first) {
                        context.getString(R.string.name) -> {
                            event(ProfileUiEvent.OpenDialogFor(context.getString(R.string.update_name)))
                            event(ProfileUiEvent.ShowUpdatePictureDialog(true))
                        }

                        context.getString(R.string.email) -> {
                            event(ProfileUiEvent.OpenDialogFor(context.getString(R.string.update_email)))
                            event(ProfileUiEvent.ShowUpdatePictureDialog(true))
                        }

                        context.getString(R.string.update_password) -> {
                            event(ProfileUiEvent.OpenDialogFor(context.getString(R.string.update_password)))
                            event(ProfileUiEvent.ShowUpdatePictureDialog(true))
                        }

                        context.getString(R.string.manage_personalizes) -> {
                            event(ProfileUiEvent.OpenDialogFor(context.getString(R.string.manage_personalizes)))
                            event(ProfileUiEvent.ShowEditProfileDialog(false))
                            event(ProfileUiEvent.ShowManagePersonaliseDialog(true))
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdatePictureDialog(modifier: Modifier = Modifier, event: (ProfileUiEvent) -> Unit, uiState: ProfileUiState) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    Column {
        BasicBottomSheet(
            sheetState = { sheetState },
            onDismissRequest = {
                event(ProfileUiEvent.ShowUpdatePictureDialog(false))
                event(ProfileUiEvent.IsNewProfileImageAdded(false))
            },
            isSheetVisible = { uiState.showUpdatePictureDialog },
            title =
            when (uiState.openDialogFor) {
                stringResource(R.string.update_picture) -> stringResource(R.string.update_picture)
                stringResource(R.string.update_name) -> stringResource(R.string.update_name)
                stringResource(R.string.update_email) -> stringResource(R.string.update_email)
                stringResource(R.string.update_password) -> stringResource(R.string.update_password)
                else -> stringResource(R.string.update_picture)
            }
        ) {
            UpdatePictureContent(modifier, event, uiState)
        }
    }
}

@Composable
fun UpdatePictureContent(modifier: Modifier = Modifier, event: (ProfileUiEvent) -> Unit, uiState: ProfileUiState) {
    VStack(
        0.dp, modifier = modifier
            .fillMaxWidth()
            .background(white)
    ) {
        when (uiState.openDialogFor) {
            stringResource(R.string.update_picture) -> {
                ProfilePicture(uiState = uiState, event = event, isFrom = "UpdatePictureContent")
            }

            stringResource(R.string.update_name) -> {
                UpdateNameContent(modifier = modifier, uiState = uiState, event = event)
            }

            stringResource(R.string.update_email) -> {
                UpdateEmailContent(modifier = modifier, uiState = uiState, event = event)
            }

            stringResource(R.string.update_password) -> {
                UpdatePasswordContent(modifier = modifier, uiState = uiState, event = event)
            }
        }
        Spacer(modifier = Modifier.padding(top = 50.dp))
        HStack(8.dp, modifier = Modifier.padding(horizontal = 18.dp)) {
            SkaiButton(
                text = stringResource(R.string.cancel),
                makeUpperCase = true,
                color = white,
                textStyle = SkaiButtonDefault.textStyle.copy(black25, fontSize = 14.sp),
                borderStroke = BorderStroke(1.dp, black25),
                modifier = Modifier.weight(1f),
                elevation = 0.dp,
                innerPadding = PaddingValues(horizontal = 28.dp, vertical = 18.dp),
                onClick = {
                    event(ProfileUiEvent.IsNewProfileImageAdded(false))
                    event(ProfileUiEvent.ShowUpdatePictureDialog(false))
                }
            )
            Spacer(modifier = Modifier.weight(0.03f))
            SkaiButton(
                text = stringResource(R.string.update),
                innerPadding = PaddingValues(horizontal = 28.dp, vertical = 18.dp),
                makeUpperCase = true,
                modifier = Modifier.weight(1f),
                textStyle = SkaiButtonDefault.textStyle.copy(fontSize = 14.sp),
                color = if (checkUpdatedValues(uiState)) MineShaft else MineShaft.copy(alpha = 0.3f),
                elevation = if (checkUpdatedValues(uiState)) SkaiButtonDefault.elevation else 0.dp,
                enable = checkUpdatedValues(uiState),
            ) {
                //event(ProfileUiEvent.ShowUpdatePictureDialog(false))
                event(ProfileUiEvent.PerformUpdateClick(UpdateProfileReq(isFrom = uiState.openDialogFor, profileImage = uiState.profileImage)))
            }
        }
        Spacer(modifier = Modifier.padding(top = 30.dp))
    }
}

@Composable
private fun checkUpdatedValues(uiState: ProfileUiState): Boolean {
    when (uiState.openDialogFor) {
        stringResource(R.string.update_picture) -> {
            if (uiState.isNewProfileImageAdded) {
                return true
            }
        }

        stringResource(R.string.update_name) -> {
            if ((uiState.firstName != uiState.userData.firstName && uiState.firstNameErrorMsg == null) || (uiState.lastName != uiState.userData.lastName && uiState.lastNameErrorMsg == null)) {
                return true
            }
        }

        stringResource(R.string.update_email) -> {
            if (uiState.email != uiState.userData.email && uiState.emailErrorMsg == null) {
                return true
            }
        }

        stringResource(R.string.update_password) -> {
            if (uiState.password.isNotEmpty() && uiState.newPassword.isNotEmpty() && uiState.confirmPassword.isNotEmpty() && uiState.passwordErrorMsg == null && uiState.newPasswordErrorMsg == null && uiState.passwordNotMatch == null) {
                return true
            }
        }
    }
    return false
}

@SuppressLint("InlinedApi")
@Composable
fun ProfilePicture(uiState: ProfileUiState = ProfileUiState(), event: (ProfileUiEvent) -> Unit, isFrom: String) {
    val context = LocalContext.current

    val android13PermissionList: ArrayList<String> = arrayListOf(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_MEDIA_IMAGES,
        Manifest.permission.READ_MEDIA_VIDEO
    )
    val permissionList: ArrayList<String> = arrayListOf(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            imageUri = uri
            event(ProfileUiEvent.StoreData(UpdateProfileReq(profileImage = imageUri, isFrom = context.getString(R.string.update_picture))))
            event(ProfileUiEvent.IsNewProfileImageAdded(true))
        }
    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            imageUri = bitmap?.let { uriFromBitmap(context, it) }
            event(ProfileUiEvent.StoreData(UpdateProfileReq(profileImage = imageUri, isFrom = context.getString(R.string.update_picture))))
            event(ProfileUiEvent.IsNewProfileImageAdded(true))
        }

    val startForCameraPermissionResult =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { _: ActivityResult -> }

    if (uiState.showPermissionDialog) {
        PermissionDialog(
            onDismissRequest = { event(ProfileUiEvent.ShowPermissionDialog(false)) },
            title = stringResource(R.string.app_name),
            description = stringResource(R.string.camera_permission_txt),
            negativeText = stringResource(R.string.cancel),
            positiveText = stringResource(R.string.open_setting),
            onPositiveClick = {
                event(ProfileUiEvent.ShowPermissionDialog(false))
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uriIntent = Uri.fromParts("package", context.packageName, null)
                intent.data = uriIntent
                startForCameraPermissionResult.launch(intent)
            },
        )
    }
    Box(modifier = Modifier
        .padding(top = 20.dp)
        .noRippleClickable {
            if (isFrom == "UpdatePictureContent") {
                permissionCheck(
                    context,
                    android13PermissionList,
                    permissionList,
                    launcher,
                    cameraLauncher,
                    event
                )
            } else {
                event(ProfileUiEvent.OpenDialogFor(context.getString(R.string.update_picture)))
                event(ProfileUiEvent.ShowUpdatePictureDialog(true))
                event(ProfileUiEvent.ShowEditProfileDialog(false))
            }
        }) {

        if (imageUri == null) {
            AsyncImage(
                model = uiState.userData.profileImage,
                placeholder = painterResource(R.drawable.ic_dummy_profile),
                error = painterResource(R.drawable.ic_dummy_profile),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
            )
        } else {
            AsyncImage(
                model = imageUri,
                placeholder = painterResource(R.drawable.ic_dummy_profile),
                error = painterResource(R.drawable.ic_dummy_profile),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
            )
        }
        Image(
            painter = painterResource(R.drawable.ic_edit_camera),
            contentDescription = null,
            modifier = Modifier
                .size(35.dp)
                .align(Alignment.BottomCenter)
                .offset(y = 12.dp)
                .noRippleClickable {
                    if (isFrom == "UpdatePictureContent") {
                        permissionCheck(
                            context,
                            android13PermissionList,
                            permissionList,
                            launcher,
                            cameraLauncher,
                            event
                        )
                    } else {
                        event(ProfileUiEvent.OpenDialogFor(context.getString(R.string.update_picture)))
                        event(ProfileUiEvent.ShowUpdatePictureDialog(true))
                        event(ProfileUiEvent.ShowEditProfileDialog(false))
                    }
                }
        )
    }
}

private fun permissionCheck(
    context: Context,
    android13PermissionList: ArrayList<String>,
    permissionList: ArrayList<String>,
    galleryLauncher: ManagedActivityResultLauncher<String, Uri?>,
    cameraLauncher: ManagedActivityResultLauncher<Void?, Bitmap?>,
    event: (ProfileUiEvent) -> Unit
) {
    var isGranted = false

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        EzPermission.with(context)
            .permissions(android13PermissionList)
            .request { granted, denied, _ ->
                android13PermissionList.forEach {
                    if (granted.contains(it)) {
                        isGranted = true
                    } else if (denied.contains(it)) {
                        isGranted = false
                    }
                }
                if (isGranted) {
                    showImagePickerOptions(context, galleryLauncher, cameraLauncher)
                } else {
                    event(ProfileUiEvent.ShowPermissionDialog(true))
                }
            }
    } else {
        EzPermission.with(context)
            .permissions(permissionList)
            .request { granted, denied, _ ->
                permissionList.forEach {
                    if (granted.contains(it)) {
                        isGranted = true
                    } else if (denied.contains(it)) {
                        isGranted = false
                    }
                }
                if (isGranted) {
                    showImagePickerOptions(context, galleryLauncher, cameraLauncher)
                } else {
                    event(ProfileUiEvent.ShowPermissionDialog(true))
                }
            }
    }
}

@Composable
private fun UpdateNameContent(modifier: Modifier = Modifier, uiState: ProfileUiState, event: (ProfileUiEvent) -> Unit) {
    LaunchedEffect(Unit) {
        event(ProfileUiEvent.FirstNameValueChange(uiState.userData.firstName ?: ""))
        event(ProfileUiEvent.LastNameValueChange(uiState.userData.lastName ?: ""))
    }
    VStack(
        0.dp, modifier = modifier
            .fillMaxWidth()
            .background(white)
    ) {
        AppInputTextField(
            fieldValue = uiState.firstName,
            fieldErrorValue = uiState.firstNameErrorMsg,
            fieldIconId = R.drawable.ic_user,
            fieldHint = stringResource(R.string.first_name),
            imeAction = ImeAction.Next,
            keyboardType = KeyboardType.Text,
            isEnable = true,
            onInputTextChange = {
                event(ProfileUiEvent.FirstNameValueChange(it))
            },
            modifier = Modifier.padding(top = 20.dp)
        )
        Spacer(modifier = Modifier.padding(6.dp))
        AppInputTextField(
            fieldValue = uiState.lastName,
            fieldErrorValue = uiState.lastNameErrorMsg,
            fieldIconId = R.drawable.ic_user,
            fieldHint = stringResource(R.string.last_name),
            imeAction = ImeAction.Done,
            keyboardType = KeyboardType.Text,
            isEnable = true,
            onInputTextChange = {
                event(ProfileUiEvent.LastNameValueChange(it))
            }
        )
    }
}

@Composable
private fun UpdateEmailContent(modifier: Modifier = Modifier, uiState: ProfileUiState, event: (ProfileUiEvent) -> Unit) {
    LaunchedEffect(Unit) {
        event(ProfileUiEvent.EmailValueChange(uiState.userData.email ?: ""))
    }
    VStack(
        0.dp, modifier = modifier
            .fillMaxWidth()
            .background(white)
    ) {
        AppInputTextField(
            fieldValue = uiState.email,
            fieldErrorValue = uiState.emailErrorMsg,
            fieldIconId = R.drawable.ic_email,
            fieldHint = stringResource(R.string.email),
            imeAction = ImeAction.Done,
            keyboardType = KeyboardType.Email,
            isEnable = true,
            onInputTextChange = { event(ProfileUiEvent.EmailValueChange(it)) },
            modifier = Modifier.padding(top = 20.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OtpVerificationDialog(
    uiState: ProfileUiState,
    event: (ProfileUiEvent) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    Column {
        OTPVerificationBottomSheet(
            sheetState = { sheetState },
            onDismissRequest = { event(ProfileUiEvent.ShowOtpVerificationDialog(false)) },
            onChangeEmailClick = { },
            isSheetVisible = { uiState.showOtpVerificationDialog },
            otpValue = uiState.otp,
            onOtpTextChange = { string, _ ->
                event(ProfileUiEvent.OnOTPValueInsert(string))
            },
            seconds = { uiState.counter },
            onResendCodeClick = {
                if (uiState.counter == 0) {
                    event(ProfileUiEvent.PerformResendOTP)
                }
            },
            isLoading = uiState.isLoading,
            isOTPResend = uiState.isOTPResend,
            isFromReset = true,
            email = uiState.email,
            errorMsg = uiState.errorMsg,
            onVerifyClick = {
                event(ProfileUiEvent.PerformVerifyOtp)
            }
        )
    }

    LaunchedEffect(key1 = uiState.isSuccess) {
        uiState.let {
            if (it.isSuccess) {
                launch {
                    sheetState.hide()
                }.invokeOnCompletion {
                    if (!sheetState.isVisible) {
                        event(ProfileUiEvent.ShowOtpVerificationDialog(false))
                    }
                }
            }
        }
    }
}

@Composable
private fun UpdatePasswordContent(modifier: Modifier = Modifier, uiState: ProfileUiState, event: (ProfileUiEvent) -> Unit) {
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var currentPasswordVisible by rememberSaveable { mutableStateOf(false) }
    var confirmPasswordVisible by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        event(ProfileUiEvent.ResetErrorMsgPassword)
    }
    VStack(
        0.dp, modifier = modifier
            .fillMaxWidth()
            .background(white)
    ) {
        AppInputTextField(
            fieldValue = uiState.password,
            fieldErrorValue = uiState.passwordErrorMsg,
            fieldIconId = R.drawable.ic_password,
            fieldHint = stringResource(R.string.current_password),
            imeAction = ImeAction.Next,
            keyboardType = KeyboardType.Password,
            isEnable = true,
            onInputTextChange = { event(ProfileUiEvent.PasswordValueChange(it)) },
            horizontalPadding = 1,
            isTrailingIconVisible = true,
            trailingIconId = if (currentPasswordVisible) R.drawable.ic_show_pass else R.drawable.ic_pass_close,
            visualTransformation = if (currentPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            onTogglePasswordVisibility = { currentPasswordVisible = !currentPasswordVisible },
            modifier = Modifier
                .padding(top = 20.dp)
                .padding(horizontal = 18.dp)
        )
        AppInputTextField(
            fieldValue = uiState.newPassword,
            fieldErrorValue = uiState.newPasswordErrorMsg,
            fieldIconId = R.drawable.ic_password,
            fieldHint = stringResource(R.string.password),
            imeAction = ImeAction.Next,
            keyboardType = KeyboardType.Password,
            isEnable = true,
            onInputTextChange = { event(ProfileUiEvent.NewPasswordValueChange(it)) },
            horizontalPadding = 1,
            isTrailingIconVisible = true,
            trailingIconId = if (passwordVisible) R.drawable.ic_show_pass else R.drawable.ic_pass_close,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            onTogglePasswordVisibility = { passwordVisible = !passwordVisible },
            modifier = Modifier
                .padding(top = 10.dp)
                .padding(horizontal = 18.dp)
        )
        AppInputTextField(
            fieldValue = uiState.confirmPassword,
            fieldErrorValue = uiState.passwordNotMatch,
            fieldIconId = R.drawable.ic_password,
            fieldHint = stringResource(R.string.confirm_password),
            imeAction = ImeAction.Done,
            keyboardType = KeyboardType.Password,
            isEnable = true,
            onInputTextChange = { event(ProfileUiEvent.ConfirmPasswordValueChange(it)) },
            isTrailingIconVisible = true,
            horizontalPadding = 1,
            trailingIconId = if (confirmPasswordVisible) R.drawable.ic_show_pass else R.drawable.ic_pass_close,
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            onTogglePasswordVisibility = { confirmPasswordVisible = !confirmPasswordVisible },
            modifier = Modifier
                .padding(top = 10.dp)
                .padding(horizontal = 18.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UpdateSuccessDialog(uiState: ProfileUiState, event: (ProfileUiEvent) -> Unit, viewModel: ProfileViewModel) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    Column {
        VerifiedBottomSheet(
            sheetState = { sheetState },
            onDismissRequest = { event(ProfileUiEvent.ShowUpdateSuccessDialog(false)) },
            isSheetVisible = { uiState.showUpdateSuccessDialog },
            onClickOfGetStarted = {
                event(ProfileUiEvent.ShowUpdatePictureDialog(false))
                event(ProfileUiEvent.ShowUpdateSuccessDialog(false))
                event(ProfileUiEvent.ShowEditProfileDialog(false))
                //event(ProfileUiEvent.ShowManagePersonaliseDialog(false))
                event(ProfileUiEvent.ShowUpdatePersonaliseDialog(false))
                viewModel.getUserData()
            },
            isFromResetPassword = false,
            isFromUpdateProfile = true,
            titleText = when (uiState.openDialogFor) {
                stringResource(R.string.update_name) -> stringResource(R.string.name)
                stringResource(R.string.update_email) -> stringResource(R.string.email)
                stringResource(R.string.update_password) -> stringResource(R.string.password)
                else -> when (uiState.managePersonaliseFor) {
                    stringResource((R.string.age)) -> stringResource(R.string.age)
                    stringResource((R.string.height)) -> stringResource(R.string.height)
                    stringResource((R.string.weight)) -> stringResource(R.string.weight)
                    stringResource((R.string.energy_level)) -> stringResource(R.string.energy_level)
                    stringResource((R.string.fitness_level)) -> stringResource(R.string.fitness_level)
                    stringResource((R.string.goals)) -> stringResource(R.string.goals)
                    else -> stringResource(R.string.age)
                }
            }
        )
    }
}

@Preview
@Composable
private fun Preview() {
    Surface {
        ProfileContent(modifier = Modifier.fillMaxSize(), viewModel = hiltViewModel(), uiState = ProfileUiState(), event = {})
    }
}