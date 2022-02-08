package com.avatye.cashblock.base.internal.server.entity.offerwall

import com.avatye.cashblock.base.component.domain.entity.offerwall.*
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

    val sections: MutableList<OfferwallSectionEntity> = mutableListOf()
    private val joinCompleteItems: MutableList<OfferwallItemEntity> = mutableListOf()


    private fun getTypeFirstItem(listType: OfferwallBindItemListType): OfferwallItemEntity {
        when (listType) {
            OfferwallBindItemListType.SECTION -> {
                return OfferwallItemEntity(
                    sectionID = sectionEntity.sectionID,
                    sectionName = sectionEntity.sectionName,
                    viewType = OfferwallViewStateType.VIEW_TYPE_SECTION,
                    sectionTitle = sectionEntity.sectionTitle,
                )
                // TODO sectionPOS,categoryPos
            }

            OfferwallBindItemListType.CATEGORY -> {
                return OfferwallItemEntity(
                    sectionID = categoryEntity.categoryID,
                    viewType = OfferwallViewStateType.VIEW_TYPE_CATEGORY,
                    sectionTitle = categoryEntity.categoryName,
                )
                // TODO sectionPOS,categoryPos
            }

            else -> return OfferwallItemEntity()
        }
    }


    override fun makeBody(responseValue: String) {
        JSONObject(responseValue).let {
            // region # pointName
            pointName = it.toStringValue("pointName")
            // endregion

            // region # section (Root)
            val sectionCollection = it.toJSONArrayValue("sections")
            sectionCollection?.until { sectionObj ->
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
                            val item = getItemEntity(section)
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

                // first item (section)
                val sectionFirstItem: OfferwallItemEntity = getTypeFirstItem(listType = OfferwallBindItemListType.SECTION)

                // endregion


                // region # categories
                val categoryCollection = sectionObj.toJSONArrayValue("categories")
                categoryCollection?.let { it ->
                    if (it.length() > 0) {
                        it.until { categoryObj ->
                            categoryEntity = OfferwallSectionEntity.OfferwallCategoryEntity(
                                categoryID = categoryObj.toStringValue("categoryID"),
                                categoryName = categoryObj.toStringValue("categoryName"),
                                categorySortOrder = categoryObj.toIntValue("categorySortOrder"),
                            )
                            val categoryItems = categoryObj.toJSONArrayValue("items")

                            categoryItems?.until { categories ->
                                val item = getItemEntity(categories)
                                viewType = OfferwallViewStateType.VIEW_TYPE_CATEGORY_ITEM

                                if (item.journeyState == OfferwallJourneyStateType.COMPLETED_REWARDED) {
                                    joinCompleteItems.add(item)
                                } else {
                                    categoryEntity.items.add(item)
                                }

                                // first item (category)
                                val categoryFirstItem = getTypeFirstItem(listType = OfferwallBindItemListType.CATEGORY)

                                if (categoryEntity.items.size > 0) {
                                    categoryEntity.items.add(0, categoryFirstItem)
                                    sectionEntity.categories.add(categoryEntity)
                                }

                                categoryPos++
                            }
                        }
                    }
                }
                // endregion

                // item add
                if (sectionEntity.items.size > 0 || sectionEntity.categories.size > 0) {
                    sectionEntity.items.add(0, sectionFirstItem)
                    sections.add(sectionEntity)
                    sectionPos++
                }
                // endregion

                // region # w 적립완료 및 참여 불가
                viewType = OfferwallViewStateType.VIEW_TYPE_SECTION
                // endregion

            }
            // endregion
        }
    }

    private fun getItemEntity(itemObj: JSONObject): OfferwallItemEntity {
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