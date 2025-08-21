package com.medrevpatient.mobile.app.ux.main.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.ui.theme.ColorSwansDown
import com.medrevpatient.mobile.app.ui.theme.MineShaft
import com.medrevpatient.mobile.app.ui.theme.MineShaft3
import com.medrevpatient.mobile.app.ui.theme.outFit
import com.medrevpatient.mobile.app.ui.theme.white
import com.medrevpatient.mobile.app.utils.AppUtils.noRippleClickable

@Composable
fun SelectionItem(
    title: String,
    isSelected: Boolean,
    width: Int = 100,
    onSelect: () -> Unit
) {
    Text(
        text = title,
        fontFamily = outFit,
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal,
        color = MineShaft,
        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        modifier = Modifier
            .width(if (width <= 50) 70.dp else width.dp)
            .padding(
                end = when (title) {
                    in stringArrayResource(id = R.array.fitness_level_array) -> 8.dp
                    "None" -> 1.dp
                    else -> 10.dp
                }
            )
            .background(
                brush = if (isSelected) Brush.linearGradient(
                    colors = listOf(
                        white,
                        ColorSwansDown.copy(alpha = 0.7f),
                        ColorSwansDown
                    )
                ) else Brush.verticalGradient(
                    colors = listOf(
                        MineShaft3,
                        MineShaft3
                    )
                ),
                shape = RoundedCornerShape(27)
            )
            .padding(12.dp)
            .noRippleClickable { onSelect() }
    )
}