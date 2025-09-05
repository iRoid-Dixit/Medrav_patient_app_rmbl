package com.medrevpatient.mobile.app.ui.compose.common
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medrevpatient.mobile.app.ui.theme.AppThemeColor
import com.medrevpatient.mobile.app.ui.theme.GrayBD
import com.medrevpatient.mobile.app.ui.theme.SteelGray
import com.medrevpatient.mobile.app.ui.theme.White
import com.medrevpatient.mobile.app.ui.theme.nunito_sans_400
import com.medrevpatient.mobile.app.ui.theme.nunito_sans_600
import com.medrevpatient.mobile.app.utils.AppUtils.noRippleClickable
import com.medrevpatient.mobile.app.ux.main.appointment.AppointmentTab

@Composable
fun AppointmentFilterTabs(
    modifier: Modifier = Modifier,
    selectedTab: AppointmentTab,
    onTabSelected: (AppointmentTab) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AppointmentTab.entries.forEach { tab ->
            AppointmentTabItem(
                tab = tab,
                isSelected = selectedTab == tab,
                onClick = { onTabSelected(tab) }
                // 🔹 no weight here → size depends on text
            )
        }
    }
}

@Composable
private fun AppointmentTabItem(
    tab: AppointmentTab,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = White
    val borderColors  = if (isSelected) AppThemeColor else SteelGray.copy(alpha = 0.2f)
    val textColor = if (isSelected) AppThemeColor else GrayBD
    Box(
        modifier = modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(20.dp)
            )
            .border(color = borderColors, width = 1.dp, shape = RoundedCornerShape(20.dp))
            .noRippleClickable { onClick() }
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = when (tab) {
                AppointmentTab.ALL -> "All"
                AppointmentTab.UPCOMING -> "Upcoming"
                AppointmentTab.PAST -> "Past"
            },
            color = textColor,
            fontSize = 14.sp,
            fontFamily = nunito_sans_400,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 15.dp)
        )
    }
}

@Preview
@Composable
fun AppointmentFilterTabsPreview() {
    AppointmentFilterTabs(
        selectedTab = AppointmentTab.ALL,
        onTabSelected = {}
    )
}
