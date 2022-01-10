package com.avatye.cashblock.base.library.ad.curator.queue.loader

abstract class ADLoaderBase {

    abstract val loaderType: ADLoaderType

    abstract fun requestAD(): Unit

    abstract fun show(action: (success: Boolean) -> Unit = {}): Unit

    abstract fun onResume(): Unit

    abstract fun onPause(): Unit

    abstract fun release(): Unit

}