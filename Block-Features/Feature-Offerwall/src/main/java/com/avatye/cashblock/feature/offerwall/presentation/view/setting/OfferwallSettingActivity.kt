package com.avatye.cashblock.feature.offerwall.presentation.view.setting

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.Fragment
import com.avatye.cashblock.base.component.domain.entity.base.ActivityTransitionType
import com.avatye.cashblock.base.component.support.launch
import com.avatye.cashblock.feature.offerwall.R
import com.avatye.cashblock.feature.offerwall.databinding.AcbsoActivitySettingBinding
import com.avatye.cashblock.feature.offerwall.presentation.AppBaseActivity
import com.avatye.cashblock.feature.offerwall.presentation.view.main.OfferwallMainActivity

internal class OfferwallSettingActivity : AppBaseActivity() {

    companion object {
        fun open(activity: Activity, close: Boolean = false) {
            activity.launch(
                intent = Intent(activity, OfferwallSettingActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                },
                transition = ActivityTransitionType.NONE.value,
                close = close
            )
        }
    }

    private val vb: AcbsoActivitySettingBinding by lazy {
        AcbsoActivitySettingBinding.inflate(LayoutInflater.from(this))
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentViewWith(vb.root)
        vb.headerView.actionClose { finish() }
        transaction(OfferwallSettingFragment())
    }


    private fun transaction(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.setting_container, fragment)
            commit()
        }

    }

}