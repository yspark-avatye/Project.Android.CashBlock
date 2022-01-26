package com.avatye.cashblock.feature.offerwall.presentation.view.inquiry

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import com.avatye.cashblock.base.component.domain.entity.base.ActivityTransitionType
import com.avatye.cashblock.base.component.support.launch
import com.avatye.cashblock.feature.offerwall.R
import com.avatye.cashblock.feature.offerwall.databinding.AcbsoActivityInquiryRewardBinding
import com.avatye.cashblock.feature.offerwall.presentation.AppBaseActivity

internal class InquiryRewardActivity : AppBaseActivity() {

    companion object {
        fun open(activity: Activity, close: Boolean = false) {
            activity.launch(
                intent = Intent(activity, InquiryRewardActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                },
                transition = ActivityTransitionType.NONE.value,
                close = close
            )
        }
    }

    private val vb: AcbsoActivityInquiryRewardBinding by lazy {
        AcbsoActivityInquiryRewardBinding.inflate(LayoutInflater.from(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentViewWith(vb.root)
        vb.headerView.actionBack { finish() }

        transactionFragment(InquiryRewardFragment())
    }

    private fun transactionFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.inquiry_reward_container, fragment)
            commit()
        }
    }
}