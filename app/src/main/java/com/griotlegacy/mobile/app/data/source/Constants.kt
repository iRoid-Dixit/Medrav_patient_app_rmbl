package com.griotlegacy.mobile.app.data.source

object Constants {

    const val IMAGE_URI = "imageUri"
    const val IS_COME_FOR = "is_come_for"
    const val IS_FORM = "is_form"
    const val CANCEL_SUBSCRIPTION_INFO: String = "Before you make it official,\n" +
            "is there anything we can do to improve your Thought Rise experience?\n" +
            "\n" +
            "If so, we would love to hear your feedback and have you stay with us for a bit!\n" +
            "\n" +
            "If not, we are sad to see you go, but we understand and will miss you!\n"
    const val DEVICE_TYPE: Int = 1 //here in backend they set device type 2 for android

    object AppScreen {
        const val START_UP: String = "startUp"
        const val SIGN_IN: String = "sign-in"
        const val FORGET_PASSWORD_SCREEN: String = "forgetPasswordScreen"
        const val REGISTER_SCREEN: String = "registerScreen"
        const val PERSONALIZE_AUDIO: String = "personalize-audio"
        const val HELP_AND_SUPPORT: String = "help-and-support"
        const val ABOUT_US: String = "about-us"
        const val PRIVACY_POLICY_SCREEN: String = "privacyPolicyScreen"
        const val TERM_AND_CONDITION_SCREEN: String = "termAndConditionScreen"
        const val LEGACY_REFLECTION_SCREEN: String = "legacyReflectionScreen"
        const val CHANGE_PASSWORD_SCREEN: String = "changePasswordScreen"
        const val CONTACT_US_SCREEN: String = "contactUsScreen"
        const val FQA_SCREEN: String = "faqScreen"
        const val EDIT_PROFILE_SCREEN: String = "editProfileScreen"
        const val MY_CIRCLE_SCREEN: String = "myCircleScreen"
        const val POST_DETAILS_SCREEN: String = "postDetailsScreen"
        const val CREATE_CIRCLE_SCREEN: String = "createCircleScreen"
        const val BUILD_LEGACY_SCREEN: String = "buildLegacyScreen"
        const val GRIOT_LEGACY_SCREEN: String = "griotLegacyScreen"
        const val STORAGE_SCREEN: String = "storageScreen"
        const val MAIN_VILLAGE_SCREEN: String = "mainVillageScreen"
        const val BLOCK_LIST_SCREEN: String = "blockListScreen"
        const val NOTIFICATION_SCREEN: String = "notificationScreen"
        const val USER_PROFILE: String = "userProfile"
        const val ADD_GROUP_MEMBER: String = "addGroupMember"
        const val CREATE_INNER_CIRCLE_OR_TRIBE: String = "createInnerCircleOrTribe"
        const val CHAT_SCREEN: String = "chatScreen"
        const val ADVERTISEMENT_SCREEN: String = "advertisementScreen"
        const val GROUP_MEMBER_SCREEN: String = "groupMemberScreen"
        const val TRIBE_INNER_USER_SCREEN: String = "tribeInnerUserScreen"
        const val SEARCH_SCREEN: String = "searchScreen"
        const val MESSAGE_SCREEN: String = "messageScreen"
        const val EDIT_ADVERTISEMENT_SCREEN: String = "editAdvertisementScreen"
        const val ADD_ADVERTISEMENT_SCREEN: String = "addAdvertisementScreen"


    }

    object Socket {
        const val SOCKET_URL: String = "https://dev.iroidsolutions.com:4009"
        const val SEND_MESSAGE: String = "sendMessage"
        const val ROOM_ID: String = "roomId"
        const val SENDER_ID: String = "senderId"

        const val RECEIVER_ID: String = "receiverId"
        const val MESSAGE: String = "message"
        const val CREATE_ROOM: String = "createRoom"
        const val GROUP_ID: String = "groupId"
        const val ROOM_CONNECT: String = "roomConnected"
        const val NEW_MESSAGE: String = "newMessage"
        const val UPDATE_STATUS_TO_ONLINE: String = "UpdateStatusToOnline"
        const val STATUS_ONLINE: String = "statusOnline"
        const val USER_BACK_TO_HOME: String = "userBackToHome"

    }

    object MessageType {
        const val SINGLE_CHAT = 1
        const val GROUP_CHAT = 2
    }


    object Values {
        const val VIDEO_LINK = "videoLink"
    }

    object AppInfo {
        const val DIR_NAME = "GriotLegacy"
        const val FILE_PREFIX_NAME = "GriotLegacy_"
    }

    object EditProfile {
        const val NAME = "name"
        const val EMAIL = "email"
        const val MOBILE_NUMBER = "mobileNumber"
        const val DATE_OF_BIRTH = "dateOfBirth"
        const val COUNTRY_CODE = "countryCode"
        const val GENDER = "gender"
        const val PROFILE = "profileImage"

    }

    object CreateInnerCircleAndTribe {
        const val NAME = "name"
        const val TYPE = "type"
        const val MEMBER = "members"
        const val IMAGE = "image"
    }

    object AddMember {
        const val GROUP_NAME = "groupName"
        const val GROUP_IMAGE = "groupImage"
        const val MEMBER = "members"
        const val GROUP_ID = "groupId"
    }

