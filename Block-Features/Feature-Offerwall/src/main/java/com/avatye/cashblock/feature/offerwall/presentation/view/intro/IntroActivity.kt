package com.avatye.cashblock.feature.offerwall.presentation.view.intro

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import com.avatye.cashblock.base.FeatureCore
import com.avatye.cashblock.base.component.support.extraParcel
import com.avatye.cashblock.feature.offerwall.databinding.AcbsoActivityIntroBinding
import com.avatye.cashblock.feature.offerwall.presentation.AppBaseActivity
import com.avatye.cashblock.feature.offerwall.presentation.parcel.IntroViewParcel
import com.avatye.cashblock.feature.offerwall.presentation.view.main.OfferwallMainActivity


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

    private val vb: AcbsoActivityIntroBinding by lazy {
        AcbsoActivityIntroBinding.inflate(LayoutInflater.from(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentViewWith(
            view = vb.root,
            logKey = FeatureCore.CASHBLOCK_LOG_OFFERWALL_INTRO,
            logParam = hashMapOf("source" to (extraParcel<IntroViewParcel>(IntroViewParcel.NAME)?.source ?: 0))
        )

        loadingView?.show(cancelable = false)
        leakHandler.postDelayed({
            loadingView?.dismiss()
            OfferwallMainActivity.open(activity = this@IntroActivity, close = true)
        }, 1500L)
    }

}