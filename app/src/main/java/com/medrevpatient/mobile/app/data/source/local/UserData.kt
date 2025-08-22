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

}






