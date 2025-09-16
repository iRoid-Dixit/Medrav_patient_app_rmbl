package com.medrevpatient.mobile.app.ui.compose.common

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.ui.theme.AppThemeColor
import com.medrevpatient.mobile.app.ui.theme.Gray94
import com.medrevpatient.mobile.app.ui.theme.White
import com.medrevpatient.mobile.app.ui.theme.nunito_sans_400

data class BottomNavItem(
    val icon: Int,
    val selectedIcon: Int,
    val label: String,
    val isSelected: Boolean = false
)

@Composable
fun AppBottomNavigation(
    modifier: Modifier = Modifier,
    items: List<BottomNavItem>,
    onItemClick: (Int) -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, item ->
                BottomNavItem(
                    item = item,
                    onClick = { onItemClick(index) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun BottomNavItem(
    item: BottomNavItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        val iconRes = if (item.isSelected) item.selectedIcon else item.icon
        val iconColor = if (item.isSelected) AppThemeColor else Gray94
        val textColor = if (item.isSelected) AppThemeColor else Gray94

        androidx.compose.foundation.Image(
            painter = painterResource(id = iconRes),
            contentDescription = item.label,
            modifier = Modifier.size(24.dp),
            colorFilter = ColorFilter.tint(iconColor)
        )

        Text(
            text = item.label,
            fontSize = 10.sp,
            fontFamily = nunito_sans_400,
            color = textColor,
            textAlign = TextAlign.Center
        )
    }
}

@Preview
@Composable
fun AppBottomNavigationPreview() {
    val items = listOf(
        BottomNavItem(
            icon = R.drawable.ic_unselected_home,
            selectedIcon = R.drawable.ic_selected_home,
            label = "Home",
            isSelected = false
        ),
        BottomNavItem(
            icon = R.drawable.ic_unselected_appointments,
            selectedIcon = R.drawable.ic_selected_appointments,
            label = "Appointments",
            isSelected = true
        ),
        BottomNavItem(
            icon = R.drawable.ic_unselected_medication,
            selectedIcon = R.drawable.ic_selected_medication,
            label = "Medication",
            isSelected = false
        ),
        BottomNavItem(
            icon = R.drawable.ic_unselected_message,
            selectedIcon = R.drawable.ic_selected_message,
            label = "Message",
            isSelected = false
        ),
        BottomNavItem(
            icon = R.drawable.ic_unselected_profile,
            selectedIcon = R.drawable.ic_selected_profile,
            label = "Profile",
            isSelected = false
        )
    )

    AppBottomNavigation(
        items = items,
        onItemClick = {}
    )
}
