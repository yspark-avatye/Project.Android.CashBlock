package com.avatye.cashblock.feature.roulette.component.controller

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.appcompat.widget.AppCompatImageView
import com.avatye.cashblock.base.component.contract.AccountContract
import com.avatye.cashblock.base.component.contract.RemoteContract
import com.avatye.cashblock.base.component.support.AnimatorEventCallback
import com.avatye.cashblock.base.component.support.toPX
import com.avatye.cashblock.base.library.LogHandler
import com.avatye.cashblock.feature.roulette.MODULE_NAME
import com.avatye.cashblock.feature.roulette.R
import com.avatye.cashblock.feature.roulette.component.data.PreferenceData
import com.avatye.cashblock.feature.roulette.component.model.listener.ITicketBoxCount
import org.joda.time.DateTime
import kotlin.random.Random

internal object TicketBoxController {



    const val piecesCount = 10
    const val popupCloseDelay = 1.1 * 1000

    private const val boxHeight = 75.5F
    private val intervalRange = intArrayOf(-2, -1, 0, 1, 2)
    private val pieces = intArrayOf(
        R.drawable.acbsr_ic_puzzle_bx00,
        R.drawable.acbsr_ic_puzzle_bx01,
        R.drawable.acbsr_ic_puzzle_bx02,
        R.drawable.acbsr_ic_puzzle_bx03,
        R.drawable.acbsr_ic_puzzle_bx04,
        R.drawable.acbsr_ic_puzzle_bx05,
        R.drawable.acbsr_ic_puzzle_bx06,
        R.drawable.acbsr_ic_puzzle_bx07,
        R.drawable.acbsr_ic_puzzle_bx08,
        R.drawable.acbsr_ic_puzzle_bx09,
        R.drawable.acbsr_ic_puzzle_bx10
    )

    val allowNoAd: Boolean
        get() {
            return RemoteContract.ticketBoxSetting.allowNoAd
        }

    val hasTicketBox: Boolean
        get() {
            return PreferenceData.Box.ticketBoxCheckDate != DateTime().toString("yyyyMMdd")
        }

    val popupExposeCount: Int
        get() {
            val interval = RemoteContract.ticketBoxSetting.popAD.interval
            return interval - intervalRange[Random.nextInt(intervalRange.size - 1)]
        }

    fun popupPosition(allowExcludeADNetwork: Boolean): Int {
        val position = if (allowExcludeADNetwork) {
            boxHeight * RemoteContract.ticketBoxSetting.popAD.excludePosition
        } else {
            boxHeight * RemoteContract.ticketBoxSetting.popAD.position
        }
        return position.toPX.toInt()
    }

    fun updateComplete(isComplete: Boolean) {
        when (isComplete) {
            true -> PreferenceData.Box.update(ticketBoxCheckDate = DateTime().toString("yyyyMMdd"))
            false -> PreferenceData.Box.update(ticketBoxCheckDate = "")
        }
    }

    fun pieces(index: Int): Int {
        if (index >= 0 && index <= pieces.size - 1) {
            return pieces[index]
        }
        return pieces[0]
    }

    fun animateCollect(targetView: View, pieceIndex: Int) {
        if (targetView is AppCompatImageView) {
            targetView.setImageResource(pieces(pieceIndex))
        }
        AnimatorSet().apply {
            duration = 125L
            interpolator = OvershootInterpolator()
            playTogether(
                ObjectAnimator.ofFloat(targetView, "scaleX", 1.2F, 0.8F, 1.0F),
                ObjectAnimator.ofFloat(targetView, "scaleY", 1.2F, 0.8F, 1.0F)
            )
        }.start()
    }

    fun animateComplete(targetView: View, callback: () -> Unit) {
        AnimatorSet().apply {
            duration = 500L
            interpolator = OvershootInterpolator()
            playTogether(
                ObjectAnimator.ofFloat(targetView, "scaleX", 1.0F, 0.2F, 1.0F),
                ObjectAnimator.ofFloat(targetView, "scaleY", 1.0F, 0.2F, 1.0F),
            )
            addListener(object : AnimatorEventCallback() {
                override fun onAnimationEnd(animation: Animator?) {
                    callback()
                }
            })
        }.start()
    }

    object Session {
        fun checkTicketBoxCondition(listener: ITicketBoxCount) {
            checkLogin { success ->
                if (success) {
                    listener.callback(condition = if (hasTicketBox) 1 else 0)
                } else {
                    LogHandler.e(moduleName = MODULE_NAME) {
                        "TicketBoxManager -> AccountTicket -> login failed"
                    }
                    listener.callback(condition = -1)
                }
            }
        }

        private fun checkLogin(callback: (success: Boolean) -> Unit) {
            when (AccountContract.isLogin) {
                true -> callback(true)
                false -> AccountContract.login(callback)
            }
        }
    }
}