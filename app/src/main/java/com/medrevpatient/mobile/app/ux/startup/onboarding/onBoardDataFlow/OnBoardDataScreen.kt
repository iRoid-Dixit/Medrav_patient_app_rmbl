package com.medrevpatient.mobile.app.ux.startup.onboarding.onBoardDataFlow


import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.Settings
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.androidisland.ezpermission.EzPermission
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.domain.response.OnBoardSelection
import com.medrevpatient.mobile.app.navigation.HandleNavigation
import com.medrevpatient.mobile.app.ui.DialogLoader
import com.medrevpatient.mobile.app.ui.common.PermissionDialog
import com.medrevpatient.mobile.app.ui.theme.MedrevPatientTheme
import com.medrevpatient.mobile.app.ui.theme.outFit
import com.medrevpatient.mobile.app.ui.theme.white
import com.medrevpatient.mobile.app.ui.theme.white5
import com.medrevpatient.mobile.app.utils.ext.requireActivity
import com.medrevpatient.mobile.app.utils.wheelPickerComponents.WheelDatePicker
import com.medrevpatient.mobile.app.utils.wheelPickerComponents.WheelPickerDefaults
import com.medrevpatient.mobile.app.utils.wheelPickerComponents.WheelTextPicker
import com.medrevpatient.mobile.app.ux.startup.onboarding.OnboardingTemplate
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun OnBoardDataScreen(
    navController: NavController,
    viewModel: OnBoardDataViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
    MedrevPatientTheme {
        WindowCompat.setDecorFitsSystemWindows(LocalContext.current.requireActivity().window, false)
        OnBoardDataScreenContent(uiState, uiState.event)
        if (uiState.isLoading.collectAsStateWithLifecycle().value) {
            DialogLoader()
        }
    }
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}

@Composable
private fun OnBoardDataScreenContent(
    uiState: OnBoardDataUiState,
    event: (OnBoardDataUiEvent) -> Unit
) {
    val currentStep by uiState.currentStepCounter.collectAsStateWithLifecycle()
    OnboardingTemplate(
        title = when (currentStep) {
            0 -> stringResource(id = R.string.upload_picture)
            1 -> stringResource(id = R.string.age)
            2 -> stringResource(id = R.string.height)
            3 -> stringResource(id = R.string.weight)
            // 4 -> stringResource(id = R.string.body_type)
            4 -> stringResource(id = R.string.energy_level)
            // 6 -> stringResource(id = R.string.lifestyle)
            5 -> stringResource(id = R.string.fitness_level)
            6 -> stringResource(id = R.string.goals)
            else -> ""
        },
        subtitle = if (currentStep == 0) stringResource(id = R.string.upload_picture_txt) else stringResource(
            id = R.string.age_text
        ),
        content = {
            when (currentStep) {
                0 -> UploadPictureContent(event, uiState)
                1 -> AgePickerContent(event, uiState)
                2 -> HeightPickerContent(event, uiState)
                3 -> WeightPickerContent(event, uiState)
                //4 -> BodyTypeContent(event, uiState)
                4 -> EnergyLevelContent(event, uiState)
                //6 -> LifestyleContent(event, uiState)
                5 -> FitnessContent(event, uiState)
                6 -> GoalContent(event, uiState)
            }
        },
        onNextClick = {
            uiState.onNextClick()
        },
        onPrevClick = {
            uiState.onPrevClick()
        },
        onSkipClick = {
            uiState.onSkipClick()
        },
        currentStep = currentStep,
        totalSteps = 7,
        nextOrStartText = when (currentStep) {
            5, 6 -> stringResource(id = R.string.start)
            else -> stringResource(id = R.string.next)
        }
    )
}

