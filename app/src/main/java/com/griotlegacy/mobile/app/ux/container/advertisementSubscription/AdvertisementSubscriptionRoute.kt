package com.griotlegacy.mobile.app.ux.container.advertisementSubscription

import androidx.navigation.NamedNavArgument
import com.griotlegacy.mobile.app.navigation.NavComposeRoute
import com.griotlegacy.mobile.app.navigation.NavRoute
import com.griotlegacy.mobile.app.navigation.NavRouteDefinition
import com.griotlegacy.mobile.app.navigation.asNavRoute
import com.griotlegacy.mobile.app.navigation.asNavRouteDefinition
import com.griotlegacy.mobile.app.utils.RouteUtil

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
