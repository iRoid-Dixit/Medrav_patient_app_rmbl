package com.medrevpatient.mobile.app.ux.startup.auth.resetPassword

import androidx.navigation.NamedNavArgument
import com.medrevpatient.mobile.app.navigation.NavComposeRoute
import com.medrevpatient.mobile.app.navigation.NavRoute
import com.medrevpatient.mobile.app.navigation.NavRouteDefinition
import com.medrevpatient.mobile.app.navigation.asNavRoute
import com.medrevpatient.mobile.app.navigation.asNavRouteDefinition
import com.medrevpatient.mobile.app.utils.RouteUtil

object ResetPasswordRoute : NavComposeRoute() {
    private const val ROUTE_BASE = "resetPassword"
    override val routeDefinition: NavRouteDefinition = "${ROUTE_BASE}/${RouteUtil.defineArg(Arg.EMAIL)}".asNavRouteDefinition()

    fun createRoute(email: String): NavRoute {
        return "${ROUTE_BASE}/$email".asNavRoute()
    }

    override fun getArguments(): List<NamedNavArgument> {
        return emptyList()
    }
    object Arg {
        const val EMAIL = "email"

    }
}