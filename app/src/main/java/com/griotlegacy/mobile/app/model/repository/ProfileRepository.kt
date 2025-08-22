package com.griotlegacy.mobile.app.model.repository


import com.griotlegacy.mobile.app.model.domain.type.ProfileType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepository
@Inject constructor(){
    fun getSelectedProfileTypeFlow(profileType: ProfileType?): Flow<String> {
        return when(profileType){
            ProfileType.FULL_PROFILE -> flowOf("Full Profile")
            ProfileType.SOME_PART_OF_PROFILE -> flowOf("Some Part Of Profile")
            else -> emptyFlow()
        }
    }

    fun getProfileId(profileId: Int): Flow<Int>{
        return flowOf(profileId)
    }
}