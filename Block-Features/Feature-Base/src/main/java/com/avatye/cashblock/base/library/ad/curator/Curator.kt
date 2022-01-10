package com.avatye.cashblock.base.library.ad.curator

import android.content.Context
import com.avatye.cashblock.base.MODULE_NAME
import com.avatye.cashblock.base.library.LogHandler
import com.igaworks.ssp.AdPopcornSSP
import com.igaworks.ssp.SSPErrorCode

internal object Curator {

    const val RefreshTime: Int = -1
    const val NetworkScheduleTimeout: Int = 8
    const val VideoNetworkScheduleTimeout: Int = 8

    // region { advertise ssp module init - async }
    internal fun initSSP(context: Context, appKey: String, actionCompleteInitialize: () -> Unit) {
        if (!AdPopcornSSP.isInitialized(context.applicationContext)) {
            AdPopcornSSP.init(context.applicationContext, appKey) {
                actionCompleteInitialize()
                verifyAdvertiseMediationImport()
            }
        } else {
            actionCompleteInitialize()
        }
    }

    internal fun release(context: Context) {
        if (AdPopcornSSP.isInitialized(context.applicationContext)) {
            kotlin.runCatching {
                AdPopcornSSP.destroy()
            }.onFailure {
                LogHandler.e(moduleName = MODULE_NAME, throwable = it) {
                    "AdPopcornSSP -> Destroy -> Exception"
                }
            }
        }
    }
    // endregion

    private val mediationClasses: HashMap<String, String> by lazy {
        hashMapOf(
            "ADCOLONY" to "com.adcolony.sdk.AdColonyRewardListener",
            "ADFIT" to "com.kakao.adfit.ads.AdListener",
            "ADMOB" to "com.google.android.gms.ads.AdListener",
            "CAULY" to "com.fsn.cauly.CaulyAdViewListener",
            "FACEBOOK" to "com.facebook.ads.AbstractAdListener",
            "FYBER" to "com.fyber.inneractive.sdk.external.InneractiveAdSpot.RequestListener",
            "MEZZO-MEDIA" to "com.mmc.man.AdListener",
            "MINTEGRAL" to "com.mintegral.msdk.out.RewardVideoListener",
            "MOBON" to "com.mobon.sdk.callback.iMobonBannerCallback",
            "MOPUB" to "com.mopub.mobileads.MoPubView.BannerAdListener",
            "PANGLE" to "com.bytedance.sdk.openadsdk.TTAdSdk.InitCallback",
            "TABJOY" to "com.tapjoy.TJConnectListener",
            "UNITY" to "com.unity3d.services.monetization.IUnityMonetizationListener",
            "VUNGLE" to "com.vungle.warren.LoadAdCallback"
        )
    }

    /* valid advertise mediation class */
    private fun verifyAdvertiseMediationImport() {
        if (LogHandler.allowLog) {
            println("ADVERTISE MEDIATION SDK ********************************************************************************")
            mediationClasses.forEach { map ->
                kotlin.runCatching {
                    Class.forName(map.value)
                }.onSuccess {
                    println("*  -> Import: ${map.key}")
                }
            }
            println("******************************************************************************** ADVERTISE MEDIATION SDK")
        }
    }

    internal fun isBlocked(sspErrorCode: SSPErrorCode?): Boolean {
        return (sspErrorCode?.errorCode ?: 0) == SSPErrorCode.UNKNOWN_SERVER_ERROR
    }
}