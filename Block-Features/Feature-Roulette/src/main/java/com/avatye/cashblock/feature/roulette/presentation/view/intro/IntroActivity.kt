package com.avatye.cashblock.feature.roulette.presentation.view.intro

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.avatye.cashblock.base.FeatureCore
import com.avatye.cashblock.base.component.support.extraParcel
import com.avatye.cashblock.feature.roulette.R
import com.avatye.cashblock.feature.roulette.RouletteConfig
import com.avatye.cashblock.feature.roulette.RouletteConfig.logger
import com.avatye.cashblock.feature.roulette.component.data.PreferenceData
import com.avatye.cashblock.feature.roulette.component.model.parcel.IntroViewParcel
import com.avatye.cashblock.feature.roulette.databinding.AcbsrActivityIntroBinding
import com.avatye.cashblock.feature.roulette.presentation.AppBaseActivity
import com.avatye.cashblock.feature.roulette.presentation.view.main.RouletteMainActivity

internal class IntroActivity : AppBaseActivity() {

    companion object {
        /** this activity start */
        fun open(context: Context, source: Int = 0) {
            context.startActivity(
                Intent(context, IntroActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    putExtra(IntroViewParcel.NAME, IntroViewParcel(source = source))
                }
            )
        }
    }

    private enum class IntroStatus { INTRO_LOADING, INTRO, CONTENT_LOADING }

    private val vb: AcbsrActivityIntroBinding by lazy {
        AcbsrActivityIntroBinding.inflate(LayoutInflater.from(this))
    }

    private var introStatus: IntroStatus = IntroStatus.INTRO_LOADING
        set(value) {
            if (field != value) {
                field = value
                when (field) {
                    IntroStatus.INTRO_LOADING -> {
                        vb.introSkeleton.isVisible = true
                        vb.introContent.isVisible = false
                        vb.mainViewSkeleton.isVisible = false
                    }
                    IntroStatus.INTRO -> {
                        vb.introSkeleton.isVisible = false
                        vb.introContent.isVisible = true
                        vb.mainViewSkeleton.isVisible = false
                    }
                    IntroStatus.CONTENT_LOADING -> {
                        vb.introSkeleton.isVisible = false
                        vb.introContent.isVisible = false
                        vb.mainViewSkeleton.isVisible = true
                    }
                }
            }
        }

    private var pagerIndex: Int = 0
        set(value) {
            if (field != value) {
                field = value
                when (field) {
                    0 -> {
                        vb.introViewPagerIndicatorDot1.setImageResource(R.drawable.acb_common_dot_black)
                        vb.introViewPagerIndicatorDot2.setImageResource(R.drawable.acb_common_dot_gray)
                        vb.introViewPagerIndicatorDot3.setImageResource(R.drawable.acb_common_dot_gray)
                    }
                    1 -> {
                        vb.introViewPagerIndicatorDot1.setImageResource(R.drawable.acb_common_dot_gray)
                        vb.introViewPagerIndicatorDot2.setImageResource(R.drawable.acb_common_dot_black)
                        vb.introViewPagerIndicatorDot3.setImageResource(R.drawable.acb_common_dot_gray)
                    }
                    else -> {
                        vb.introViewPagerIndicatorDot1.setImageResource(R.drawable.acb_common_dot_gray)
                        vb.introViewPagerIndicatorDot2.setImageResource(R.drawable.acb_common_dot_gray)
                        vb.introViewPagerIndicatorDot3.setImageResource(R.drawable.acb_common_dot_black)
                    }
                }
            }
        }

    private val isFirstIntro = PreferenceData.First.isFirstIntro

    fun actionStart() {
        loadingView?.dismiss()
        introStatus = IntroStatus.CONTENT_LOADING
        PreferenceData.First.update(isFirstIntro = false)
        leakHandler.postDelayed({
            RouletteMainActivity.open(activity = this@IntroActivity, close = true)
        }, 250L)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentViewWith(
            view = vb.root,
            logKey = FeatureCore.CASHBLOCK_LOG_ROULETTE_INTRO,
            logParam = hashMapOf("source" to (extraParcel<IntroViewParcel>(IntroViewParcel.NAME)?.source ?: 0))
        )
        // init view
        loadingView?.show(cancelable = false)
        introStatus = if (isFirstIntro) IntroStatus.INTRO_LOADING else IntroStatus.CONTENT_LOADING
        // popup notice synchronization
        RouletteConfig.popupNoticeController.synchronization {
            when (isFirstIntro) {
                true -> actionShowGuide()
                false -> moveToMain()
            }
        }
    }

    private fun actionShowGuide() {
        vb.introViewPager.adapter = IntroPagerAdapter(supportFragmentManager)
        vb.introViewPager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                logger.i { "$viewTag -> actionIntro -> onPageSelected -> position:$position" }
                pagerIndex = position
            }
        })
        pagerIndex = 0
        introStatus = IntroStatus.INTRO
        loadingView?.dismiss()
    }

    private fun moveToMain() {
        RouletteMainActivity.open(activity = this@IntroActivity, close = true)
        finish()
    }

    private class IntroPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getCount() = 3
        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> Intro1Fragment()
                1 -> Intro2Fragment()
                else -> Intro3Fragment()
            }
        }
    }
}