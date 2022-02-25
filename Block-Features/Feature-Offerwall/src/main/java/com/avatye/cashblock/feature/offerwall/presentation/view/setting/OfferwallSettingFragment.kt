package com.avatye.cashblock.feature.offerwall.presentation.view.setting

import android.os.Bundle
import android.view.View
import com.avatye.cashblock.base.component.support.CoreUtil
import com.avatye.cashblock.feature.offerwall.R
import com.avatye.cashblock.feature.offerwall.component.controller.AdvertiseListController
import com.avatye.cashblock.feature.offerwall.component.data.PreferenceData
import com.avatye.cashblock.feature.offerwall.databinding.AcbsoFragmentSettingBinding
import com.avatye.cashblock.feature.offerwall.presentation.AppBaseFragment

internal class OfferwallSettingFragment : AppBaseFragment<AcbsoFragmentSettingBinding>(AcbsoFragmentSettingBinding::inflate), View.OnClickListener {

    private val parentActivity: SettingActivity by lazy {
        activity as SettingActivity
    }

    private var hiddenItems: List<String>? = null


    override fun onResume() {
        super.onResume()
        hiddenItems = PreferenceData.Hidden.hiddenItems
        setView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding.configRestoreAd) {
            setOnClickListener(this@OfferwallSettingFragment)
        }

        with(binding.configNotice) {
            setOnClickListener(this@OfferwallSettingFragment)
        }

        with(binding.configRewardInquiry) {
            setOnClickListener(this@OfferwallSettingFragment)
        }

        with(binding.configCsInquire) {
            setOnClickListener(this@OfferwallSettingFragment)
        }

        with(binding.configTerms) {
            setOnClickListener(this@OfferwallSettingFragment)
        }

        with(binding.configLicense) {
            setOnClickListener(this@OfferwallSettingFragment)
        }

        with(binding.configAlliance) {
            setOnClickListener(this@OfferwallSettingFragment)
        }
    }


    private fun setView() {
        hiddenItems = PreferenceData.Hidden.hiddenItems
        hiddenItems?.let {
            binding.configRestoreAd.setBackgroundResource(
                if (it.isEmpty()) {
                    R.drawable.acbso_shape_restore_hidden_ad
                } else {
                    R.drawable.acb_library_ad_frame_cta
                }
            )
        } ?: run {
            binding.configRestoreAd.setBackgroundResource(R.drawable.acbso_shape_restore_hidden_ad)
        }
    }


    private fun restoreAds() {
        hiddenItems?.let {
            if (it.isEmpty()) {
                CoreUtil.showToast(getString(R.string.acbso_offerwall_hide_restore_nothing_toast))
            } else {
                // hiddenItems update
                PreferenceData.Hidden.update(hiddenItems = listOf())

                // set View
                setView()

                // need refresh list
                AdvertiseListController.needRefreshList = true

                CoreUtil.showToast(getString(R.string.acbso_offerwall_hide_restore_toast))
            }
        } ?: run {
            CoreUtil.showToast(getString(R.string.acbso_offerwall_hide_restore_nothing_toast))
        }
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            binding.configRestoreAd.id -> {
                restoreAds()
            }
            binding.configNotice.id -> {

            }
            binding.configRewardInquiry.id -> {

            }
            binding.configCsInquire.id -> {

            }
            binding.configTerms.id -> {

            }
            binding.configLicense.id -> {

            }
            binding.configAlliance.id -> {
            }
        }
    }
}