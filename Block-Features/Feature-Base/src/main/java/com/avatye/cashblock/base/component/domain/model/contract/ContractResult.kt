package com.avatye.cashblock.base.component.domain.model.contract

sealed class ContractResult<out T> {
    data class Success<out T>(val contract: T) : ContractResult<T>()
    data class Failure<out T>(val statusCode: Int, val errorCode: String, val message: String) : ContractResult<T>()
}