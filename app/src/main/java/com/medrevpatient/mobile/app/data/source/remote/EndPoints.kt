package com.medrevpatient.mobile.app.data.source.remote

import com.medrevpatient.mobile.app.BuildConfig


object EndPoints {
    object URLs {
        const val BASE_URL: String = BuildConfig.BASE_URL
    }

    object Auth {
        const val SIGN_UP: String = "auth/register"
        const val SIGN_IN: String = "auth/login"
        const val LOG_OUT: String = "auth/logout"
        const val OTP_VERIFY: String = "auth/verifyotp"
        const val SEND_OTP: String = "auth/sendotp"
        const val CHANGE_PASSWORD: String = "auth/changepassword"
        const val TAKE_USER_DETAILS: String = "user/details"
        const val DELETE_ACCOUNT: String = "user/delete-account"
        const val UPDATE_USER_DETAILS: String = "user"
        const val UPDATE_EMAIL: String = "user/update-email"
        const val VERIFY_OTP_FOR_UPDATE_EMAIL: String = "user/verify-email-otp"
        const val UPDATE_PASSWORD: String = "password/update"
        const val CALENDAR: String = "user/calendar"
        const val GET_PROGRAMS_GOALS_FOR_REMINDER: String = "user/program-and-goals"
        const val CREATE_UPDATE_REMINDER: String = "user/reminder"
        const val REGISTER_PUSH: String = "fcmtoken/createtoken"
    }

    object Main {
        const val PROGRAMS: String = "program"
        const val ADD_STRENGTH_LOG = "program/add-strength-log"
        const val RECIPE: String = "program/recipes/{recipeId}"
        const val PROGRAM_BY_ID: String = "program/{programId}"
        const val COMPLETE_REST_DAY: String = "program/completeRestDays"
        const val SEARCH_RECIPE = "program/recipes-search"
        const val SEARCH_PROGRAMS_AND_RECIPES = "program/program-recipes-workout-search"
        const val DAY_EXERCISES = "program/day-exercises/{day}/{programId}"
        const val EXERCISE_COMPLETED = "program/exercises-is-completed/{exerciseId}"
        const val STRENGTH_LOG = "program/get-strength-log"
        const val STRENGTH_LOG_EXERCISES = "program/get-strength-log-exercises"
        const val ON_DEMAND_CLASSES = "program/on-demand-class"
        const val PIN = "program/pin-item"
        const val FOR_ME = "program/get-for-me"

        //MY_PROGRESS
        const val LAST_WEEK_STATS = "progress/get-my-progress-goal-logs"
        const val TODAY_STATS = "progress/get-goals-logs"
        const val GOALS = "progress/get-goals"
        const val ADD_LOG = "progress/add-log"
        const val UPDATE_GOAL = "progress/goals/{goalId}"
        const val DELETE_GOAL = "progress/delete-goal/{goalId}"
        const val VIEW_GOAL = "progress/get-goal/{goalId}"
        const val CREATE_GOAL = "progress/add-goal"
        const val MY_PROGRESS = "progress/my-progress"

        //Notes
        const val GET_NOTES = "notes/get-user-notes"
        const val CREATE_NOTE = "notes/create-notes"
        const val DELETE_NOTE = "notes/delete-notes/{notesId}"
        const val UPDATE_NOTE = "notes/notes/{notesId}"
    }

    //Used for separate recipe api response.
    enum class ResultType(val value: Int) {
        FOR_GENERAL(1),
        FOR_ME(2)
    }

    object Home {
        const val HOME: String = "home/get-user-data"
    }

    object Community {
        const val GET_ALL_POSTS = "community/get-all-posts"
        const val COMMENT_ON_POST = "community/comment-post/{postId}"
        const val GET_COMMENTS = "community/get-comments/{postId}"
        const val GET_MY_POST = "community/get-user-posts"
        const val LIKE_POST = "community/like-post/{postId}"
        const val CREATE_POST = "community/create-post"
        const val ACCEPT_COMMUNITY_GUIDE_LINE = "community/community-guide-line"
        const val COMMUNITY_DELETE_POST = "community/delete-post/{postId}"
        const val COMMUNITY_EDIT_POST = "community/update-post/{postId}"
        const val COMMUNITY_DELETE_POST_IMAGE = "community/post-image-delete/{imageId}"
        const val COMMUNITY_REPORT_POST = "community/report-post"
    }
}