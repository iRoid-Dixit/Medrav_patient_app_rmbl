package com.medrevpatient.mobile.app.ux.main.myprogress.sheetcontent.component

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Medium
import androidx.compose.ui.text.font.FontWeight.Companion.W300
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.medrevpatient.mobile.app.ui.VStack
import com.medrevpatient.mobile.app.ui.theme.black25
import com.medrevpatient.mobile.app.ui.theme.grey94
import com.medrevpatient.mobile.app.ui.theme.white
import com.medrevpatient.mobile.app.utils.AppUtils.toFile
import com.medrevpatient.mobile.app.ux.main.component.TextFieldFilledWithTrailing
import timber.log.Timber
import java.io.File

@Preview(showBackground = true)
@Composable
fun UploadPhotoComponentPreview() {
    UploadPhotoComponent(
        getImages = emptyList(),
        deleteImage = {},
        pickerFiles = {},
        navigateToCamera = {}
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun UploadPhotoComponent(
    getImages: List<File>,
    deleteImage: (File) -> Unit,
    pickerFiles: (List<File>) -> Unit,
    navigateToCamera: () -> Unit,
    modifier: Modifier = Modifier
) {

    val context = LocalContext.current
    var hasCameraPermission by remember { mutableStateOf(false) }
    var launchImagePickerOption by remember { mutableStateOf(false) }

    // Create a launcher for the permission request
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    val pickMedia =
        rememberLauncherForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(3)) { uris ->
            if (uris.isNotEmpty()) {
                Timber.d("PhotoPickerNumber of items selected: ${uris[0].path}")
                val fileConversion = uris.mapNotNull { it.toFile(context) }
                pickerFiles(fileConversion)
            } else {
                Timber.d("PhotoPicker No media selected")
            }
        }


    // Check if the permission is already granted
    LaunchedEffect(Unit) {
        hasCameraPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    val textStyle =
        MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = black25)

    VStack(
        8.dp, horizontalAlignment = Alignment.Start,
        modifier = modifier.fillMaxWidth()
    ) {

        Text(
            text = "Upload Photos",
            style = textStyle.copy(
                fontWeight = FontWeight.SemiBold, fontSize = 16.sp,
                lineHeight = 1.sp
            ),
        )

        Text(
            text = "Upload your latest 3 pictures (Front, Side & Back)",
            style = textStyle.copy(
                fontSize = 12.sp,
                fontWeight = Medium,
                color = grey94
            ),
        )

        TextFieldFilledWithTrailing(
            value = "",
            onValueChange = {},
            enable = false,
            placeholder = {
                Text(
                    text = "Upload Photo",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = W300,
                    color = black25
                )
            },
            trailingIcon = {
                Icon(
                    ImageVector.vectorResource(drawable.camera),
                    contentDescription = "upload photo",
                    tint = black25
                )
            },
            modifier = Modifier
                .clip(RoundedCornerShape(25))
                .clickable {
                    launchImagePickerOption = !launchImagePickerOption
                }
                .fillMaxWidth()
        )

        Spacer(Modifier.padding(2.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(18.dp),
            maxItemsInEachRow = 3
        ) {
            repeat(getImages.size) { index ->

                val image = BitmapFactory.decodeFile(getImages[index].absolutePath)

                Box(modifier = Modifier.wrapContentSize(), contentAlignment = Alignment.Center) {
                    AsyncImage(
                        model = image,
                        contentDescription = null,
                        placeholder = painterResource(drawable.img_portrait_placeholder_transparent),
                        error = painterResource(drawable.img_portrait_placeholder_transparent),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(height = 116.dp, width = 88.dp)
                            .clip(RoundedCornerShape(25)),
                        onSuccess = {
// todo: reference        it.result.drawable.toBitmap().asImageBitmap()
                        },
                    )

                    IconButton(
                        onClick = {
                            deleteImage(getImages[index])
                        },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 8.dp)
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(drawable.delete_with_bg__filled),
                            contentDescription = "delete",
                            tint = white,
                        )
                    }
                }
            }
        }
    }


    if (launchImagePickerOption) {
        ImageChooserDialog(
            onDismiss = { launchImagePickerOption = !launchImagePickerOption },
            onGallery = {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            },
            onCamera = {
                if (hasCameraPermission) {
                    navigateToCamera()
                } else {
                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                }
            }
        )
    }

}


@Preview
@Composable
private fun DialogPreview() {
    ImageChooserDialog(
        onDismiss = {},
        onGallery = {},
        onCamera = {}
    )
}


@Composable
fun ImageChooserDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onGallery: () -> Unit,
    onCamera: () -> Unit
) {

    Dialog(
        onDismissRequest = onDismiss
    ) {
        VStack(
            spaceBy = 0.dp,
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12))
                .background(black25)
                .padding(horizontal = 9.dp, vertical = 18.dp)
        ) {

            Text(
                text = "Choose an option",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = Medium,
                    color = white
                ),
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            TextButton(
                onClick = {
                    onGallery()
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors().copy(
                    containerColor = Color.Transparent,
                    contentColor = black25
                )
            ) {
                Text(
                    text = "Select from Gallery",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = Medium,
                        color = white
                    )
                )
            }

            TextButton(
                onClick = {
                    onCamera()
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors().copy(
                    containerColor = Color.Transparent,
                    contentColor = black25
                )
            ) {
                Text(
                    text = "Open Camera",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = Medium,
                        color = white
                    ),
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}