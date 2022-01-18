package com.avatye.cashblock.feature.offerwall.presentation

import android.os.Bundle
import com.avatye.cashblock.R
import com.avatye.cashblock.base.block.BlockType
import com.avatye.cashblock.base.component.domain.entity.base.ServiceType
import com.avatye.cashblock.base.component.domain.model.app.CoreBaseActivity
import com.avatye.cashblock.base.component.domain.model.parcel.ServiceNameParcel
import com.avatye.cashblock.base.component.support.CoreUtil
import com.avatye.cashblock.base.component.support.extraParcel
import com.avatye.cashblock.feature.offerwall.OfferwallConfig

internal abstract class AppBaseActivity : CoreBaseActivity() {

    override val blockType: BlockType = OfferwallConfig.blockType

    private val _serviceType: ServiceType? by lazy {
        extraParcel<ServiceNameParcel>(ServiceNameParcel.NAME)?.serviceType
    }

    protected val serviceType: ServiceType get() = _serviceType!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (_serviceType == null) {
            CoreUtil.showToast(R.string.acb_common_message_error)
            finish()
        }
    }

}