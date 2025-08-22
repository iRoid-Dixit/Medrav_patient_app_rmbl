package com.medrevpatient.mobile.app.ui.compose.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import com.medrevpatient.mobile.app.ui.theme.Black
import com.medrevpatient.mobile.app.ui.theme.White
import com.medrevpatient.mobile.app.ui.theme.WorkSans

@Composable
fun CustomDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    menuItems: List<Pair<String, () -> Unit>>,
    offsetY: Dp = 0.dp,

    ) {
    if (expanded) {
        val offsetYPx = with(LocalDensity.current) { offsetY.roundToPx() }

        Popup(
            alignment = Alignment.TopEnd,
            offset = IntOffset(0, offsetYPx),
            onDismissRequest = onDismissRequest
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = White,
                shadowElevation = 8.dp,
                modifier = Modifier
                    .width(170.dp)
                    .padding(top = 10.dp, bottom = 10.dp) // top & bottom space outside Surface
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 10.dp) //  internal top & bottom padding
                ) {
                    menuItems.forEachIndexed { index, (title, action) ->
                        Text(
                            text = title,
                            fontSize = 16.sp,
                            color = Black,
                            fontFamily = WorkSans,
                            fontWeight = FontWeight.W400,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    action()
                                    onDismissRequest()
                                }
                                .padding(
                                    vertical = 8.dp,
                                    horizontal = 16.dp
                                ) //  reduce item spacing here
                        )

                    }
                }
            }
        }
    }
}