@SuppressLint("InlinedApi")
@Composable
private fun UploadPictureContent(event: (OnBoardDataUiEvent) -> Unit, uiState: OnBoardDataUiState) {
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
    val showPermissionDialog by uiState.showPermissionDialog.collectAsStateWithLifecycle()
    val profileImage by uiState.profileImage.collectAsStateWithLifecycle()

    var imageUri by remember { mutableStateOf<Uri?>(profileImage) }
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            imageUri = uri
            imageUri?.let { OnBoardDataUiEvent.ProfileImage(it) }?.let { event(it) }
        }
    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            imageUri = bitmap?.let { uriFromBitmap(context, it) }
            imageUri?.let { OnBoardDataUiEvent.ProfileImage(it) }?.let { event(it) }
        }

    val startForCameraPermissionResult =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { _: ActivityResult -> }

    if (showPermissionDialog) {
        PermissionDialog(
            onDismissRequest = { uiState.onShowPermissionDialog(false) },
            title = stringResource(R.string.app_name),
            description = stringResource(R.string.camera_permission_txt),
            negativeText = stringResource(R.string.cancel),
            positiveText = stringResource(R.string.open_setting),
            onPositiveClick = {
                uiState.onShowPermissionDialog(false)
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uriIntent = Uri.fromParts("package", context.packageName, null)
                intent.data = uriIntent
                startForCameraPermissionResult.launch(intent)
            },
        )
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(top = 5.dp)
            .padding(start = 8.dp, end = 8.dp),
        color = Color.Transparent
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (imageUri == null || imageUri?.path == "") {
                Image(
                    painter = painterResource(id = R.drawable.ic_camera),
                    contentDescription = null,
                    modifier = Modifier
                        .size(112.dp)
                        .clickable {
                            permissionCheck(
                                context,
                                android13PermissionList,
                                permissionList,
                                launcher,
                                cameraLauncher,
                                uiState
                            )
                        }
                )
                Text(
                    text = stringResource(id = R.string.upload_picture),
                    fontFamily = outFit,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Light,
                    color = white,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 5.dp)
                )
            } else {
                Box {
                    AsyncImage(
                        model = imageUri.toString(),
                        contentDescription = null,
                        modifier = Modifier
                            .size(112.dp)
                            .clip(CircleShape)
                            .background(white),
                        contentScale = ContentScale.Crop
                    )
                    Image(
                        painter = painterResource(id = R.drawable.ic_camera_white),
                        contentDescription = null,
                        modifier = Modifier
                            .size(36.dp)
                            .align(Alignment.BottomCenter)
                            .offset(y = 15.dp)
                            .clickable {
                                permissionCheck(
                                    context,
                                    android13PermissionList,
                                    permissionList,
                                    launcher,
                                    cameraLauncher,
                                    uiState
                                )
                            }
                    )
                }
                Text(
                    text = stringResource(id = R.string.picture_uploaded),
                    fontFamily = outFit,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Light,
                    color = white,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 25.dp)
                )
            }
        }
    }
}

private fun permissionCheck(
    context: Context,
    android13PermissionList: ArrayList<String>,
    permissionList: ArrayList<String>,
    galleryLauncher: ManagedActivityResultLauncher<String, Uri?>,
    cameraLauncher: ManagedActivityResultLauncher<Void?, Bitmap?>,
    uiState: OnBoardDataUiState
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
                    uiState.onShowPermissionDialog(true)
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
                    uiState.onShowPermissionDialog(true)
                }
            }
    }
}

fun uriFromBitmap(context: Context, bitmap: Bitmap): Uri {
    val bytes = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
    val path = MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "Title", null)
    return Uri.parse(path)
}

fun showImagePickerOptions(
    context: Context,
    galleryLauncher: ActivityResultLauncher<String>,
    cameraLauncher: ActivityResultLauncher<Void?>
) {
    val options = arrayOf(
        context.getString(R.string.select_from_gallery),
        context.getString(R.string.open_camera)
    )
    AlertDialog.Builder(context)
        .setTitle(context.getString(R.string.choose_an_option))
        .setItems(options) { _, which ->
            when (which) {
                0 -> galleryLauncher.launch("image/*")
                1 -> cameraLauncher.launch(null)
            }
        }
        .create().apply {
            window?.setBackgroundDrawableResource(R.drawable.bg_permissions)
        }
        .show()
}

@Composable
private fun AgePickerContent(event: (OnBoardDataUiEvent) -> Unit, uiState: OnBoardDataUiState) {
    val age by uiState.age.collectAsStateWithLifecycle()
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(top = 5.dp)
            .padding(start = 8.dp, end = 8.dp),
        color = Color.Transparent

    ) {
        WheelDatePicker(
            textStyle = TextStyle(
                fontSize = 16.sp,
                fontFamily = outFit,
                fontWeight = FontWeight.Medium,
                color = Color.White
            ),
            startDate = if (age != "") LocalDate.parse(
                age,
                DateTimeFormatter.ofPattern("yyyy-MM-dd")
            ) else LocalDate.now(),
        ) { snappedDate ->
            event(OnBoardDataUiEvent.AgeValueChange(snappedDate.toString()))
        }
    }
}

