package com.avatye.cashblock.feature.offerwall.presentation.view.setting

import android.os.Bundle
import android.util.Log
import android.view.View
import com.avatye.cashblock.feature.offerwall.databinding.AcbsoFragmentSettingBinding
import com.avatye.cashblock.feature.offerwall.presentation.AppBaseFragment

internal class OfferwallSettingFragment : AppBaseFragment<AcbsoFragmentSettingBinding>(AcbsoFragmentSettingBinding::inflate), View.OnClickListener {

    private val parentActivity: OfferwallSettingActivity by lazy {
        activity as OfferwallSettingActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding.configRestoreAd) {

        }

        with(binding.configNotice) {

        }

        with(binding.configRewardInquiry) {

        }

        with(binding.configCsInquire) {

        }

        with(binding.configTerms) {

        }

        with(binding.configLicense) {

        }

        with(binding.configAlliance) {

        }


    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.configRestoreAd.id -> {

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