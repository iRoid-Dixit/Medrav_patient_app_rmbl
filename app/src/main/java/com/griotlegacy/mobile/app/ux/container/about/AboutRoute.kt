package com.griotlegacy.mobile.app.ux.container.about

import android.net.Uri
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.griotlegacy.mobile.app.navigation.NavComposeRoute
import com.griotlegacy.mobile.app.navigation.NavRoute
import com.griotlegacy.mobile.app.navigation.NavRouteDefinition
import com.griotlegacy.mobile.app.navigation.asNavRoute
import com.griotlegacy.mobile.app.navigation.asNavRouteDefinition
import com.griotlegacy.mobile.app.utils.RouteUtil
object AboutRoute : NavComposeRoute() {
    private const val ROUTE_BASE = "about_us"
    override val routeDefinition: NavRouteDefinition =
        "$ROUTE_BASE/${RouteUtil.defineArg(Arg.URL)}".asNavRouteDefinition()

    fun createRoute(url: String): NavRoute {
        val encodedUrl = Uri.encode(url)
        return "${ROUTE_BASE}/$encodedUrl".asNavRoute()
    }

    override fun getArguments(): List<NamedNavArgument> {
        return listOf(
            navArgument(Arg.URL) { type = NavType.StringType },
        )
    }

    object Arg {
        const val URL = "url"
    }
}