@Composable
private fun HeightPickerContent(event: (OnBoardDataUiEvent) -> Unit, uiState: OnBoardDataUiState) {
    val heightInFeet by uiState.height.collectAsStateWithLifecycle()
    val heightInInch by uiState.heightInches.collectAsStateWithLifecycle()
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(top = 5.dp)
            .padding(start = 18.dp, end = 30.dp),
        color = Color.Transparent
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(contentAlignment = Alignment.Center) {
                Surface(
                    modifier = Modifier
                        .size((256 / 3).dp, (228 / 5).dp),
                    shape = RoundedCornerShape(15.dp),
                    color = Color.Transparent,
                    border = BorderStroke(1.dp, Color(0xFFFFFFFF)),
                    content = {}
                )
                WheelTextPicker(
                    size = DpSize(
                        width = (256 / 3).dp,
                        height = 210.dp
                    ),
                    texts = listOf("1", "2", "3", "4", "5", "6", "7", "8"),
                    rowCount = 5,
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontFamily = outFit,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    ),
                    color = Color.White,
                    selectorProperties = WheelPickerDefaults.selectorProperties(
                        enabled = false
                    ),
                    startIndex = if (heightInFeet != -1) heightInFeet.minus(1) else 0,
                    onScrollFinished = { snappedIndex ->
                        event(OnBoardDataUiEvent.HeightValueChange(snappedIndex.plus(1)))
                        return@WheelTextPicker snappedIndex

                    }
                )
            }
            Text(
                text = stringResource(R.string.feet),
                fontFamily = outFit,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = white
            )
            Box(contentAlignment = Alignment.Center) {
                Surface(
                    modifier = Modifier
                        .size((256 / 3).dp, (228 / 5).dp),
                    shape = RoundedCornerShape(15.dp),
                    color = Color.Transparent,
                    border = BorderStroke(1.dp, Color(0xFFFFFFFF)),
                    content = {}
                )
                WheelTextPicker(
                    size = DpSize(
                        width = (256 / 3).dp,
                        height = 210.dp
                    ),
                    texts = listOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11"),
                    rowCount = 5,
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontFamily = outFit,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    ),
                    color = Color.White,
                    selectorProperties = WheelPickerDefaults.selectorProperties(
                        enabled = false
                    ),
                    startIndex = if (heightInInch != -1) heightInInch else 0,
                    onScrollFinished = { snappedIndex ->
                        event(OnBoardDataUiEvent.HeightInchValueChange(snappedIndex))
                        return@WheelTextPicker snappedIndex
                    }
                )
            }
            Text(
                text = stringResource(R.string.inch),
                fontFamily = outFit,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = white
            )
        }

    }

}

@Composable
private fun WeightPickerContent(event: (OnBoardDataUiEvent) -> Unit, uiState: OnBoardDataUiState) {
    val weight by uiState.weight.collectAsStateWithLifecycle()
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(top = 5.dp)
            .padding(horizontal = 18.dp),
        color = Color.Transparent
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.padding(horizontal = 30.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Surface(
                    modifier = Modifier
                        .size((256 / 3).dp, (228 / 5).dp),
                    shape = RoundedCornerShape(15.dp),
                    color = Color.Transparent,
                    border = BorderStroke(1.dp, Color(0xFFFFFFFF)),
                    content = {}
                )
                val weightArray = arrayListOf(0..657).flatten().map { it.toString() }
                WheelTextPicker(
                    size = DpSize(
                        width = (256 / 3).dp,
                        height = 210.dp
                    ),
                    texts = weightArray,
                    rowCount = 5,
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontFamily = outFit,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    ),
                    color = Color.White,
                    selectorProperties = WheelPickerDefaults.selectorProperties(
                        enabled = false
                    ),
                    startIndex = if (weight != -1) weight else 0,
                    onScrollFinished = { snappedIndex ->
                        event(OnBoardDataUiEvent.WeightValueChange(snappedIndex))
                        return@WheelTextPicker snappedIndex
                    }
                )
            }
            Text(
                text = stringResource(R.string.lbs),
                fontFamily = outFit,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = white
            )
        }

    }

}

@Composable
private fun BodyTypeContent(event: (OnBoardDataUiEvent) -> Unit, uiState: OnBoardDataUiState) {
    val bodyType by uiState.bodyType.collectAsStateWithLifecycle()
    val arr = stringArrayResource(id = R.array.body_type_array).map { OnBoardSelection(data = it) }
    val array = remember { arr }
    val selectedIndex = remember { mutableIntStateOf(-1) } // Track selected index
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(top = 25.dp),
        color = Color.Transparent
    ) {
        LazyColumn {
            itemsIndexed(array) { index, it ->
                RowCommonSelection(data = it,
                    isSelected = selectedIndex.intValue == index || (bodyType != -1 && bodyType.minus(
                        1
                    ) == index),
                    onSelect = {
                        selectedIndex.intValue = index
                        event(OnBoardDataUiEvent.BodyTypeValueChange(index.plus(1)))
                    })
            }
        }

    }
}

