package com.medrevpatient.mobile.app.ux.container.chat

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

//object ChatRoute : SimpleNavComposeRoute("Chat")

object ChatRoute : NavComposeRoute() {
    private const val ROUTE_BASE = "Chat"
    override val routeDefinition: NavRouteDefinition =
        "${ROUTE_BASE}/${RouteUtil.defineArg(Arg.CHAT_DATA)}/${RouteUtil.defineArg(Arg.SCREEN_NAME)}".asNavRouteDefinition()

    fun createRoute(legacyPostData: String, screeName: String): NavRoute {
        val encodedLegacyPostData = Uri.encode(legacyPostData)
        return "${ROUTE_BASE}/$encodedLegacyPostData/$screeName".asNavRoute()
    }

    override fun getArguments(): List<NamedNavArgument> {
        return listOf(
            navArgument(Arg.CHAT_DATA) { type = NavType.StringType },
            navArgument(Arg.SCREEN_NAME) { type = NavType.StringType }
        )
    }

    object Arg {
        const val CHAT_DATA = "chatData"
        const val SCREEN_NAME = "screenName"

    }
}

