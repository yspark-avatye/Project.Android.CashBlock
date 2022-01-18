package com.avatye.cashblock.feature.offerwall.presentation

import androidx.viewbinding.ViewBinding
import com.avatye.cashblock.base.component.domain.entity.base.ServiceType
import com.avatye.cashblock.base.component.domain.model.app.CoreBaseFragment
import com.avatye.cashblock.base.component.domain.model.app.Inflate
import com.avatye.cashblock.base.component.domain.model.parcel.ServiceNameParcel
import com.avatye.cashblock.base.component.support.extraParcel

internal abstract class AppBaseFragment<VB : ViewBinding>(inflate: Inflate<VB>) : CoreBaseFragment<VB>(inflate) {

    protected val serviceType: ServiceType? by lazy {
        activity?.extraParcel<ServiceNameParcel>(ServiceNameParcel.NAME)?.serviceType
    }

}