package com.medrevpatient.mobile.app.ux.main.bottombar
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.ui.theme.Black14
import com.medrevpatient.mobile.app.ux.main.BottomNavCurve
import com.medrevpatient.mobile.app.ux.main.home.HomeRoute

@Composable
fun CustomBottomNavigationBar(
    currDestination: NavDestination?,
    onNavItemClicked: (NavBarItem) -> Unit,
    floatingButtonClick: () -> Unit = {},
) {

    Box(Modifier.fillMaxWidth()) {
        Surface(
            color = Black14,
            modifier = Modifier
                .fillMaxWidth()
                .height(95.dp)
                .graphicsLayer {
                    clip = true
                    shape = BottomNavCurve()
                    alpha = 1f // Ensure no transparency issues
                }
                .align(Alignment.BottomCenter)
        ){ Spacer(modifier = Modifier.fillMaxSize())}

        // Bottom Navigation Items + FAB inside a single structure
        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            NavigationBar(
                containerColor = Color.Transparent,
                modifier = Modifier.fillMaxWidth()
            ) {
                // First Two Navigation Items
                NavBarItem.entries.take(2).forEach { item -> // First two icons
                    val isSelected = isSelected(currDestination, item)
                    AppBottomNavigationItem(
                        navBarItem = item,
                        unselectedIconDrawableId = item.unselectedIconDrawableId,
                        selectedIconDrawableId = item.selectedIconDrawableId,
                        selectedBarItem = isSelected,
                    ) { onNavItemClicked(it) }
                }

                // Spacer for FAB
                Spacer(modifier = Modifier.weight(1f))

                // Last Two Navigation Items
                NavBarItem.entries.drop(2).forEach { item -> // Last two icons
                    val isSelected = isSelected(currDestination, item)
                    AppBottomNavigationItem(
                        navBarItem = item,
                        unselectedIconDrawableId = item.unselectedIconDrawableId,
                        selectedIconDrawableId = item.selectedIconDrawableId,
                        selectedBarItem = isSelected,
                    ) { onNavItemClicked(it) }
                }
            }

            // Floating Action Button (FAB) inside the Bottom Bar Box
            FloatingActionButton(
                onClick = {
                    floatingButtonClick()
                },
                shape = CircleShape,
                containerColor = Black14, // FAB Background
                modifier = Modifier
                    .align(Alignment.Center) // Ensures it is centered
                    .offset(y = (-20).dp),  // Moves FAB slightly up
                elevation = FloatingActionButtonDefaults.elevation(8.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_app_icon),
                    contentDescription = null,

                )
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