package com.griotlegacy.mobile.app.ux.startup.auth.verifyOtp

import androidx.navigation.NamedNavArgument
import com.griotlegacy.mobile.app.navigation.NavComposeRoute
import com.griotlegacy.mobile.app.navigation.NavRoute
import com.griotlegacy.mobile.app.navigation.NavRouteDefinition
import com.griotlegacy.mobile.app.navigation.asNavRoute
import com.griotlegacy.mobile.app.navigation.asNavRouteDefinition
import com.griotlegacy.mobile.app.utils.RouteUtil
object VerifyOtpRoute : NavComposeRoute() {
    private const val ROUTE_BASE = "VerifyOtp"
    override val routeDefinition: NavRouteDefinition = "${ROUTE_BASE}/${RouteUtil.defineArg(Arg.EMAIL)}/${RouteUtil.defineArg(Arg.SCREEN_NAME)}".asNavRouteDefinition()

    fun createRoute(email: String,screenName:String): NavRoute {
        return "${ROUTE_BASE}/$email/$screenName".asNavRoute()
    }

    override fun getArguments(): List<NamedNavArgument> {
        return emptyList()
    }
    object Arg {
        const val EMAIL = "email"
        const val SCREEN_NAME = "screenName"
    }
}
