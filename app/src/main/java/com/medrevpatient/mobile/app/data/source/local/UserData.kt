package com.medrevpatient.mobile.app.data.source.local


import com.medrevpatient.mobile.app.data.source.local.datastore.AppPreferenceDataStore
import com.medrevpatient.mobile.app.R


object UserData {
    private val appPreferenceDataStore: AppPreferenceDataStore? = null

    data class SettingData(
        val title: Int,
        val colors: Int = R.color.warning_colors,
        val isArrowVisible: Boolean = false
    )

    data class BeforeServicePicture(
        val image: Int,
    )

    data class AllLegacyData(
        val title: String,
        val date: String,
        val image: List<BeforeServicePicture>
    )

    data class StorageTierPlan(
        val name: String,
        val size: String,
        val price: String,
        val iconRes: Int
    )

    val settingScreenListingData = listOf(
        SettingData(title = R.string.app_name, colors = R.color.white, true),
        SettingData(title = R.string.app_name, colors = R.color.white, true),
        SettingData(title = R.string.app_name, colors = R.color.white, true),
        SettingData(title = R.string.app_name, colors = R.color.white, true),
        SettingData(title = R.string.app_name, colors = R.color.white, false),
        SettingData(title = R.string.app_name, colors = R.color.white, true),
        SettingData(title = R.string.app_name, colors = R.color.white, false),
        SettingData(title = R.string.app_name, colors = R.color.white, true),
        SettingData(title = R.string.app_name, colors = R.color.white, true),
        SettingData(title = R.string.app_name, colors = R.color.white, true),
        SettingData(title = R.string.app_name, colors = R.color.white, true),
        SettingData(title = R.string.app_name, colors = R.color.white, true),
        SettingData(title = R.string.app_name, colors = R.color.white, true),
        SettingData(title = R.string.app_name, colors = R.color.white, true),
        SettingData(title = R.string.app_name, colors = R.color.white, true),
        SettingData(title = R.string.app_name, R.color.red, false),
        SettingData(title = R.string.app_name, R.color.red, false),
    )

    // Data class for question structure
    data class Question(
        val id: Int,
        val text: String,
        val options: List<String>
    )


     var questions = listOf(
        Question(
            id = 1,
            text = "Have you felt nausea since your last injection?",
            options = listOf("No", "A little", "Too much")
        ),
        Question(
            id = 2,
            text = "Have you felt constipated since your last injection?",
            options = listOf("No", "A little", "Too much")
        ),
        Question(
            id = 3,
            text = "Have you got diarrhea since your last injection?",
            options = listOf("No", "A little", "Too much")
        ),
        Question(
            id = 4,
            text = "Have you had stomach ache or acid reflux since your last injection?",
            options = listOf("No", "A little", "Too much")
        ),
        Question(
            id = 5,
            text = "Have you felt a lack of interest or pleasure in doing things over the past week?",
            options = listOf("No", "A little", "Too much")
        )
    )

}






