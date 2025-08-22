package com.griotlegacy.mobile.app.ux.main.bottombar

import com.griotlegacy.mobile.app.navigation.NavRoute
import com.griotlegacy.mobile.app.ux.main.home.HomeRoute
import com.griotlegacy.mobile.app.R
import com.griotlegacy.mobile.app.ux.main.search.SearchRoute
import com.griotlegacy.mobile.app.ux.main.setting.SettingRoute
import com.griotlegacy.mobile.app.ux.main.message.MessageRoute


enum class NavBarItem(
    val unselectedIconDrawableId: Int,
    val selectedIconDrawableId: Int,
    val route: NavRoute,

    ) {
    HOME(unselectedIconDrawableId = R.drawable.ic_app_icon, selectedIconDrawableId = R.drawable.ic_app_icon, route = HomeRoute.createRoute()),
    SEARCH(unselectedIconDrawableId = R.drawable.ic_app_icon, selectedIconDrawableId = R.drawable.ic_app_icon, route = SearchRoute.createRoute()),
    MESSAGE(unselectedIconDrawableId = R.drawable.ic_app_icon, selectedIconDrawableId = R.drawable.ic_app_icon, route = MessageRoute.createRoute()),
    SETTING(unselectedIconDrawableId = R.drawable.ic_app_icon, selectedIconDrawableId = R.drawable.ic_app_icon, route = SettingRoute.createRoute());

    companion object {
        fun getNavBarItemRouteMap(): Map<NavBarItem, NavRoute> {
            return entries.associateWith { item -> item.route }

        }
    }
}

