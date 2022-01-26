package com.avatye.cashblock.base.component.domain.entity.offerwall

/**
유저의 광고 참여 상태
 * 1: 광고 내용 확인.
 * 2: 참여
 * 3: 적립 요청
 * 4: 적립 완료
 * 5: 참여 불가 처리
 * */

enum class OfferwallJourneyStateType(val value: Int) {
    NONE(0),
    VIEW(1),
    PARTICIPATE(2),
    COMPLETED_NOT_REWARDED(3),
    COMPLETED_REWARDED(4),
    COMPLETED_FAILED(5);

    @Override
    fun equals(other: Int): Boolean = this.value == other

    internal companion object {
        fun from(value: Int): OfferwallJourneyStateType = values().find { it.value == value }
            ?: NONE
    }
}

