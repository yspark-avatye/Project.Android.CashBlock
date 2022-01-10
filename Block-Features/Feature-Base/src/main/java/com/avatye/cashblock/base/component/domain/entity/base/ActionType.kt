package com.avatye.cashblock.base.component.domain.entity.base

enum class ActionType {
    UNAUTHORIZED {
        override val actionCode = 100901
        override val actionName = "cashblock:action:account:unauthorized"
    },
    FORBIDDEN {
        override val actionCode = 100902
        override val actionName = "cashblock:action:state:forbidden"
    },
    INSPECTION {
        override val actionCode = 100903
        override val actionName = "cashblock:action:state:inspection"
    },
    TICKET_BALANCE_UPDATE {
        override val actionCode = 300101
        override val actionName = "cashblock:action:ticket:balance:update"
    },
    TICKET_BOX_UPDATE {
        override val actionCode = 300102
        override val actionName = "cashblock:action:ticket-box:update"
    },
    TOUCH_TICKET_CONDITION_UPDATE {
        override val actionCode = 300103
        override val actionName = "cashblock:action:ticket-touch:condition:update"
    },
    VIDEO_TICKET_CONDITION_UPDATE {
        override val actionCode = 300104
        override val actionName = "cashblock:action:ticket-video:condition:update"
    },
    BOX_CONDITION_UPDATE {
        override val actionCode = 300105
        override val actionName = "cashblock:action:box:condition:update"
    },
    WINNER_BOARD_UPDATE {
        override val actionCode = 300201
        override val actionName = "cashblock:action:winner-board:update"
    },
    NOTIFICATION_STATUS_UPDATE {
        override val actionCode = 300301
        override val actionName = "cashblock:action:notification:status:update"
    },
    APP_LAUNCH_MAIN {
        override val actionCode = 9001001
        override val actionName = "cashblock:action:app-launch:main"
    };

    abstract val actionCode: Int
    abstract val actionName: String
}