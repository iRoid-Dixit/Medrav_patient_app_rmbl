package com.griotlegacy.mobile.app.data.source.local

import com.griotlegacy.mobile.app.R
import com.griotlegacy.mobile.app.data.source.local.datastore.AppPreferenceDataStore


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
        SettingData(title = R.string.edit_profile, colors = R.color.white, true),
        SettingData(title = R.string.change_password, colors = R.color.white, true),
        SettingData(title = R.string.friend, colors = R.color.white, true),
        SettingData(title = R.string.invite_friend, colors = R.color.white, true),
        SettingData(title = R.string.notification_on, colors = R.color.white, false),
        SettingData(title = R.string.block, colors = R.color.white, true),
        SettingData(title = R.string.profile_profile_privacy, colors = R.color.white, false),
        SettingData(title = R.string.about_us, colors = R.color.white, true),
        SettingData(title = R.string.term_condition, colors = R.color.white, true),
        SettingData(title = R.string.privacy_and_policy, colors = R.color.white, true),
        SettingData(title = R.string.faq, colors = R.color.white, true),
        SettingData(title = R.string.legacy_reflection, colors = R.color.white, true),
        SettingData(title = R.string.advertisement, colors = R.color.white, true),
        SettingData(title = R.string.contact_us, colors = R.color.white, true),
        SettingData(title = R.string.storage, colors = R.color.white, true),
        SettingData(title = R.string.delete_account, R.color.red, false),
        SettingData(title = R.string.logout, R.color.red, false),
    )



}






