package com.griotlegacy.mobile.app.ux.main.bottombar
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource


@Composable
fun <T : Enum<T>> RowScope.AppBottomNavigationItem(
    navBarItem: T,
    unselectedIconDrawableId: Int,
    selectedIconDrawableId: Int,
    selectedBarItem: Boolean,
    onNavItemClicked: (T) -> Unit
) {
    NavigationBarItem(
        icon = {
            if (selectedBarItem) {
                Image(
                    painter = painterResource(id = selectedIconDrawableId),
                    contentDescription = null
                )
            } else {
                Image(
                    painter = painterResource(id = unselectedIconDrawableId),
                    contentDescription = null
                )
            }
        },


        selected = false,
        onClick = {
            onNavItemClicked(navBarItem)
        }
    )
}