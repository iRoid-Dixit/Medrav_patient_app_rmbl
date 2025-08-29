package com.medrevpatient.mobile.app.ux.main.bottombar

import androidx.annotation.StringRes
import com.medrevpatient.mobile.app.navigation.NavRoute
import com.medrevpatient.mobile.app.ux.main.home.HomeRoute
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.ux.main.appointment.AppointmentRoute
import com.medrevpatient.mobile.app.ux.main.message.MessageRoute
import com.medrevpatient.mobile.app.ux.main.medication.MedicationRoute
import com.medrevpatient.mobile.app.ux.main.profile.ProfileRoute


enum class NavBarItem(
    val unselectedIconDrawableId: Int,
    val selectedIconDrawableId: Int,
    val route: NavRoute,
    @StringRes val textResId: Int? = null,
) {
    HOME(unselectedIconDrawableId = R.drawable.ic_unselected_home, selectedIconDrawableId = R.drawable.ic_selected_home, route = HomeRoute.createRoute(), textResId = R.string.home),
    APPOINTMENTS(unselectedIconDrawableId = R.drawable.ic_unselected_appointments, selectedIconDrawableId = R.drawable.ic_selected_appointments, route = AppointmentRoute.createRoute(), textResId = R.string.appointments),
    MEDICATIONS(unselectedIconDrawableId = R.drawable.ic_unselected_medication, selectedIconDrawableId = R.drawable.ic_selected_medication, route = MedicationRoute.createRoute(), textResId = R.string.medication),
    MESSAGES(unselectedIconDrawableId = R.drawable.ic_unselected_message, selectedIconDrawableId = R.drawable.ic_selected_message, route = MessageRoute.createRoute(), textResId = R.string.messages),
    PROFILE(unselectedIconDrawableId = R.drawable.ic_unselected_profile, selectedIconDrawableId = R.drawable.ic_selected_profile, route = ProfileRoute.createRoute(), textResId = R.string.profile);

    companion object {
        fun getNavBarItemRouteMap(): Map<NavBarItem, NavRoute> {
            return entries.associateWith { item -> item.route }

        }
    }
}

