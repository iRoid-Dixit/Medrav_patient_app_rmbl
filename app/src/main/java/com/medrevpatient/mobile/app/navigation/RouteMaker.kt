package com.medrevpatient.mobile.app.navigation

import android.net.Uri
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.medrevpatient.mobile.app.data.source.remote.EndPoints.ResultType
import com.medrevpatient.mobile.app.data.source.remote.EndPoints.ResultType.FOR_GENERAL
import com.medrevpatient.mobile.app.data.source.remote.dto.Note
import com.medrevpatient.mobile.app.navigation.RouteMaker.Keys.DATA_ONE
import com.medrevpatient.mobile.app.navigation.RouteMaker.Keys.DAY
import com.medrevpatient.mobile.app.navigation.RouteMaker.Keys.ID
import com.medrevpatient.mobile.app.navigation.RouteMaker.Keys.IS_FOR_ME
import com.medrevpatient.mobile.app.navigation.RouteMaker.Keys.IS_FROM_NOTIFICATION
import com.medrevpatient.mobile.app.navigation.RouteMaker.Keys.POST_DATA
import com.medrevpatient.mobile.app.navigation.RouteMaker.Keys.REFRESH
import com.medrevpatient.mobile.app.navigation.RouteMaker.Keys.REFRESH_FOR_ME
import com.medrevpatient.mobile.app.utils.RouteUtil
import com.medrevpatient.mobile.app.utils.ext.toJsonString

object RouteMaker {

    object SubsRoute : NavComposeRoute() {
        private const val ROUTE_BASE = "subscription"
        override val routeDefinition: NavRouteDefinition = ROUTE_BASE.asNavRouteDefinition()

        fun createRoute(): NavRoute {
            return ROUTE_BASE.asNavRoute()
        }

        override fun getArguments(): List<NamedNavArgument> {
            return emptyList()
        }
    }

    object SignInRoute : NavComposeRoute() {
        private const val ROUTE_BASE = "signIn"
        override val routeDefinition: NavRouteDefinition = ROUTE_BASE.asNavRouteDefinition()

        fun createRoute(): NavRoute {
            return ROUTE_BASE.asNavRoute()
        }

        override fun getArguments(): List<NamedNavArgument> {
            return emptyList()
        }
    }

    object OnboardingRoute : NavComposeRoute() {
        private const val ROUTE_BASE = "onboarding"
        override val routeDefinition: NavRouteDefinition = ROUTE_BASE.asNavRouteDefinition()

        fun createRoute(): NavRoute {
            return ROUTE_BASE.asNavRoute()
        }

        override fun getArguments(): List<NamedNavArgument> {
            return emptyList()
        }
    }


    object SignupRoute : NavComposeRoute() {
        private const val ROUTE_BASE = "signup"
        override val routeDefinition: NavRouteDefinition = ROUTE_BASE.asNavRouteDefinition()

        fun createRoute(): NavRoute {
            return ROUTE_BASE.asNavRoute()
        }

        override fun getArguments(): List<NamedNavArgument> {
            return emptyList()
        }
    }

    object OnBoardDataRoute : NavComposeRoute() {
        private const val ROUTE_BASE = "OnboardingData"
        override val routeDefinition: NavRouteDefinition = ROUTE_BASE.asNavRouteDefinition()

        fun createRoute(): NavRoute {
            return ROUTE_BASE.asNavRoute()
        }

        override fun getArguments(): List<NamedNavArgument> {
            return emptyList()
        }
    }


    object SplashRoute : SimpleNavComposeRoute("splash")


    object Home : SimpleNavComposeRoute("Home")

    //ProgramAndWorkoutRoutes
    object ProgramAndWorkout : SimpleNavComposeRoute("ProgramAndWorkout")
    object AllPrograms : NavComposeRoute() {

        override val routeDefinition: NavRouteDefinition
            get() = "AllPrograms/{$IS_FOR_ME}/{$IS_FROM_NOTIFICATION}".asNavRouteDefinition()

