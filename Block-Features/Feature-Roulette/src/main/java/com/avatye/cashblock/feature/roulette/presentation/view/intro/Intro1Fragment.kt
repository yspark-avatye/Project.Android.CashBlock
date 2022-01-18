package com.avatye.cashblock.feature.roulette.presentation.view.intro

import android.os.Bundle
import android.view.View
import com.avatye.cashblock.base.component.contract.business.SettingContractor
import com.avatye.cashblock.base.component.support.toHtml
import com.avatye.cashblock.feature.roulette.R
import com.avatye.cashblock.feature.roulette.databinding.AcbsrFragmentIntro1Binding
import com.avatye.cashblock.feature.roulette.presentation.AppBaseFragment

internal class Intro1Fragment : AppBaseFragment<AcbsrFragmentIntro1Binding>(AcbsrFragmentIntro1Binding::inflate) {

    private val parentActivity: IntroActivity by lazy {
        activity as IntroActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding.introText11) {
            text = getString(R.string.acbsr_intro_1_1)
                .format(SettingContractor.appInfoSetting.rouletteName)
        }
        with(binding.introText13) {
            text = getString(R.string.acbsr_intro_1_3)
                .format(SettingContractor.appInfoSetting.pointName)
                .toHtml
        }
    }
}