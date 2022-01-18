package com.avatye.cashblock.base.library.ad.curator.queue

import android.app.Activity
import com.avatye.cashblock.base.Core.logger
import com.avatye.cashblock.base.MODULE_NAME
import com.avatye.cashblock.base.library.LogHandler
import com.avatye.cashblock.base.library.ad.curator.IADAgeVerifier
import com.avatye.cashblock.base.library.ad.curator.queue.loader.ADLoaderBase
import com.avatye.cashblock.base.library.ad.curator.queue.loader.ADLoaderType
import com.avatye.cashblock.base.library.ad.curator.queue.loader.IADLoaderCallback
import com.avatye.cashblock.base.library.ad.curator.queue.loader.box.BoxBannerADLoader
import com.avatye.cashblock.base.library.ad.curator.queue.loader.interstitial.InterstitialADLoader
import com.avatye.cashblock.base.library.ad.curator.queue.loader.interstitial.InterstitialNativeADLoader
import com.avatye.cashblock.base.library.ad.curator.queue.loader.interstitial.InterstitialVideoADLoader
import com.avatye.cashblock.base.library.ad.curator.queue.loader.rewardvideo.RewardVideoADLoader
import java.util.*

class CuratorQueue(
    private val activity: Activity,
    private val placementAppKey: String,
    private val sequential: List<CuratorQueueEntity>,
    private val verifier: IADAgeVerifier,
    private val callback: ICuratorQueueCallback
) : IADLoaderCallback {

    companion object {
        const val tagName: String = "CuratorQueue"
    }

    private var currentADLoader: ADLoaderBase? = null
    private val loaderQueues: Queue<CuratorQueueEntity> = LinkedList()

    init {
        sequential.forEach {
            loaderQueues.add(it)
            logger.i(viewName = tagName) { "init -> make-queue { ADLoaderType: ${it.loaderType.name}, placementID: ${it.placementID} }" }
        }
    }

    private fun poll() {
        // isAgeVerified
        if (!verifier.isVerified()) {
            callback.onNeedAgeVerification()
            return
        }
        // poll
        loaderQueues.poll()?.let {
            logger.i(viewName = tagName) { "poll { ADLoaderType: ${it.loaderType.name}, placementID: ${it.placementID} }" }
            currentADLoader = loaderFactory(it)
            currentADLoader?.requestAD()
        } ?: run {
            LogHandler.i(moduleName = MODULE_NAME) {
                "$tagName -> poll -> queue -> empty }"
            }
            callback.ondFailed(isBlocked = false)
        }
    }

    fun requestAD() = poll()

    fun onResume() = currentADLoader?.onResume()

    fun onPause() = currentADLoader?.onPause()

    fun release() = currentADLoader?.release()

    override fun onLoaded() {
        logger.i(viewName = tagName) { "onLoaded" }
        currentADLoader?.let {
            callback.onLoaded(it)
        } ?: run {
            poll()
        }
    }

    override fun ondFailed(isBlocked: Boolean) {
        if (isBlocked) {
            logger.i(viewName = tagName) { "ondFailed { isBlocked: true }" }
            currentADLoader?.release()
            currentADLoader = null
            loaderQueues.clear()
        } else {
            logger.i(viewName = tagName) { "ondFailed { isBlocked: false } -> poll" }
            currentADLoader?.release()
            loaderQueues.clear()
            poll()
        }
    }

    override fun onOpened() {
        logger.i(viewName = tagName) { "onOpened" }
        callback.onOpened()
    }

    override fun onClosed(isCompleted: Boolean) {
        logger.i(viewName = tagName) { "onClosed { isCompleted: $isCompleted }" }
        callback.onComplete(success = isCompleted)
    }

    private fun loaderFactory(entity: CuratorQueueEntity): ADLoaderBase {
        return when (entity.loaderType) {
            ADLoaderType.INTERSTITIAL -> createInterstitial(entity.placementID)
            ADLoaderType.INTERSTITIAL_NATIVE -> createInterstitialNative(entity.placementID)
            ADLoaderType.INTERSTITIAL_VIDEO -> createInterstitialVideo(entity.placementID)
            ADLoaderType.BOX_BANNER -> createBoxBanner(entity.placementID)
            ADLoaderType.REWARD_VIDEO -> createRewardVideo(entity.placementID)
        }
    }

    private fun createInterstitial(placementID: String): InterstitialADLoader {
        return InterstitialADLoader(
            activity = this.activity,
            placementAppKey = this.placementAppKey,
            placementID = placementID,
            callback = this@CuratorQueue
        )
    }

    private fun createInterstitialNative(placementID: String): InterstitialNativeADLoader {
        return InterstitialNativeADLoader(
            activity = this.activity,
            placementAppKey = this.placementAppKey,
            placementID = placementID,
            callback = this@CuratorQueue
        )
    }

    private fun createInterstitialVideo(placementID: String): InterstitialVideoADLoader {
        return InterstitialVideoADLoader(
            activity = this.activity,
            placementAppKey = this.placementAppKey,
            placementID = placementID,
            callback = this@CuratorQueue
        )
    }

    private fun createBoxBanner(placementID: String): BoxBannerADLoader {
        return BoxBannerADLoader(
            activity = this.activity,
            placementAppKey = this.placementAppKey,
            placementID = placementID,
            callback = this@CuratorQueue
        )
    }

    private fun createRewardVideo(placementID: String): RewardVideoADLoader {
        return RewardVideoADLoader(
            activity = this.activity,
            placementAppKey = this.placementAppKey,
            placementID = placementID,
            callback = this@CuratorQueue
        )
    }
}