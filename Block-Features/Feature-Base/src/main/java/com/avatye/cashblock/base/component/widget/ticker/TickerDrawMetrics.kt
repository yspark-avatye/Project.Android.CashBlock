/*
 * Copyright (C) 2016 Robinhood Markets, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.avatye.cashblock.base.component.widget.ticker

import android.graphics.Paint
import com.avatye.cashblock.base.component.widget.ticker.TickerView.ScrollingDirection
import java.util.*

/**
 * This wrapper class represents some core drawing metrics that [TickerView] and
 * [TickerColumnManager] require to calculate the positions and offsets for rendering
 * the text onto the canvas.
 *
 * @author Jin Cao
 */
internal class TickerDrawMetrics(private val textPaint: Paint) {

    // These are attributes on the text paint used for measuring and drawing the text on the
    // canvas. These attributes are reset whenever anything on the text paint changes.
    private val charWidths: MutableMap<Char, Float> = HashMap(256)

    var charHeight = 0f
        private set

    var charBaseline = 0f
        private set

    var preferredScrollingDirection = ScrollingDirection.ANY


    fun invalidate() {
        charWidths.clear()
        val fm = textPaint.fontMetrics
        charHeight = fm.bottom - fm.top
        charBaseline = -fm.top
    }


    fun getCharWidth(character: Char): Float {
        if (character == TickerUtils.EMPTY_CHAR) {
            return 0F
        }

        // This method will lazily initialize the char width map.
        val value = charWidths[character]
        return if (value != null) {
            value
        } else {
            val width = textPaint.measureText(character.toString())
            charWidths[character] = width
            width
        }
    }

    init {
        invalidate()
    }
}