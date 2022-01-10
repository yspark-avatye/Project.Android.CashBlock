package com.avatye.cashblock.base.library.miscellaneous

import android.content.Context

open class SingletonHolder<out T : Any, in A>(creator: (A) -> T) {

    private var creator: ((A) -> T)? = creator

    @Volatile
    private var instance: T? = null

    fun create(arg: A): T {
        val i = instance
        if (i != null) {
            return i
        }

        return synchronized(this) {
            val i2 = instance
            if (i2 != null) {
                i2
            } else {
                val created = creator!!(arg)
                instance = created
                creator = null
                created
            }
        }
    }
}


open class SingletonContextHolder<out T : Any>(creator: (context: Context) -> T) {

    private var creator: ((Context) -> T)? = creator

    @Volatile
    private var instance: T? = null

    fun create(context: Context): T {
        val i = instance
        if (i != null) {
            return i
        }

        return synchronized(this) {
            val i2 = instance
            if (i2 != null) {
                i2
            } else {
                val created = creator!!(context.applicationContext)
                instance = created
                creator = null
                created
            }
        }
    }
}