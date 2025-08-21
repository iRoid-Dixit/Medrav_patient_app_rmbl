package com.medrevpatient.mobile.app.utils

import com.medrevpatient.mobile.app.R

object Constants {

    const val ANDROID = 1

    object Intents {
        const val Player_ONE_KEY = "player_one_data"
    }

    object AppScreen {
        const val START_UP: String = "startUp"
        const val SIGN_IN: String = "sign-in"
        const val HOME: String = "home"
        const val SETTINGS: String = "settings"
        const val PROFILE: String = "profile"
    }

    object SubscriptionPlan {
        const val MONTHLY_PLAN = 1
        const val YEARLY_PLAN = 2
    }

    object DataStore {
        const val PREFERENCE_NAME = "medrev_patient_preference"
    }

    object IntentKeys {
        const val NEED_TO_OPEN = "needToOpen"
        const val START_DESTINATION_FOR_CONTAINER = "startDestinationForContainer"
        const val START_DESTINATION_FOR_MAIN = "startDestinationForMain"
        const val GOAL_ID = "goalId"
        const val PUSH_NOTIFICATION_RECEIVED = "pushNotificationReceived"
    }

    object Keywords {
        const val COMMUNITY = "community"
        const val MY_POST = "myPost"
        const val POST_DETAILS = "postDetails"
        const val ALL_PROGRAMS = "allPrograms"
        const val VIEW_GOALS = "viewGoals"
    }

    object RequestParams {
        const val PROFILE_IMAGE = "profileImage"
        const val BIRTH_DATE = "birthDate"
        const val HEIGHT_IN_FEET = "heightInFeet"
        const val HEIGHT_IN_INCH = "heightInInch"
        const val WEIGHT = "weight"
        const val BODY_TYPE = "bodyType"
        const val ENERGY_LEVEL = "energyLevel"
        const val LIFE_STYLE = "lifeStyle"
        const val FITNESS_LEVEL = "fitnessLevel"
        const val GOALS = "goals"
        const val FIRST_NAME = "firstName"
        const val LAST_NAME = "lastName"
        const val EMAIL = "email"
        const val COMMENT_TEXT = "text"
        const val CONTENT = "content"
        const val IMAGES = "images"
        const val DELETE_IMAGES = "deleteImages"
    }

    enum class Goals(val type: Int, val icon: Int, val displayName: String) {
        CALORIES(1, R.drawable.calorie, "Calories"),
        WATER(2, R.drawable.water, "Water"),
        SLEEP(3, R.drawable.sleep, "Sleep"),
        PROTEIN(4, R.drawable.protein, "Protein"),
        STEPS(5, R.drawable.steps, "Steps"),
        EXERCISE(6, R.drawable.exercise, "Exercise"),
        WEIGHT(7, R.drawable.weight, "Weight");

        companion object {
            fun getIconByType(type: Int): Int {
                return entries.firstOrNull { it.type == type }?.icon ?: R.drawable.steps
            }

            fun getNameByType(type: Int): String {
                return entries.firstOrNull { it.type == type }?.displayName ?: "Unknown"
            }
        }
    }

    enum class RecipeDifficultyLevel(val type: Int, val displayName: String) {
        VERY_EASY(1, "Very easy"),
        EASY(2, "Easy"),
        MEDIUM(3, "Medium"),
        HARD(3, "Hard");

        companion object {
            fun getNameByType(type: Int): String {
                return entries.firstOrNull { it.type == type }?.displayName ?: "Unknown"
            }
        }
    }

    object Paging {
        const val PER_PAGE = 20
        const val CATCH_SIZE = 100
        const val TYPE_ALL_POST = "allPost"
        const val TYPE_MY_POST = "myPost"
    }

    object FileParams{
        const val KEY_CLASS_JSON = "key_data_class_json"
        const val KEY_FILE_URL = "key_file_url"
        const val KEY_FILE_TYPE = "key_file_type"
        const val KEY_FILE_NAME = "key_file_name"
        const val KEY_PARENT_DIR = "key_parent_dir"
        const val KEY_CHILD_DIR = "key_child_dir"
        const val KEY_FILE_URI = "key_file_uri"
    }

    object NotificationConstants{
        const val PUSH_TYPE_PROGRAM = "1"
        const val PUSH_TYPE_GOAL = "2"
    }


}