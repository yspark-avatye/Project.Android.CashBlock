package com.avatye.cashblock.base.internal.server.entity.user

import com.avatye.cashblock.base.component.domain.entity.user.UserEntity
import com.avatye.cashblock.base.library.miscellaneous.toBooleanValue
import com.avatye.cashblock.base.library.miscellaneous.toStringValue
import com.avatye.cashblock.base.internal.server.serve.ServeSuccess
import org.json.JSONObject

internal class ResUser : ServeSuccess() {
    var userEntity = UserEntity()
        private set

    override fun makeBody(responseValue: String) {
        JSONObject(responseValue).let {
            userEntity = UserEntity(
                userID = it.toStringValue("userID"),
                ageVerified = it.toBooleanValue("ageVerified")
            )
        }
    }
}