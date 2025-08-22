package com.medrevpatient.mobile.app.ux.startup.auth.verifyOtp

import androidx.navigation.NamedNavArgument
import com.medrevpatient.mobile.app.navigation.NavComposeRoute
import com.medrevpatient.mobile.app.navigation.NavRoute
import com.medrevpatient.mobile.app.navigation.NavRouteDefinition
import com.medrevpatient.mobile.app.navigation.asNavRoute
import com.medrevpatient.mobile.app.navigation.asNavRouteDefinition
import com.medrevpatient.mobile.app.utils.RouteUtil
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
