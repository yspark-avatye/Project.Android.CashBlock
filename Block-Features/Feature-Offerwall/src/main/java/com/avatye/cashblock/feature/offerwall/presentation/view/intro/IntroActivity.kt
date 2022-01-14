package com.avatye.cashblock.feature.offerwall.presentation.view.intro

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import com.avatye.cashblock.base.CoreConstants
import com.avatye.cashblock.base.component.domain.entity.base.EntrySourceType
import com.avatye.cashblock.base.component.domain.entity.base.ServiceType
import com.avatye.cashblock.base.component.domain.model.parcel.ServiceNameParcel
import com.avatye.cashblock.base.component.support.extraParcel
import com.avatye.cashblock.feature.offerwall.OfferwallConfig
import com.avatye.cashblock.feature.offerwall.OfferwallConfig.logger
import com.avatye.cashblock.feature.offerwall.databinding.AcbsoActivityIntroBinding
import com.avatye.cashblock.feature.offerwall.presentation.AppBaseActivity
import com.avatye.cashblock.feature.offerwall.presentation.parcel.IntroViewParcel
import com.avatye.cashblock.feature.offerwall.presentation.view.main.OfferwallMainActivity


internal class IntroActivity : AppBaseActivity() {

    companion object {
        var entryIntent: Intent? = null
            private set

        /** this activity start */
        fun open(
            context: Context,
            serviceType: ServiceType,
            entryIntent: Intent? = null,
            entrySource: EntrySourceType = EntrySourceType.DIRECT
        ) {
            this.entryIntent = entryIntent
            context.startActivity(
                Intent(context, IntroActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    putExtra(IntroViewParcel.NAME, IntroViewParcel(source = entrySource.value))
                    putExtra(ServiceNameParcel.NAME, ServiceNameParcel(serviceType = serviceType))
                }
            )
        }
    }

    private val vb: AcbsoActivityIntroBinding by lazy {
        AcbsoActivityIntroBinding.inflate(LayoutInflater.from(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logger.i(viewName = viewTag) { "onCreate { serviceType: ${serviceType.name} }" }
        setContentViewWith(
            view = vb.root,
            logKey = if (serviceType == ServiceType.OFFERWALL) {
                CoreConstants.CASHBLOCK_LOG_OFFERWALL_ENTER
            } else {
                CoreConstants.CASHBLOCK_LOG_ROULETTE_OFFERWALL_ENTER
            },
            logParam = hashMapOf(
                "serviceName" to serviceType.value,
                "source" to (extraParcel<IntroViewParcel>(IntroViewParcel.NAME)?.source ?: 0)
            )
        )
        // init
        loadingView?.show(cancelable = false)
        OfferwallConfig.popupNoticeController.synchronization {
            entryIntent?.let {
                startActivity(it.apply {
                    putExtra(ServiceNameParcel.NAME, ServiceNameParcel(serviceType = serviceType))
                })
                finish()
            } ?: run {
                OfferwallMainActivity.open(activity = this@IntroActivity, serviceType = this.serviceType, close = true)
            }
        }
    }

    override fun onDestroy() {
        entryIntent = null
        super.onDestroy()
    }

}