package com.avatye.cashblock.base.internal.controller

import com.avatye.cashblock.base.Core
import com.avatye.cashblock.base.block.BlockType
import com.avatye.cashblock.base.component.contract.api.SupportApiContractor
import com.avatye.cashblock.base.component.domain.entity.base.LandingType
import com.avatye.cashblock.base.component.domain.entity.support.PopupDisplayType
import com.avatye.cashblock.base.component.domain.entity.support.PopupNoticeEntity
import com.avatye.cashblock.base.component.domain.listener.IPopupNoticeDataListener
import com.avatye.cashblock.base.component.domain.model.app.CoreBaseActivity
import com.avatye.cashblock.base.component.domain.model.contract.ContractResult
import com.avatye.cashblock.base.component.widget.dialog.DialogPopupNotice
import com.bumptech.glide.Glide
import org.joda.time.DateTime
import java.util.*
import kotlin.collections.LinkedHashMap

class PopupNoticeController(private val blockType: BlockType, private val popupNoticeDataListener: IPopupNoticeDataListener) {

    private val pattern = "yyyyMMdd"
    private val popupQueues: Queue<PopupNoticeEntity> = LinkedList()

    fun synchronization(synchronized: () -> Unit) {
        SupportApiContractor(blockType = blockType).let { support ->
            support.retrievePopups {
                when (it) {
                    is ContractResult.Failure -> synchronized()
                    is ContractResult.Success -> {
                        fillPopupQueues(it.contract)
                        synchronized()
                    }
                }
            }
        }
    }

    fun requestPopups(ownerActivity: CoreBaseActivity, callback: () -> Unit) {
        popupQueues.poll()?.let {
            showPopup(
                ownerActivity = ownerActivity,
                popupEntity = it,
                requestCallback = callback
            )
        } ?: callback()
    }

    private fun showPopup(ownerActivity: CoreBaseActivity, popupEntity: PopupNoticeEntity, requestCallback: () -> Unit) {
        DialogPopupNotice.create(
            ownerActivity = ownerActivity,
            entity = popupEntity,
            dialogAction = object : DialogPopupNotice.IDialogAction {
                override fun onClose(popupID: String, displayType: PopupDisplayType?) {
                    displayType?.let { addVerifyItem(popupID, it) }
                    // loop
                    requestPopups(ownerActivity = ownerActivity, callback = requestCallback)
                }

                override fun onLanding(landingName: String, landingValue: String) {
                    // landing
                    LandingController.requestLanding(
                        blockType = blockType,
                        ownerActivity = ownerActivity,
                        ownerActivityClose = false,
                        landingType = LandingType.from(landingName),
                        landingValue = landingValue,
                        fallback = { requestPopups(ownerActivity = ownerActivity, callback = requestCallback) }
                    )
                }
            },
            callback = {
                ownerActivity.putDialogView(it)
                it?.show(cancelable = false)
            }
        )
    }

    private fun fillPopupQueues(syncItems: MutableList<PopupNoticeEntity>) {
        if (!Core.isInitialized) {
            return
        }

        if (popupQueues.isNotEmpty()) {
            popupQueues.clear()
        }

        val dataContainer = popupNoticeDataListener.getItems()
        for (item in syncItems) {
            if (!isExists(popupID = item.popupID, mapDataContainer = dataContainer)) {
                Glide.with(Core.application).load(item.imageUrl).preload()
                popupQueues.add(item)
            }
        }
    }

    private fun isExists(popupID: String, mapDataContainer: LinkedHashMap<String, Int>): Boolean {
        val expireDateTime = mapDataContainer[popupID] ?: return false
        val currentDateTime = DateTime().toString(pattern).toInt()
        if (expireDateTime == 0) {
            return true
        }
        if (expireDateTime > currentDateTime) {
            return true
        }
        return false
    }

    private fun addVerifyItem(popupID: String, popupDisplayType: PopupDisplayType) {
        if (popupID.isEmpty()) {
            return
        }

        val expireValue = when (popupDisplayType) {
            PopupDisplayType.NEVER -> 0
            PopupDisplayType.ONETIME -> 0
            PopupDisplayType.TODAY -> DateTime().toString(pattern).toInt()
            PopupDisplayType.WEEK -> DateTime().plusDays(7).toString(pattern).toInt()
        }
        // limit 100
        val collection = popupNoticeDataListener.getItems()
        if (collection.size < 100) {
            collection[popupID] = expireValue
            saveVerifyItem(collection)
        } else {
            val newCollection = LinkedHashMap<String, Int>()
            collection.keys.forEachIndexed { i, s ->
                if (i >= 10) {
                    collection[s]?.let { newCollection[s] = it }
                }
            }
            newCollection[popupID] = expireValue
            saveVerifyItem(newCollection)
        }
    }

    private fun saveVerifyItem(entries: Map<String, Int>) {
        popupNoticeDataListener.setItems(data = entries)
    }
}