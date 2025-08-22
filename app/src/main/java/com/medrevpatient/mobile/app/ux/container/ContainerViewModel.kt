package com.medrevpatient.mobile.app.ux.container

import com.medrevpatient.mobile.app.model.base.BaseViewModel
import com.medrevpatient.mobile.app.navigation.ViewModelNav
import com.medrevpatient.mobile.app.navigation.ViewModelNavImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ContainerViewModel
@Inject constructor(): BaseViewModel(), ViewModelNav by ViewModelNavImpl() {
}