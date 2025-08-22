package com.griotlegacy.mobile.app.ux.container.addAdvertisement

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
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.W400
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.griotlegacy.mobile.app.R
import com.griotlegacy.mobile.app.data.source.Constants
import com.griotlegacy.mobile.app.navigation.HandleNavigation
import com.griotlegacy.mobile.app.navigation.scaffold.AppScaffold
import com.griotlegacy.mobile.app.ui.compose.common.AppButtonComponent
import com.griotlegacy.mobile.app.ui.compose.common.AppInputTextField
import com.griotlegacy.mobile.app.ui.compose.common.AppInputTextFieldMultipleLine
import com.griotlegacy.mobile.app.ui.compose.common.DateSelectComponent
import com.griotlegacy.mobile.app.ui.compose.common.FutureAndPastDatePickerWithDialog
import com.griotlegacy.mobile.app.ui.compose.common.TopBarComponent
import com.griotlegacy.mobile.app.ui.compose.common.countryCode.CountryCodePickerComponent
import com.griotlegacy.mobile.app.ui.compose.common.dialog.CameraGalleryDialog
import com.griotlegacy.mobile.app.ui.compose.common.dialog.PermissionDialog
import com.griotlegacy.mobile.app.ui.compose.common.loader.CustomLoader
import com.griotlegacy.mobile.app.ui.compose.common.permission.PhotoPickerManager
import com.griotlegacy.mobile.app.ui.theme.AppThemeColor
import com.griotlegacy.mobile.app.ui.theme.White
import com.griotlegacy.mobile.app.ui.theme.WorkSans
@ExperimentalMaterial3Api
@Composable
fun AddAdvertisementScreen(
    navController: NavController,
    viewModel: AddAdvertisementViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState
    val context = LocalContext.current
    val addAdvertisementUiState by uiState.addAdvertisementDataFlow.collectAsStateWithLifecycle()
    uiState.event(AddAdvertisementUiEvent.GetContext(context))
    AppScaffold(
        containerColor = AppThemeColor,
        modifier = Modifier,
        topAppBar = {
            TopBarComponent(
                header = if (addAdvertisementUiState?.screen == Constants.AppScreen.EDIT_ADVERTISEMENT_SCREEN) {
                    stringResource(id = R.string.edit_advertisement)
                } else {
                    stringResource(id = R.string.create)
                },
                isBackVisible = true,
                onClick = {
                    uiState.event(AddAdvertisementUiEvent.BackClick)
                },
            )
        },
        navBarData = null
    ) {
        AddAdvertisementScreenContent(uiState)
    }
    if (addAdvertisementUiState?.showLoader == true) {
        CustomLoader()
    }
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}

