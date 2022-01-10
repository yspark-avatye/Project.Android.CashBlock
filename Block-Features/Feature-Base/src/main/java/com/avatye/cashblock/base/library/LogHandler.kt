package com.avatye.cashblock.base.library

import android.util.Log
import com.avatye.cashblock.base.FeatureCore
import java.io.PrintWriter
import java.io.StringWriter

class LogHandler(private val moduleName: String) {

    // region # class instance
    fun i(throwable: Throwable? = null, trace: () -> String) = LogHandler.i(
        throwable = throwable,
        moduleName = moduleName,
        trace = trace
    )

    fun d(throwable: Throwable? = null, trace: () -> String) = LogHandler.d(
        throwable = throwable,
        moduleName = moduleName,
        trace = trace
    )

    fun v(throwable: Throwable? = null, trace: () -> String) = LogHandler.v(
        throwable = throwable,
        moduleName = moduleName,
        trace = trace
    )

    fun w(throwable: Throwable? = null, trace: () -> String) = LogHandler.w(
        throwable = throwable,
        moduleName = moduleName,
        trace = trace
    )

    fun e(throwable: Throwable? = null, trace: () -> String) = LogHandler.e(
        throwable = throwable,
        moduleName = moduleName,
        trace = trace
    )

    fun e(throwable: Throwable? = null) = LogHandler.e(
        throwable = throwable,
        moduleName = moduleName,
        trace = { "" }
    )

    fun p(throwable: Throwable? = null, trace: () -> String) = LogHandler.p(
        throwable = throwable,
        moduleName = moduleName,
        trace = trace
    )
    // endregion

    companion object {
        private val TAG: String = "CASHBLOCK"
        val allowLog: Boolean get() = FeatureCore.allowLog

        fun p(throwable: Throwable? = null, moduleName: String, trace: () -> String) {
            println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++")
            println("$TAG:[$moduleName]>>>")
            println(trace())
            println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++")
        }

        fun i(throwable: Throwable? = null, moduleName: String, trace: () -> String) {
            if (allowLog) {
                Log.i(TAG, makeLog(throwable, moduleName, trace))
            }
        }

        fun d(throwable: Throwable? = null, moduleName: String, trace: () -> String) {
            if (allowLog) {
                Log.d(TAG, makeLog(throwable, moduleName, trace))
            }
        }

        fun v(throwable: Throwable? = null, moduleName: String, trace: () -> String) {
            if (allowLog) {
                Log.v(TAG, makeLog(throwable, moduleName, trace))
            }
        }

        fun w(throwable: Throwable? = null, moduleName: String, trace: () -> String) {
            if (allowLog) {
                Log.w(TAG, makeLog(throwable, moduleName, trace))
            }
        }

        fun e(throwable: Throwable? = null, moduleName: String, trace: () -> String) {
            if (allowLog) {
                Log.e(TAG, makeLog(throwable, moduleName, trace))
            }
        }

        fun e(throwable: Throwable? = null, moduleName: String) {
            if (allowLog) {
                Log.e(TAG, makeLog(throwable, moduleName) { "" })
            }
        }


        private fun makeLog(throwable: Throwable? = null, moduleName: String, trace: () -> String): String {
            val builder = StringBuilder()
            throwable?.let {
                builder.appendLine("")
                builder.appendLine("$moduleName:Log =>")
                builder.appendLine("[${trace()}]")
                builder.appendLine("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++")
                builder.appendLine("StackTrace =>")
                builder.appendLine("[${getStackTraceString(it)}]")
                builder.appendLine("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++")
            } ?: run {
                builder.append("[$moduleName:Log => ${trace()}]")
            }
            return builder.toString()
        }


        private fun getStackTraceString(t: Throwable): String {
            // Don't replace this with Log.getStackTraceString() - it hides
            // UnknownHostException, which is not what we want.
            val sw = StringWriter(256)
            val pw = PrintWriter(sw, false)
            t.printStackTrace(pw)
            pw.flush()
            return sw.toString()
        }
    }
}