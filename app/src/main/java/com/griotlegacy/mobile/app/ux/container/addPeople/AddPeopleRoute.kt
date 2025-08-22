package com.griotlegacy.mobile.app.ux.container.addPeople

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.griotlegacy.mobile.app.navigation.NavComposeRoute
import com.griotlegacy.mobile.app.navigation.NavRoute
import com.griotlegacy.mobile.app.navigation.NavRouteDefinition
import com.griotlegacy.mobile.app.navigation.asNavRoute
import com.griotlegacy.mobile.app.navigation.asNavRouteDefinition
import com.griotlegacy.mobile.app.utils.RouteUtil

object AddPeopleRoute : NavComposeRoute(){
    private const val ROUTE_BASE = "addPeople"
    override val routeDefinition: NavRouteDefinition =
        "$ROUTE_BASE/${RouteUtil.defineArg(Arg.TRIBE_ID)}/${RouteUtil.defineArg(Arg.GROUP_ID)}".asNavRouteDefinition()

    fun createRoute(tribeId: String, groupId: String): NavRoute {
        return "${ROUTE_BASE}/$tribeId/$groupId".asNavRoute()
    }

    override fun getArguments(): List<NamedNavArgument> {
        return listOf(
            navArgument(Arg.TRIBE_ID) { type=NavType.StringType},
            navArgument(Arg.GROUP_ID) { type = NavType.StringType },
        )
    }
    object Arg {
        const val TRIBE_ID = "tribeId"
        const val GROUP_ID = "groupId"
    }
}


