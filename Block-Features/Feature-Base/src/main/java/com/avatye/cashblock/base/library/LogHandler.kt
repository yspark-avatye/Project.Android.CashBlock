package com.avatye.cashblock.base.library

import android.util.Log
import com.avatye.cashblock.base.Core
import java.io.PrintWriter
import java.io.StringWriter

class LogHandler(private val moduleName: String) {

    // region # class instance
    fun i(throwable: Throwable? = null, viewName: String? = null, trace: () -> String) = LogHandler.i(
        throwable = throwable,
        moduleName = moduleName,
        viewName = viewName,
        trace = trace
    )

    fun d(throwable: Throwable? = null, viewName: String? = null, trace: () -> String) = LogHandler.d(
        throwable = throwable,
        moduleName = moduleName,
        viewName = viewName,
        trace = trace
    )

    fun v(throwable: Throwable? = null, viewName: String? = null, trace: () -> String) = LogHandler.v(
        throwable = throwable,
        moduleName = moduleName,
        viewName = viewName,
        trace = trace
    )

    fun w(throwable: Throwable? = null, viewName: String? = null, trace: () -> String) = LogHandler.w(
        throwable = throwable,
        moduleName = moduleName,
        viewName = viewName,
        trace = trace
    )

    fun e(throwable: Throwable? = null, viewName: String? = null, trace: () -> String) = LogHandler.e(
        throwable = throwable,
        moduleName = moduleName,
        viewName = viewName,
        trace = trace
    )

    fun e(throwable: Throwable? = null, viewName: String? = null) = LogHandler.e(
        throwable = throwable,
        moduleName = moduleName,
        viewName = viewName,
        trace = { "" }
    )

    fun p(throwable: Throwable? = null, viewName: String? = null, trace: () -> String) = LogHandler.p(
        throwable = throwable,
        moduleName = moduleName,
        viewName = viewName,
        trace = trace
    )
    // endregion

    companion object {
        private val TAG: String = "CASHBLOCK"
        private val VANILLA: String = "cashblock.vanilla"

        val allowLog: Boolean
            get() {
                return Core.allowLog
            }

        fun p(throwable: Throwable? = null, moduleName: String, viewName: String? = null, trace: () -> String) {
            println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++")
            viewName?.let {
                println("$TAG:[$moduleName]/[$it]")
            } ?: run {
                println("$TAG:[$moduleName]")
            }
            println("=> ${trace()}")
            println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++")
        }

        fun i(throwable: Throwable? = null, moduleName: String, viewName: String? = null, trace: () -> String) {
            if (allowLog) {
                Log.i(TAG, makeLog(throwable = throwable, moduleName = moduleName, viewName = viewName, trace = trace))
            } else {
                val hasVanilla = Log.isLoggable(VANILLA, Log.INFO)
                if (hasVanilla) {
                    Log.i(VANILLA, makeLog(throwable = throwable, moduleName = moduleName, viewName = viewName, trace = trace))
                }
            }
        }

        fun d(throwable: Throwable? = null, moduleName: String, viewName: String? = null, trace: () -> String) {
            if (allowLog) {
                Log.d(TAG, makeLog(throwable = throwable, moduleName = moduleName, viewName = viewName, trace = trace))
            } else {
                val hasVanilla = Log.isLoggable(VANILLA, Log.DEBUG)
                if (hasVanilla) {
                    Log.d(VANILLA, makeLog(throwable = throwable, moduleName = moduleName, viewName = viewName, trace = trace))
                }
            }
        }

        fun v(throwable: Throwable? = null, moduleName: String, viewName: String? = null, trace: () -> String) {
            if (allowLog) {
                Log.v(TAG, makeLog(throwable = throwable, moduleName = moduleName, viewName = viewName, trace = trace))
            } else {
                val hasVanilla = Log.isLoggable(VANILLA, Log.VERBOSE)
                if (hasVanilla) {
                    Log.v(VANILLA, makeLog(throwable = throwable, moduleName = moduleName, viewName = viewName, trace = trace))
                }
            }
        }

        fun w(throwable: Throwable? = null, moduleName: String, viewName: String? = null, trace: () -> String) {
            if (allowLog) {
                Log.w(TAG, makeLog(throwable = throwable, moduleName = moduleName, viewName = viewName, trace = trace))
            } else {
                val hasVanilla = Log.isLoggable(VANILLA, Log.WARN)
                if (hasVanilla) {
                    Log.w(VANILLA, makeLog(throwable = throwable, moduleName = moduleName, viewName = viewName, trace = trace))
                }
            }
        }

        fun e(throwable: Throwable? = null, moduleName: String, viewName: String? = null, trace: () -> String) {
            if (allowLog) {
                Log.e(TAG, makeLog(throwable = throwable, moduleName = moduleName, viewName = viewName, trace = trace))
            } else {
                val hasVanilla = Log.isLoggable(VANILLA, Log.ERROR)
                if (hasVanilla) {
                    Log.e(VANILLA, makeLog(throwable = throwable, moduleName = moduleName, viewName = viewName, trace = trace))
                }
            }
        }

        fun e(throwable: Throwable? = null, moduleName: String, viewName: String? = null) {
            if (allowLog) {
                Log.e(TAG, makeLog(throwable, moduleName, viewName = viewName) { "" })
            } else {
                val hasVanilla = Log.isLoggable(VANILLA, Log.ERROR)
                if (hasVanilla) {
                    Log.e(VANILLA, makeLog(throwable, moduleName, viewName = viewName) { "" })
                }
            }
        }


        private fun makeLog(throwable: Throwable? = null, moduleName: String, viewName: String? = null, trace: () -> String): String {
            val builder = StringBuilder()
            throwable?.let {
                builder.appendLine("[Module: $moduleName] =>")
                viewName?.let {
                    builder.appendLine("Trace => [$it] => [${trace()}]")
                } ?: run {
                    builder.appendLine("Trace => [${trace()}]")
                }
                builder.appendLine("StackTrace => [${getStackTraceString(it)}]")
            } ?: run {
                builder.appendLine("[Module: $moduleName] =>")
                viewName?.let {
                    builder.appendLine("Trace => [$it] => [${trace()}]")
                } ?: run {
                    builder.appendLine("Trace => [${trace()}]")
                }
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