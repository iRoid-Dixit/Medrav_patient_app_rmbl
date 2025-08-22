package com.griotlegacy.mobile.app.ux.startup.auth.login

import androidx.navigation.NamedNavArgument
import com.griotlegacy.mobile.app.navigation.NavComposeRoute
import com.griotlegacy.mobile.app.navigation.NavRoute
import com.griotlegacy.mobile.app.navigation.NavRouteDefinition
import com.griotlegacy.mobile.app.navigation.asNavRoute
import com.griotlegacy.mobile.app.navigation.asNavRouteDefinition

object LoginRoute : NavComposeRoute(){
    private const val ROUTE_BASE = "login"
    override val routeDefinition: NavRouteDefinition = ROUTE_BASE.asNavRouteDefinition()

    fun createRoute() : NavRoute {
       return ROUTE_BASE.asNavRoute()
    }

    override fun getArguments(): List<NamedNavArgument> {
        return emptyList()
    }
}