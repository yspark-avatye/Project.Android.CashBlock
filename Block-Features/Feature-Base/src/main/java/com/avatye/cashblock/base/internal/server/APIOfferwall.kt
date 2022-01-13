package com.avatye.cashblock.base.internal.server

internal object APIOfferwall {
//    fun getOfferwalls(appId: String, tokenizer: IServeToken, response: ServeResponse<ResOfferwallList>) {
//        Serve<ResOfferwallList>()
//            .withTag(tag = tag)
//            .withMethod(method = Serve.Method.GET)
//            .withRequestUrl(requestUrl = "advertising/offerwalls")
//            .withAcceptVersion("1.2.0")
//            .withAuthorization(authorization = Serve.Authorization.BEARER)
//            .withBody(
//                argsBody = hashMapOf(
//                    "appID" to UnitCoreConfig.appID,
//                    "userID" to AccountPreferenceData.appUserId
//                )
//            )
//            .withResponseClass(ResOfferwallList::class.java)
//            .withResponseCallback(responseCallback = response)
//            .execute()
//    }
//
//    fun getOfferwallTabs(appId: String, tokenizer: IServeToken, response: ServeResponse<ResVoid>) {
//        Serve<ResVoid>()
//            .withTag(tag = tag)
//            .withMethod(method = Serve.Method.GET)
//            .withRequestUrl(requestUrl = "advertising/offerwall/tabs")
//            .withAcceptVersion("1.0.0")
//            .withAuthorization(authorization = Serve.Authorization.BASIC)
//            .withBody(argsBody = hashMapOf("appID" to UnitCoreConfig.appID))
//            .withResponseClass(ResVoid::class.java)
//            .withResponseCallback(responseCallback = response)
//            .execute()
//    }
//
//    fun postOfferwallImpression(appId: String, tokenizer: IServeToken, advertiseID: String, response: ServeResponse<ResOfferWallImpression>) {
//        Serve<ResOfferWallImpression>()
//            .withTag(tag = tag)
//            .withMethod(method = Serve.Method.POST)
//            .withRequestUrl(requestUrl = "advertising/offerwall/impression")
//            .withAcceptVersion("1.0.0")
//            .withAuthorization(authorization = Serve.Authorization.BEARER)
//            .withBody(
//                argsBody = hashMapOf(
//                    "appID" to UnitCoreConfig.appID,
//                    "userID" to AccountPreferenceData.appUserId,
//                    "advertiseID" to advertiseID
//                )
//            )
//            .withResponseClass(ResOfferWallImpression::class.java)
//            .withResponseCallback(responseCallback = response)
//            .execute()
//    }
//
//    fun postOfferwallClick(
//        appId: String,
//        tokenizer: IServeToken,
//        advertiseID: String,
//        impressionID: String,
//        deviceADID: String,
//        androidID: String,
//        response: ServeResponse<ResOfferwallClick>
//    ) {
//        Serve<ResOfferwallClick>()
//            .withTag(tag = tag)
//            .withMethod(method = Serve.Method.POST)
//            .withRequestUrl(requestUrl = "advertising/offerwall/click")
//            .withAcceptVersion("1.0.0")
//            .withAuthorization(authorization = Serve.Authorization.BEARER)
//            .withBody(
//                argsBody = hashMapOf(
//                    "appID" to UnitCoreConfig.appID,
//                    "userID" to AccountPreferenceData.appUserId,
//                    "advertiseID" to advertiseID,
//                    "impressionID" to impressionID,
//                    "deviceADID" to deviceADID,
//                    "deviceID" to androidID,
//                    "deviceAccounts" to "",
//                    "deviceNetwork" to UnitCoreConfig.appContext.connectivityTypeName,
//                    "deviceModel" to Build.MODEL.replace("\\s".toRegex(), "-").uppercase(),
//                    "deviceCarrier" to Build.VERSION.RELEASE,
//                    "deviceOS" to "Android-${Build.VERSION.RELEASE}",
//                    "deviceIP" to ""
//                )
//            )
//            .withResponseClass(ResOfferwallClick::class.java)
//            .withResponseCallback(responseCallback = response)
//            .execute()
//    }
//
//    fun postOfferwallConversion(
//        appId: String,
//        tokenizer: IServeToken,
//        advertiseID: String,
//        clickID: String,
//        deviceADID: String,
//        androidID: String,
//        response: ServeResponse<ResVoid>
//    ) {
//        Serve<ResVoid>()
//            .withTag(tag = tag)
//            .withMethod(method = Serve.Method.POST)
//            .withRequestUrl(requestUrl = "advertising/offerwall/conversion")
//            .withAcceptVersion("1.0.0")
//            .withAuthorization(authorization = Serve.Authorization.BEARER)
//            .withBody(
//                argsBody = hashMapOf(
//                    "appID" to UnitCoreConfig.appID,
//                    "userID" to AccountPreferenceData.appUserId,
//                    "advertiseID" to advertiseID,
//                    "clickID" to clickID,
//                    "deviceADID" to deviceADID,
//                    "deviceIP" to "",
//                    "deviceNetwork" to UnitCoreConfig.appContext.connectivityTypeName,
//                    "deviceID" to androidID
//                )
//            )
//            .withResponseClass(ResVoid::class.java)
//            .withResponseCallback(responseCallback = response)
//            .execute()
//    }
//
//    fun postOfferwallClose(appId: String, tokenizer: IServeToken, advertiseID: String, response: ServeResponse<ResVoid>) {
//        Serve<ResVoid>()
//            .withTag(tag = tag)
//            .withMethod(method = Serve.Method.POST)
//            .withRequestUrl(requestUrl = "advertising/offerwall/close")
//            .withAcceptVersion("1.0.0")
//            .withAuthorization(authorization = Serve.Authorization.BEARER)
//            .withBody(
//                argsBody = hashMapOf(
//                    "appID" to UnitCoreConfig.appID,
//                    "userID" to AccountPreferenceData.appUserId,
//                    "advertiseID" to advertiseID
//                )
//            )
//            .withResponseClass(ResVoid::class.java)
//            .withResponseCallback(responseCallback = response)
//            .execute()
//    }
}