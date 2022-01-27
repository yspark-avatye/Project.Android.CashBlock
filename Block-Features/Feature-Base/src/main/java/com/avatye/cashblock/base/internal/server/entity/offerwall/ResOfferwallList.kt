package com.avatye.cashblock.base.internal.server.entity.offerwall

import com.avatye.cashblock.base.component.domain.entity.offerwall.OfferwallItemEntity
import com.avatye.cashblock.base.component.domain.entity.offerwall.OfferwallJourneyStateType
import com.avatye.cashblock.base.component.domain.entity.offerwall.OfferwallSectionEntity
import com.avatye.cashblock.base.component.domain.entity.offerwall.OfferwallViewStateType
import com.avatye.cashblock.base.internal.server.serve.ServeSuccess
import com.avatye.cashblock.base.library.miscellaneous.toIntValue
import com.avatye.cashblock.base.library.miscellaneous.toJSONArrayValue
import com.avatye.cashblock.base.library.miscellaneous.toStringValue
import com.avatye.cashblock.base.library.miscellaneous.until
import org.json.JSONObject

internal class ResOfferwallList : ServeSuccess() {

    var pointName: String = ""
        private set

    var sectionEntity = OfferwallSectionEntity()
        private set

    var categoryEntity = OfferwallSectionEntity.OfferwallCategoryEntity()
        private set

    var itemEntity = OfferwallItemEntity()
        private set

    var sectionPos: Int = 0
        private set

    var categoryPos: Int = 0
        private set

    var viewType: OfferwallViewStateType = OfferwallViewStateType.VIEW_TYPE_ITEM

    private val sections: MutableList<OfferwallSectionEntity> = mutableListOf()
    private val joinCompleteItems: MutableList<OfferwallItemEntity> = mutableListOf()


    override fun makeBody(responseValue: String) {
        JSONObject(responseValue).let {
            // region # pointName
            pointName = it.toStringValue("pointName")
            // endregion

            // region # section (Root)
            val sectionArray = it.toJSONArrayValue("sections")
            sectionArray?.until { sectionObj ->
                sectionEntity = OfferwallSectionEntity(
                    sectionID = sectionObj.toStringValue("sectionID"),
                    sectionName = sectionObj.toStringValue("sectionName"),
                    sectionTitle = sectionObj.toStringValue("sectionTitle"),
                    sectionSortOrder = sectionObj.toIntValue("sectionSortOrder"),
                )

                // region # items
                val sectionItems = sectionObj.toJSONArrayValue("items")
                sectionItems?.let { sections ->
                    if (sections.length() > 0) {
                        sections.until { section ->
                            val item = getItem(section)
                            viewType = OfferwallViewStateType.VIEW_TYPE_ITEM
                            categoryPos = -1

                            if (item.journeyState == OfferwallJourneyStateType.COMPLETED_REWARDED) {
                                joinCompleteItems.add(item)
                            } else {
                                sectionEntity.items.add(item)
                            }

                        }
                    }
                }
                // endregion

                // region # categories
                val categoryCollection = sectionObj.toJSONArrayValue("categories")
                categoryCollection?.let { categories ->
                    if (categories.length() > 0) {
                        categories.until { categoryObj ->
                            categoryEntity = OfferwallSectionEntity.OfferwallCategoryEntity(
                                categoryID = categoryObj.toStringValue("categoryID"),
                                categoryName = categoryObj.toStringValue("categoryName"),
                                categorySortOrder = categoryObj.toIntValue("categorySortOrder"),
                            )

                            val categoryItems = categoryObj.toJSONArrayValue("items")
                            categoryItems?.until { it ->
                                val item = getItem(it)
                                viewType = OfferwallViewStateType.VIEW_TYPE_CATEGORY_ITEM

                                if (item.journeyState == OfferwallJourneyStateType.COMPLETED_REWARDED) {
                                    joinCompleteItems.add(item)
                                } else {
                                    categoryEntity.items.add(item)
                                }

                                if (categoryEntity.items.size > 0) {
                                    sectionEntity.categories.add(categoryEntity)
                                }

                                categoryPos++
                            }
                        }
                    }
                }

                // endregion

                // region # w 적립완료 및 참여 불가
                viewType = OfferwallViewStateType.VIEW_TYPE_SECTION

                // endregion

            }
            // endregion

        }
    }

    private fun getItem(itemObj: JSONObject): OfferwallItemEntity {
        return OfferwallItemEntity(
            advertiseID = itemObj.toStringValue("advertiseID"),
            productID = itemObj.toStringValue("productID"),
            badgeIconUrl = itemObj.toStringValue("badgeIconUrl"),
            title = itemObj.toStringValue("title"),
            displayTitle = itemObj.toStringValue("displayTitle"),
            iconUrl = itemObj.toStringValue("iconUrl"),
            state = itemObj.toIntValue("state"),
            userGuide = itemObj.toStringValue("userGuide"),
            actionName = itemObj.toStringValue("actionName"),
            reward = itemObj.toIntValue("reward"),
            packageName = itemObj.toStringValue("packageName"),
            journeyState = OfferwallJourneyStateType.from(itemObj.toIntValue("journeyState")),
            actionBGColor = itemObj.toStringValue("actionBGColor"),
            additionalDescription = itemObj.toStringValue("additionalDescription"),
        )
    }
}