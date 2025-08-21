package com.medrevpatient.mobile.app.navigation.graph

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.medrevpatient.mobile.app.navigation.RouteMaker
import com.medrevpatient.mobile.app.navigation.RouteMaker.Home
import com.medrevpatient.mobile.app.navigation.RouteMaker.MyProgress
import com.medrevpatient.mobile.app.navigation.RouteMaker.ProgramAndWorkout
import com.medrevpatient.mobile.app.navigation.RouteMaker.Recipes
import com.medrevpatient.mobile.app.navigation.handleNavigation
import com.medrevpatient.mobile.app.navigation.navHiltViewModel
import com.medrevpatient.mobile.app.navigation.setInBackStack
import com.medrevpatient.mobile.app.utils.Constants
import com.medrevpatient.mobile.app.ux.main.community.CommunityScreen
import com.medrevpatient.mobile.app.ux.main.community.CommunityViewModel
import com.medrevpatient.mobile.app.ux.main.community.communityDisclaimer.CommunityDisclaimerScreen
import com.medrevpatient.mobile.app.ux.main.community.communityDisclaimer.CommunityDisclaimerViewModel
import com.medrevpatient.mobile.app.ux.main.community.myPosts.MyPostsScreen
import com.medrevpatient.mobile.app.ux.main.community.myPosts.MyPostsViewModel
import com.medrevpatient.mobile.app.ux.main.community.postDetails.PostDetailsViewModel
import com.medrevpatient.mobile.app.ux.main.community.postDetails.PostDetailsViewScreen
import com.medrevpatient.mobile.app.ux.main.home.HomeScreen
import com.medrevpatient.mobile.app.ux.main.home.HomeViewModel
import com.medrevpatient.mobile.app.ux.main.myprogress.MyGoalsScreen
import com.medrevpatient.mobile.app.ux.main.myprogress.MyProgressScreen
import com.medrevpatient.mobile.app.ux.main.myprogress.MyProgressViewModel
import com.medrevpatient.mobile.app.ux.main.myprogress.ViewGoalScreen
import com.medrevpatient.mobile.app.ux.main.myprogress.sheetcontent.component.WeightCameraScreen
import com.medrevpatient.mobile.app.ux.main.notes.AllNotesScreen
import com.medrevpatient.mobile.app.ux.main.notes.CreateNotesScreen
import com.medrevpatient.mobile.app.ux.main.profile.CalendarScreen
import com.medrevpatient.mobile.app.ux.main.profile.CalendarViewModel
import com.medrevpatient.mobile.app.ux.main.profile.ProfileScreen
import com.medrevpatient.mobile.app.ux.main.profile.ProfileViewModel
import com.medrevpatient.mobile.app.ux.main.profile.ReminderScreen
import com.medrevpatient.mobile.app.ux.main.profile.ReminderViewModel
import com.medrevpatient.mobile.app.ux.main.programandworkout.AllOnDemandClassesScreen
import com.medrevpatient.mobile.app.ux.main.programandworkout.AllProgramScreen
import com.medrevpatient.mobile.app.ux.main.programandworkout.ForMeScreen
import com.medrevpatient.mobile.app.ux.main.programandworkout.MoveNowScreen
import com.medrevpatient.mobile.app.ux.main.programandworkout.ProgramsAndWorkOutScreen
import com.medrevpatient.mobile.app.ux.main.programandworkout.ProgramsAndWorkOutViewModel
import com.medrevpatient.mobile.app.ux.main.programandworkout.RecipesScreen
import com.medrevpatient.mobile.app.ux.main.programandworkout.SearchScreen
import com.medrevpatient.mobile.app.ux.main.programandworkout.TodayRoutineScreen
import com.medrevpatient.mobile.app.ux.main.programandworkout.ViewProgramScreen
import com.medrevpatient.mobile.app.ux.main.programandworkout.ViewRecipeScreen

