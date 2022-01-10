package com.avatye.cashblock.base.component.widget.dialog

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.view.LayoutInflater
import androidx.core.widget.doOnTextChanged
import com.avatye.cashblock.base.FeatureCore
import com.avatye.cashblock.R
import com.avatye.cashblock.base.component.domain.entity.user.AgeVerifiedType
import com.avatye.cashblock.base.component.domain.model.contract.ContractResult
import com.avatye.cashblock.base.component.support.CoreUtil
import com.avatye.cashblock.base.component.support.takeIfNullOrEmpty
import com.avatye.cashblock.databinding.AcbCommonWidgetDialogAgeVerifyBinding
import com.avatye.cashblock.base.internal.preference.AccountPreferenceData
import com.avatye.cashblock.base.component.contract.data.UserDataContract
import com.avatye.cashblock.base.component.support.setOnClickWithDebounce
import org.joda.time.DateTime
import java.text.SimpleDateFormat

class DialogPopupAgeVerifyView private constructor(
    private val ownerActivity: Activity,
    private val actionCallback: IDialogAction
) : IDialogView {

    companion object {
        fun create(ownerActivity: Activity, callback: IDialogAction): DialogPopupAgeVerifyView {
            return DialogPopupAgeVerifyView(ownerActivity = ownerActivity, actionCallback = callback)
        }
    }

    interface IDialogAction {
        fun onAction()
        fun onClose()
    }

    private val tagName = "DialogPopupAgeVerifyView"
    private val vb: AcbCommonWidgetDialogAgeVerifyBinding by lazy {
        AcbCommonWidgetDialogAgeVerifyBinding.inflate(LayoutInflater.from(ownerActivity), null, false)
    }

    private var dialog: AlertDialog? = null
    private val builder: AlertDialog.Builder by lazy { AlertDialog.Builder(ownerActivity, R.style.CashBlock_Widget_Dialog).setView(vb.root) }
    private val loading: DialogLoadingView by lazy { DialogLoadingView.create(ownerActivity = ownerActivity) }

    init {
        with(vb.birthDate) {
            doOnTextChanged { _, _, _, _ ->
                vb.birthDateLength.text = "${vb.birthDate.text.toString().length}/8"
            }
        }
        vb.buttonAction.setOnClickWithDebounce {
            val result = checkAgeRange()
            if (result.isNotEmpty() && result.length == 8) {
                requestVerifyAge(result) {
                    if (it) {
                        this.dismiss()
                        AccountPreferenceData.update(ageVerifiedType = AgeVerifiedType.VERIFIED)
                        actionCallback.onAction()
                    } else {
                        vb.birthDate.requestFocus()
                    }
                }
            } else {
                vb.birthDate.requestFocus()
                CoreUtil.showToast(R.string.acb_common_age_popup_message_wrong_format)
            }
        }
        vb.buttonClose.setOnClickWithDebounce {
            this.dismiss()
            actionCallback.onClose()
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun checkAgeRange(): String {
        val currentDateTime = DateTime().toString("yyyyMMdd").toInt()
        val inputTextValue = vb.birthDate.text.toString()
        val birth = inputTextValue.toIntOrNull() ?: 0
        return if (birth in 19000101..currentDateTime) {
            try {
                val df = SimpleDateFormat("yyyyMMdd")
                df.isLenient = false
                df.parse(inputTextValue)
                inputTextValue
            } catch (e: Exception) {
                ""
            }
        } else {
            ""
        }
    }

    private fun requestVerifyAge(birthDate: String, callback: (success: Boolean) -> Unit) {
        loading.show(cancelable = false)
        UserDataContract(FeatureCore.coreBlockCode).let { user ->
            user.postVerifyAge(birthDate = birthDate) {
                when (it) {
                    is ContractResult.Success -> {
                        loading.dismiss()
                        callback(true)
                    }
                    is ContractResult.Failure -> {
                        loading.dismiss()
                        CoreUtil.showToast(it.message.takeIfNullOrEmpty {
                            ownerActivity.getString(R.string.acb_common_message_error)
                        })
                        callback(false)
                    }
                }
            }
        }
    }

    override fun show(cancelable: Boolean) {
        if (ownerActivity.isFinishing) {
            return
        }
        dialog = builder.create()
        dialog?.setCancelable(cancelable)
        dialog?.show()
    }

    override fun dismiss() {
        dialog?.dismiss()
    }

    override fun isAppeared(): Boolean {
        return this.dialog?.isShowing ?: false
    }
}