package com.avatye.cashblock.feature.roulette.presentation.view.notification

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import com.avatye.cashblock.base.component.domain.entity.base.ActivityTransitionType
import com.avatye.cashblock.base.component.support.launch
import com.avatye.cashblock.base.component.support.toHtml
import com.avatye.cashblock.feature.roulette.R
import com.avatye.cashblock.feature.roulette.databinding.AcbsrActivityNotificationSettingHelpBinding
import com.avatye.cashblock.feature.roulette.presentation.AppBaseActivity

internal class NotificationHelpActivity : AppBaseActivity() {

    internal companion object {
        val NAME: String = NotificationHelpActivity::class.java.simpleName
        fun open(activity: Activity, close: Boolean = false) {
            activity.launch(
                intent = Intent(activity, NotificationHelpActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                },
                transition = ActivityTransitionType.NONE.value,
                close = close
            )
        }
    }

    // binding
    private val vb: AcbsrActivityNotificationSettingHelpBinding by lazy {
        AcbsrActivityNotificationSettingHelpBinding.inflate(LayoutInflater.from(this))
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentViewWith(view = vb.root, logKey = "view:notification-help")
        vb.notificationHelpActionClose.setOnClickListener { finish() }
        vb.notificationHelpSectionTitle2.text = getString(R.string.acbsr_string_notification_setting_help_section_2_title).toHtml
        vb.notificationHelpSectionTitle3.text = getString(R.string.acbsr_string_notification_setting_help_section_3_title).toHtml
    }
}