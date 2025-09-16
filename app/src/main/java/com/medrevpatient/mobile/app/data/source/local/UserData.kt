package com.medrevpatient.mobile.app.data.source.local


import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import com.medrevpatient.mobile.app.data.source.local.datastore.AppPreferenceDataStore
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.ui.theme.Green4C
import com.medrevpatient.mobile.app.ui.theme.RedOrange
import com.medrevpatient.mobile.app.ui.theme.fulvous
import com.medrevpatient.mobile.app.ui.theme.violetsAreBlue


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
    data class RecentActivity(
        val title: String,
        val timestamp: String,
        val iconRes: Int,
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

    val medications = listOf(
        Medication(
            id = "1",
            name = "Metformin",
            dosage = "500mg",
            frequency = "Once daily",
            icon = R.drawable.ic_selected_medication,
            status = MedicationStatus.DUE_SOON,
            time = "2:00 PM"
        ),
        Medication(
            id = "2",
            name = "Ozempic",
            dosage = "0.5mg",
            frequency = "Weekly injection",
            icon = R.drawable.ic_injection,
            status = MedicationStatus.TAKEN,
            time = "8:00 AM",
            nextDoseInfo = "Great job! Next dose due in 6 days"
        ),
        Medication(
            id = "3",
            name = "Vitamin D3",
            dosage = "1000 IU",
            frequency = "Daily",
            icon = R.drawable.ic_selected_medication,
            status = MedicationStatus.MISSED,
            time = "9:00 AM"
        ),
        Medication(
            id = "4",
            name = "Multivitamin",
            dosage = "1 tablet",
            frequency = "Daily",
            icon = R.drawable.ic_selected_medication,
            status = MedicationStatus.TAKEN,
            time = "7:30 AM",
            nextDoseInfo = "Next dose tomorrow at 7:30 AM"
        )
    )
    enum class MedicationStatus(
        val displayText: String,
        val textColor: Color,
    ) {
        TAKEN("Taken", Green4C),
        DUE_SOON("Due Soon", violetsAreBlue),
        MISSED("Missed", RedOrange),
    }

    val summary = MedicationSummary(
        totalMedications = 4,
        taken = 2,
        due = 1,
        skipped = 0,
        missed = 1
    )

    data class MedicationSummary(
        val totalMedications: Int,
        val taken: Int,
        val due: Int,
        val skipped: Int,
        val missed: Int
    )

    data class Medication(
        val id: String,
        val name: String,
        val dosage: String,
        val frequency: String,
        @DrawableRes val icon: Int, // Using emoji instead of drawable resource
        val status: MedicationStatus,
        val time: String,
        val nextDoseInfo: String? = null
    )

    data class ProfileItem(
        @DrawableRes val icon: Int,
        val title: Int,
        val isArrowVisible: Boolean = true
    )
    val profileItems = listOf(
        ProfileItem(
            icon = R.drawable.ic_edit_profile,
            title = R.string.edit_profile,
            isArrowVisible = true
        ),
        ProfileItem(
            icon = R.drawable.ic_lock,
            title = R.string.change_password,
            isArrowVisible = true
        ),
        ProfileItem(
            icon = R.drawable.ic_customer,
            title = R.string.customer_service,
            isArrowVisible = true
        ),
        ProfileItem(
            icon = R.drawable.ic_delete,
            title = R.string.delete_account,
            isArrowVisible = true
        ),
        ProfileItem(
            icon = R.drawable.ic_logout,
            title = R.string.logout,
            isArrowVisible = true
        )
    )
    data class StatusIndicatorData(
        val icon: Int,
        val count: Int,
        val label: String,
        val color: Color
    )
    val statusList = listOf(
        StatusIndicatorData(R.drawable.ic_right_mark, 2, "Taken", Green4C),
        StatusIndicatorData(R.drawable.ic_clock, 1, "Due", violetsAreBlue),
        StatusIndicatorData(R.drawable.ic_pause, 0, "Skipped", fulvous),
        StatusIndicatorData(R.drawable.ic_wrong_mark, 1, "Missed", RedOrange),
    )
}






