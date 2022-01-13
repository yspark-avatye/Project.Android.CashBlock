package com.avatye.cashblock.publisher

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.annotation.Keep
import com.avatye.cashblock.CashBlockSDK
import com.avatye.cashblock.base.block.BlockController
import com.avatye.cashblock.base.block.BlockType
import com.avatye.cashblock.base.component.contract.business.CoreContractor
import com.avatye.cashblock.base.component.contract.api.PublisherApiContractor
import com.avatye.cashblock.base.component.domain.model.contract.ContractResult
import com.avatye.cashblock.base.component.support.CoreUtil
import com.avatye.cashblock.feature.roulette.CashBlockRoulette
import com.avatye.cashblock.feature.roulette.component.model.entity.notification.NotificationServiceConfig
import com.avatye.cashblock.publisher.PublisherConfig.logger
import com.avatye.cashblock.publisher.component.data.PreferenceData
import org.joda.time.DateTime

@Keep
object CashBlockPublisherSDK {
    @JvmStatic
    fun initialize(application: Application, publisherId: String, publisherAppKey: String, log: Boolean = false) {
        // log
        if (log) {
            logger.p { "## CashBlockPublisherSDK -> initialize" }
            logger.p { "## Version { versionName:${BuildConfig.X_BUILD_SDK_VERSION_NAME}, versionCode:${BuildConfig.X_BUILD_SDK_VERSION_CODE} }" }
            logger.p { "## Publisher { PublisherId:$publisherId, publisherAppKey:$publisherAppKey }" }
        }
        // valid publisher keys
        checkPublisherKey(context = application, publisherId = publisherId, publisherAppKey = publisherAppKey)
        // publisher app-block key
        val publisherBlockAppId = PreferenceData.cashBlockAppId
        val publisherBlockAppSecret = PreferenceData.cashBlockAppSecret
        if (publisherBlockAppId.isNotEmpty() && publisherBlockAppSecret.isNotEmpty()) {
            if (log) {
                logger.p { "## CashBlockPublisherSDK { publisherBlockAppId:$publisherBlockAppId, publisherBlockAppSecret:$publisherBlockAppSecret }" }
            }
            CashBlockSDK.initialize(application = application)
        } else {
            if (log) {
                println("## CashBlockPublisherSDK ## Publisher initialize")
            }
        }
    }

    @JvmStatic
    fun launch(context: Context, publisherId: String, publisherAppKey: String, aaid: String, listener: ILaunchCallback) {
        // check sdk initialize
        if (CoreContractor.isInitialized) {
            logger.e { "launch -> 저장된 값으로 초기화 완료 !!" }
            // success
            listener.onSuccess()
        } else {
            // synchronized
            if (allowSyncPublisher()) {
                logger.e { "launch -> 저장값 없음 -> 서버 요청" }
                syncPublisherLaunch(context = context, publisherId = publisherId, publisherAppKey = publisherAppKey, publisherAAID = aaid, listener = listener)
            } else {
                val endDate = DateTime(PreferenceData.publisherRetryEndTime)
                logger.e { "launch -> 저장값 없음 -> 서버 요청 -> 재시도 시간 안됨 { ${endDate.toString("yyyy.MM.dd HH:mm")} }" }
                // callback
                listener.onFailure()
            }
        }
    }

    // region # base
    @JvmStatic
    fun getAppUserId(): String = CashBlockSDK.getAppUserId()

    @JvmStatic
    fun setAppUserId(appUserId: String) = CashBlockSDK.setAppUserId(appUserId = appUserId)

    @JvmStatic
    fun setCustomData(customData: String?) = CashBlockSDK.setCustomData(customData = customData)
    // endregion


    // region # ROULETTE
    @JvmStatic
    fun startRoulette(activity: Activity, callback: ICompleteCallback) {
        BlockController.launchBlock(context = activity, blockType = BlockType.PUBLISHER) {
            when (it) {
                true -> {
                    callback.onSuccess()
                    logger.i { "CashBlockPublisherSDK -> startRoulette -> success" }
                }
                false -> {
                    callback.onFailure()
                    CoreUtil.showToast(message = R.string.acb_common_message_error)
                    logger.i { "CashBlockPublisherSDK -> startRoulette -> fail" }

                }
            }
        }
    }