        override fun getArguments(): List<NamedNavArgument> {
            return listOf(
                navArgument(IS_FOR_ME) {
                    type = NavType.IntType
                    nullable = false
                },
                navArgument(IS_FROM_NOTIFICATION) {
                    type = NavType.BoolType
                    nullable = false
                }
            )
        }

        fun createRoute(
            type: ResultType = FOR_GENERAL,
            isFromNotification: Boolean = false
        ): NavRoute {
            return "AllPrograms/${type.value}/${isFromNotification}".asNavRoute()
        }


    }


    object Search : SimpleNavComposeRoute("SearchProgram")
    object ViewProgram : NavComposeRoute() {

        fun createRoute(id: String, shouldRefresh: Boolean = false): NavRoute {
            return "ViewProgram/$id/$shouldRefresh".asNavRoute()
        }

        override val routeDefinition: NavRouteDefinition
            get() = "ViewProgram/${RouteUtil.defineArg(ID)}/${RouteUtil.defineArg(REFRESH)}".asNavRouteDefinition()

        override fun getArguments(): List<NamedNavArgument> {
            return listOf(
                navArgument(ID) {
                    type = NavType.StringType
                    nullable = false
                }, navArgument(REFRESH) {
                    type = NavType.BoolType
                    nullable = false
                }
            )
        }

    }

    object TodayRoutine : NavComposeRoute() {

        fun createRoute(id: String, day: String): NavRoute {
            return "TodayRoutine/$id/$day".asNavRoute()
        }

        override val routeDefinition: NavRouteDefinition
            get() = "TodayRoutine/{$ID}/{$DAY}".asNavRouteDefinition()

        override fun getArguments(): List<NamedNavArgument> {
            return listOf(
                navArgument(DAY) {
                    type = NavType.StringType
                    nullable = false
                },
                navArgument(ID) {
                    type = NavType.StringType
                    nullable = false
                }
            )
        }

    }


    object MyProgress : SimpleNavComposeRoute("MyProgress")
    object Community : SimpleNavComposeRoute("Community")
    object CommunityDisclaimer : SimpleNavComposeRoute("CommunityDisclaimer")
    object Profile : SimpleNavComposeRoute("Profile")
    object Calendar : SimpleNavComposeRoute("Calendar")
    object Reminder : SimpleNavComposeRoute("Reminder")
    object TermsAndConditions : SimpleNavComposeRoute("TermsAndConditions")
    object MyPosts : SimpleNavComposeRoute("MyPosts")


    object MoveNow : SimpleNavComposeRoute("MoveNow")
    object ForMe : NavComposeRoute() {
        override val routeDefinition: NavRouteDefinition
            get() = "ForMe/{$REFRESH_FOR_ME}".asNavRouteDefinition()

        override fun getArguments(): List<NamedNavArgument> {
            return listOf(
                navArgument(REFRESH_FOR_ME) {
                    type = NavType.BoolType
                    nullable = false
                }
            )
        }

        fun createRoute(shouldRefresh: Boolean = false): NavRoute {
            return "ForMe/$shouldRefresh".asNavRoute()
        }
    }

    object CrudNote : NavComposeRoute() {
        override val routeDefinition: NavRouteDefinition
            get() = "CrudNote/{$DATA_ONE}".asNavRouteDefinition()

        override fun getArguments(): List<NamedNavArgument> {
            return listOf(
                navArgument(DATA_ONE) {
                    type = NavType.StringType
                    nullable = false
                }
            )
        }

        fun createRoute(note: Note = Note()): NavRoute {
            val encoded = Uri.encode(note.toJsonString())
            return "CrudNote/${encoded}".asNavRoute()
        }
    }

    object AllNotes : SimpleNavComposeRoute("AllNotes")

