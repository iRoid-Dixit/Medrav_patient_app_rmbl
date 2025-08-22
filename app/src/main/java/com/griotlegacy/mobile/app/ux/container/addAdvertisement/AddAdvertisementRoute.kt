package com.griotlegacy.mobile.app.ux.container.addAdvertisement

import android.net.Uri
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.griotlegacy.mobile.app.navigation.NavComposeRoute
import com.griotlegacy.mobile.app.navigation.NavRoute
import com.griotlegacy.mobile.app.navigation.NavRouteDefinition
import com.griotlegacy.mobile.app.navigation.asNavRoute
import com.griotlegacy.mobile.app.navigation.asNavRouteDefinition
import com.griotlegacy.mobile.app.utils.RouteUtil

object AddAdvertisementRoute : NavComposeRoute() {
    private const val ROUTE_BASE = "AddAdvertisement"
    override val routeDefinition: NavRouteDefinition =
        "${ROUTE_BASE}/${RouteUtil.defineArg(Arg.SCREEN)}/${RouteUtil.defineArg(Arg.ADVERTISEMENT_DATA)}".asNavRouteDefinition()

    fun createRoute(screen: String, advertisementData: String?): NavRoute {
        val encodedScreen = Uri.encode(screen)
        val encodedMessageData = Uri.encode(advertisementData)
        return "${ROUTE_BASE}/$encodedScreen/$encodedMessageData".asNavRoute()
    }
    override fun getArguments(): List<NamedNavArgument> {
        return listOf(
            navArgument(Arg.SCREEN) { type = NavType.StringType },
            navArgument(Arg.ADVERTISEMENT_DATA) { type = NavType.StringType },
        )
    }
    object Arg {
        const val SCREEN = "screen"
        const val ADVERTISEMENT_DATA = "advertisementData"
    }
}