package com.avatye.cashblock.feature.roulette.presentation.view.intro

import android.os.Bundle
import android.view.View
import com.avatye.cashblock.base.component.contract.RemoteContract
import com.avatye.cashblock.base.component.support.setOnClickWithDebounce
import com.avatye.cashblock.base.component.support.toHtml
import com.avatye.cashblock.feature.roulette.R
import com.avatye.cashblock.feature.roulette.databinding.AcbsrFragmentIntro3Binding
import com.avatye.cashblock.feature.roulette.presentation.AppBaseFragment

internal class Intro3Fragment : AppBaseFragment<AcbsrFragmentIntro3Binding>(AcbsrFragmentIntro3Binding::inflate) {

    private val parentActivity: IntroActivity by lazy {
        activity as IntroActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding.introText32) {
            this.text = getString(R.string.acbsr_intro_3_2)
                .format(RemoteContract.appInfoSetting.rouletteName)
        }
        with(binding.introText33) {
            this.text = getString(R.string.acbsr_intro_3_3)
                .format(RemoteContract.appInfoSetting.pointName).toHtml
        }
        with(binding.introComplete) {
            this.text = getString(R.string.acbsr_intro_start)
                .format(RemoteContract.appInfoSetting.rouletteName)
            this.setOnClickWithDebounce {
                parentActivity.actionStart()
            }
        }
    }
}