@Composable
private fun AddAdvertisementScreenContent(uiState: AddAdvertisementUiState) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val addAdvertisementUiState by uiState.addAdvertisementDataFlow.collectAsStateWithLifecycle()
    Column(
        modifier = Modifier
            .clickable {
                keyboardController?.hide()
            }
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
            .padding(horizontal = 16.dp),
    ) {
        Spacer(modifier = Modifier.padding(8.dp))
        AddAdvertisementInputField(addAdvertisementUiState, uiState)
        UploadImageComponent(uiState, addAdvertisementUiState)
        Spacer(modifier = Modifier.padding(12.dp))
        AppButtonComponent(
            onClick = {
                uiState.event(AddAdvertisementUiEvent.AddAdvertisementClick)
            },
            modifier = Modifier.fillMaxWidth(),
            text = if (addAdvertisementUiState?.screen == Constants.AppScreen.EDIT_ADVERTISEMENT_SCREEN) stringResource(R.string.update) else stringResource(R.string.next),
        )
        Spacer(modifier = Modifier.padding(25.dp))
    }
}
@Composable
fun UploadImageComponent(
    uiState: AddAdvertisementUiState,
    addAdvertisementUiState: AddAdvertisementDataState?
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
                uiState.event(AddAdvertisementUiEvent.ProfileValueChange(photoPath))
            },
            onError = {
                uiState.event(AddAdvertisementUiEvent.ShowPermissionDialog(true))
            },
            onCameraErrorClick = {
                uiState.event(AddAdvertisementUiEvent.ShowDialog(false))
            }
        )
    }
    Column {
        Spacer(modifier = Modifier.padding(10.dp))
        Text(
            text = stringResource(R.string.upload_image),
            fontSize = 16.sp,
            fontFamily = WorkSans,
            fontWeight = W400,
            color = White
        )
        Spacer(modifier = Modifier.padding(5.dp))
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    uiState.event(AddAdvertisementUiEvent.ShowDialog(true))
                }
                .height(120.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                // Dashed border using Canvas

                Canvas(modifier = Modifier.matchParentSize()) {
                    val strokeWidth = 1.dp.toPx()
                    val pathEffect = PathEffect.dashPathEffect(floatArrayOf(30f, 20f), 5f)
                    drawRoundRect(
                        color = Color.White,
                        size = size,
                        style = Stroke(width = strokeWidth, pathEffect = pathEffect),
                        cornerRadius = CornerRadius(10.dp.toPx())
                    )
                }
                // Your surface inside the dashed border
                Surface(
                    color = AppThemeColor,
                    onClick = {
                        uiState.event(AddAdvertisementUiEvent.ShowDialog(true))
                    },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (addAdvertisementUiState?.photo?.isNotEmpty() == true) {
                        AsyncImage(
                            model = addAdvertisementUiState.photo,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Column(
                            verticalArrangement = Arrangement.Center,

                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable {
                                    uiState.event(AddAdvertisementUiEvent.ShowDialog(true))
                                }
                        ) {
                            Text(
                                text = stringResource(R.string.upload_photo_banner),
                                fontWeight = W400,
                                color = White,
                                fontSize = 12.sp,
                                fontFamily = WorkSans
                            )
                        }
                    }
                }

            }

        }
        if (addAdvertisementUiState?.photoErrorMsg?.isNotEmpty() == true) {
            Text(
                text = addAdvertisementUiState.photoErrorMsg,
                color = MaterialTheme.colorScheme.error,
                fontFamily = WorkSans,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                modifier = Modifier.padding(start = 16.dp, top = 10.dp)
            )
        }
    }
    if (addAdvertisementUiState?.showDialog == true) {
        CameraGalleryDialog(
            onDismissRequest = {
                uiState.event(AddAdvertisementUiEvent.ShowDialog(false))
                uiState.event(AddAdvertisementUiEvent.ShowPermissionDialog(false))
            },
            galleryText = stringResource(id = R.string.choose_from_gallery),
            cameraText = stringResource(id = R.string.take_photo),
            onGalleryClick = {
                uiState.event(AddAdvertisementUiEvent.ShowDialog(false))
                photoPickerManager.pickPhotoFromGallery()
                uiState.event(AddAdvertisementUiEvent.ShowPermissionDialog(false))
            },
            onCameraClick = {
                uiState.event(AddAdvertisementUiEvent.ShowDialog(false))
                photoPickerManager.capturePhoto()
                uiState.event(AddAdvertisementUiEvent.ShowPermissionDialog(false))
            }
        )
    }
    if (addAdvertisementUiState?.showPermissionDialog == true) {
        PermissionDialog(
            onDismissRequest = { uiState.event(AddAdvertisementUiEvent.ShowPermissionDialog(false)) },
            title = stringResource(R.string.griot_legacy_app),
            description = stringResource(R.string.allow_griot_legacy_app_to_access_your_storage_and_camera_while_you_are_using_the_app),
            negativeText = stringResource(id = R.string.cancel),
            positiveText = stringResource(R.string.open_setting),
            onPositiveClick = {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uriIntent = Uri.fromParts("package", context.packageName, null)
                intent.data = uriIntent
                startForCameraPermissionResult.launch(intent)
                uiState.event(AddAdvertisementUiEvent.ShowPermissionDialog(false))
            },
        )
    }
}
@Composable
fun AddAdvertisementInputField(
    addAdvertisementUiState: AddAdvertisementDataState?,
    uiState: AddAdvertisementUiState
) {
    var startDatePickerDialog by remember { mutableStateOf(false) }
    var endDatePickerDialog by remember { mutableStateOf(false) }
    var temp by rememberSaveable { mutableStateOf<Long?>(null) }
    Column(verticalArrangement = Arrangement.spacedBy(22.dp)) {
        if (addAdvertisementUiState?.screen == Constants.AppScreen.EDIT_ADVERTISEMENT_SCREEN) {
            Text(
                text = "Note: ${addAdvertisementUiState.rejectReason}",
                fontSize = 14.sp,
                fontFamily = WorkSans,
                lineHeight = 18.sp,
                fontWeight = W400,
                color = White,
            )
        }
        AppInputTextField(
            value = addAdvertisementUiState?.companyName ?: "",
            onValueChange = { uiState.event(AddAdvertisementUiEvent.CompanyNameValueChange(it)) },
            isLeadingIconVisible = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
                capitalization = KeyboardCapitalization.Sentences,
            ),
            errorMessage = addAdvertisementUiState?.companyNameErrorMsg,
            header = stringResource(R.string.company_name),
            leadingIcon = R.drawable.ic_app_icon,
        )

        AppInputTextField(
            value = addAdvertisementUiState?.contactPerson ?: "",
            onValueChange = { uiState.event(AddAdvertisementUiEvent.ContactPersonValueChange(it)) },
            isLeadingIconVisible = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.NumberPassword,
                imeAction = ImeAction.Next
            ),
            errorMessage = addAdvertisementUiState?.contactPersonErrorMsg,
            header = stringResource(R.string.contact_person),
            leadingIcon = R.drawable.ic_app_icon,
        )
        AppInputTextField(
            value = addAdvertisementUiState?.email ?: "",
            onValueChange = { uiState.event(AddAdvertisementUiEvent.EmailValueChange(it)) },
            isLeadingIconVisible = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            errorMessage = addAdvertisementUiState?.emailErrorMsg,
            header = stringResource(R.string.email_address),
            leadingIcon = R.drawable.ic_app_icon,
        )
        CountryCodePickerComponent(
            value = addAdvertisementUiState?.mobileNumber ?: "",
            onValueChange = { newValue ->
                val filteredValue = newValue.filter { it.isDigit() }
                uiState.event(AddAdvertisementUiEvent.MobileNumberValueChange(filteredValue))
            },
            isLeadingIconVisible = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.NumberPassword,
                imeAction = ImeAction.Next
            ),
            errorMessage = addAdvertisementUiState?.mobileNumberErrorMsg,
            header = stringResource(R.string.mobile_number),
            setCountryCode = addAdvertisementUiState?.defaultCountryCode
        )
        AppInputTextField(
            value = addAdvertisementUiState?.physicalAddress ?: "",
            onValueChange = { uiState.event(AddAdvertisementUiEvent.PhysicalAddressValueChange(it)) },
            isLeadingIconVisible = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
                capitalization = KeyboardCapitalization.Sentences,
            ),
            errorMessage = addAdvertisementUiState?.physicalAddressErrorMsg,
            header = stringResource(R.string.physical_address),
            leadingIcon = R.drawable.ic_app_icon,
        )
        AppInputTextField(
            value = addAdvertisementUiState?.purposeAdvertisement ?: "",
            onValueChange = {
                uiState.event(
                    AddAdvertisementUiEvent.PurposeAdvertisementValueChange(
                        it
                    )
                )
            },
            isLeadingIconVisible = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
                capitalization = KeyboardCapitalization.Sentences,
            ),
            errorMessage = addAdvertisementUiState?.purposeAdvertisementErrorMsg,
            header = stringResource(R.string.purpose_of_advertisement),
            leadingIcon = R.drawable.ic_app_icon,
        )
        AppInputTextFieldMultipleLine(
            value = addAdvertisementUiState?.description ?: "",
            errorMessage = addAdvertisementUiState?.descriptionErrorMsg,
            height = 130,
            isLeadingIconVisible = true,
            leadingIcon = R.drawable.ic_app_icon,
            onValueChange = { uiState.event(AddAdvertisementUiEvent.DescriptionValueChange(it)) },
            header = stringResource(R.string.description),
        )
        AppInputTextField(
            value = addAdvertisementUiState?.link ?: "",
            onValueChange = { uiState.event(AddAdvertisementUiEvent.LinkValueChange(it)) },
            isLeadingIconVisible = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
            ),
            errorMessage = addAdvertisementUiState?.linkErrorMsg,
            header = stringResource(R.string.link),
            leadingIcon = R.drawable.ic_app_icon,
        )
        AppInputTextField(
            value = addAdvertisementUiState?.title ?: "",
            onValueChange = { uiState.event(AddAdvertisementUiEvent.TitleValueChange(it)) },
            isLeadingIconVisible = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Done,
                capitalization = KeyboardCapitalization.Sentences,
            ),
            errorMessage = addAdvertisementUiState?.titleErrorMsg,
            header = stringResource(R.string.title),
            leadingIcon = R.drawable.ic_app_icon,
        )
        DateSelectComponent(
            value = addAdvertisementUiState?.startDate ?: "",
            header = stringResource(R.string.start_date),
            trailingIcon = R.drawable.ic_app_icon,
            errorMessage = addAdvertisementUiState?.startDateErrorMsg,
            onClick = {
                startDatePickerDialog = true
            },
        )
        DateSelectComponent(
            value = addAdvertisementUiState?.endDate ?: "",
            header = stringResource(R.string.end_date),
            trailingIcon = R.drawable.ic_app_icon,
            errorMessage = addAdvertisementUiState?.endDateErrorMsg,
            onClick = {
                endDatePickerDialog = true
            },
        )
    }
    if (startDatePickerDialog) {
        FutureAndPastDatePickerWithDialog(
            onSelectedDate = temp,
            onDateSelected = { dateString ->
                uiState.event(AddAdvertisementUiEvent.OnClickOfStartDate(dateString))
                Log.d("TAG", "RegisterInputField: $dateString")
            },
            onDismiss = {
                startDatePickerDialog = false
            },
            onDateSelectedLong = {
                temp = it
            }
        )
    }
    if (endDatePickerDialog) {
        FutureAndPastDatePickerWithDialog(
            onSelectedDate = temp,
            onDateSelected = { dateString ->
                uiState.event(AddAdvertisementUiEvent.OnClickOfEndDate(dateString))
                Log.d("TAG", "RegisterInputField: $dateString")
            },
            onDismiss = {
                endDatePickerDialog = false
            },
            onDateSelectedLong = {
                temp = it
            }
        )
    }

}








