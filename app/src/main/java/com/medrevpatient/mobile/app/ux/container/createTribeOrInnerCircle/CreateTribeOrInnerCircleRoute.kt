package com.medrevpatient.mobile.app.ux.container.createTribeOrInnerCircle

import android.net.Uri
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.medrevpatient.mobile.app.navigation.NavComposeRoute
import com.medrevpatient.mobile.app.navigation.NavRoute
import com.medrevpatient.mobile.app.navigation.NavRouteDefinition
import com.medrevpatient.mobile.app.navigation.asNavRoute
import com.medrevpatient.mobile.app.navigation.asNavRouteDefinition
import com.medrevpatient.mobile.app.utils.RouteUtil

object CreateTribeOrInnerCircleRoute : NavComposeRoute() {
    private const val ROUTE_BASE = "CreateTribeOrInnerCircle"
    override val routeDefinition: NavRouteDefinition =
        "${ROUTE_BASE}/${RouteUtil.defineArg(Arg.SCREEN)}/${RouteUtil.defineArg(Arg.MESSAGE_DATA)}".asNavRouteDefinition()

    fun createRoute(screen: String, messageData: String?): NavRoute {
        val encodedMessageData = Uri.encode(messageData)
        return "${ROUTE_BASE}/$screen/$encodedMessageData".asNavRoute()
    }

    override fun getArguments(): List<NamedNavArgument> {
        return listOf(
            navArgument(Arg.SCREEN) { type = NavType.StringType },
            navArgument(Arg.MESSAGE_DATA) { type = NavType.StringType },

            )
    }

    object Arg {
        const val SCREEN = "screen"
        const val MESSAGE_DATA = "messageData"

    }
}

/*object BuildLegacyRoute : NavComposeRoute() {
    private const val ROUTE_BASE = "buildLegacy"
    override val routeDefinition: NavRouteDefinition =
        "${ROUTE_BASE}/${RouteUtil.defineArg(Arg.LEGACY_POST_DATA)}/${RouteUtil.defineArg(Arg.SCREEN_NAME)}".asNavRouteDefinition()

    fun createRoute(legacyPostData: String, screeName: String): NavRoute {
        val encodedLegacyPostData = Uri.encode(legacyPostData)
        return "${ROUTE_BASE}/$encodedLegacyPostData/$screeName".asNavRoute()
    }

    override fun getArguments(): List<NamedNavArgument> {
        return listOf(
            navArgument(Arg.LEGACY_POST_DATA) { type = NavType.StringType },
            navArgument(Arg.SCREEN_NAME) { type = NavType.StringType }
        )
    }

    object Arg {
        const val LEGACY_POST_DATA = "legacyPostData"
        const val SCREEN_NAME = "screenName"

    }
}*/