    object NotificationPush {
        const val LIKE_DISLIKE_TYPE: Int = 5
        const val COMMENT_TYPE: Int = 4
        const val ADD_POST: Int = 3
        const val MESSAGE: Int = 2

    }

    object AddAdvertisement {
        const val COMPANY_NAME = "companyName"
        const val ADVERTISEMENT_ID = "advertisementId"
        const val CONTACT_PERSON = "contactPerson"
        const val EMAIL = "email"
        const val COUNTRY_CODE = "countryCode"
        const val PHONE_NUMBER = "phoneNumber"
        const val PHYSICAL_ADDRESS = "physicalAddress"
        const val PURPOSE = "purpose"
        const val DESCRIPTION = "description"
        const val LINK = "link"
        const val TITLE = "title"
        const val START_DATE = "startDate"
        const val END_DATE = "endDate"
        const val IMAGE = "image"


    }

    object CreateLegacyPost {
        const val TYPE = "type"
        const val ALBUM_NAME = "albumName"
        const val LEGACY_TEXT = "legacyText"
        const val TRIBE_LIST = "tribeList"
        const val MEDIA = "media"
        const val POST_ID = "postId"


    }

    object BundleKey {
        const val POST_ID = "postId"
        const val COMMENT_COUNT = "commentCount"
        const val LIKE_COUNT = "likeCount"
        const val OWN_LIKE = "ownLike"
        const val MESSAGE_RESPONSE = "messageResponse"
        const val USER_ID = "userId"
        const val URL = "url"
        const val MEMBER_ADDED = "memberAdded"
        const val RESET = "reset"
        const val RESTART_APP = "restartApp"
        const val NOTIFICATION_TYPE = "notificationType"
        const val MEDIA_LIST = "mediaList"
    }

    object ReportType {
        const val REPORT_USER = 2
        const val REPORT_POST = 1
    }

    object NotificationType {
        const val ACCEPT_NOTIFICATION = 1
        const val REJECT_NOTIFICATION = 2
    }

    object IntentData {
        const val SCREEN_NAME = "screenName"
        const val CREATE_GROUP = "createGroup"
        const val PROFILE_IMAGE = "profileImage"
        const val IS_AFTER_PROFILE_SCREEN = "isAfterProfileScreen"
    }

    object FriendType {
        const val BLOCK_USER = 1
        const val REMOVE_USER = 2
    }

    object ValidLength {
        const val MAX_GOAL_LENGTH = 100
    }

    object SocialLogin {
        const val GOOGLE = "google"
        const val FACEBOOK = "facebook"
        const val APPLE = "apple"
    }

    object GroupMember {
        const val ADD_MEMBER = 1
        const val REMOVE_MEMBER = 2
    }

    enum class Gender(val value: Int) {
        MALE(1),
        FEMALE(2),
        NON_BINARY(3),
    }

    fun getGenderLabel(gender: String?): String {
        return when (gender) {
            "1" -> "Male"
            "2" -> "Female"
            "3" -> "Non Binary"
            else -> "" // Default empty if no valid gender is selected
        }
    }

    fun getTribeAndInnerCircleLabel(type: String?): String {
        return when (type) {
            "1" -> "Tribe"
            "2" -> "InnerCircle"
            else -> "" // Default empty if no valid gender is selected
        }
    }

    object SupportType {
        const val FEEDBACK: Int = 1
        const val FEATURE_REQ: Int = 2
        const val REPORT_PROBLEM: Int = 3
        const val SUPPORT_MAIL = "support@thoughtriseapp.com"
    }

    object OtpVerificationType {
        const val USER_REGISTER: Int = 1
        const val FORGET_PASSWORD: Int = 2
    }

    object AppStatus {
        const val UP_TO_DATE: Int = 3
        const val FORCE_UPDATE: Int = 1
        const val RECOMMEND_UPDATE: Int = 2
    }

    object AboutUS {
        const val ABOUT_US: String = "Embracing the West African\n" +
                "ancestral tradition of oral storytelling, Griot Legacy is a unique place to gather and share a curated collection of life’s most meaningful moments. Griot Legacy allows individuals to collaborate in creating  legacy of personal stories to share now and with future generation. It is our vision that the greatest , with Griot Legacy that vision is now possible."

    }

    object Subscription {
        const val PUBLIC_API_KEY = "goog_qkrJhwmptRXKRfThzVCxFuKymmM"
        const val ANDROID: Int = 1

        enum class SubscriptionPlan(val productId: String) {
            // Storage tier plans
            MINI_ARCHIVIST("m_a_10_gb"),
            HISTORIAN("h_25_gb"),
            LEGACY_ARCHIVIST("l_a_100_gb"),
            ANCESTRAL_HISTORIAN("a_h_200_gb"),
            LEGACY_VAULT_KEEPER("l_v_k_500_gb"),
            TIME_CAPSULE("t_c_1_tb")
        }

        enum class AdvertisementClickPlan(val productId: String) {
            // Advertisement click plans
            QUICK_PROMO("click_pack_50"),
            MID_SIZE_CAMPAIGN("click_pack_150"),
            MOST_POPULAR_TIER("click_pack_300"),
            LONG_TERM_VISIBILITY("click_pack_500")
        }
    }
}