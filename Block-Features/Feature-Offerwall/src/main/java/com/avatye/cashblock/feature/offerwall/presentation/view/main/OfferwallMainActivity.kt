package com.avatye.cashblock.feature.offerwall.presentation.view.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import com.avatye.cashblock.base.component.contract.business.CoreContractor
import com.avatye.cashblock.base.component.contract.business.ViewOpenContractor
import com.avatye.cashblock.base.component.domain.entity.base.ActivityTransitionType
import com.avatye.cashblock.base.component.domain.entity.base.ServiceType
import com.avatye.cashblock.base.component.domain.model.parcel.ServiceNameParcel
import com.avatye.cashblock.base.component.support.launch
import com.avatye.cashblock.base.component.widget.banner.BannerLinearView
import com.avatye.cashblock.feature.offerwall.OfferwallConfig.logger
import com.avatye.cashblock.feature.offerwall.component.controller.AdvertiseController
import com.avatye.cashblock.feature.offerwall.databinding.AcbsoActivityOfferwallMainBinding
import com.avatye.cashblock.feature.offerwall.presentation.AppBaseActivity
import com.avatye.cashblock.feature.offerwall.presentation.view.setting.OfferwallSettingActivity

internal class OfferwallMainActivity : AppBaseActivity() {

    companion object {
        fun open(
            activity: Activity,
            serviceType: ServiceType,
            close: Boolean = false
        ) {
            activity.launch(
                intent = Intent(activity, OfferwallMainActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    putExtra(ServiceNameParcel.NAME, ServiceNameParcel(serviceType = serviceType))
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
                blockType = getBlockType(),
                close = true
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentViewWith(vb.root)
        logger.i(viewName = viewTag) { "onCreate { serviceType: ${serviceType.name} }" }
        // region { banner }
        vb.bannerLinearView.bannerData = AdvertiseController.createBannerData()
        vb.bannerLinearView.sourceType = BannerLinearView.SourceType.OFFERWALL
        vb.bannerLinearView.requestBanner()
        // endregion

        with(vb.headerView) {
            actionMore { OfferwallSettingActivity.open(activity = this@OfferwallMainActivity, serviceType = serviceType, close = false) }
            actionClose { finish() }
        }

        logger.i(viewName = viewTag) { "onCreate" }

        // region # deviceAAID
        CoreContractor.DeviceSetting.fetchAAID {
            logger.i(viewName = viewTag) {
                "CoreContractor.DeviceSetting.fetchAAID { complete: $it }"
            }
        }
        // endregion

        transactionFragment(fragment = OfferwallMainFragment())
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

    private fun transactionFragment(fragment: Fragment) {
//        supportFragmentManager.beginTransaction().apply {
//            replace(R.id.offerwall_list_container, fragment)
//            commit()
//        }
    }

}