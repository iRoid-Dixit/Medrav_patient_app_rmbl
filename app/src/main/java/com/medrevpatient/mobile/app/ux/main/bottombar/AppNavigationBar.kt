package com.medrevpatient.mobile.app.ux.main.bottombar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.NavigationBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import com.medrevpatient.mobile.app.ui.theme.White
import com.medrevpatient.mobile.app.ux.main.home.HomeRoute
@Composable
fun AppNavigationBar(
    currDestination: NavDestination?,
    onNavItemClicked: (NavBarItem) -> Unit,
    ) {
    Column {
        NavigationBar(
            containerColor = White,
            modifier = Modifier
                .graphicsLayer {
                    shadowElevation = 12.dp.toPx()
                    shape = RoundedCornerShape(topStart = 35.dp, topEnd = 35.dp)
                    clip = true
                }
                .background(
                    color = White,
                    shape = RoundedCornerShape(35.dp)
                )
        ) {
            NavBarItem.entries.forEach { item ->
                val isSelected = isSelected(currDestination, item)
                AppBottomNavigationItem(
                    navBarItem = item,
                    unselectedIconDrawableId = item.unselectedIconDrawableId,
                    selectedIconDrawableId = item.selectedIconDrawableId,
                    selectedBarItem = isSelected,
                    textResId = item.textResId,
                ) { onNavItemClicked(it) }
            }
        }
    }
}
private fun isSelected(currDestination: NavDestination?, navBarItem: NavBarItem): Boolean {
    return if (navBarItem == NavBarItem.HOME) {
        currDestination?.hierarchy?.any {
            it.route in setOf(
                HomeRoute.routeDefinition.value,

                )
        } == true
    } else {
        currDestination?.hierarchy?.any { it.route == navBarItem.route.value } == true
    }
}