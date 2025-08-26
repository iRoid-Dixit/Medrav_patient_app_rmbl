package com.medrevpatient.mobile.app.ux.container.editProfile

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.navigation.HandleNavigation
import com.medrevpatient.mobile.app.navigation.scaffold.AppScaffold
import com.medrevpatient.mobile.app.ui.compose.common.AppButtonComponent
import com.medrevpatient.mobile.app.ui.compose.common.AppInputTextField
import com.medrevpatient.mobile.app.ui.compose.common.DatePickerWithDialog
import com.medrevpatient.mobile.app.ui.compose.common.DateSelectComponent
import com.medrevpatient.mobile.app.ui.compose.common.DropdownField
import com.medrevpatient.mobile.app.ui.compose.common.TopBarComponent
import com.medrevpatient.mobile.app.ui.compose.common.countryCode.CountryCodePickerComponent
import com.medrevpatient.mobile.app.ui.compose.common.dialog.CameraGalleryDialog
import com.medrevpatient.mobile.app.ui.compose.common.dialog.PermissionDialog
import com.medrevpatient.mobile.app.ui.compose.common.loader.CustomLoader
import com.medrevpatient.mobile.app.ui.compose.common.permission.PhotoPickerManager
import com.medrevpatient.mobile.app.ui.theme.AppThemeColor
import com.medrevpatient.mobile.app.ui.theme.Mercury
import com.medrevpatient.mobile.app.ui.theme.Scorpion
@ExperimentalMaterial3Api
@Composable
fun EditProfileScreen(
    navController: NavController,
    viewModel: EditProfileViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState
    val context = LocalContext.current
    val changePasswordUiState by uiState.editProfileDataFlow.collectAsStateWithLifecycle()
    uiState.event(EditProfileUiEvent.GetContext(context))
    AppScaffold(
        containerColor = AppThemeColor,
        modifier = Modifier,
        topAppBar = {
            TopBarComponent(
                onClick = { navController.popBackStack() },
                titleText = "BMI & Health Check"
            )
        },
        navBarData = null
    ) {
        ContactUsScreenContent(uiState, uiState.event)
    }
    if (changePasswordUiState?.showLoader == true) {
        CustomLoader()
    }
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}

