package com.avatye.cashblock.feature.roulette.component.controller

import com.avatye.cashblock.base.component.contract.api.MissionApiContractor
import com.avatye.cashblock.base.component.contract.business.SettingContractor
import com.avatye.cashblock.base.component.domain.entity.mission.MissionStateEntity
import com.avatye.cashblock.base.component.domain.model.app.CoreBaseActivity
import com.avatye.cashblock.base.component.domain.model.contract.ContractResult
import com.avatye.cashblock.feature.roulette.RouletteConfig
import com.avatye.cashblock.feature.roulette.component.data.PreferenceData
import com.avatye.cashblock.feature.roulette.component.widget.dialog.DialogPopupAttendanceView

internal object MissionController {
    object Attendance {
        fun sync(ownerActivity: CoreBaseActivity, callback: () -> Unit) {
            if (!PreferenceData.Mission.Attendance.needShowDialog) {
                callback()
                return
            }

            if (!PreferenceData.Mission.Attendance.needRequestMission) {
                callback()
                return
            }

            // request
            val mission = MissionApiContractor(blockType = RouletteConfig.blockType)
            mission.retrieveMission(missionId = SettingContractor.missionSetting.attendanceId) {
                when (it) {
                    is ContractResult.Failure -> callback()
                    is ContractResult.Success -> {
                        when {
                            it.contract.participatable -> showView(ownerActivity = ownerActivity, attendanceList = it.contract.stateItems)
                            else -> callback()
                        }
                    }
                }
            }
        }

        fun showView(ownerActivity: CoreBaseActivity, attendanceList: MutableList<MissionStateEntity>) {
            val dialog = DialogPopupAttendanceView.create(ownerActivity = ownerActivity, attendanceList = attendanceList)
            ownerActivity.putDialogView(dialog)
            dialog.show(cancelable = false)
        }

        fun requestList(ownerActivity: CoreBaseActivity, callback: (missionStateList: MutableList<MissionStateEntity>?) -> Unit) {
            val mission = MissionApiContractor(blockType = RouletteConfig.blockType)
            mission.retrieveMission(missionId = SettingContractor.missionSetting.attendanceId) {
                when (it) {
                    is ContractResult.Failure -> callback(null)
                    is ContractResult.Success -> {
                        when (it.contract.stateItems.size > 0) {
                            true -> callback(it.contract.stateItems)
                            false -> callback(null)
                        }
                    }
                }
            }
        }
    }
}