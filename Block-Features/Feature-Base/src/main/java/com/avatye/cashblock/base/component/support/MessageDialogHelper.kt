package com.avatye.cashblock.base.component.support

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.StringRes
import com.avatye.cashblock.BuildConfig
import com.avatye.cashblock.R
import com.avatye.cashblock.base.Core
import com.avatye.cashblock.base.MODULE_NAME
import com.avatye.cashblock.base.block.BlockType
import com.avatye.cashblock.base.component.contract.business.AccountContractor
import com.avatye.cashblock.base.component.contract.business.SettingContractor
import com.avatye.cashblock.base.component.widget.dialog.DialogMessageView
import com.avatye.cashblock.base.library.LogHandler
import java.util.*

object MessageDialogHelper {

    fun confirm(activity: Activity, @StringRes message: Int): DialogMessageView {
        return confirm(activity, activity.getString(message), onConfirm = {})
    }

    fun confirm(activity: Activity, message: String): DialogMessageView {
        return confirm(activity, message, onConfirm = {})
    }

    fun confirm(activity: Activity, messageSequence: CharSequence): DialogMessageView {
        return confirm(activity = activity, messageSequence = messageSequence, onConfirm = {})
    }

    fun determine(activity: Activity, @StringRes message: Int): DialogMessageView {
        return determine(activity, activity.getString(message), onPositive = {}, onNegative = {})
    }

    fun determine(activity: Activity, message: String): DialogMessageView {
        return determine(activity, message, onPositive = {}, onNegative = {})
    }

    fun determine(activity: Activity, messageSequence: CharSequence): DialogMessageView {
        return determine(
            activity = activity,
            messageSequence = messageSequence,
            onPositive = {},
            onNegative = {}
        )
    }

    fun confirm(
        activity: Activity,
        @StringRes message: Int,
        onConfirm: () -> Unit
    ): DialogMessageView {
        return DialogMessageView.create(activity).apply {
            this.setMessage(message)
            this.setPositiveButton {
                onConfirm()
            }
        }
    }

    fun confirm(activity: Activity, message: String, onConfirm: () -> Unit): DialogMessageView {
        return DialogMessageView.create(activity).apply {
            this.setMessage(message)
            this.setPositiveButton {
                onConfirm()
            }
        }
    }

    fun confirm(
        activity: Activity,
        messageSequence: CharSequence,
        onConfirm: () -> Unit
    ): DialogMessageView {
        return DialogMessageView.create(activity).apply {
            this.setMessage(messageSequence)
            this.setPositiveButton {
                onConfirm()
            }
        }
    }

    fun determine(
        activity: Activity,
        message: String,
        onPositive: () -> Unit = {},
        onNegative: () -> Unit = {}
    ): DialogMessageView {
        return DialogMessageView.create(activity).apply {
            this.setMessage(message)
            this.setPositiveButton { onPositive() }
            this.setNegativeButton { onNegative() }
        }
    }

    fun determine(
        activity: Activity,
        @StringRes message: Int,
        onPositive: () -> Unit = {},
        onNegative: () -> Unit = {}
    ): DialogMessageView {
        return DialogMessageView.create(activity).apply {
            this.setMessage(message)
            this.setPositiveButton { onPositive() }
            this.setNegativeButton { onNegative() }
        }
    }

    fun determine(
        activity: Activity,
        messageSequence: CharSequence,
        onPositive: () -> Unit = {},
        onNegative: () -> Unit = {}
    ): DialogMessageView {
        return DialogMessageView.create(activity).apply {
            this.setMessage(messageSequence)
            this.setPositiveButton { onPositive() }
            this.setNegativeButton { onNegative() }
        }
    }

    fun determine(
        activity: Activity,
        @StringRes message: Int,
        positiveText: Int? = null,
        negativeText: Int? = null,
        onPositive: () -> Unit = {},
        onNegative: () -> Unit = {}
    ): DialogMessageView {
        return DialogMessageView.create(activity).apply {
            this.setMessage(message)
            // positive button
            positiveText?.let {
                this.setPositiveButton(resourceID = it, callback = { onPositive() })
            } ?: run {
                this.setPositiveButton { onPositive() }
            }
            // negative button
            negativeText?.let {
                this.setNegativeButton(resourceID = it, callback = { onNegative() })
            } ?: run {
                this.setNegativeButton { onNegative() }
            }
        }
    }