@Composable
fun MainGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = Home.routeDefinition.value,
    goalId: String = ""
) {


    NavHost(
        navController = navController,
        startDestination = when (startDestination) {
            Constants.Keywords.COMMUNITY -> RouteMaker.Community.routeDefinition.value
            Constants.Keywords.ALL_PROGRAMS -> RouteMaker.AllPrograms.routeDefinition.value.replace(
                "{is_for_me}",
                "1"
            ).replace("{is_from_notification}", "true")

            Constants.Keywords.VIEW_GOALS -> RouteMaker.ViewGoal.routeDefinition.value.replace(
                "{globalID}",
                goalId
            ).replace("{is_from_notification}", "true")

            else -> Home.routeDefinition.value
        },
        modifier = modifier
    ) {

        Home.addNavigationRoute(this) {
            val viewModel = hiltViewModel<HomeViewModel>().handleNavigation(navController)
            HomeScreen(viewModel = viewModel)
        }

        ProgramAndWorkout.addNavigationRoute(this) {
            ProgramsAndWorkOutScreen(
                viewModel = navHiltViewModel(navController)
            )
        }

        RouteMaker.TodayRoutine.addNavigationRoute(this) {
            TodayRoutineScreen(navHiltViewModel(navController))
        }

        RouteMaker.Search.addNavigationRoute(this) {
            SearchScreen(
                viewModel = hiltViewModel<ProgramsAndWorkOutViewModel>().handleNavigation(
                    navController
                )
            )
        }

        RouteMaker.AllPrograms.addNavigationRoute(this) {
            AllProgramScreen(viewModel = navHiltViewModel(navController))
        }

        RouteMaker.ViewProgram.addNavigationRoute(this) { backStack ->

            ViewProgramScreen(
                navController = navController,
                viewModel = navHiltViewModel(navController)
            )
        }

        RouteMaker.MoveNow.addNavigationRoute(this) {
            MoveNowScreen(viewModel = navHiltViewModel(navController))
        }

        RouteMaker.ForMe.addNavigationRoute(this) {
            ForMeScreen(navController, viewModel = navHiltViewModel(navController))
        }

        RouteMaker.CrudNote.addNavigationRoute(this) {
            CreateNotesScreen(viewModel = navHiltViewModel(navController))
        }

        RouteMaker.AllNotes.addNavigationRoute(this) {
            AllNotesScreen(navController, viewModel = navHiltViewModel(navController))
        }

        MyProgress.addNavigationRoute(this) {

            MyProgressScreen(
                navController = navController,
                viewModel = navHiltViewModel<MyProgressViewModel>(navController)
            )
        }

        RouteMaker.MyGoals.addNavigationRoute(this) {
            MyGoalsScreen(
                navController = navController,
                viewModel = navHiltViewModel(navController)
            )
        }

        RouteMaker.ViewGoal.addNavigationRoute(this) {
            ViewGoalScreen(
                navController = navController,
                viewModel = navHiltViewModel(navController)
            )
        }

        RouteMaker.Community.addNavigationRoute(this) {
            val viewModel = hiltViewModel<CommunityViewModel>().handleNavigation(navController)
            CommunityScreen(viewModel = viewModel)
        }

        RouteMaker.CommunityDisclaimer.addNavigationRoute(this) {
            val viewModel =
                hiltViewModel<CommunityDisclaimerViewModel>().handleNavigation(navController)
            CommunityDisclaimerScreen(viewModel = viewModel)
        }

        Recipes.addNavigationRoute(this) {
            RecipesScreen(viewModel = navHiltViewModel(navController))
        }

        RouteMaker.Profile.addNavigationRoute(this) {
            val viewModel = hiltViewModel<ProfileViewModel>().handleNavigation(navController)
            ProfileScreen(viewModel = viewModel)
        }

        RouteMaker.Calendar.addNavigationRoute(this) {
            val viewModel = hiltViewModel<CalendarViewModel>().handleNavigation(navController)
            CalendarScreen(viewModel = viewModel)
        }

        RouteMaker.ViewRecipe.addNavigationRoute(this) {
            ViewRecipeScreen(viewModel = navHiltViewModel(navController))
        }

        RouteMaker.AllOnDemandClasses.addNavigationRoute(this) {
            AllOnDemandClassesScreen(viewModel = navHiltViewModel(navController))
        }

        RouteMaker.WeightCamera.addNavigationRoute(this) {
            WeightCameraScreen(
                onImageCaptured = { files ->
                    setInBackStack(
                        navController,
                        RouteMaker.Keys.FILE_PATHS,
                        files.toList()
                    )
                    navController.popBackStack()
                },
                onBackPress = {
                    navController.popBackStack()
                }
            )
        }

        RouteMaker.Reminder.addNavigationRoute(this) {
            val viewModel = hiltViewModel<ReminderViewModel>().handleNavigation(navController)
            ReminderScreen(viewModel = viewModel)
        }

        RouteMaker.MyPosts.addNavigationRoute(this) {
            val viewModel = hiltViewModel<MyPostsViewModel>().handleNavigation(navController)
            MyPostsScreen(viewModel = viewModel, navController = navController)
        }

        RouteMaker.PostDetails.addNavigationRoute(this) {
            val viewModel = hiltViewModel<PostDetailsViewModel>().handleNavigation(navController)
            PostDetailsViewScreen(viewModel = viewModel)
        }
    }
}


