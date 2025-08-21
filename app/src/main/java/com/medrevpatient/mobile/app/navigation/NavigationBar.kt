package com.medrevpatient.mobile.app.navigation

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.medrevpatient.mobile.app.ui.theme.black25
import com.medrevpatient.mobile.app.ui.theme.neonNazar

//Todo Bottom Bar Composable

@Composable
fun NavigationBar(
    navController: NavHostController,
    onNavItemClicked: (NavBarItem) -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = Color.White,
) {

    val currentDestination = navController.currentDestination

    NavigationBar(
        modifier = modifier
            .fillMaxWidth(),
        containerColor = containerColor
    ) {

        NavBarItem.entries.forEach { navBarItem ->

            val isSelected = currentDestination.isSelected(navBarItem)

            NavigationBarItem(
                colors = NavigationBarItemDefaults.colors(indicatorColor = Transparent),
                selected = isSelected,
                onClick = {
                    onNavItemClicked(navBarItem)
                },
                icon = {
                    navBarItem.unselectedIconDrawableId?.let {
                        Surface(
                            modifier = Modifier,
                            color = if (isSelected) black25 else Transparent,
                            shape = RoundedCornerShape(16.dp),

                            ) {
                            Image(
                                painterResource(id = it),
                                contentDescription = "",
                                colorFilter = ColorFilter.tint(if (isSelected) neonNazar else black25),
                                modifier = Modifier.padding(12.dp),
                            )
                        }
                    }
                }
            )
        }
    }
}


@Preview
@Composable
private fun NavigationBarPreview() {
    NavigationBar(navController = rememberNavController(),
        onNavItemClicked = {}
    )
}


private fun NavDestination?.isSelected(navBarItem: NavBarItem): Boolean {
    return this?.hierarchy?.any { it.route == navBarItem.route.value } == true
}


enum class NavBarItem(
    val unselectedIconDrawableId: Int?,
    val selectedIconDrawableId: Int?,
    val route: NavRoute,
    @StringRes val textResId: Int? = null,
) {

    HOME(
        unselectedIconDrawableId = drawable.unselected_home,
        selectedIconDrawableId = null,
        route = com.medrevpatient.mobile.app.navigation.RouteMaker.Home.createRoute(),
        textResId = null
    ),
    PROGRAM_WORKOUT(
        unselectedIconDrawableId = drawable.unselected_workouts_programs,
        selectedIconDrawableId = null,
        route = com.medrevpatient.mobile.app.navigation.RouteMaker.ProgramAndWorkout.createRoute(),
        textResId = null
    ),
    MY_PROGRESS(
        unselectedIconDrawableId = drawable.unselected_progress,
        selectedIconDrawableId = null,
        route = com.medrevpatient.mobile.app.navigation.RouteMaker.MyProgress.createRoute(),
        textResId = null
    ),
    COMMUNITY(
        unselectedIconDrawableId = drawable.unselected_community,
        selectedIconDrawableId = null,
        route = com.medrevpatient.mobile.app.navigation.RouteMaker.Community.createRoute(),
        textResId = null
    );

    companion object {
        fun getNavBarItemRouteMap(): Map<NavBarItem, NavRoute> {
            return entries.associateWith { item -> item.route }
        }
    }
}

