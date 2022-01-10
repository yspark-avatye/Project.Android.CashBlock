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

import com.avatye.cashblock.base.component.widget.ticker.TickerView.ScrollingDirection
import java.util.*

/**
 * This is the primary class that Ticker uses to determine how to animate from one character
 * to another. The provided string dictates what characters will appear between
 * the start and end characters.
 *
 *
 * For example, given the string "abcde", if the view wants to animate from 'd' to 'b',
 * it will know that it has to go from 'd' to 'c' to 'b', and these are the characters
 * that avatye_animator_cashfloter_show up during the animation scroll.
 *
 * @author Jin Cao, Robinhood
 */
internal class TickerCharacterList(characterList: String) {
    private val numOriginalCharacters: Int

    // The saved character list will always be of the format: EMPTY, list, list
    val characterList: CharArray

    // A minor optimization so that we can cache the indices of each character.
    private val characterIndicesMap: MutableMap<Char, Int?>

    /**
     * @param start the character that we want to animate from
     * @param end the character that we want to animate to
     * @param direction the preferred {@Link TickerView#ScrollingDirection}
     * @return a valid pair of start and end indices, or null if the inputs are not supported.
     */
    fun getCharacterIndices(
        start: Char,
        end: Char,
        direction: ScrollingDirection?
    ): CharacterIndices? {
        var startIndex = getIndexOfChar(start)
        var endIndex = getIndexOfChar(end)
        if (startIndex < 0 || endIndex < 0) {
            return null
        }
        when (direction) {
            ScrollingDirection.DOWN -> {
                if (end == TickerUtils.EMPTY_CHAR) {
                    endIndex = characterList.size
                } else if (endIndex < startIndex) {
                    endIndex += numOriginalCharacters
                }
            }
            ScrollingDirection.UP -> {
                if (startIndex < endIndex) {
                    startIndex += numOriginalCharacters
                }
            }
            ScrollingDirection.ANY -> { // see if the wrap-around animation is shorter distance than the original animation
                if (start != TickerUtils.EMPTY_CHAR && end != TickerUtils.EMPTY_CHAR) {
                    if (endIndex < startIndex) { // If we are potentially going backwards
                        val nonWrapDistance = startIndex - endIndex
                        val wrapDistance = numOriginalCharacters - startIndex + endIndex
                        if (wrapDistance < nonWrapDistance) {
                            endIndex += numOriginalCharacters
                        }
                    } else if (startIndex < endIndex) { // If we are potentially going forwards
                        val nonWrapDistance = endIndex - startIndex
                        val wrapDistance = numOriginalCharacters - endIndex + startIndex
                        if (wrapDistance < nonWrapDistance) {
                            startIndex += numOriginalCharacters
                        }
                    }
                }
            }
            else -> {
                //  nothing
            }
        }
        return CharacterIndices(startIndex, endIndex)
    }


    val supportedCharacters: Set<Char>
        get() = characterIndicesMap.keys


    private fun getIndexOfChar(c: Char): Int {
        return if (c == TickerUtils.EMPTY_CHAR) {
            0
        } else if (characterIndicesMap.containsKey(c)) {
            characterIndicesMap[c]!! + 1
        } else {
            -1
        }
    }


    internal inner class CharacterIndices(val startIndex: Int, val endIndex: Int)


    init {
        require(!characterList.contains(Character.toString(TickerUtils.EMPTY_CHAR))) { "You cannot include TickerUtils.EMPTY_CHAR in the character list." }
        val charsArray = characterList.toCharArray()
        val length = charsArray.size
        numOriginalCharacters = length
        characterIndicesMap = HashMap(length)
        for (i in 0 until length) {
            characterIndicesMap[charsArray[i]] = i
        }
        this.characterList = CharArray(length * 2 + 1)
        this.characterList[0] = TickerUtils.EMPTY_CHAR
        for (i in 0 until length) {
            this.characterList[1 + i] = charsArray[i]
            this.characterList[1 + length + i] = charsArray[i]
        }
    }
}