@Composable
private fun ContactUsScreenContent(
    uiState: EditProfileUiState,
    event: (EditProfileUiEvent) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val editProfileUiState by uiState.editProfileDataFlow.collectAsStateWithLifecycle()
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
        Spacer(modifier = Modifier.height(30.dp))
        ProfilePicture(editProfileUiState, event)
        Spacer(modifier = Modifier.height(40.dp))
        EditProfileInputField(editProfileUiState, event)
        Spacer(modifier = Modifier.height(25.dp))
        AppButtonComponent(
            onClick = {
                event(EditProfileUiEvent.ProfileSubmitClick)
            },
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = R.string.update),

            )
    }
}
@Composable
fun EditProfileInputField(
    editProfileUiState: EditProfileDataState?,
    event: (EditProfileUiEvent) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var temp by rememberSaveable { mutableStateOf<Long?>(null) }
    var showDatePickerDialog by remember { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        AppInputTextField(
            value = editProfileUiState?.name ?: "",
            onValueChange = { event(EditProfileUiEvent.NameValueChange(it)) },
            isLeadingIconVisible = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
                capitalization = KeyboardCapitalization.Sentences,
            ),
            errorMessage = editProfileUiState?.nameErrorMsg ?: "",
            header = stringResource(R.string.full_name),
            leadingIcon = R.drawable.ic_app_icon,
        )
        AppInputTextField(
            value = editProfileUiState?.email ?: "",
            onValueChange = { event(EditProfileUiEvent.EmailValueChange(it)) },
            isLeadingIconVisible = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            errorMessage = editProfileUiState?.emailErrorMsg ?: "",
            header = stringResource(id = R.string.email),
            leadingIcon = R.drawable.ic_app_icon,
        )
        CountryCodePickerComponent(
            value = editProfileUiState?.phoneNumber ?: "",
            onValueChange = { newValue ->
                val filteredValue = newValue.filter { it.isDigit() }
                event(EditProfileUiEvent.PhoneNumberValueChange(filteredValue))
            },
            isLeadingIconVisible = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.NumberPassword,
                imeAction = ImeAction.Next
            ),
            errorMessage = editProfileUiState?.phoneNumberErrorMsg,
            header = stringResource(R.string.mobile_number),
            setCountryCode = editProfileUiState?.showCountryCode
        )
        DateSelectComponent(
            value = editProfileUiState?.dateSelected ?: "",
            header = stringResource(id = R.string.date_of_birth),
            trailingIcon = R.drawable.ic_app_icon,
            errorMessage = editProfileUiState?.dateOfBirthValidationMsg,
            onClick = {
                showDatePickerDialog = true
            },
        )
        DropdownField(
            list = listOf(
                stringResource(R.string.male),
                stringResource(R.string.female),
                stringResource(R.string.non_binary)
            ),
            expanded = expanded,
            selectedRole = editProfileUiState?.selectGender ?: "",
            onRoleDropDownExpanded = {
                expanded = it
            },
            errorMessage = editProfileUiState?.selectGanderErrorMsg,
            onUserRoleValue = { event(EditProfileUiEvent.RoleDropDownExpanded(it)) },
        )
    }
    if (showDatePickerDialog) {
        DatePickerWithDialog(
            onSelectedDate = temp,
            onDateSelected = { dateString ->
                event(EditProfileUiEvent.OnClickOfDate(dateString))
            },
            onDismiss = {
                showDatePickerDialog = false
            },
            onDateSelectedLong = {
                temp = it
            }
        )
    }
}
@Composable
fun ProfilePicture(editProfileUiState: EditProfileDataState?, event: (EditProfileUiEvent) -> Unit) {
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
                event(EditProfileUiEvent.ProfileValueChange(photoPath))
            },
            onError = {
                event(EditProfileUiEvent.ShowPermissionDialog(true))
            },
            onCameraErrorClick = {
                event(EditProfileUiEvent.ShowDialog(false))
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
                    event(EditProfileUiEvent.ShowDialog(true))
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
            Surface(
                onClick = {
                    event(EditProfileUiEvent.ShowDialog(true))
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
    if (editProfileUiState?.showDialog == true) {
        CameraGalleryDialog(
            onDismissRequest = {
                event(EditProfileUiEvent.ShowDialog(false))
                event(EditProfileUiEvent.ShowPermissionDialog(false))
            },
            galleryText = stringResource(id = R.string.choose_from_gallery),
            cameraText = stringResource(id = R.string.take_photo),
            onGalleryClick = {
                event(EditProfileUiEvent.ShowDialog(false))
                photoPickerManager.pickPhotoFromGallery()
                event(EditProfileUiEvent.ShowPermissionDialog(false))
            },
            onCameraClick = {
                event(EditProfileUiEvent.ShowDialog(false))
                photoPickerManager.capturePhoto()
                event(EditProfileUiEvent.ShowPermissionDialog(false))
            }
        )
    }
    if (editProfileUiState?.showPermissionDialog == true) {
        PermissionDialog(
            onDismissRequest = { event(EditProfileUiEvent.ShowPermissionDialog(false)) },
            title = stringResource(R.string.griot_legacy_app),
            description = stringResource(R.string.allow_griot_legacy_app_to_access_your_storage_and_camera_while_you_are_using_the_app),
            negativeText = stringResource(id = R.string.cancel),
            positiveText = stringResource(R.string.open_setting),
            onPositiveClick = {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uriIntent = Uri.fromParts("package", context.packageName, null)
                intent.data = uriIntent
                startForCameraPermissionResult.launch(intent)
                event(EditProfileUiEvent.ShowPermissionDialog(false))
            },
        )
    }
}

@Preview
@Composable
fun AboutScreenContentPreview() {
    val uiState = EditProfileUiState()
    ContactUsScreenContent(uiState = uiState, event = uiState.event)

}