    /**
     * 티켓박스 사용자의 알림창 상태바 상태값을 설정합니다.
     * NotificationServiceConfig.smallIconResourceId
     * NotificationServiceConfig.largeIconResourceId
     * NotificationServiceConfig.channelName
     * NotificationServiceConfig.title
     * NotificationServiceConfig.text
     */
    @JvmStatic
    fun setNotificationServiceConfig(notificationServiceConfig: NotificationServiceConfig) {
        CashBlockRoulette.setNotificationServiceConfig(notificationServiceConfig = notificationServiceConfig)
    }
    // endregion


    private fun checkPublisherKey(context: Context, publisherId: String, publisherAppKey: String) {
        // check parameter value
        if (publisherId.isEmpty()) {
            throw Exception("publisherID value cannot be empty or null, parameter:publisherID is null or empty")
        }
        // check parameter value
        if (publisherAppKey.isEmpty()) {
            throw Exception("publisherAppKey value cannot be empty or null, parameter:publisherAppKey is null or empty")
        }
        // check cache value
        var currentPublisherId = PreferenceData.publisherId
        var currentPublisherAppKey = PreferenceData.publisherAppKey
        if (currentPublisherId.isEmpty()) {
            currentPublisherId = publisherId
            PreferenceData.update(publisherId = publisherId)
        }
        if (currentPublisherAppKey.isEmpty()) {
            currentPublisherAppKey = publisherAppKey
            PreferenceData.update(publisherAppKey = publisherAppKey)
        }
        // check compare value
        if (publisherId != currentPublisherId || publisherAppKey != currentPublisherAppKey) {
            // cache clear
            logger.e { "launch -> 퍼블리셔 정보가 다름 -> 삭제후 마지막 전달 값으로 변경" }
            PreferenceData.clear()
            PreferenceData.update(publisherId = publisherId, publisherAppKey = publisherAppKey)
        }
    }


    private fun allowSyncPublisher(): Boolean {
        return DateTime().millis >= PreferenceData.publisherRetryEndTime
    }


    private fun syncPublisherLaunch(context: Context, publisherId: String, publisherAppKey: String, publisherAAID: String, listener: ILaunchCallback) {
        // basic key value empty
        // communication - request api
        // PublisherDataContract()
        PublisherApiContractor().retrieveInitialize(
            blockType = BlockType.PUBLISHER,
            publisherID = publisherId,
            publisherAppKey = publisherAppKey,
            publisherAppName = CoreContractor.appName,
            publisherAAID = publisherAAID
        ) {
            when (it) {
                is ContractResult.Success -> {
                    if (it.contract.isSuccess) {
                        logger.e { "launch -> 저장값 없음 -> 서버 요청 -> 초기화 성공" }
                        // save key set
                        PreferenceData.update(
                            cashBlockAppId = it.contract.appID,
                            cashBlockAppSecret = it.contract.appSecret,
                            publisherRetryEndTime = 0L
                        )
                        // entrust to CashRouletteSDK
                        CashBlockSDK.initialize(application = context as Application)
                        listener.onSuccess()
                    } else {
                        logger.e { "launch -> 저장값 없음 -> 서버 요청 -> 실패 -> 서버요청 금지 -> ${it.contract.retryHours}시간" }
                        // save retry time(millis)
                        val retryEndTime = if (CoreContractor.allowDeveloper) {
                            // minutes/test
                            DateTime().plusMinutes(it.contract.retryHours).millis
                        } else {
                            // hours/real
                            DateTime().plusHours(it.contract.retryHours).millis
                        }
                        PreferenceData.update(publisherRetryEndTime = retryEndTime)
                        // callback
                        listener.onFailure()
                    }
                }
                is ContractResult.Failure -> {
                    logger.e { "launch -> 저장값 없음 -> 서버 요청 -> 서버 오류 { statusCode:${it.statusCode}, code: ${it.errorCode}, message: ${it.message} }" }
                    listener.onFailure()
                }
            }
        }
    }
}