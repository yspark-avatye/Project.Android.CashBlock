package com.avatye.cashblock.base.internal.server.entity.banner

import com.avatye.cashblock.base.component.domain.entity.banner.BannerBalanceEntity
import com.avatye.cashblock.base.library.miscellaneous.toIntValue
import com.avatye.cashblock.base.internal.server.serve.ServeSuccess
import org.json.JSONObject

internal class ResBannerBalance : ServeSuccess() {
    var bannerBalanceEntity = BannerBalanceEntity()
        private set

    override fun makeBody(responseValue: String) {
        JSONObject(responseValue).let {
            bannerBalanceEntity = BannerBalanceEntity(
                balance = it.toIntValue("balance")
            )
        }
    }
}


