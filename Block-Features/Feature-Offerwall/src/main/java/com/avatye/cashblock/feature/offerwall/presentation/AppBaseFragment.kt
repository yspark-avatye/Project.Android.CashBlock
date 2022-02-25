package com.avatye.cashblock.feature.offerwall.presentation

import androidx.viewbinding.ViewBinding
import com.avatye.cashblock.base.component.domain.entity.base.ServiceType
import com.avatye.cashblock.base.component.domain.model.app.CoreBaseFragment
import com.avatye.cashblock.base.component.domain.model.app.Inflate
import com.avatye.cashblock.base.component.domain.model.parcel.ServiceNameParcel
import com.avatye.cashblock.base.component.support.extraParcel
import com.avatye.cashblock.base.library.LogHandler

internal abstract class AppBaseFragment<VB : ViewBinding>(inflate: Inflate<VB>) : CoreBaseFragment<VB>(inflate) {

    private var isDestroyed: Boolean = false

    protected val serviceType: ServiceType? by lazy {
        activity?.extraParcel<ServiceNameParcel>(ServiceNameParcel.NAME)?.serviceType
    }

    val isAvailable:Boolean
        get() {
            return try {
                activity != null && isAdded
            } catch (e: Exception) {
                LogHandler.e(moduleName = this::class.java.simpleName, throwable = e)
                false
            }
        }

}