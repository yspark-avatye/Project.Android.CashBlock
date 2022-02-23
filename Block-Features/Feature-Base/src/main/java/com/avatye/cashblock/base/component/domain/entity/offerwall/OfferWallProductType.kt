package com.avatye.cashblock.base.component.domain.entity.offerwall

enum class OfferWallProductType(val value: String) {

    NONE(""),
    CPA("CPA"),
    CPC("CPC"),
    CPE("CPE"),
    CPFL("CPFL"),
    CPI("CPI"),
    CPIF("CPIF"),
    CPJ("CPJ"),
    CPKP("CPKP"),
    CPM("CPM"),
    CPNB("CPNB"),
    CPNC("CPNC"),
    CPNS("CPNS"),
    CPP("CPP"),
    CPQ("CPQ"),
    CPR("CPR"),
    CPS("CPS"),
    CPV("CPV"),
    CPYS("CPYS");

    @Override
    fun equals(other: String): Boolean = this.value.equals(other = other, ignoreCase = true)

    internal companion object {
        fun from(value: String): OfferWallProductType = values().find { it.value.equals(other = value, ignoreCase = true) }
            ?: NONE
    }

}