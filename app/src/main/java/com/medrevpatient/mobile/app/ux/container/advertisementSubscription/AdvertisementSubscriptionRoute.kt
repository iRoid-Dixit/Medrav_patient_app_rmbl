package com.medrevpatient.mobile.app.ux.container.advertisementSubscription

import androidx.navigation.NamedNavArgument
import com.medrevpatient.mobile.app.navigation.NavComposeRoute
import com.medrevpatient.mobile.app.navigation.NavRoute
import com.medrevpatient.mobile.app.navigation.NavRouteDefinition
import com.medrevpatient.mobile.app.navigation.asNavRoute
import com.medrevpatient.mobile.app.navigation.asNavRouteDefinition
import com.medrevpatient.mobile.app.utils.RouteUtil

//object AdvertisementSubscriptionRoute : SimpleNavComposeRoute("advertisementSubscription")


object
AdvertisementSubscriptionRoute : NavComposeRoute() {
    private const val ROUTE_BASE = "advertisementSubscription"
    override val routeDefinition: NavRouteDefinition = "${ROUTE_BASE}/${RouteUtil.defineArg(Arg.ADVERTISEMENT_ID)}".asNavRouteDefinition()

    fun createRoute(advertisementId: String): NavRoute {
        return "${ROUTE_BASE}/$advertisementId".asNavRoute()
    }

    override fun getArguments(): List<NamedNavArgument> {
        return emptyList()
    }

    object Arg {
        const val ADVERTISEMENT_ID = "advertisementId"
    }
}
