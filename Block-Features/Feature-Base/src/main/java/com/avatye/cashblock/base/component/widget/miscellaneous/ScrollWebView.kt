package com.avatye.cashblock.base.component.widget.miscellaneous

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.net.http.SslError
import android.os.Build
import android.util.AttributeSet
import android.webkit.*
import androidx.appcompat.app.AlertDialog
import com.avatye.cashblock.base.MODULE_NAME
import com.avatye.cashblock.R
import com.avatye.cashblock.base.library.LogHandler

@SuppressLint("SetJavaScriptEnabled")
internal class ScrollWebView(context: Context, attrs: AttributeSet? = null) : WebView(context, attrs) {

    companion object {
        val NAME: String = ScrollWebView::class.java.simpleName
    }

    interface IScrollComputeCallback {
        fun onScrollCompute(webView: WebView?, offSetY: Int, scrollRange: Int, scrollExtent: Int)
    }

    var scrollCallback: IScrollComputeCallback? = null

    init {
        settings.domStorageEnabled = true
        settings.javaScriptEnabled = true
        settings.cacheMode = WebSettings.LOAD_NO_CACHE
        settings.textZoom = 100
        settings.setSupportZoom(false)
        settings.displayZoomControls = false
        settings.defaultTextEncodingName = "utf-8"
        settings.loadWithOverviewMode = true
        settings.allowFileAccess = true
        settings.allowContentAccess = true
        settings.allowFileAccessFromFileURLs = true
        settings.allowUniversalAccessFromFileURLs = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            settings.mediaPlaybackRequiresUserGesture = false
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O) {
            settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
        }
        webViewClient = object : WebViewClient() {
            override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
                val alertBuilder: AlertDialog.Builder = AlertDialog.Builder(context)
                alertBuilder.setMessage(R.string.acb_common_webview_ssl_warning)
                alertBuilder.setPositiveButton(R.string.acb_common_webview_ssl_continue) { _, _ -> handler?.proceed() }
                alertBuilder.setNegativeButton(R.string.acb_common_webview_ssl_stop) { _, _ -> handler?.cancel() }
                val alertDialog = alertBuilder.create()
                alertDialog.show()
            }

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                val requestUrl = url ?: ""
                val landingResult = requestUrlLanding(requestUrl = requestUrl) ?: false
                return if (landingResult) {
                    true
                } else {
                    view?.loadUrl(requestUrl)
                    false
                }
            }

            @TargetApi(21)
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val requestUrl = request?.url?.toString() ?: ""
                val landingResult = requestUrlLanding(requestUrl = requestUrl) ?: false
                return if (landingResult) {
                    true
                } else {
                    view?.loadUrl(requestUrl)
                    false
                }
            }

            private fun requestUrlLanding(requestUrl: String): Boolean? {
                if (requestUrl.isNotEmpty() && requestUrl.startsWith("intent://")) {
                    try {
                        val packageName = Intent.parseUri(requestUrl, Intent.URI_INTENT_SCHEME)?.`package` ?: ""
                        if (packageName.isNotEmpty()) {
                            val packageIntent = context.packageManager.getLaunchIntentForPackage(packageName)
                            if (packageIntent != null) {
                                context.startActivity(packageIntent)
                            } else {
                                val marketIntent = Intent(Intent.ACTION_VIEW)
                                marketIntent.data = Uri.parse("market://details?id=$packageName")
                                context.startActivity(marketIntent)
                            }
                            return true
                        }
                    } catch (e: Exception) {
                        LogHandler.e(throwable = e, moduleName = MODULE_NAME) {
                            "$NAME -> shouldOverrideUrlLoading -> check -> intent -> error"
                        }
                    }
                }

                // market intent
                if (requestUrl.isNotEmpty() && requestUrl.startsWith("market://")) {
                    try {
                        val marketIntent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
                        if (marketIntent != null) {
                            context.startActivity(marketIntent)
                        }
                        return true
                    } catch (e: Exception) {
                        LogHandler.e(throwable = e, moduleName = MODULE_NAME) {
                            "$NAME -> shouldOverrideUrlLoading -> check -> market -> error"
                        }
                    }
                }

                // custom
                if (requestUrl.isNotEmpty() && !URLUtil.isNetworkUrl(requestUrl)) {
                    try {
                        val customIntent = Intent(Intent.ACTION_VIEW, Uri.parse(requestUrl))
                        context.startActivity(customIntent)
                        return true
                    } catch (e: Exception) {
                        LogHandler.e(throwable = e, moduleName = MODULE_NAME) {
                            "$NAME -> shouldOverrideUrlLoading -> check -> custom -> error"
                        }
                    }
                }
                // null
                return null
            }
        }
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        scrollCallback?.onScrollCompute(this, computeVerticalScrollOffset(), computeVerticalScrollRange(), computeVerticalScrollExtent())
    }
}