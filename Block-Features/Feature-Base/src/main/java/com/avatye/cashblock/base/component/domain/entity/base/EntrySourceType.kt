package com.avatye.cashblock.base.component.domain.entity.base

enum class EntrySourceType(val value: Int) {
    // 제공괸 함수를 통해 진입
    DIRECT(0),

    // 제공된 위젯을 통해 진입
    BUTTON(1),

    // 알림창 상태바를 통해 진입
    NOTIFICATION(2),

    // 기타
    ETC(3);
}