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

import android.graphics.Canvas
import android.graphics.Paint
import com.avatye.cashblock.base.component.widget.ticker.LevenshteinUtils.computeColumnActions
import java.util.*
import kotlin.collections.ArrayList

/**
 * In ticker, each character in the rendered text is represented by a [TickerColumn]. The
 * column can be seen as a column of text in which we can animate from one character to the next
 * by scrolling the column vertically. The [TickerColumnManager] is then a
 * manager/convenience class for handling a list of [TickerColumn] which then combines into
 * the entire string we are rendering.
 *
 * @author Jin Cao, Robinhood
 */
internal class TickerColumnManager(private val metrics: TickerDrawMetrics) {

    private val tickerColumns = ArrayList<TickerColumn>()

    var characterLists: ArrayList<TickerCharacterList> = ArrayList()
        private set

    private var supportedCharacters: MutableSet<Char> = HashSet()


    /**
     * @inheritDoc TickerView#setCharacterLists
     */
    fun setCharacterLists(vararg inputCharacters: String?) {
        this.characterLists = ArrayList()
        for (i in inputCharacters.indices) {
            inputCharacters[i]?.let {
                this.characterLists.add(TickerCharacterList(it))
            }
        }

        supportedCharacters = HashSet()
        for (element in this.characterLists) {
            supportedCharacters.addAll(element.supportedCharacters)
        }
    }

    /**
     * Tell the column manager the new target text that it should display.
     */
    fun setText(text: CharArray) {
        // First remove any zero-width columns
        run {
            var i = 0
            while (i < tickerColumns.size) {
                val tickerColumn = tickerColumns[i]
                if (tickerColumn.getCurrentWidth() > 0) {
                    i++
                } else {
                    tickerColumns.removeAt(i)
                }
            }
        }

        // Use Levenshtein distance algorithm to figure out how to manipulate the columns
        val actions = computeColumnActions(currentText, text, supportedCharacters)
        var columnIndex = 0
        var textIndex = 0
        for (i in actions.indices) {
            when (actions[i]) {
                LevenshteinUtils.ACTION_INSERT -> {
                    tickerColumns.add(
                        columnIndex,
                        TickerColumn(this.characterLists, metrics)
                    )
                    tickerColumns[columnIndex].setTargetChar(text[textIndex])
                    columnIndex++
                    textIndex++
                }
                LevenshteinUtils.ACTION_SAME -> {
                    tickerColumns[columnIndex].setTargetChar(text[textIndex])
                    columnIndex++
                    textIndex++
                }
                LevenshteinUtils.ACTION_DELETE -> {
                    tickerColumns[columnIndex].setTargetChar(TickerUtils.EMPTY_CHAR)
                    columnIndex++
                }
                else -> throw IllegalArgumentException("Unknown action: " + actions[i])
            }
        }
    }

    fun onAnimationEnd() {
        var i = 0
        val size = tickerColumns.size
        while (i < size) {
            val column = tickerColumns[i]
            column.onAnimationEnd()
            i++
        }
    }

    fun setAnimationProgress(animationProgress: Float) {
        var i = 0
        val size = tickerColumns.size
        while (i < size) {
            val column = tickerColumns[i]
            column.setAnimationProgress(animationProgress)
            i++
        }
    }

    val minimumRequiredWidth: Float
        get() {
            var width = 0f
            var i = 0
            val size = tickerColumns.size
            while (i < size) {
                width += tickerColumns[i].getMinimumRequiredWidth()
                i++
            }
            return width
        }

    val currentWidth: Float
        get() {
            var width = 0f
            var i = 0
            val size = tickerColumns.size
            while (i < size) {
                width += tickerColumns[i].getCurrentWidth()
                i++
            }
            return width
        }

    val currentText: CharArray
        get() {
            val size = tickerColumns.size
            val currentText = CharArray(size)
            for (i in 0 until size) {
                currentText[i] = tickerColumns[i].currentChar
            }
            return currentText
        }

    /**
     * This method will draw onto the canvas the appropriate UI state of each column dictated
     * by {@param animationProgress}. As a side effect, this method will also translate the canvas
     * accordingly for the draw procedures.
     */
    fun draw(canvas: Canvas, textPaint: Paint?) {
        var i = 0
        val size = tickerColumns.size
        while (i < size) {
            val column = tickerColumns[i]
            column.draw(canvas, textPaint!!)
            canvas.translate(column.getCurrentWidth(), 0f)
            i++
        }
    }

}