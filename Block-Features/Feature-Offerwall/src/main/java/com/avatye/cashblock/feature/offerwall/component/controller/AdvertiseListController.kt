package com.avatye.cashblock.feature.offerwall.component.controller

import com.avatye.cashblock.base.component.domain.entity.offerwall.*
import com.avatye.cashblock.feature.offerwall.component.data.PreferenceData

object AdvertiseListController {
    // list refresh
    var needRefreshList: Boolean = false

    // sectionEntity
    private lateinit var sectionEntity:OfferwallSectionEntity

    // categoryItem
    private var categoryItemList: MutableList<OfferwallItemEntity> = mutableListOf()
    private var categoryItemPos: Int = 0

    // items
    private var offerwalls: MutableList<OfferwallItemEntity> = mutableListOf()



    fun makeOfferwallList(sectionList: MutableList<OfferwallSectionEntity>, tabEntity: OfferwallTabEntity): MutableList<OfferwallItemEntity> {
        offerwalls = mutableListOf()

        when (tabEntity.listType) {
            // Mix
            OfferwallListType.MIX.value -> {
                sectionList.filter { tabEntity.sectionIDList.contains(it.sectionID) }.forEach {
                    sectionEntity = it
                    if (it.categories.size > 0) {
                        it.categories.forEach { category ->
                            val filterItems = getFilterItems(category.items)
                            categoryItemPos += filterItems[0].sectionPos
                            categoryItemList.addAll(
                                getHiddenSections(
                                    tabEntity = tabEntity,
                                    list = filterItems
                                )
                            )
                        }

                        it.items[0].sectionPos = categoryItemPos
                        categoryItemList.add(0, it.items[0])

                        offerwalls.addAll(
                            getHiddenSections(tabEntity = tabEntity, list = categoryItemList)
                        )

                    } else {
                        offerwalls.addAll(
                            getHiddenSections(tabEntity = tabEntity, list = getFilterItems(it.items))
                        )
                    }
                }

                if(sectionEntity.joinCompleteItems.size > 1) {
                    offerwalls.addAll(getHiddenSections(tabEntity = tabEntity, list = getFilterItems(sectionEntity.joinCompleteItems)))
                }
            }

            // Section
            OfferwallListType.ONLY_SECTION.value -> {
                sectionList.filter { tabEntity.sectionIDList.contains(it.sectionID) }.forEach {
                    offerwalls.addAll(
                        getHiddenSections(
                            tabEntity = tabEntity,
                            list = getFilterItems(it.items),
                        )
                    )
                }
            }

            // Category
            OfferwallListType.ONLY_CATEGORY.value -> {
                sectionList.filter { tabEntity.sectionIDList.contains(it.sectionID) }.forEach {
                    it.categories.forEach { category ->
                        offerwalls.addAll(
                            getHiddenSections(
                                tabEntity = tabEntity,
                                list = getFilterItems(category.items)
                            )
                        )
                    }
                }
            }
        }


        if (offerwalls.isNotEmpty()) {
            offerwalls.last().isLast = false
        }

        return offerwalls
    }

    fun getHiddenSectionID(tabEntity: OfferwallTabEntity, entity:OfferwallItemEntity): String {
        val tabID = tabEntity.tabID
        val sectionID = if(entity.sectionName.isEmpty()) {
            entity.sectionID
        } else {
            entity.sectionName
        }
//        val sectionID = if(list[0].sectionName.isEmpty()) {
//            list[0].sectionID
//        } else {
//            list[0].sectionName
//        }
//        val sectionID = list[0].sectionID
        return tabID.plus(sectionID)
    }


    fun changeAllList(sections: MutableList<OfferwallSectionEntity>, entity: OfferwallItemEntity, journeyStateType: OfferwallJourneyStateType) {
        when (journeyStateType) {
            OfferwallJourneyStateType.COMPLETED_REWARDED,
            OfferwallJourneyStateType.COMPLETED_FAILED -> {
                entity.journeyState = journeyStateType
                entity.progressType = OfferwallProgressType.COMPLETED
                sectionEntity.joinCompleteItems.add(entity)

                if (entity.viewType == OfferwallViewStateType.VIEW_TYPE_ITEM) {
                    sections[entity.sectionPos].items.remove(entity)
                } else if (entity.viewType == OfferwallViewStateType.VIEW_TYPE_CATEGORY_ITEM) {
                    sections[entity.sectionPos].categories[entity.categoryPos].items.remove(entity)
                }
            }
            else -> {
                if (entity.viewType == OfferwallViewStateType.VIEW_TYPE_ITEM) {
                    sections[entity.sectionPos].items.find { it == entity }?.journeyState = journeyStateType
                } else if (entity.viewType == OfferwallViewStateType.VIEW_TYPE_CATEGORY_ITEM) {
                    sections[entity.sectionPos].categories[entity.categoryPos].items.find { it == entity }?.journeyState = journeyStateType
                }
            }
        }
    }


    private fun getFilterItems(list: MutableList<OfferwallItemEntity>): MutableList<OfferwallItemEntity> {
        if (list.isEmpty()) {
            return mutableListOf()
        }

        val unHiddenItems = list.filter {
            !(PreferenceData.Hidden.hiddenItems ?: listOf()).contains(it.advertiseID)
        }
        unHiddenItems[0].sectionPos = unHiddenItems.size - 1

        if (unHiddenItems.lastIndex != 0) {
            unHiddenItems[unHiddenItems.lastIndex - 1].isLast = false
        } else {
            unHiddenItems.last().isLast = true
        }

        return unHiddenItems.toMutableList()
    }


    private fun getHiddenSections(tabEntity: OfferwallTabEntity, list: MutableList<OfferwallItemEntity>): MutableList<OfferwallItemEntity> {
        if (list.isEmpty()) {
            return mutableListOf()
        }

        val hiddenSectionID = getHiddenSectionID(tabEntity = tabEntity, entity = list[0])

        if ((PreferenceData.Hidden.hiddenSections ?: listOf()).contains(hiddenSectionID)) {
            return listOf(list[0]).toMutableList()
        }

        return list

//        val sectionID = if(list[0].sectionName.isEmpty()) {
//            list[0].sectionID
//        } else {
//            list[0].sectionName
//        }

    }


}