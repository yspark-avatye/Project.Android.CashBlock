package com.avatye.cashblock.base.component.domain.entity.mission

import org.joda.time.DateTime

data class MissionEntity(
    val isComplete: Boolean = false,
    val participateDateTime: DateTime? = null,
    val rewardMessage: String = "",
    val stateItems: MutableList<MissionStateEntity> = mutableListOf()
) {
    val participatable: Boolean
        get() {
            return stateItems.any { it.actionAvailable && !it.isDone }
        }
}
