package com.avatye.cashblock.feature.roulette.component.widget.dialog

import android.app.AlertDialog
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.avatye.cashblock.base.component.contract.RemoteContract
import com.avatye.cashblock.base.component.contract.data.MissionDataContract
import com.avatye.cashblock.base.component.domain.entity.mission.MissionStateEntity
import com.avatye.cashblock.base.component.domain.model.app.CoreBaseActivity
import com.avatye.cashblock.base.component.domain.model.contract.ContractResult
import com.avatye.cashblock.base.component.support.CoreUtil
import com.avatye.cashblock.base.component.support.setOnClickWithDebounce
import com.avatye.cashblock.base.component.support.toHtml
import com.avatye.cashblock.base.component.widget.dialog.DialogLoadingView
import com.avatye.cashblock.base.component.widget.dialog.DialogMessageView
import com.avatye.cashblock.base.component.widget.dialog.IDialogView
import com.avatye.cashblock.feature.roulette.RouletteConfig
import com.avatye.cashblock.feature.roulette.R
import com.avatye.cashblock.feature.roulette.component.data.PreferenceData
import com.avatye.cashblock.feature.roulette.component.livedata.TicketBalanceLiveData
import com.avatye.cashblock.feature.roulette.databinding.AcbsrItemAttendanceRewardBinding
import com.avatye.cashblock.feature.roulette.databinding.AcbsrWidgetDialogPopupAttendanceBinding
import org.joda.time.DateTime

