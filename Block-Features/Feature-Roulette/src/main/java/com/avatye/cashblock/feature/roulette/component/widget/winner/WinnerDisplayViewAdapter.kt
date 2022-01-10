package com.avatye.cashblock.unit.roulette.widget.winner

import android.content.Context
import android.graphics.Color
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.avatye.cashblock.base.component.support.toFeedTimeShort
import com.avatye.cashblock.base.library.LogHandler
import com.avatye.cashblock.feature.roulette.MODULE_NAME
import com.avatye.cashblock.feature.roulette.R
import com.avatye.cashblock.feature.roulette.component.model.entity.winner.WinnerItemEntity
import com.avatye.cashblock.feature.roulette.databinding.AcbsrItemWinnerDisplayBinding

internal class WinnerDisplayViewAdapter(
    private val context: Context?,
    private val displayType: Int = 0,
    private val layoutManager: RecyclerView.LayoutManager,
    private val winners: MutableList<WinnerItemEntity>
) : RecyclerView.Adapter<WinnerDisplayViewAdapter.ItemViewHolder>() {

    private val tagName: String = "WinnerDisplayViewAdapter"
    private val millisPerInch = 500f
    private var currentPosition: Int = 0
    private val colorWhite = Color.parseColor("#FFFFFF")
    private val colorBlack = Color.parseColor("#212121")
    private val colorBrownish = Color.parseColor("#757575")
    private val colorCornflower = Color.parseColor("#FFAEAC")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            AcbsrItemWinnerDisplayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.itemViewBind(winners[position])
    }

    override fun getItemCount(): Int = winners.size

    fun requestScroll() {
        if (winners.size <= 1) {
            return
        }
        when (currentPosition == 0) {
            true -> layoutManager.scrollToPosition(0)
            else -> layoutManager.startSmoothScroll(makeScroller(currentPosition))
        }
        when (winners.size > (currentPosition + 1)) {
            true -> currentPosition++
            else -> currentPosition = 0
        }
        try {
            notifyItemChanged(currentPosition)
            LogHandler.i(moduleName = MODULE_NAME) {
                "$tagName -> requestScroll() { notifyItemChanged: $currentPosition }"
            }
        } catch (e: Exception) {
            LogHandler.e(moduleName = MODULE_NAME, throwable = e) {
                "$tagName -> requestScroll() { notifyItemChanged: $currentPosition }"
            }
        }
    }

    private fun makeScroller(position: Int): LinearSmoothScroller {
        return object : LinearSmoothScroller(context) {
            override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                return millisPerInch / displayMetrics.densityDpi
            }
        }.apply {
            targetPosition = position
        }
    }

    inner class ItemViewHolder(private val ib: AcbsrItemWinnerDisplayBinding) : RecyclerView.ViewHolder(ib.root) {
        fun itemViewBind(entity: WinnerItemEntity) {
            // message
            with(ib.winnerMessage) {
                text = entity.message
                setTextColor(if (displayType == 0) colorWhite else colorBlack)
            }
            // reward
            with(ib.winnerReward) {
                text = entity.rewardText
                setTextColor(if (displayType == 0) colorWhite else colorBlack)
                isVisible = entity.rewardText.isNotEmpty()
            }
            // create datetime
            entity.createDateTime?.let {
                with(ib.winnerDatetime) {
                    text = context?.getString(R.string.acbsr_string_winner_datetime)?.format(it.toFeedTimeShort()) ?: ""
                    setTextColor(if (displayType == 0) colorCornflower else colorBrownish)
                    isVisible = entity.rewardText.isNotEmpty()
                }
            } ?: run {
                with(ib.winnerDatetime) {
                    text = ""
                    isVisible = false
                }
            }
        }
    }
}