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
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import java.text.SimpleDateFormat
import java.util.Locale
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
import com.medrevpatient.mobile.app.ui.compose.common.TopBarComponent
import com.medrevpatient.mobile.app.ui.compose.common.dialog.CameraGalleryDialog
import com.medrevpatient.mobile.app.ui.compose.common.dialog.PermissionDialog
import com.medrevpatient.mobile.app.ui.compose.common.permission.PhotoPickerManager
import com.medrevpatient.mobile.app.ui.theme.PurpleHeart
import com.medrevpatient.mobile.app.ui.theme.RedE4
import com.medrevpatient.mobile.app.ui.theme.RedF8
import com.medrevpatient.mobile.app.ui.theme.SteelGray
import com.medrevpatient.mobile.app.ui.theme.White
import com.medrevpatient.mobile.app.ui.theme.nunito_sans_600
import com.medrevpatient.mobile.app.utils.AppUtils.noRippleClickable
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
        containerColor = White,
        modifier = Modifier,
        topAppBar = {
            TopBarComponent(
                onClick = {
                    uiState.event(EditProfileUiEvent.BackClick)
                },
                titleText = "Edit Profile",
                isBackVisible = true
            )
        },
        navBarData = null
    ) {
        EditProfileScreenContent(uiState, uiState.event)
    }
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}
@Composable
private fun EditProfileScreenContent(
    uiState: EditProfileUiState,
    event: (EditProfileUiEvent) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val editProfileUiState by uiState.editProfileDataFlow.collectAsStateWithLifecycle()
    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
            .imePadding()
            .noRippleClickable {
                keyboardController?.hide()
            }
            .fillMaxSize(),
    ) {
        Spacer(modifier = Modifier.size(40.dp))
        ProfileHeader(editProfileUiState, uiState.event)
        Spacer(modifier = Modifier.size(30.dp))
        EditProfileInputField(
            editProfileUiState,
            event
        )
    }
}
@Composable
fun ProfileHeader(editProfileUiState: EditProfileDataState?, event: (EditProfileUiEvent) -> Unit) {
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
                modifier = Modifier
                    .size(90.dp)
                    .background(White)
            ) {
                AsyncImage(
                    model = editProfileUiState?.profileImage,
                    contentDescription = null,
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape),
                    placeholder = painterResource(id = R.drawable.ic_place_holder),
                    error = painterResource(id = R.drawable.ic_place_holder),
                    contentScale = ContentScale.Crop
                )
            }
            Surface(
                onClick = {
                    event(EditProfileUiEvent.ShowDialog(true))
                },
                shape = RoundedCornerShape(100), color = PurpleHeart, shadowElevation = 3.dp,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = (10).dp, y = (10).dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_camera),
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
@Composable
fun EditProfileInputField(
    editProfileUiState: EditProfileDataState?,
    event: (EditProfileUiEvent) -> Unit
) {
    var showDatePickerDialog by remember { mutableStateOf(false) }
    var selectedDateMillis by rememberSaveable { mutableStateOf<Long?>(null) }
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Text(
            text = "Personal Information",
            fontFamily = nunito_sans_600,
            color = SteelGray,
            fontSize = 18.sp
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.weight(1f)) {
                AppInputTextField(
                    value = editProfileUiState?.firstName ?: "",
                    onValueChange = { event(EditProfileUiEvent.FirstNameValueChange(it)) },
                    title = "First Name",
                    isTitleVisible = true,
                    isLeadingIconVisible = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next,
                        capitalization = KeyboardCapitalization.Words
                    ),
                    errorMessage = editProfileUiState?.firstNameErrorMsg,
                    header = "Enter first name"
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                AppInputTextField(
                    value = editProfileUiState?.lastName ?: "",
                    onValueChange = { event(EditProfileUiEvent.LastNameValueChange(it)) },
                    isLeadingIconVisible = true,
                    isTitleVisible = true,
                    title = "Last Name",
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next,
                        capitalization = KeyboardCapitalization.Words
                    ),
                    errorMessage = editProfileUiState?.lastNameErrorMsg,
                    header = "Enter last name"
                )
            }
        }
        AppInputTextField(
            value = editProfileUiState?.email ?: "",
            onValueChange = { event(EditProfileUiEvent.EmailValueChange(it)) },
            isLeadingIconVisible = true,
            isTitleVisible = true,
            title = "Email Address",
            isVerifyButtonVisible = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next,
                capitalization = KeyboardCapitalization.Words
            ),
            header = "Enter Email"
        )
        DateSelectComponent(
            value = editProfileUiState?.dateSelected ?: "",
            header = "Enter your DOB",
            onClick = {
                showDatePickerDialog = true
            },
            isTitleVisible = true,
            title = "Date of Birth"
        )
        Text(
            text = "Medical Information",
            fontFamily = nunito_sans_600,
            color = SteelGray,
            fontSize = 18.sp
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // First Name
            Column(modifier = Modifier.weight(1f)) {
                AppInputTextField(
                    value = editProfileUiState?.height ?: "",
                    onValueChange = { event(EditProfileUiEvent.HeightValueChange(it)) },
                    title = "Height",
                    isTitleVisible = true,
                    isLeadingIconVisible = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next,
                        capitalization = KeyboardCapitalization.Words
                    ),
                    header = "Enter your height"
                )
            }

            // Last Name
            Column(modifier = Modifier.weight(1f)) {
                AppInputTextField(
                    value = editProfileUiState?.weight ?: "",
                    onValueChange = { event(EditProfileUiEvent.WeightValueChange(it)) },
                    isLeadingIconVisible = true,
                    isTitleVisible = true,
                    title = "Weight",
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next,
                        capitalization = KeyboardCapitalization.Words
                    ),
                    header = "Enter your weight"
                )
            }
        }
        AppInputTextField(
            value = editProfileUiState?.bmi ?: "",
            onValueChange = { event(EditProfileUiEvent.BmiValueChange(it)) },
            isLeadingIconVisible = true,
            isTitleVisible = true,
            title = "BMI",
            isVerifyButtonVisible = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next,
                capitalization = KeyboardCapitalization.Words
            ),
            header = "Your BMI"
        )
        AppInputTextField(
            value = editProfileUiState?.allergies ?: "",
            onValueChange = { event(EditProfileUiEvent.AllergiesValueChange(it)) },
            isTitleVisible = true,
            title = "Allergies",
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next,
                capitalization = KeyboardCapitalization.Words
            ),
            header = "Enter your allergies"
        )
        AppInputTextField(
            value = editProfileUiState?.medicalConditions ?: "",
            onValueChange = { event(EditProfileUiEvent.MedicalConditionsValueChange(it)) },
            isTitleVisible = true,
            title = "Medical Conditions",
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next,
                capitalization = KeyboardCapitalization.Words
            ),
            header = "Enter your medical conditions"
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(top = 20.dp, bottom = 50.dp)
        ) {
            AppButtonComponent(
                onClick = {
                    event(EditProfileUiEvent.BackClick)

                },
                modifier = Modifier.weight(1f),
                textColor = RedE4,
                borderColors = RedF8,
                drawableResId = R.drawable.ic_close,
                backgroundBrush = Brush.linearGradient(
                    colors = listOf(
                        White,
                        White
                    )
                ),
                text = "Cancel",
            )
            AppButtonComponent(
                onClick = {
                    event(EditProfileUiEvent.UpdateClick)
                },
                modifier = Modifier.weight(1f),
                text = "Update",
                isLoading = editProfileUiState?.showLoader == true
            )
            if (showDatePickerDialog) {
                DatePickerWithDialog(
                    onSelectedDate = selectedDateMillis,
                    onDateSelected = { dateString ->
                        event(EditProfileUiEvent.OnClickOfDate(dateString))
                        showDatePickerDialog = false
                    },
                    onDismiss = {
                        showDatePickerDialog = false
                    },
                    onDateSelectedLong = {
                        selectedDateMillis = it
                    }
                )
            }
        }
    }
}
/**
 * Converts display date format (MMMM dd, yyyy) to millis for date picker
 */
private fun convertDisplayDateToMillis(displayDate: String): Long? {
    return try {
        if (displayDate.isBlank()) return null
        
        val inputFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH)
        val date = inputFormat.parse(displayDate)
        date?.time
    } catch (e: Exception) {
        null
    }
}

@Preview
@Composable
fun AboutScreenContentPreview() {
    val uiState = EditProfileUiState()
    EditProfileScreenContent(uiState = uiState, event = uiState.event)
}




