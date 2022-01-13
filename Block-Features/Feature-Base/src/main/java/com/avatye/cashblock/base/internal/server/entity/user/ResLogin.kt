package com.avatye.cashblock.base.internal.server.entity.user

import com.avatye.cashblock.base.component.domain.entity.user.AgeVerifiedType
import com.avatye.cashblock.base.component.domain.entity.user.LoginEntity
import com.avatye.cashblock.base.component.domain.entity.user.TokenType
import com.avatye.cashblock.base.library.miscellaneous.toBooleanValue
import com.avatye.cashblock.base.library.miscellaneous.toStringValue
import com.avatye.cashblock.base.internal.server.serve.ServeSuccess
import org.json.JSONObject

internal class ResLogin : ServeSuccess() {
    var loginEntity = LoginEntity()
        private set

    override fun makeBody(responseValue: String) {
        JSONObject(responseValue).let {
            loginEntity = LoginEntity(
                sdkUserID = it.toStringValue("userID"),
                ageVerifiedType = AgeVerifiedType.from(it.toBooleanValue("ageVerified")),
                accessToken = it.toStringValue("accessToken"),
                tokenType = TokenType.from(it.toStringValue("tokenType")),
            )
        }
    }
}