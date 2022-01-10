package com.avatye.cashblock.feature.roulette.component.controller

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.OvershootInterpolator
import androidx.appcompat.widget.AppCompatImageView
import com.avatye.cashblock.base.component.contract.AccountContract
import com.avatye.cashblock.base.component.contract.RemoteContract
import com.avatye.cashblock.base.component.support.AnimatorEventCallback
import com.avatye.cashblock.base.component.support.toPX
import com.avatye.cashblock.feature.roulette.RouletteConfig
import com.avatye.cashblock.feature.roulette.R
import com.avatye.cashblock.feature.roulette.component.livedata.TicketBalanceLiveData
import com.avatye.cashblock.feature.roulette.component.livedata.TouchTicketLiveData
import com.avatye.cashblock.feature.roulette.component.livedata.VideoTicketLiveData
import com.avatye.cashblock.feature.roulette.component.model.listener.ITicketCount
import org.joda.time.LocalTime
import kotlin.random.Random
import com.avatye.cashblock.R as CoreResource

internal object TicketController {
    fun periodText(period: Int): String {
        val time: LocalTime = LocalTime(0, 0).plusSeconds(period)
        var periodString = ""
        if (time.hourOfDay > 0) {
            periodString = periodString.plus(RouletteConfig.application.getString(CoreResource.string.acb_common_hh).format(time.hourOfDay))
        }
        if (time.minuteOfHour > 0) {
            periodString = periodString.plus(RouletteConfig.application.getString(CoreResource.string.acb_common_mm).format(time.minuteOfHour))
        }
        return periodString
    }


    object TouchTicketAcquire {

        const val piecesCount = 10
        const val popupCloseDelay = 1.1 * 1000

        private const val ticketHeight = 71F
        private val intervalRange = intArrayOf(-2, -1, 0, 1, 2)
        private val pieces = intArrayOf(
            R.drawable.acbsr_ic_puzzle_tk00,
            R.drawable.acbsr_ic_puzzle_tk01,
            R.drawable.acbsr_ic_puzzle_tk02,
            R.drawable.acbsr_ic_puzzle_tk03,
            R.drawable.acbsr_ic_puzzle_tk04,
            R.drawable.acbsr_ic_puzzle_tk05,
            R.drawable.acbsr_ic_puzzle_tk06,
            R.drawable.acbsr_ic_puzzle_tk07,
            R.drawable.acbsr_ic_puzzle_tk08,
            R.drawable.acbsr_ic_puzzle_tk09,
            R.drawable.acbsr_ic_puzzle_tk10
        )

        val allowNoAd: Boolean
            get() {
                return RemoteContract.touchTicketSetting.allowNoAd
            }

        val popupExposeCount: Int
            get() {
                val interval = RemoteContract.touchTicketSetting.popAD.interval
                return interval - intervalRange[Random.nextInt(intervalRange.size - 1)]
            }


        fun popupPosition(allowExcludeADNetwork: Boolean): Int {
            val position = if (allowExcludeADNetwork) {
                ticketHeight * RemoteContract.touchTicketSetting.popAD.excludePosition
            } else {
                ticketHeight * RemoteContract.touchTicketSetting.popAD.position
            }
            return position.toPX.toInt()
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
                duration = 1250L
                interpolator = AccelerateDecelerateInterpolator()
                playTogether(
                    ObjectAnimator.ofFloat(targetView, "scaleX", 1.0F, 0.3F, 1.0F),
                    ObjectAnimator.ofFloat(targetView, "scaleY", 1.0F, 0.3F, 1.0F),
                    ObjectAnimator.ofFloat(targetView, "rotationX", 0f, 360F * 5)
                )
                addListener(object : AnimatorEventCallback() {
                    override fun onAnimationEnd(animation: Animator?) {
                        callback()
                    }
                })
            }.start()
        }
    }


    object VideoTicketAcquire {
        fun animateComplete(targetView: View, callback: () -> Unit) {
            AnimatorSet().apply {
                duration = 1250L
                interpolator = AccelerateDecelerateInterpolator()
                playTogether(
                    ObjectAnimator.ofFloat(targetView, "scaleX", 1.0F, 0.3F, 1.0F),
                    ObjectAnimator.ofFloat(targetView, "scaleY", 1.0F, 0.3F, 1.0F),
                    ObjectAnimator.ofFloat(targetView, "rotationX", 0f, 360F * 5)
                )
                addListener(object : AnimatorEventCallback() {
                    override fun onAnimationEnd(animation: Animator?) {
                        callback()
                    }
                })
            }.start()
        }
    }


    // outer
    internal object Session {
        fun syncTicketCondition(callback: () -> Unit) {
            checkLogin { success ->
                if (success) {
                    checkBalance {
                        checkCondition {
                            callback()
                        }
                    }
                } else {
                    callback()
                }
            }
        }


        fun checkTicketCondition(listener: ITicketCount) {
            checkLogin { success ->
                if (success) {
                    checkBalance { balance ->
                        checkCondition { condition ->
                            listener.callback(balance = balance, condition = condition)
                        }
                    }
                } else {
                    listener.callback(balance = -1, condition = -1)
                }
            }
        }


        private fun checkLogin(callback: (success: Boolean) -> Unit) {
            when (AccountContract.isLogin) {
                true -> callback(true)
                false -> AccountContract.login(callback)
            }
        }


        private fun checkBalance(callback: (balance: Int) -> Unit = {}) {
            if (TicketBalanceLiveData.balance >= 0) {
                callback(TicketBalanceLiveData.balance)
            } else {
                TicketBalanceLiveData.synchronization { _, syncValue ->
                    callback(syncValue)
                }
            }
        }


        private fun checkCondition(callback: (condition: Int) -> Unit) {
            TouchTicketLiveData.synchronizationFrequency { touch ->
                VideoTicketLiveData.synchronizationFrequency { video ->
                    callback(touch + video)
                }
            }
        }
    }
}