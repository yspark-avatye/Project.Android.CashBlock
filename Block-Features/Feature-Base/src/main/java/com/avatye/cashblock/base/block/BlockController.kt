package com.avatye.cashblock.base.block

import android.app.Application
import android.content.Context
import com.avatye.cashblock.base.FeatureCore.logger
import com.avatye.cashblock.base.component.support.CoreUtil.showToast
import com.avatye.cashblock.base.internal.controller.LoginController
import com.avatye.cashblock.base.internal.controller.RemoteSettingSyncController

object BlockController {
    // tag name
    private const val tagName: String = "BlockController"

    /** block module checker */
    internal fun hasBlock(blockType: BlockType): Boolean {
        return kotlin.runCatching { Class.forName(blockType.value) }.isSuccess
    }

    fun launchBlock(context: Context, blockCode: BlockCode, callback: (success: Boolean) -> Unit) {
        makeBlockConnector(referenceName = "launchBlock", blockType = blockCode.blockType)?.let { connector ->
            logger.i { "$tagName -> launchBlock -> connector($blockCode) -> success -> initialize" }
            connector.initialize(application = context.applicationContext as Application, blockCode = blockCode)
            synchronizeBlockSession {
                when (it) {
                    true -> {
                        logger.i { "$tagName -> launchBlock -> connector($blockCode) -> synchronizeBlockSession -> launch" }
                        connector.launch(context = context, blockCode = blockCode)
                        callback(true)
                    }
                    false -> {
                        logger.e { "$tagName -> launchBlock -> connector($blockCode) -> synchronizeBlockSession -> false" }
                        callback(false)
                    }
                }
            }
        } ?: run {
            logger.e { "$tagName -> launchBlock -> connector($blockCode) is null" }
            callback(false)
        }
    }

    fun syncBlockSession(callback: (success: Boolean) -> Unit) = synchronizeBlockSession(callback = callback)

    fun createBlockConnector(context: Context, blockCode: BlockCode, callback: (connector: BlockConnector?) -> Unit) {
        makeBlockConnector(referenceName = "launchBlock", blockType = blockCode.blockType)?.let { connector ->
            logger.e { "$tagName -> openBlockConnector -> connector($blockCode) -> success -> initialize" }
            connector.initialize(application = context.applicationContext as Application, blockCode = blockCode)
            synchronizeBlockSession {
                when (it) {
                    true -> {
                        logger.i { "$tagName -> openBlockConnector -> connector($blockCode) -> synchronizeBlockSession -> return connector" }
                        callback(connector)
                    }
                    false -> {
                        logger.e { "$tagName -> openBlockConnector -> connector($blockCode) -> synchronizeBlockSession -> false" }
                        callback(null)
                    }
                }
            }
        } ?: run {
            logger.e { "$tagName -> openBlockConnector -> connector($blockCode) is null" }
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
                Class.forName(blockType.value).let { cls ->
                    cls.getConstructor().newInstance().let {
                        connector = (it as BlockConnector)
                        logger.i { "$tagName -> makeBlockConnector -> success { functionName: $referenceName, connector: $blockType }" }
                    }
                }
            } catch (e: Exception) {
                logger.i(throwable = e) { "$tagName -> makeBlockConnector -> error { functionName: $referenceName, connector: $blockType }" }
            }
        }
        return connector
    }

    private fun synchronizeBlockSession(callback: (success: Boolean) -> Unit) {
        // 1. remote data sync
        RemoteSettingSyncController.synchronization {
            // 2. popup or etc sync
            // 3. sync session
            LoginController.requestLogin(listener = object : LoginController.ILoginListener {
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