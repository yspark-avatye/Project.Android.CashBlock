package com.avatye.cashblock.base.library.ad.curator.queue.loader

enum class ADLoaderType {
    /** 전면 광고 */
    INTERSTITIAL,

    /** 전면 비디오 광고 */
    INTERSTITIAL_VIDEO,

    /** 전면 네이티브 광고 */
    INTERSTITIAL_NATIVE,

    /** 보상형 비디오 광고 */
    REWARD_VIDEO,

    /** 300x250 박스형 광고(back-fill) */
    BOX_BANNER
}