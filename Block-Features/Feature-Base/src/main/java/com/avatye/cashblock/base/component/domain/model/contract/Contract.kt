package com.avatye.cashblock.base.component.domain.model.contract

import com.avatye.cashblock.base.internal.server.serve.ServeFailure
import com.avatye.cashblock.base.component.domain.model.sealed.ViewModelResult

object Contract {
    internal fun <T> onSuccess(success: T): ContractResult.Success<T> {
        return ContractResult.Success(success)
    }

    internal fun <T> onFailure(failure: ServeFailure): ContractResult.Failure<T> {
        return ContractResult.Failure(statusCode = failure.statusCode, errorCode = failure.errorCode, message = failure.errorMessage)
    }

    fun <T : Any> postComplete(result: T): ViewModelResult.Complete<T> {
        return ViewModelResult.Complete(result)
    }

    fun <T : Any, R> postError(error: ContractResult.Failure<R>): ViewModelResult.Error<T> {
        return ViewModelResult.Error(statusCode = error.statusCode, errorCode = error.errorCode, message = error.message)
    }

    fun postInProgress() = ViewModelResult.InProgress
}