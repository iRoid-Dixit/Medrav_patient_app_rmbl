package com.medrevpatient.mobile.app.model.domain.type

import androidx.annotation.StringRes
import com.medrevpatient.mobile.app.R

enum class ProfileType(
    @StringRes val typeId: Int
) {
    FULL_PROFILE(R.string.full_profile),
    SOME_PART_OF_PROFILE(R.string.some_part_of_profile)
}