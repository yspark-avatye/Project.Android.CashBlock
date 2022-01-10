package com.avatye.cashblock.feature.roulette.presentation.view.intro

import android.os.Bundle
import android.view.View
import com.avatye.cashblock.base.component.support.toHtml
import com.avatye.cashblock.feature.roulette.R
import com.avatye.cashblock.feature.roulette.databinding.AcbsrFragmentIntro2Binding
import com.avatye.cashblock.feature.roulette.presentation.AppBaseFragment

internal class Intro2Fragment : AppBaseFragment<AcbsrFragmentIntro2Binding>(AcbsrFragmentIntro2Binding::inflate) {

    private val parentActivity: IntroActivity by lazy {
        activity as IntroActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding.introText21) {
            text = getString(R.string.acbsr_intro_2_1).toHtml
        }
        with(binding.introText22) {
            text = getString(R.string.acbsr_intro_2_2).toHtml
        }
        with(binding.introText23) {
            text = getString(R.string.acbsr_intro_2_3).toHtml
        }
    }
}