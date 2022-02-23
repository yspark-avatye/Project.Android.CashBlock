package com.avatye.cashblock.base.block

import android.content.Context
import com.avatye.cashblock.base.Core.logger
import com.avatye.cashblock.base.block.BlockType.Companion.connector
import com.avatye.cashblock.base.component.contract.business.AccountContractor
import com.avatye.cashblock.base.component.contract.business.SettingContractor
import com.avatye.cashblock.base.component.domain.listener.ILoginListener
import com.avatye.cashblock.base.component.support.CoreUtil.showToast

object BlockController {
    // tag name
    private const val tagName: String = "BlockController"

    /** block module checker */
    fun hasBlock(blockType: BlockType): Boolean {
        return kotlin.runCatching { Class.forName(blockType.connector) }.isSuccess
    }

    fun launchBlock(context: Context, blockType: BlockType, callback: (success: Boolean) -> Unit) {
        makeBlockConnector(referenceName = "launchBlock", blockType = blockType)?.let { connector ->
            synchronizeBlockSession(blockType = blockType) {
                logger.i(viewName = tagName) { "launchBlock -> connector(${blockType.name}) -> synchronizeBlockSession -> launch { success: $it }" }
                when (it) {
                    true -> {
                        connector.connect(context = context)
                        callback(true)
                    }
                    false -> callback(false)
                }
            }
        } ?: run {
            logger.i(viewName = tagName) { "launchBlock -> connector(${blockType.name}) { connector is null }" }
            callback(false)
        }
    }

    fun syncBlockSession(blockType: BlockType, callback: (success: Boolean) -> Unit) {
        synchronizeBlockSession(blockType = blockType, callback = callback)
    }

    fun createBlockConnector(context: Context, blockType: BlockType, callback: (connector: BlockConnector?) -> Unit) {
        makeBlockConnector(referenceName = "launchBlock", blockType = blockType)?.let { connector ->
            synchronizeBlockSession(blockType = blockType) {
                logger.i(viewName = tagName) { "openBlockConnector -> connector(${blockType.name}) -> synchronizeBlockSession { success: $it }" }
                when (it) {
                    true -> callback(connector)
                    false -> callback(null)
                }
            }
        } ?: run {
            logger.i(viewName = tagName) { "openBlockConnector -> connector(${blockType.name}) { connector is null }" }
            callback(null)
        }
    }

    /** block module clear-session */
    internal fun clearSessionBlock(context: Context, blockType: BlockType) {
        makeBlockConnector(referenceName = "clearSessionBlock", blockType = blockType)?.clearSession(context = context)
    }

    // region # inner
    private fun makeBlockConnector(referenceName: String, blockType: BlockType): BlockConnector? {
        var connector: BlockConnector? = null
        if (hasBlock(blockType = blockType)) {
            try {
                Class.forName(blockType.connector).let { cls ->
                    cls.getConstructor().newInstance().let {
                        connector = (it as BlockConnector)
                        logger.i(viewName = tagName) { "makeBlockConnector -> success { functionName: $referenceName, connector: $blockType }" }
                    }
                }
            } catch (e: Exception) {
                logger.i(viewName = tagName) { "makeBlockConnector -> error { functionName: $referenceName, connector: $blockType }" }
            }
        }
        return connector
    }

    private fun synchronizeBlockSession(blockType: BlockType, callback: (success: Boolean) -> Unit) {
        // 1. remote data sync
        SettingContractor.Controller.synchronization(blockType = blockType) {
            // 2. popup or etc sync
            // 3. sync session
            AccountContractor.login(blockType = blockType, listener = object : ILoginListener {
                override fun onSuccess() = callback(true)

                override fun onFailure(reason: String) {
                    showToast(reason)
                    callback(false)
                }
            })
        }
    }
    // endregion
}