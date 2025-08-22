package com.griotlegacy.mobile.app.navigation.graph
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.griotlegacy.mobile.app.data.source.Constants
import com.griotlegacy.mobile.app.ux.container.about.AboutRoute
import com.griotlegacy.mobile.app.ux.container.about.AboutScreen
import com.griotlegacy.mobile.app.ux.container.addAdvertisement.AddAdvertisementRoute
import com.griotlegacy.mobile.app.ux.container.addAdvertisement.AddAdvertisementScreen
import com.griotlegacy.mobile.app.ux.container.addPeople.AddPeopleRoute
import com.griotlegacy.mobile.app.ux.container.addPeople.AddPeopleScreen
import com.griotlegacy.mobile.app.ux.container.advertisement.AdvertisementRoute
import com.griotlegacy.mobile.app.ux.container.advertisement.AdvertisementScreen
import com.griotlegacy.mobile.app.ux.container.advertisementSubscription.AdvertisementSubscriptionRoute
import com.griotlegacy.mobile.app.ux.container.advertisementSubscription.AdvertisementSubscriptionScreen
import com.griotlegacy.mobile.app.ux.container.block.BLockListScreen
import com.griotlegacy.mobile.app.ux.container.block.BlockListRoute
import com.griotlegacy.mobile.app.ux.container.buildLegacy.BuildLegacyRoute
import com.griotlegacy.mobile.app.ux.container.buildLegacy.BuildLegacyScreen
import com.griotlegacy.mobile.app.ux.container.changePassword.ChangePasswordRoute
import com.griotlegacy.mobile.app.ux.container.changePassword.ChangePasswordScreen
import com.griotlegacy.mobile.app.ux.container.chat.ChatRoute
import com.griotlegacy.mobile.app.ux.container.chat.ChatScreen
import com.griotlegacy.mobile.app.ux.container.contactUs.ContactUsRoute
import com.griotlegacy.mobile.app.ux.container.contactUs.ContactUsScreen
import com.griotlegacy.mobile.app.ux.container.createTribeOrInnerCircle.CreateTribeOrInnerCircleRoute
import com.griotlegacy.mobile.app.ux.container.createTribeOrInnerCircle.CreateTribeOrInnerCircleScreen
import com.griotlegacy.mobile.app.ux.container.editProfile.EditProfileRoute
import com.griotlegacy.mobile.app.ux.container.editProfile.EditProfileScreen
import com.griotlegacy.mobile.app.ux.container.faq.FaqRoute
import com.griotlegacy.mobile.app.ux.container.faq.FaqScreenScreen
import com.griotlegacy.mobile.app.ux.container.groupMember.GroupMemberRoute
import com.griotlegacy.mobile.app.ux.container.groupMember.GroupMemberScreen
import com.griotlegacy.mobile.app.ux.container.myCircle.MyCircleRoute
import com.griotlegacy.mobile.app.ux.container.myCircle.MyCircleScreen
import com.griotlegacy.mobile.app.ux.container.notification.NotificationRoute
import com.griotlegacy.mobile.app.ux.container.notification.NotificationScreen
import com.griotlegacy.mobile.app.ux.container.postDetails.PostDetailsRoute
import com.griotlegacy.mobile.app.ux.container.postDetails.PostDetailsScreenScreen
import com.griotlegacy.mobile.app.ux.container.storage.StorageRoute
import com.griotlegacy.mobile.app.ux.container.storage.StorageScreen
import com.griotlegacy.mobile.app.ux.container.storageSubscription.StorageSubscriptionRoute
import com.griotlegacy.mobile.app.ux.container.storageSubscription.StorageSubscriptionScreen
import com.griotlegacy.mobile.app.ux.container.tribeList.TribeListRoute
import com.griotlegacy.mobile.app.ux.container.tribeList.TribeListScreen
import com.griotlegacy.mobile.app.ux.container.userProfile.FriendProfileRoute
import com.griotlegacy.mobile.app.ux.container.userProfile.FriendProfileScreen

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
        Constants.AppScreen.MY_CIRCLE_SCREEN -> {
            MyCircleRoute.routeDefinition.value
        }
        Constants.AppScreen.POST_DETAILS_SCREEN -> {
            PostDetailsRoute.routeDefinition.value
        }
        Constants.AppScreen.MAIN_VILLAGE_SCREEN -> {
            PostDetailsRoute.routeDefinition.value
        }
        Constants.AppScreen.BUILD_LEGACY_SCREEN -> {
            BuildLegacyRoute.routeDefinition.value
        }
        Constants.AppScreen.STORAGE_SCREEN -> {
            StorageRoute.routeDefinition.value
        }
        Constants.AppScreen.BLOCK_LIST_SCREEN -> {
            BlockListRoute.routeDefinition.value
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
        Constants.AppScreen.CHAT_SCREEN -> {
            ChatRoute.routeDefinition.value
        }
        Constants.AppScreen.ADVERTISEMENT_SCREEN -> {
            AdvertisementRoute.routeDefinition.value
        }
        Constants.AppScreen.LEGACY_REFLECTION_SCREEN -> {
            FaqRoute.routeDefinition.value
        }
        else -> {
            BuildLegacyRoute.routeDefinition.value
        }
    }
    NavHost(navController = navController, startDestination = appStartDestination) {
        AboutRoute.addNavigationRoute(this) {
            AboutScreen(
                navController,
                screenName = screenName,
                url = url
            )
        }
        ChangePasswordRoute.addNavigationRoute(this) { ChangePasswordScreen(navController) }
        ContactUsRoute.addNavigationRoute(this) { ContactUsScreen(navController) }
        FaqRoute.addNavigationRoute(this) {
            FaqScreenScreen(
                navController,
                screenName = screenName
            )
        }
        EditProfileRoute.addNavigationRoute(this) { EditProfileScreen(navController) }
        MyCircleRoute.addNavigationRoute(this) { MyCircleScreen(navController) }
        TribeListRoute.addNavigationRoute(this) { TribeListScreen(navController) }
        AddPeopleRoute.addNavigationRoute(this) { AddPeopleScreen(navController) }
        BuildLegacyRoute.addNavigationRoute(this) { BuildLegacyScreen(navController) }
        CreateTribeOrInnerCircleRoute.addNavigationRoute(this) {
            CreateTribeOrInnerCircleScreen(
                navController
            )
        }
        PostDetailsRoute.addNavigationRoute(this) {
            PostDetailsScreenScreen(
                navController,
                postId = postId,
                screen = screenName
            )
        }
        StorageRoute.addNavigationRoute(this) { StorageScreen(navController) }
        BlockListRoute.addNavigationRoute(this) { BLockListScreen(navController) }
        NotificationRoute.addNavigationRoute(this) { NotificationScreen(navController) }
        FriendProfileRoute.addNavigationRoute(this) {
            FriendProfileScreen(
                navController,
                userId = userId
            )
        }
        ChatRoute.addNavigationRoute(this) {
            ChatScreen(
                navController,
                messageResponse = messageResponse
            )
        }
        AdvertisementRoute.addNavigationRoute(this) { AdvertisementScreen(navController) }
        AddAdvertisementRoute.addNavigationRoute(this) { AddAdvertisementScreen(navController) }
        GroupMemberRoute.addNavigationRoute(this) { GroupMemberScreen(navController) }
        StorageSubscriptionRoute.addNavigationRoute(this) { StorageSubscriptionScreen(navController) }
        AdvertisementSubscriptionRoute.addNavigationRoute(this) { AdvertisementSubscriptionScreen(navController) }
    }
}