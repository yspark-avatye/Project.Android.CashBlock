package com.avatye.cashblock.feature.offerwall.component.controller

import com.avatye.cashblock.base.component.domain.entity.offerwall.OfferWallListType
import com.avatye.cashblock.base.component.domain.entity.offerwall.OfferWallTabEntity
import com.avatye.cashblock.base.component.domain.entity.offerwall.OfferwallItemEntity
import com.avatye.cashblock.base.component.domain.entity.offerwall.OfferwallSectionEntity
import com.avatye.cashblock.feature.offerwall.component.data.PreferenceData

object AdvertiseListController {

    // section
//    private var sectionList: MutableList<OfferwallSectionEntity> = mutableListOf()

    // categoryItem
    private var categoryItemList: MutableList<OfferwallItemEntity> = mutableListOf()
    private var categoryItemCount: Int = 0

    // items
    private var offerwalls: MutableList<OfferwallItemEntity> = mutableListOf()


    fun makeOfferwallList(sectionList: MutableList<OfferwallSectionEntity>, tabEntity: OfferWallTabEntity): MutableList<OfferwallItemEntity> {
        when (tabEntity.listType) {
            // Mix
            OfferWallListType.MIX.value -> {
                sectionList.filter { tabEntity.sectionIDList.contains(it.sectionID) }.forEach {
                    if (it.categories.size > 0) {
                        it.categories.forEach { category ->
                            val filterItems = getFilterItems(category.items)
                            categoryItemCount += filterItems[0].sectionCount
                            categoryItemList.addAll(
                                getHiddenSections(
                                    tabEntity = tabEntity,
                                    list = filterItems
                                )
                            )
                        }

                        it.items[0].sectionCount = categoryItemCount
                        categoryItemList.add(0, it.items[0])

                        offerwalls.addAll(
                            getHiddenSections(
                                tabEntity = tabEntity,
                                list = categoryItemList
                            )
                        )

                    } else {
                        offerwalls.addAll(
                            getHiddenSections(
                                tabEntity = tabEntity,
                                list = getFilterItems(it.items)
                            )
                        )
                    }

                }
            }

            // Section
            OfferWallListType.ONLY_SECTION.value -> {
                sectionList.filter { tabEntity.sectionIDList.contains(it.sectionID) }.forEach {
                    offerwalls.addAll(
                        getHiddenSections(
                            tabEntity = tabEntity,
                            list = it.items,
                        )
                    )
                }
            }

            // Category
            OfferWallListType.ONLY_CATEGORY.value -> {
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

    fun getHiddenSectionID(tabEntity: OfferWallTabEntity, list: MutableList<OfferwallItemEntity>): String {
        val tabID = tabEntity.tabID
        val sectionID = list[0].sectionID
        return tabID.plus(sectionID)
    }


    private fun getFilterItems(list: MutableList<OfferwallItemEntity>): MutableList<OfferwallItemEntity> {
        if (list.isEmpty()) {
            return mutableListOf()
        }

        val unHiddenItems = list.filter {
            !(PreferenceData.Hidden.hiddenItems ?: listOf()).contains(it.advertiseID)
        }
        unHiddenItems[0].sectionCount = unHiddenItems.size - 1

        if (unHiddenItems.lastIndex != 0) {
            unHiddenItems[unHiddenItems.lastIndex - 1].isLast = false
        } else {
            unHiddenItems.last().isLast = true
        }

        return unHiddenItems.toMutableList()
    }


    private fun getHiddenSections(tabEntity: OfferWallTabEntity, list: MutableList<OfferwallItemEntity>): MutableList<OfferwallItemEntity> {
        if (list.isEmpty()) {
            return mutableListOf()
        }

        val hiddenSectionID = getHiddenSectionID(tabEntity = tabEntity, list = list)

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