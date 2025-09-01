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
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.navigation.HandleNavigation
import com.medrevpatient.mobile.app.navigation.scaffold.AppScaffold
import com.medrevpatient.mobile.app.ui.compose.common.AppButtonComponent
import com.medrevpatient.mobile.app.ui.compose.common.AppInputTextField
import com.medrevpatient.mobile.app.ui.compose.common.BmiTag
import com.medrevpatient.mobile.app.ui.compose.common.DatePickerWithDialog
import com.medrevpatient.mobile.app.ui.compose.common.DateSelectComponent
import com.medrevpatient.mobile.app.ui.compose.common.DropdownField
import com.medrevpatient.mobile.app.ui.compose.common.TopBarComponent
import com.medrevpatient.mobile.app.ui.compose.common.VerifyButton
import com.medrevpatient.mobile.app.ui.compose.common.dialog.CameraGalleryDialog
import com.medrevpatient.mobile.app.ui.compose.common.dialog.PermissionDialog
import com.medrevpatient.mobile.app.ui.compose.common.loader.CustomLoader
import com.medrevpatient.mobile.app.ui.compose.common.permission.PhotoPickerManager
import com.medrevpatient.mobile.app.ui.theme.Mercury
import com.medrevpatient.mobile.app.ui.theme.PurpleHeart
import com.medrevpatient.mobile.app.ui.theme.Scorpion
import com.medrevpatient.mobile.app.ui.theme.SteelGray
import com.medrevpatient.mobile.app.ui.theme.White
import com.medrevpatient.mobile.app.ui.theme.nunito_sans_600

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
    if (changePasswordUiState?.showLoader == true) {
        CustomLoader()
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
            .clickable {
                keyboardController?.hide()
            }
            .fillMaxSize(),
    ) {
        Spacer(modifier = Modifier.size(50.dp))
        ProfileHeader(editProfileUiState, uiState.event)
        Spacer(modifier = Modifier.size(30.dp))
        EditProfileInputField(
            editProfileUiState,
            event
        )
        Spacer(modifier = Modifier.weight(1f))
        ActionButtons(event)
        Spacer(modifier = Modifier.size(20.dp))
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
    var expanded by remember { mutableStateOf(false) }
    var temp by rememberSaveable { mutableStateOf<Long?>(null) }
    var showDatePickerDialog by remember { mutableStateOf(false) }
    
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        // Personal Information Section
        Text(
            text = "Personal Information",
            fontFamily = nunito_sans_600,
            color = SteelGray,
            fontSize = 18.sp
        )
        
        // First Name and Last Name Row
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // First Name
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "First Name",
                    fontFamily = nunito_sans_600,
                    color = SteelGray,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                AppInputTextField(
                    value = editProfileUiState?.firstName ?: "",
                    onValueChange = { event(EditProfileUiEvent.FirstNameValueChange(it)) },
                    isLeadingIconVisible = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next,
                        capitalization = KeyboardCapitalization.Words
                    ),
                    errorMessage = editProfileUiState?.firstNameErrorMsg ?: "",
                    header = "Enter first name"
                )
            }

            // Last Name
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Last Name",
                    fontFamily = nunito_sans_600,
                    color = SteelGray,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                AppInputTextField(
                    value = editProfileUiState?.lastName ?: "",
                    onValueChange = { event(EditProfileUiEvent.LastNameValueChange(it)) },
                    isLeadingIconVisible = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done,
                        capitalization = KeyboardCapitalization.Words
                    ),
                    errorMessage = editProfileUiState?.lastNameErrorMsg ?: "",
                    header = "Enter last name"
                )
            }
        }
        // Email with Verify Button

        Row {
            AppInputTextField(
                value = editProfileUiState?.email ?: "you123@gmail.com",
                onValueChange = { event(EditProfileUiEvent.EmailValueChange(it)) },
                isLeadingIconVisible = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                errorMessage = editProfileUiState?.emailErrorMsg ?: "",
                header = "Email Address",
                modifier = Modifier.weight(1f)
            )

            VerifyButton(
                text = "Verify",
                onClick = { event(EditProfileUiEvent.VerifyEmailClick) }
            )

        }

        // Date of Birth
        Column {
            Text(
                text = "Date of Birth",
                fontFamily = nunito_sans_600,
                color = SteelGray,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            DateSelectComponent(
                value = editProfileUiState?.dateSelected ?: "",
                header = "Enter your DOB",
                errorMessage = editProfileUiState?.dateOfBirthValidationMsg,
                onClick = {
                    showDatePickerDialog = true
                },
            )
        }
        
        // Medical Information Section
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Medical Information",
            fontFamily = nunito_sans_600,
            color = SteelGray,
            fontSize = 18.sp
        )
        
        // Height and Weight Row
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Height
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Height",
                    fontFamily = nunito_sans_600,
                    color = SteelGray,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                AppInputTextField(
                    value = editProfileUiState?.height ?: "",
                    onValueChange = { event(EditProfileUiEvent.HeightValueChange(it)) },
                    isLeadingIconVisible = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    errorMessage = editProfileUiState?.heightErrorMsg ?: "",
                    header = "Enter your height"
                )
            }
            
            // Weight
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Weight",
                    fontFamily = nunito_sans_600,
                    color = SteelGray,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                AppInputTextField(
                    value = editProfileUiState?.weight ?: "",
                    onValueChange = { event(EditProfileUiEvent.WeightValueChange(it)) },
                    isLeadingIconVisible = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    errorMessage = editProfileUiState?.weightErrorMsg ?: "",
                    header = "Enter your weight"
                )
            }
        }
        
        // BMI with Tag
        Column {
            Text(
                text = "BMI",
                fontFamily = nunito_sans_600,
                color = SteelGray,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AppInputTextField(
                    value = editProfileUiState?.bmi ?: "26.7",
                    onValueChange = { },
                    isLeadingIconVisible = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    errorMessage = null,
                    header = "BMI",
                    modifier = Modifier.weight(1f),
                    isReadOnly = true
                )
                
                BmiTag(
                    text = editProfileUiState?.bmiStatus ?: "Overweight"
                )
            }
        }
        
        // Allergies
        Column {
            Text(
                text = "Allergies",
                fontFamily = nunito_sans_600,
                color = SteelGray,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            AppInputTextField(
                value = editProfileUiState?.allergies ?: "",
                onValueChange = { event(EditProfileUiEvent.AllergiesValueChange(it)) },
                isLeadingIconVisible = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next,
                    capitalization = KeyboardCapitalization.Sentences
                ),
                errorMessage = editProfileUiState?.allergiesErrorMsg ?: "",
                header = "Enter your allergies"
            )
        }
        
        // Medical Conditions
        Column {
            Text(
                text = "Medical Conditions",
                fontFamily = nunito_sans_600,
                color = SteelGray,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            AppInputTextField(
                value = editProfileUiState?.medicalConditions ?: "",
                onValueChange = { event(EditProfileUiEvent.MedicalConditionsValueChange(it)) },
                isLeadingIconVisible = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done,
                    capitalization = KeyboardCapitalization.Sentences
                ),
                errorMessage = editProfileUiState?.medicalConditionsErrorMsg ?: "",
                header = "Enter your medical conditions"
            )
        }
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
fun ActionButtons(event: (EditProfileUiEvent) -> Unit) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        // Cancel Button
        AppButtonComponent(
            onClick = { event(EditProfileUiEvent.CancelClick) },
            modifier = Modifier.weight(1f),
            text = "× Cancel",
            textColor = com.medrevpatient.mobile.app.ui.theme.RedF7,
            borderColors = com.medrevpatient.mobile.app.ui.theme.RedF7,
            backgroundBrush = androidx.compose.ui.graphics.Brush.linearGradient(
                colors = listOf(
                    White,
                    White
                )
            ),
            drawableResId = R.drawable.ic_delete
        )
        
        // Update Button
        AppButtonComponent(
            onClick = { event(EditProfileUiEvent.UpdateClick) },
            modifier = Modifier.weight(1f),
            text = "Update",
            textColor = White,
            backgroundBrush = androidx.compose.ui.graphics.Brush.linearGradient(
                colors = listOf(
                    PurpleHeart,
                    PurpleHeart
                )
            )
        )
    }
}

@Preview
@Composable
fun AboutScreenContentPreview() {
    val uiState = EditProfileUiState()
    EditProfileScreenContent(uiState = uiState, event = uiState.event)
}






