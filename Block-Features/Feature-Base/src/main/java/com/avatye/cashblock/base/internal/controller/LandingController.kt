package com.avatye.cashblock.base.internal.controller

import android.app.Activity
import com.avatye.cashblock.base.block.BlockCode
import com.avatye.cashblock.base.block.BlockController
import com.avatye.cashblock.base.component.domain.entity.base.LandingType

internal object LandingController {

    fun requestLanding(
        blockCode: BlockCode,
        ownerActivity: Activity,
        ownerActivityClose: Boolean = false,
        landingType: LandingType,
        landingValue: String? = null,
        fallback: () -> Unit
    ) {
        when (landingType) {
            // common
            LandingType.COMMON_NOTICE_VIEW -> {
                if (landingValue.isNullOrEmpty()) {
                    fallback()
                    return
                }
                // common -> notice
            }
            LandingType.COMMON_TERMS_VIEW -> {
                if (landingValue.isNullOrEmpty()) {
                    fallback()
                    return
                }
                // common -> terms
            }
            LandingType.COMMON_EXTERNAL_LINK -> {
                if (landingValue.isNullOrEmpty()) {
                    fallback()
                    return
                }
            }
            // roulette
            LandingType.ROULETTE_MAIN,
            LandingType.ROULETTE_TICKET_BOX,
            LandingType.ROULETTE_TOUCH_TICKET,
            LandingType.ROULETTE_VIDEO_TICKET -> {
                landingBlock(
                    blockCode = blockCode,
                    ownerActivity = ownerActivity,
                    ownerActivityClose = ownerActivityClose,
                    landingType = landingType,
                    landingValue = landingValue,
                    fallback = fallback
                )
            }
            // offerwall
            LandingType.OFFERWALL_MAIN_LIST -> {
                landingBlock(
                    blockCode = blockCode,
                    ownerActivity = ownerActivity,
                    ownerActivityClose = ownerActivityClose,
                    landingType = landingType,
                    landingValue = landingValue,
                    fallback = fallback
                )
            }
            LandingType.OFFERAALL_MAIN_VIEW -> {
                if (landingValue.isNullOrEmpty()) {
                    fallback()
                    return
                }

            }
            else -> fallback()
        }
    }


    private fun landingBlock(
        blockCode: BlockCode,
        ownerActivity: Activity,
        ownerActivityClose: Boolean = false,
        landingType: LandingType,
        landingValue: String? = null,
        fallback: () -> Unit
    ) {
        BlockController.createBlockConnector(context = ownerActivity, blockCode = blockCode) {
            it?.landing(
                ownerActivity = ownerActivity,
                ownerActivityClose = ownerActivityClose,
                landingType = landingType,
                landingValue = landingValue
            ) ?: fallback()
        }
    }

}