@Composable
private fun EnergyLevelContent(event: (OnBoardDataUiEvent) -> Unit, uiState: OnBoardDataUiState) {
    val energyLevel by uiState.energyLevel.collectAsStateWithLifecycle()
    val arr =
        stringArrayResource(id = R.array.energy_level_array).map { OnBoardSelection(data = it) }
    val array = remember { arr }
    val selectedIndex = remember { mutableIntStateOf(-1) } // Track selected index
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(top = 25.dp),
        color = Color.Transparent
    ) {
        LazyColumn {
            itemsIndexed(array) { index, it ->
                RowCommonSelection(data = it,
                    isSelected = selectedIndex.intValue == index || (energyLevel != -1 && energyLevel.minus(
                        1
                    ) == index),
                    onSelect = {
                        selectedIndex.intValue = index
                        event(OnBoardDataUiEvent.EnergyLevelValueChange(index.plus(1)))
                    })
            }
        }
    }
}

@Composable
private fun LifestyleContent(event: (OnBoardDataUiEvent) -> Unit, uiState: OnBoardDataUiState) {
    val lifestyle by uiState.lifeStyle.collectAsStateWithLifecycle()
    val arr = stringArrayResource(id = R.array.lifestyle_array).map { OnBoardSelection(data = it) }
    val array = remember { arr }
    val selectedIndex = remember { mutableIntStateOf(-1) } // Track selected index
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(top = 25.dp),
        color = Color.Transparent
    ) {
        LazyColumn {
            itemsIndexed(array) { index, it ->
                RowCommonSelection(data = it,
                    isSelected = selectedIndex.intValue == index || (lifestyle != -1 && lifestyle.minus(
                        1
                    ) == index),
                    onSelect = {
                        selectedIndex.intValue = index
                        event(OnBoardDataUiEvent.LifeStyleValueChange(index.plus(1)))
                    })
            }
        }

    }
}

@Composable
private fun FitnessContent(event: (OnBoardDataUiEvent) -> Unit, uiState: OnBoardDataUiState) {
    val fitnessLevel by uiState.fitnessLevel.collectAsStateWithLifecycle()
    val arr =
        stringArrayResource(id = R.array.fitness_level_array).map { OnBoardSelection(data = it) }
    val array = remember { arr }
    val selectedIndex = remember { mutableIntStateOf(-1) } // Track selected index
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(top = 25.dp),
        color = Color.Transparent
    ) {
        LazyColumn {
            itemsIndexed(array) { index, it ->
                RowCommonSelection(data = it,
                    isSelected = selectedIndex.intValue == index || (fitnessLevel != -1 && fitnessLevel.minus(
                        1
                    ) == index),
                    onSelect = {
                        selectedIndex.intValue = index
                        event(OnBoardDataUiEvent.FitnessLevelValueChange(index.plus(1)))
                    })
            }
        }

    }
}

@Composable
private fun GoalContent(event: (OnBoardDataUiEvent) -> Unit, uiState: OnBoardDataUiState) {
    val goals by uiState.goals.collectAsStateWithLifecycle()
    val arr = stringArrayResource(id = R.array.goals_array).map { OnBoardSelection(data = it) }
    val array = remember { arr }
    val selectedIndex = remember { mutableIntStateOf(-1) } // Track selected index
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(top = 25.dp),
        color = Color.Transparent
    ) {
        LazyColumn {
            itemsIndexed(array) { index, it ->
                RowCommonSelection(
                    data = it,
                    isSelected = selectedIndex.intValue == index || (goals != -1 && goals.minus(1) == index),
                    onSelect = {
                        selectedIndex.intValue = index
                        event(OnBoardDataUiEvent.GoalsValueChange(index.plus(1)))
                    }
                )
            }
        }
    }
}

@Composable
fun RowCommonSelection(
    data: OnBoardSelection,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val colorStopBottom = arrayOf(
        0.0f to Color.Transparent,
        0.3f to Color.Transparent,
        0.5f to white
    )
    val colorStop = arrayOf(
        0.0f to white,
        0.0f to white,
        0.13f to Color.Transparent
    )
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
            .clickable {
                onSelect()
            },
        shape = RoundedCornerShape(18.dp),
        border = if (isSelected) BorderStroke(
            1.dp,
            Brush.linearGradient(
                colorStops = colorStop,
                start = Offset(0f, 0f),
                end = Offset(300f, 900f)
            )
        ) else null,
        color = white5
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    if (isSelected) {
                        BorderStroke(
                            1.dp,
                            Brush.linearGradient(
                                colorStops = colorStopBottom,
                                start = Offset(0f, 0f),
                                end = Offset(300f, 900f)
                            )
                        )
                    } else BorderStroke(0.dp, Color.Transparent),
                    shape = RoundedCornerShape(18.dp)
                )
        ) {
            Text(
                text = data.data,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = Color.White,
                fontFamily = outFit,
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 15.dp)
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    Surface {
        OnBoardDataScreenContent(uiState = OnBoardDataUiState(), event = {})
    }
}