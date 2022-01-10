package com.avatye.cashblock.base.internal.server.entity.mission

import com.avatye.cashblock.base.component.domain.entity.mission.MissionEntity
import com.avatye.cashblock.base.component.domain.entity.mission.MissionStateEntity
import com.avatye.cashblock.base.library.miscellaneous.*
import com.avatye.cashblock.base.internal.server.serve.ServeSuccess
import org.json.JSONArray
import org.json.JSONObject

class ResMission : ServeSuccess() {
    var missionEntity = MissionEntity()
        private set

    override fun makeBody(responseValue: String) {
        JSONObject(responseValue).let {
            missionEntity = MissionEntity(
                isComplete = it.toBooleanValue("isComplete"),
                participateDateTime = it.toDateTimeValue("participateDateTime"),
                rewardMessage = it.toStringValue("rewardMessage"),
                stateItems = makeStateItems(it.toJSONArrayValue("userMissionState"))
            )
            //PreferenceData.Mission.attendanceComplete = isComplete
        }
    }

    private fun makeStateItems(items: JSONArray?): MutableList<MissionStateEntity> {
        val stateEntities = mutableListOf<MissionStateEntity>()
        items?.until { item ->
            stateEntities.add(
                MissionStateEntity(
                    giveID = item.toStringValue("giveID"),
                    missionActionValue = item.toIntValue("missionActionValue"),
                    itemName = item.toStringValue("itemName"),
                    isDone = item.toBooleanValue("isDone"),
                    actionAvailable = item.toBooleanValue("actionAvailable")
                )
            )
        }
        return stateEntities
    }
}