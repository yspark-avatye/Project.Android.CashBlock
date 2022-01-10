package com.avatye.cashblock.base.component.domain.entity.mission

data class MissionStateEntity(
    val giveID: String,
    val missionActionValue: Int,
    val itemName: String,
    val isDone: Boolean,
    val actionAvailable: Boolean
) {
    val status: Int
        get() {
            return when {
                isDone -> 1 // COMPLETE
                actionAvailable -> 2 // ENABLE
                else -> 3 // DISABLE
            }
        }
}
