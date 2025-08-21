package com.medrevpatient.mobile.app.ux.main.myprogress.sheetcontent.component

import android.graphics.BitmapFactory
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.ui.TopBarCenterAlignTextAndBack
import com.medrevpatient.mobile.app.utils.alias.drawable
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun WeightCameraScreen(
    onImageCaptured: (List<File>) -> Unit,
    onBackPress: () -> Unit,
    modifier: Modifier = Modifier,
) {

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    val capturedImages = remember { mutableStateListOf<File>() }
    val previewView = remember { PreviewView(context) }

    LaunchedEffect(key1 = Unit) {
        val cameraProvider = cameraProviderFuture.get()
        val preview = androidx.camera.core.Preview.Builder().build().also {
            it.surfaceProvider = previewView.surfaceProvider
        }
        imageCapture = ImageCapture.Builder().build()

        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
            )
        } catch (exc: Exception) {
            exc.printStackTrace()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {

        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize()
        )

        //Captured Images
        Column(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.BottomCenter),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            capturedImages.forEachIndexed { index, file ->
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .align(Alignment.Start)
                ) {
                    val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier.size(100.dp)
                    )
                    IconButton(
                        onClick = {
                            capturedImages.removeAt(index)
                            file.delete()
                        },
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Icon(
                            ImageVector.vectorResource(id = R.drawable.delete_with_bg__filled),
                            contentDescription = "Delete Image",
                            tint = Color.Red
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = CircleShape,
                    onClick = {
                        if (capturedImages.size < 3) {
                            captureImage(imageCapture, context) { file ->
                                capturedImages.add(file)
                            }
                        } else {
                            // Return the list of files
                            // You can handle this as per your requirement
                            onImageCaptured(capturedImages)
                        }
                    },
                    color = Color.LightGray,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(60.dp)
                ) {
                    if (capturedImages.size >= 3) {
                        Icon(
                            imageVector = ImageVector.vectorResource(drawable.ic_checkmark),
                            contentDescription = "Done",
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(4.dp)
                        )
                    }
                }
            }

        }




        TopBarCenterAlignTextAndBack(
            "",
            onBackPress = onBackPress,
            modifier = Modifier.statusBarsPadding().align(Alignment.TopCenter)
        )

    }
}

private fun captureImage(
    imageCapture: ImageCapture?,
    context: android.content.Context,
    onImageCaptured: (File) -> Unit
) {
    val storageDir = File(context.cacheDir, "weighLogsImg")
    if (!storageDir.exists()) storageDir.mkdirs()

    val photoFile = File.createTempFile(
        "IMG_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}",
        ".jpg",
        storageDir
    )

    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    imageCapture?.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                onImageCaptured(photoFile)
            }

            override fun onError(exc: ImageCaptureException) {
                exc.printStackTrace()
            }
        }
    )
}
