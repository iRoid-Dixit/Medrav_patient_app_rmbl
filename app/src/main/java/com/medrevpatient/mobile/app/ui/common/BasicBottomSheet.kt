package com.medrevpatient.mobile.app.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.ui.VStack
import com.medrevpatient.mobile.app.ui.theme.black25
import com.medrevpatient.mobile.app.ui.theme.outFit
import com.medrevpatient.mobile.app.ui.theme.white

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasicBottomSheet(
    sheetState: () -> SheetState,
    onDismissRequest: () -> Unit,
    isSheetVisible: () -> Boolean,
    modifier: Modifier = Modifier,
    windowInsets: WindowInsets = BottomSheetDefaults.windowInsets,
    title: String = "",
    content: @Composable () -> Unit
) {

    val focus = LocalFocusManager.current

    if (isSheetVisible()) {
        focus.clearFocus()
        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            sheetState = sheetState(),
            contentWindowInsets = { windowInsets },
            modifier = modifier.then(Modifier.navigationBarsPadding()),
            containerColor = white,
            dragHandle = null
        ) {
            VStack(spaceBy = 0.dp) {
                Image(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_top_handle),
                    contentDescription = "",
                    modifier = Modifier
                        .padding(top = 5.dp)
                        .align(Alignment.CenterHorizontally)
                )

                if (title.isNotEmpty()) {
                    Text(
                        text = title,
                        fontSize = 20.sp,
                        fontFamily = outFit,
                        fontWeight = Bold,
                        lineHeight = 25.sp,
                        color = black25,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 25.dp)
                    )
                }

                // Sheet content
                content()
            }
        }
    }
}