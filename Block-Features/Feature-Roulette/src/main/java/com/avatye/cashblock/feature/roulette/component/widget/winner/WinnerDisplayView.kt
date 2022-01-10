package com.avatye.cashblock.feature.roulette.component.widget.winner

import android.content.Context
import android.graphics.Color
import android.os.CountDownTimer
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.FrameLayout
import androidx.core.content.res.use
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.avatye.cashblock.base.library.LogHandler
import com.avatye.cashblock.feature.roulette.MODULE_NAME
import com.avatye.cashblock.feature.roulette.R
import com.avatye.cashblock.feature.roulette.component.model.entity.winner.WinnerItemEntity
import com.avatye.cashblock.feature.roulette.databinding.AcbsrWidgetWinnerDisplayBinding
import com.avatye.cashblock.unit.roulette.widget.winner.WinnerDisplayViewAdapter

internal class WinnerDisplayView(context: Context, attrs: AttributeSet? = null) : FrameLayout(context, attrs) {

    private val tagName: String = "WinnerDisplayView"

    private var isScrollable: Boolean = false
    private val millisInFuture: Long = 60L * 1000L
    private val countDownInterval: Long = 3L * 1000L
    private var isTicking = false
    private var countDownTimer: CountDownTimer? = null

    private var winnerDisplayAdapter: WinnerDisplayViewAdapter? = null
    private val linearLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
    private var displayType: Int = 0

    private val vb: AcbsrWidgetWinnerDisplayBinding by lazy {
        AcbsrWidgetWinnerDisplayBinding.inflate(LayoutInflater.from(context), this, true)
    }

    init {
        context.obtainStyledAttributes(attrs, R.styleable.WinnerDisplayView).use {
            displayType = it.getInt(R.styleable.WinnerDisplayView_acbsr_widget_display_type, 0)
            if (displayType == 0) {
                // fill
                vb.winnerDisplayRoot.setBackgroundResource(R.drawable.acb_common_rectangle_ef5350_r10)
                vb.winnerDisplayNull.setTextColor(Color.parseColor("#FFFFFF"))
            } else {
                // stroke
                vb.winnerDisplayRoot.setBackgroundResource(R.drawable.acb_common_outline_f8b2b2_r10)
                vb.winnerDisplayNull.setTextColor(Color.parseColor("#212121"))
            }
        }

        vb.winnerDisplayList.setHasFixedSize(true)
        vb.winnerDisplayList.layoutManager = linearLayoutManager
        vb.winnerDisplayList.addOnItemTouchListener(object : RecyclerView.SimpleOnItemTouchListener() {
            override fun onInterceptTouchEvent(view: RecyclerView, e: MotionEvent): Boolean {
                return true
            }
        })

        countDownTimer = object : CountDownTimer(millisInFuture, countDownInterval) {
            override fun onTick(millisUntilFinished: Long) {
                winnerDisplayAdapter?.requestScroll()
            }

            override fun onFinish() {
                isTicking = false
                scrollStart()
            }
        }
    }


    private fun scrollStart() {
        if (!isScrollable) {
            return
        }

        if (!isTicking) {
            isTicking = true
            countDownTimer?.start()
            LogHandler.i(moduleName = MODULE_NAME) {
                "$tagName -> CountDownTimer -> onStart -> success"
            }
        }
    }

    private fun scrollStop() {
        if (isTicking) {
            isTicking = false
            countDownTimer?.cancel()
            LogHandler.i(moduleName = MODULE_NAME) {
                "$tagName -> CountDownTimer -> onStop"
            }
        }
    }

    fun onResume() {
        scrollStart()
    }

    fun onPause() {
        scrollStop()
    }

    fun updateData(winners: MutableList<WinnerItemEntity>?) {
        try {
            isScrollable = (winners?.size ?: 0) > 0
            vb.winnerDisplayList.isVisible = false
            vb.winnerDisplayNull.isVisible = true
            winners?.let {
                if (it.size > 0) {
                    winnerDisplayAdapter = WinnerDisplayViewAdapter(
                        context = context,
                        displayType = displayType,
                        layoutManager = linearLayoutManager,
                        winners = it
                    )
                    winnerDisplayAdapter?.let { adapter ->
                        vb.winnerDisplayList.isVisible = true
                        vb.winnerDisplayNull.isVisible = false
                        vb.winnerDisplayList.adapter = adapter
                        vb.winnerDisplayList.post { scrollStart() }
                    }
                }
            }
        } catch (e: Exception) {
            isScrollable = (winners?.size ?: 0) > 0
            vb.winnerDisplayList.isVisible = false
            vb.winnerDisplayNull.isVisible = true
        }
    }
}