package com.medrevpatient.mobile.app.ux.container.groupMember

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.medrevpatient.mobile.app.navigation.NavComposeRoute
import com.medrevpatient.mobile.app.navigation.NavRoute
import com.medrevpatient.mobile.app.navigation.NavRouteDefinition
import com.medrevpatient.mobile.app.navigation.asNavRoute
import com.medrevpatient.mobile.app.navigation.asNavRouteDefinition
import com.medrevpatient.mobile.app.utils.RouteUtil


object GroupMemberRoute : NavComposeRoute() {
    private const val ROUTE_BASE = "GroupMember"
    override val routeDefinition: NavRouteDefinition =
        "$ROUTE_BASE/${RouteUtil.defineArg(Arg.GROUP_ID)}".asNavRouteDefinition()

    fun createRoute(groupId: String): NavRoute {
        return "${ROUTE_BASE}/$groupId".asNavRoute()
    }

    override fun getArguments(): List<NamedNavArgument> {
        return listOf(
            navArgument(Arg.GROUP_ID) { type = NavType.StringType },
        )
    }

    object Arg {
        const val GROUP_ID = "groupId"
    }
}