class DialogPopupAttendanceView private constructor(
    private val ownerActivity: CoreBaseActivity,
    private val attendanceList: MutableList<MissionStateEntity>
) : IDialogView {

    internal companion object {
        fun create(
            ownerActivity: CoreBaseActivity,
            attendanceList: MutableList<MissionStateEntity>
        ): DialogPopupAttendanceView {
            return DialogPopupAttendanceView(ownerActivity, attendanceList)
        }
    }

    private var dialog: AlertDialog? = null
    private val vb: AcbsrWidgetDialogPopupAttendanceBinding by lazy {
        AcbsrWidgetDialogPopupAttendanceBinding.inflate(LayoutInflater.from(ownerActivity), null, false)
    }
    private val builder: AlertDialog.Builder by lazy {
        AlertDialog.Builder(ownerActivity, R.style.CashBlock_Widget_Dialog).setView(vb.root)
    }
    private val loadingView: DialogLoadingView by lazy {
        DialogLoadingView.create(ownerActivity = ownerActivity)
    }

    var missionStateList: MutableList<MissionStateEntity>? = null
        set(value) {
            field = value
            field?.let {
                with(vb.attendanceList) {
                    setHasFixedSize(true)
                    post {
                        adapter = AttendanceListAdapter(it)
                        vb.attendanceDummy.isVisible = false
                    }
                }
            }
        }

    init {
        vb.attendanceList.setHasFixedSize(true)
        missionStateList = attendanceList
        vb.attendanceWarningDescription1.text = ownerActivity.getString(R.string.acbsr_string_attendance_dialog_warning_description_1)
        vb.attendanceWarningDescription2.text = ownerActivity.getString(R.string.acbsr_string_attendance_dialog_warning_description_2).toHtml
        vb.attendanceWarningDescription2.isVisible = RemoteContract.appInfoSetting.allowMoreMenu
        vb.attendanceClose.setOnClickListener { actionDismiss() }
        vb.attendanceList.post {
            vb.attendanceDummy.isVisible = false
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
        return dialog?.isShowing ?: false
    }

    private fun actionDismiss() {
        // CHECK REWARD
        val result = missionStateList?.filter { it.status == 2 }?.size ?: 0
        if (result > 0 && !RemoteContract.appInfoSetting.allowMoreMenu) {
            if (ownerActivity.isFinishing) {
                return
            }
            DialogMessageView.create(ownerActivity)
                .setMessage(R.string.acbsr_string_attendance_dialog_alert_message)
                .setNegativeButton(R.string.acb_common_button_cancel)
                .setPositiveButton(R.string.acb_common_button_close) {
                    dialog?.dismiss()
                    PreferenceData.Mission.Attendance.update(showDateTime = DateTime())
                }
                .show(cancelable = false)
        } else {
            dialog?.dismiss()
            PreferenceData.Mission.Attendance.update(showDateTime = DateTime())
        }
    }

    // region // ATTENDANCE LIST
    inner class AttendanceListAdapter(private val list: MutableList<MissionStateEntity>) : RecyclerView.Adapter<AttendanceListAdapter.ItemViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
            val itemBinding = AcbsrItemAttendanceRewardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ItemViewHolder(itemBinding)
        }

        override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
            holder.viewBind(entity = list[position])
        }


        override fun getItemCount() = list.size


        inner class ItemViewHolder(private val ivb: AcbsrItemAttendanceRewardBinding) : RecyclerView.ViewHolder(ivb.root) {
            fun viewBind(entity: MissionStateEntity) {
                ivb.listItemAttendanceItemReward.text = if (entity.itemName.isNotEmpty()) entity.itemName else "출석체크 완료"
                ivb.listItemAttendanceItemDay.text = ownerActivity.getString(R.string.acbsr_string_attendance_reward_item_day).format(entity.missionActionValue)
                when (entity.status) {
                    1 -> {
                        ivb.listItemAttendanceItemContainer.setBackgroundResource(R.drawable.acbsr_frame_attendance_enable)
                        ivb.listItemAttendanceItemReward.setBackgroundResource(R.drawable.acbsr_frame_attendance_reward_complete)
                        ivb.listItemAttendanceItemReward.setTextColor(Color.parseColor("#690004"))
                        ivb.listItemAttendanceItemDivider.setBackgroundColor(Color.parseColor("#BC221F"))
                        ivb.listItemAttendanceItemCheck.setImageResource(R.drawable.acbsr_ic_attendance_check_complete)
                        ivb.listItemAttendanceItemReward.setTextColor(Color.parseColor("#690004"))
                    }
                    2 -> {
                        ivb.listItemAttendanceItemContainer.setBackgroundResource(R.drawable.acbsr_frame_attendance_enable)
                        ivb.listItemAttendanceItemReward.setBackgroundResource(R.drawable.acbsr_frame_attendance_reward_enable)
                        ivb.listItemAttendanceItemReward.setTextColor(Color.parseColor("#212121"))
                        ivb.listItemAttendanceItemDivider.setBackgroundColor(Color.parseColor("#BC221F"))
                        ivb.listItemAttendanceItemCheck.setImageResource(R.drawable.acbsr_ic_attendance_check_enable)
                        ivb.listItemAttendanceItemReward.setTextColor(Color.parseColor("#212121"))
                        ivb.listItemAttendanceItemContainer.setOnClickWithDebounce {}
                    }
                    else -> {
                        ivb.listItemAttendanceItemContainer.setBackgroundResource(R.drawable.acbsr_frame_attendance_disable)
                        ivb.listItemAttendanceItemReward.setBackgroundResource(R.drawable.acbsr_frame_attendance_reward_disable)
                        ivb.listItemAttendanceItemReward.setTextColor(Color.parseColor("#979797"))
                        ivb.listItemAttendanceItemDivider.setBackgroundColor(Color.parseColor("#979797"))
                        ivb.listItemAttendanceItemCheck.setImageResource(R.drawable.acbsr_ic_attendance_check_disable)
                        ivb.listItemAttendanceItemReward.setTextColor(Color.parseColor("#979797"))
                    }
                }
                if (entity.actionAvailable) {
                    ivb.listItemAttendanceItemContainer.setOnClickWithDebounce {
                        requestReward(entity.missionActionValue)
                    }
                }
            }
        }
    }
    // endregion


    private fun requestReward(actionValue: Int) {
        MissionDataContract(blockCode = RouletteConfig.blockCode).let { apiContract ->
            loadingView.show(cancelable = false)
            apiContract.postAction(missionID = RemoteContract.missionSetting.attendanceId, actionValue = actionValue) {
                when (it) {
                    is ContractResult.Success -> {
                        PreferenceData.Mission.Attendance.update(checkDateTime = DateTime())
                        TicketBalanceLiveData.synchronization { _, _ ->
                            missionStateList = it.contract.stateItems
                            if (it.contract.rewardMessage.isNotEmpty()) {
                                CoreUtil.showToast(it.contract.rewardMessage)
                            }
                        }
                        loadingView.dismiss()
                    }
                    is ContractResult.Failure -> {
                        loadingView.dismiss()
                        when (it.errorCode) {
                            "err_already_complete_mission_no_more_action",
                            "err_not_participate_mission",
                            "err_not_action_date",
                            "err_already_action_mission" -> {
                                CoreUtil.showToast(it.message)
                                dismiss()
                            }
                            else -> {
                                CoreUtil.showToast(R.string.acb_common_message_error)
                                dismiss()
                            }
                        }
                    }
                }
            }
        }
    }
}