    fun determine(
        activity: Activity,
        message: String,
        positiveText: Int? = null,
        negativeText: Int? = null,
        onPositive: () -> Unit = {},
        onNegative: () -> Unit = {}
    ): DialogMessageView {
        return DialogMessageView.create(activity).apply {
            this.setMessage(message)
            // positive button
            positiveText?.let {
                this.setPositiveButton(resourceID = it, callback = { onPositive() })
            } ?: run {
                this.setPositiveButton { onPositive() }
            }
            // negative button
            negativeText?.let {
                this.setNegativeButton(resourceID = it, callback = { onNegative() })
            } ?: run {
                this.setNegativeButton { onNegative() }
            }
        }
    }

    fun requestSuggestion(activity: Activity, blockType: BlockType) {
        try {
            val appInfoSetting = SettingContractor.appInfoSetting
            val suggestionText = "mailto:help@avatye.com"
                .plus("?subject=${Uri.encode("[${appInfoSetting.appName}] 문의")}")
                .plus("&body=")
                .plus(Uri.encode("\n\n\n\n\n\n\n\n\n"))
                .plus(Uri.encode("\n--------------------"))
                .plus(Uri.encode("\nSystem: Android"))
                .plus(
                    Uri.encode(
                        "\nDevice: ${
                            Build.MODEL.replace("\\s".toRegex(), "-").toUpperCase(Locale.ROOT)
                        }"
                    )
                )
                // os
                .plus(Uri.encode("\nOSVersion: ${Build.VERSION.SDK_INT}"))
                // app
                .plus(Uri.encode("\nApp-ID: ${Core.appId}"))
                .plus(Uri.encode("\nApp-Name: ${appInfoSetting.appName}"))
                .plus(Uri.encode("\nApp-Version: ${Core.appVersionName}"))
                .plus(Uri.encode("\nApp-BlockName: ${blockType.name}"))
                // sdk
                .plus(Uri.encode("\nSDK-UserID: ${AccountContractor.sdkUserId}"))
                .plus(Uri.encode("\nSDK-Version: ${BuildConfig.X_BUILD_SDK_VERSION_NAME}"))
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse(suggestionText)
            }
            activity.startActivity(intent)
        } catch (e: Exception) {
            LogHandler.e(moduleName = MODULE_NAME, throwable = e) { "requestSuggestion()" }
            CoreUtil.showToast(R.string.acb_common_message_error)
        }
    }


    fun requestAlliance(activity: Activity, blockType: BlockType) {
        try {
            val appInfoSetting = SettingContractor.appInfoSetting
            val suggestionText = "mailto:business@avatye.com"
                .plus("?subject=${Uri.encode("[캐시룰렛] 제휴 문의")}")
                .plus("&body=")
                .plus(Uri.encode("\n\n\n\n\n\n\n\n\n"))
                .plus(Uri.encode("\n--------------------"))
                .plus(Uri.encode("\nSystem: Android"))
                // app
                .plus(Uri.encode("\nApp-ID: ${Core.appId}"))
                .plus(Uri.encode("\nApp-Name: ${appInfoSetting.appName}"))
                .plus(Uri.encode("\nApp-Version: ${Core.appVersionName}"))
                .plus(Uri.encode("\nApp-BlockName: ${blockType.name}"))
                // sdk
                .plus(Uri.encode("\nSDK-UserID: ${AccountContractor.sdkUserId}"))
                .plus(Uri.encode("\nSDK-Version: ${BuildConfig.X_BUILD_SDK_VERSION_NAME}"))
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse(suggestionText)
            }
            activity.startActivity(intent)
        } catch (e: Exception) {
            LogHandler.e(moduleName = MODULE_NAME, throwable = e) { "requestAlliance()" }
            CoreUtil.showToast(R.string.acb_common_message_error)
        }
    }


    fun showSystemSettingDialog(activity: Activity) {
        activity.startActivity(Intent().apply {
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                    action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                    putExtra(Settings.EXTRA_APP_PACKAGE, Core.appPackageName)
                }
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> {
                    action = "android.settings.APP_NOTIFICATION_SETTINGS"
                    putExtra("app_package", Core.appPackageName)
                    putExtra("app_uid", activity.applicationInfo?.uid)
                }
                else -> {
                    action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    addCategory(Intent.CATEGORY_DEFAULT)
                    data = Uri.parse("package:${Core.appPackageName}")
                }
            }
        })
    }
}