    object AllOnDemandClasses : NavComposeRoute() {
        override val routeDefinition: NavRouteDefinition
            get() = "AllOnDemandClasses/{$IS_FOR_ME}".asNavRouteDefinition()

        override fun getArguments(): List<NamedNavArgument> {
            return listOf(
                navArgument(IS_FOR_ME) {
                    type = NavType.IntType
                    nullable = false
                }
            )
        }

        fun createRoute(type: ResultType = FOR_GENERAL): NavRoute {
            return "AllOnDemandClasses/${type.value}".asNavRoute()
        }
    }

    object MyGoals : NavComposeRoute() {
        override val routeDefinition: NavRouteDefinition
            get() = "MyGoals/{$REFRESH}".asNavRouteDefinition()

        override fun getArguments(): List<NamedNavArgument> {
            return listOf(
                navArgument(REFRESH) {
                    type = NavType.BoolType
                    nullable = false
                }
            )
        }

        fun createRoute(shouldRefresh: Boolean = false): NavRoute {
            return "MyGoals/$shouldRefresh".asNavRoute()
        }
    }

    object ViewGoal : NavComposeRoute() {
        override val routeDefinition: NavRouteDefinition
            get() = "ViewGoal/{$ID}/{$IS_FROM_NOTIFICATION}".asNavRouteDefinition()

        override fun getArguments(): List<NamedNavArgument> {
            return listOf(
                navArgument(ID) {
                    type = NavType.StringType
                    nullable = false
                },
                navArgument(IS_FROM_NOTIFICATION) {
                    type = NavType.BoolType
                    nullable = false
                }
            )
        }

        fun createRoute(id: String, isFromNotification: Boolean = false): NavRoute {
            return "ViewGoal/$id/$isFromNotification".asNavRoute()
        }

    }

    object Recipes : NavComposeRoute() {
        override val routeDefinition: NavRouteDefinition
            get() = "Recipes/{$IS_FOR_ME}".asNavRouteDefinition()

        override fun getArguments(): List<NamedNavArgument> {
            return listOf(
                navArgument(IS_FOR_ME) {
                    type = NavType.IntType
                    nullable = false
                }
            )
        }

        fun createRoute(type: ResultType = FOR_GENERAL): NavRoute {
            return "Recipes/${type.value}".asNavRoute()
        }
    }

    object ViewRecipe : NavComposeRoute() {
        override val routeDefinition: NavRouteDefinition
            get() = "ViewRecipe/{$ID}".asNavRouteDefinition()

        fun createRoute(id: String): NavRoute {
            return "ViewRecipe/$id".asNavRoute()
        }

        override fun getArguments(): List<NamedNavArgument> {
            return listOf(
                navArgument(ID) {
                    type = NavType.StringType
                    nullable = false
                }
            )
        }
    }

    object WeightCamera : SimpleNavComposeRoute("Camera")

    object PostDetails : NavComposeRoute() {

        fun createRoute(postData: String): NavRoute {
            val postDetailsData = Uri.encode(postData)
            return "PostDetails/$postDetailsData".asNavRoute()
        }

        override val routeDefinition: NavRouteDefinition
            get() = "PostDetails/{$POST_DATA}".asNavRouteDefinition()

        override fun getArguments(): List<NamedNavArgument> {
            return listOf(
                navArgument(POST_DATA) {
                    type = NavType.StringType
                    nullable = false
                }
            )
        }
    }

    object Keys {
        const val ID = "globalID"
        const val DAY = "todayRoutineDay"
        const val IS_FOR_ME = "is_for_me"
        const val IS_FROM_NOTIFICATION = "is_from_notification"
        const val REFRESH_MY_PROGRESS = "refresh_my_progress"
        const val FILE_PATHS = "WeightImageFilePaths"
        const val REFRESH = "refresh_data"
        const val POST_DATA = "postData"
        const val DATA_ONE = "string_one_value"
        const val REFRESH_FOR_ME = "refresh_for_me"
    }

}