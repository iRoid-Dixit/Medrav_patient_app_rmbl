package com.medrevpatient.mobile.app.data.source.remote
import com.medrevpatient.mobile.app.BuildConfig
object EndPoints {
    object URLs {
        const val BASE_URL: String = BuildConfig.BASE_URL
    }
    object Auth {
        const val SIGN_UP: String = "auth/register"
        const val LOG_IN: String = "auth/login"
        const val OTP_VERIFY: String = "auth/otp-verification"
        const val LOGOUT: String = "auth/logout"
        const val RESEND_OTP: String = "auth/resend-otp"
        const val FORGET_PASSWORD: String = "auth/resend-otp"
        const val SOCIAL_LOGIN: String = "auth/social/login"
        const val USER_UPDATE: String = "user/update"
        const val ACCOUNT_DELETE = "auth/delete-account"
        const val SINGLE_POST_DELETE = "legacy/post-single-media-delete"
        const val REFRESH_TOKEN = "auth/refresh_token"
        const val FORCE_UPDATE = "auth/version"
        const val DELETE_LEGACY_POST = "legacy/delete-post"
        const val BMI_CALCULATE = "api/patient/bmi/calculate"
        const val SIDE_EFFECT_QUESTIONS = "api/patient/side-effects/questions"
        const val SIDE_EFFECT_SUBMIT = "api/patient/side-effects/submit"
        const val DIET_CHALLENGE_GET = "api/patient/diet-challenges/get-challenge"
        const val DIET_CHALLENGE_SUBMIT = "api/patient/diet-challenges/submit-answer"
    }
    object Notification{
        const val FCM_TOKEN = "fcmtoken/create-fcmToken"
    }
    object Main {
        const val HOME: String = "home"
        const val HOME_SCREEN: String = "api/home-screen"
        const val MARK_AS_PLAYED: String = "mark-as-played"
        const val NOTIFICATION_OFF_ON: String = "user/notification-on-off"
    }
    object Container {
        const val HOME: String = "home"
        const val MARK_AS_PLAYED: String = "mark-as-played"
        const val CHANGE_PASSWORD: String = "auth/change-password"
        const val FQA_QUESTION: String = "user/faq"
        const val INNER_CIRCLE_BLOCK_AND_LEAVE: String =
            "friends/block-member-and-remove-member-tribe"
        const val COMMENT_LIST: String = "legacy/get-comments"
        const val LEGACY_POST: String = "legacy/my-posts"
        const val TRIBE_INNER_CIRCLE: String = "friends/list-innerCircle-and-tribe"
        const val CONTACT_US: String = "user/contact-us"
        const val RESET_PASSWORD: String = "auth/reset-password"
        const val EDIT_PROFILE: String = "api/patient/profile/update"
        const val POST_DETAILS: String = "legacy/get-post"
        const val ADD_COMMENT: String = "legacy/comment-post"
        const val LIKE_DISLIKE: String = "legacy/like-post"
        const val CREATE_INNER_CIRCLE_AND_TRIBE: String = "friends/create-innerCircle-or-tribe"
        const val LEGACY_POST_CREATE: String = "legacy/create-post"
        const val USER_STORAGE: String = "user/storage"
        const val UPDATE_LEGACY_POST: String = "legacy/update-post"
        const val IMAGE_POSITION_CHANGE: String = "legacy/media-position-change"
        const val ADD_IMAGE_TO_LEGACY_POST: String = "legacy/add-media-in-post"
        const val MAIN_VILLAGE_PAGE: String = "legacy/home"
        const val REPORT_POST_AND_USER: String = "user/report"
        const val GET_NOTIFICATION: String = "notification/get-notification"
        const val NOTIFICATION_ACCEPT_AND_REJECT: String = "friends/accept-invitation"
        const val GET_MESSAGE: String = "chat/get-message-list"
        const val ADD_GROUP_MEMBER: String = "chat/create-group"
        const val GROUP_DETAILS_UPDATE: String = "chat/update-group"
        const val USER_PROFILE: String = "friends/friend-info"
        const val FRIEND_INFORMATION: String = "friends/friend-info"
        const val ADVERTISEMENT: String = "advertisement/my-advertisements"
        const val HOME_ADVERTISEMENT: String = "advertisement/home-advertisements"
        const val ADD_ADVERTISEMENT: String = "advertisement/create-advertisement"
        const val UPDATE_ADVERTISEMENT: String = "advertisement/update-advertisement"
        const val GET_CHAT_MESSAGE: String = "chat/get-chatMessage"
        const val REMOVE_ADD_GROUP_MEMBER: String = "chat/add-remove-members-group"
        const val PUBLIC_PRIVATE_PROFILE: String = "user/private-profile"
        const val LEGACY_REFLECTION = "user/reflection"
        const val ADVERTISEMENT_VIEW = "advertisement/advertisement-view"
        const val USER_TERMS = "user/terms"
    }
    object Archive {
        const val ARCHIVE = "archives"
    }
    object Friends {
        const val LIST_INNER_CIRCLE_AND_TRIBE = "friends/list-innerCircle-and-tribe"
        const val MEMBERS_INNER_CIRCLE_AND_TRIBE = "friends/members-innerCircle-or-tribe"
        const val ADD_MEMBERS_INNER_CIRCLE_AND_TRIBE = "friends/add-members-innerCircle-and-tribe"
        const val SEARCH_FRIENDS = "friends/search-friends"
        const val BLOCK_MEMBER = "user/blocked-list"
        const val GROUP_MEMBER = "chat/get-members-group"
        const val ADD_GROUP_MEMBER = "chat/list-members-group"
    }
    object PersonalizeAudio {
        const val MY_GOAL = "my-goal"
        const val GOAL_STORE = "goal/store"
        const val AFFIRMATION_LIST = "affirmation/list"
        const val NAME_PRONUNCIATION = "pronaouance/name"
    }
    object Support {
        const val FEEDBACK = "feedback/store"
    }
    object Subscription {
        const val SUBSCRIBE = "subscription/buy-subscription"
        const val ADVERTISEMENT_SUBSCRIPTION = "advertisement/buy-advertisement"
        const val CHECK_SUBSCRIPTION = "check-subscription"
    }
}