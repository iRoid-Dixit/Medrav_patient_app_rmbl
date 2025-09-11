package com.medrevpatient.mobile.app.navigation.graph

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.medrevpatient.mobile.app.data.source.Constants
import com.medrevpatient.mobile.app.ux.container.about.AboutRoute
import com.medrevpatient.mobile.app.ux.container.about.AboutScreen


import com.medrevpatient.mobile.app.ux.container.appointmentViewDetails.AppointmentViewDetailsRoute
import com.medrevpatient.mobile.app.ux.container.appointmentViewDetails.AppointmentViewDetailsScreen

import com.medrevpatient.mobile.app.ux.container.bookAppointmen.BookAppointmentRoute
import com.medrevpatient.mobile.app.ux.container.bookAppointmen.BookAppointmentScreen

import com.medrevpatient.mobile.app.ux.container.changePassword.ChangePasswordRoute
import com.medrevpatient.mobile.app.ux.container.changePassword.ChangePasswordScreen

import com.medrevpatient.mobile.app.ux.container.contactUs.ContactUsRoute
import com.medrevpatient.mobile.app.ux.container.contactUs.ContactUsScreen
import com.medrevpatient.mobile.app.ux.container.createTribeOrInnerCircle.CreateTribeOrInnerCircleRoute
import com.medrevpatient.mobile.app.ux.container.createTribeOrInnerCircle.CreateTribeOrInnerCircleScreen
import com.medrevpatient.mobile.app.ux.container.editProfile.EditProfileRoute
import com.medrevpatient.mobile.app.ux.container.editProfile.EditProfileScreen
import com.medrevpatient.mobile.app.ux.container.faq.FaqRoute
import com.medrevpatient.mobile.app.ux.container.faq.FaqScreenScreen

import com.medrevpatient.mobile.app.ux.container.notification.NotificationRoute
import com.medrevpatient.mobile.app.ux.container.notification.NotificationScreen

import com.medrevpatient.mobile.app.ux.container.userProfile.FriendProfileRoute
import com.medrevpatient.mobile.app.ux.container.userProfile.FriendProfileScreen
import com.medrevpatient.mobile.app.ux.main.home.HomeUiEvent
import com.medrevpatient.mobile.app.ux.startup.auth.bmi.BmiRoute
import com.medrevpatient.mobile.app.ux.startup.auth.bmi.BmiScreen
import com.medrevpatient.mobile.app.ux.startup.auth.dietChallenge.DietChallengeRoute
import com.medrevpatient.mobile.app.ux.startup.auth.dietChallenge.DietChallengeScreen
import com.medrevpatient.mobile.app.ux.startup.auth.sideEffectQuestion.SideEffectQuestionRoute
import com.medrevpatient.mobile.app.ux.startup.auth.sideEffectQuestion.SideEffectScreen
import com.medrevpatient.mobile.app.ux.startup.auth.weightTracker.WeightTrackerRoute
import com.medrevpatient.mobile.app.ux.startup.auth.weightTracker.WeightTrackerScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppContainerGraph(
    navController: NavHostController,
    startDestination: String,
    postId: String,
    screenName: String,
    messageResponse: String,
    userId: String,
    url: String
) {
    val appStartDestination = when (startDestination) {
        Constants.AppScreen.ABOUT_US -> {
            AboutRoute.routeDefinition.value
        }

        Constants.AppScreen.PRIVACY_POLICY_SCREEN -> {
            AboutRoute.routeDefinition.value
        }

        Constants.AppScreen.TERM_AND_CONDITION_SCREEN -> {
            AboutRoute.routeDefinition.value
        }

        Constants.AppScreen.CHANGE_PASSWORD_SCREEN -> {
            ChangePasswordRoute.routeDefinition.value
        }

        Constants.AppScreen.FQA_SCREEN -> {
            FaqRoute.routeDefinition.value
        }

        Constants.AppScreen.CONTACT_US_SCREEN -> {
            ContactUsRoute.routeDefinition.value
        }

        Constants.AppScreen.EDIT_PROFILE_SCREEN -> {
            EditProfileRoute.routeDefinition.value
        }



        Constants.AppScreen.NOTIFICATION_SCREEN -> {
            NotificationRoute.routeDefinition.value
        }

        Constants.AppScreen.ADD_GROUP_MEMBER -> {
            CreateTribeOrInnerCircleRoute.routeDefinition.value
        }

        Constants.AppScreen.USER_PROFILE -> {
            FriendProfileRoute.routeDefinition.value
        }




        Constants.AppScreen.LEGACY_REFLECTION_SCREEN -> {
            FaqRoute.routeDefinition.value
        }

        Constants.AppScreen.SIDE_EFFECT_CHECK_SCREEN -> {
            SideEffectQuestionRoute.routeDefinition.value
        }

        Constants.AppScreen.DAILY_DIET_CHALLENGE_SCREEN -> {
            DietChallengeRoute.routeDefinition.value
        }
        Constants.AppScreen.BOOK_APPOINTMENT_SCREEN -> {
            BookAppointmentRoute.routeDefinition.value
        }
        Constants.AppScreen.APPOINTMENT_DETAILS_SCREEN -> {
            AppointmentViewDetailsRoute.routeDefinition.value
        }
        Constants.AppScreen.CALCULATE_BMI_SCREEN -> {
            BmiRoute.routeDefinition.value
        }

        else -> {
           ""
        }
    }
    NavHost(navController = navController, startDestination = appStartDestination) {
        AboutRoute.addNavigationRoute(this) { AboutScreen(navController, screenName = screenName, url = url) }
        ChangePasswordRoute.addNavigationRoute(this) { ChangePasswordScreen(navController) }
        ContactUsRoute.addNavigationRoute(this) { ContactUsScreen(navController) }
        FaqRoute.addNavigationRoute(this) { FaqScreenScreen(navController, screenName = screenName) }
        EditProfileRoute.addNavigationRoute(this) { EditProfileScreen(navController) }
        CreateTribeOrInnerCircleRoute.addNavigationRoute(this) { CreateTribeOrInnerCircleScreen(navController) }
        NotificationRoute.addNavigationRoute(this) { NotificationScreen(navController) }
        FriendProfileRoute.addNavigationRoute(this) { FriendProfileScreen(navController, userId = userId) }

        // medrev
        BmiRoute.addNavigationRoute(this) { BmiScreen(navController) }
        SideEffectQuestionRoute.addNavigationRoute(this) { SideEffectScreen(navController) }
        DietChallengeRoute.addNavigationRoute(this) { DietChallengeScreen(navController) }
        WeightTrackerRoute.addNavigationRoute(this) { WeightTrackerScreen(navController) }
        BookAppointmentRoute.addNavigationRoute(this) { BookAppointmentScreen(navController) }
        AppointmentViewDetailsRoute.addNavigationRoute(this) { AppointmentViewDetailsScreen(navController) }
    }
}