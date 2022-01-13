package com.avatye.cashblock.feature.offerwall.presentation.view.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import com.avatye.cashblock.base.component.contract.business.ViewOpenContractor
import com.avatye.cashblock.base.component.domain.entity.base.ActivityTransitionType
import com.avatye.cashblock.base.component.support.launch
import com.avatye.cashblock.feature.offerwall.OfferwallConfig.logger
import com.avatye.cashblock.feature.offerwall.component.controller.AdvertiseController
import com.avatye.cashblock.feature.offerwall.databinding.AcbsoActivityOfferwallMainBinding
import com.avatye.cashblock.feature.offerwall.presentation.AppBaseActivity

internal class OfferwallMainActivity : AppBaseActivity() {

    companion object {
        fun open(activity: Activity, close: Boolean = false) {
            activity.launch(
                intent = Intent(activity, OfferwallMainActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                },
                transition = ActivityTransitionType.NONE.value,
                close = close
            )
        }
    }

    private val vb: AcbsoActivityOfferwallMainBinding by lazy {
        AcbsoActivityOfferwallMainBinding.inflate(LayoutInflater.from(this))
    }

    override fun receiveActionInspection() {
        leakHandler.post {
            ViewOpenContractor.openInspectionView(
                activity = this@OfferwallMainActivity,
                blockType = blockType,
                close = true
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentViewWith(vb.root)
        // region { banner }
        vb.bannerLinearView.bannerData = AdvertiseController.createBannerData()
        vb.bannerLinearView.requestBanner()
        // endregion
        logger.i(viewName = viewTag) { "onCreate" }
    }

    override fun onResume() {
        super.onResume()
        // banners
        vb.bannerLinearView.onResume()
    }

    override fun onPause() {
        super.onPause()
        // banners
        vb.bannerLinearView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        // banner
        vb.bannerLinearView.onDestroy()
    }
}