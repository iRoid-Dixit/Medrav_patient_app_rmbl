package com.griotlegacy.mobile.app.ux.container

import com.griotlegacy.mobile.app.model.base.BaseViewModel
import com.griotlegacy.mobile.app.navigation.ViewModelNav
import com.griotlegacy.mobile.app.navigation.ViewModelNavImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ContainerViewModel
@Inject constructor(): BaseViewModel(), ViewModelNav by ViewModelNavImpl() {
}