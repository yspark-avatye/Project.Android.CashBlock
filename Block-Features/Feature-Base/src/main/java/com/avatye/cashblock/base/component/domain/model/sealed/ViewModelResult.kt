package com.avatye.cashblock.base.component.domain.model.sealed

sealed class ViewModelResult<out T : Any> {
    data class Complete<out T : Any>(val result: T) : ViewModelResult<T>()
    data class Error<out T : Any>(val statusCode: Int, val errorCode: String, val message: String) : ViewModelResult<T>()
    object InProgress : ViewModelResult<Nothing>()
}