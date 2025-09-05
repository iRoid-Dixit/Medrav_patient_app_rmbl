package com.medrevpatient.mobile.app.ux.container.bookAppointmen

import androidx.navigation.NamedNavArgument
import com.medrevpatient.mobile.app.navigation.NavComposeRoute
import com.medrevpatient.mobile.app.navigation.NavRoute
import com.medrevpatient.mobile.app.navigation.NavRouteDefinition
import com.medrevpatient.mobile.app.navigation.asNavRoute
import com.medrevpatient.mobile.app.navigation.asNavRouteDefinition

object BookAppointmentRoute : NavComposeRoute() {
    private const val ROUTE_BASE = "bookAppointment"
    override val routeDefinition: NavRouteDefinition = ROUTE_BASE.asNavRouteDefinition()

    fun createRoute(): NavRoute {
        return ROUTE_BASE.asNavRoute()
    }

    override fun getArguments(): List<NamedNavArgument> {
        return emptyList()
    }
}
