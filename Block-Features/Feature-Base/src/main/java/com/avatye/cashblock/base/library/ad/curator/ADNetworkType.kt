package com.avatye.cashblock.base.library.ad.curator

enum class ADNetworkType(val value: Int) {
    NONE(0),
    ADMOB(1),
    FAN(2),
    MOPUB(3),
    CAULY(4),
    MEZZOMEDIA(5),
    MOBON(6),
    UNITY_ADS(7),
    MINTEGRAL(8),
    FAN_NATIVE_BANNER(9),
    ADFIT(10),
    APPNEXT(11),
    CRITEO(12),
    ADCOLONY(13),
    VUNGLE(14),
    APPLOVIN(15),
    FYBER(16),
    TAPJOY(17);

    companion object {
        fun from(value: Int): ADNetworkType {
            return when (value) {
                1 -> ADMOB
                2 -> FAN
                3 -> MOPUB
                4 -> CAULY
                5 -> MEZZOMEDIA
                6 -> MOBON
                7 -> UNITY_ADS
                8 -> MINTEGRAL
                9 -> FAN_NATIVE_BANNER
                10 -> ADFIT
                11 -> APPNEXT
                12 -> CRITEO
                13 -> ADCOLONY
                14 -> VUNGLE
                15 -> APPLOVIN
                16 -> FYBER
                17 -> TAPJOY
                else -> NONE
            }
        }
    }
}