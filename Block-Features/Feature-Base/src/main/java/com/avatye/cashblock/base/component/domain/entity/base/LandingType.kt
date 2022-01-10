package com.avatye.cashblock.base.component.domain.entity.base

enum class LandingType(val value: String) {
    NONE(""),

    // common
    COMMON_NOTICE_VIEW("Notice.View"),
    COMMON_TERMS_VIEW("Terms.View"),
    COMMON_EXTERNAL_LINK("External.Link"),

    // roulette
    ROULETTE_MAIN("Main.View"),
    ROULETTE_TICKET_BOX("TicketBox.View"),
    ROULETTE_TOUCH_TICKET("TouchTicket.View"),
    ROULETTE_VIDEO_TICKET("VideoTicket.View"),

    // offerwall
    OFFERWALL_MAIN_LIST("Offerwall.Main.List"),
    OFFERAALL_MAIN_VIEW("Offerwall.Main.View");

    // newspick
    // ...
    companion object {
        fun from(value: String): LandingType {
            return when (value) {
                // common
                COMMON_NOTICE_VIEW.value -> COMMON_NOTICE_VIEW
                COMMON_TERMS_VIEW.value -> COMMON_TERMS_VIEW
                COMMON_EXTERNAL_LINK.value -> COMMON_EXTERNAL_LINK
                // roulette
                ROULETTE_MAIN.value -> ROULETTE_MAIN
                ROULETTE_TICKET_BOX.value -> ROULETTE_TICKET_BOX
                ROULETTE_TOUCH_TICKET.value -> ROULETTE_TOUCH_TICKET
                ROULETTE_VIDEO_TICKET.value -> ROULETTE_VIDEO_TICKET
                // offerwall
                OFFERWALL_MAIN_LIST.value -> OFFERWALL_MAIN_LIST
                OFFERAALL_MAIN_VIEW.value -> OFFERAALL_MAIN_VIEW
                // newspick
                // ...
                else -> NONE
            }
        }
    }
}