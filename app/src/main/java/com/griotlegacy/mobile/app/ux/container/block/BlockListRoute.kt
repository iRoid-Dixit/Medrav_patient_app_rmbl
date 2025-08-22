package com.griotlegacy.mobile.app.ux.container.block

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.griotlegacy.mobile.app.navigation.NavComposeRoute
import com.griotlegacy.mobile.app.navigation.NavRoute
import com.griotlegacy.mobile.app.navigation.NavRouteDefinition
import com.griotlegacy.mobile.app.navigation.asNavRoute
import com.griotlegacy.mobile.app.navigation.asNavRouteDefinition

object BlockListRoute : NavComposeRoute() {
    private const val ROUTE_BASE = "BlockList"
    override val routeDefinition: NavRouteDefinition =
        "$ROUTE_BASE/{${Arg.TRIBE_ID}}/{${Arg.TRIBE_NAME}}".asNavRouteDefinition()

    fun createRoute(tribeId: String, tribeName: String): NavRoute {
        return "${ROUTE_BASE}/$tribeId/$tribeName".asNavRoute()
    }

    override fun getArguments(): List<NamedNavArgument> {
        return listOf(
            navArgument(Arg.TRIBE_ID) { type = NavType.StringType },
            navArgument(Arg.TRIBE_NAME) { type = NavType.StringType }
        )
    }

    object Arg {
        const val TRIBE_ID = "tribeId"
        const val TRIBE_NAME = "tribeName"
    }
}
