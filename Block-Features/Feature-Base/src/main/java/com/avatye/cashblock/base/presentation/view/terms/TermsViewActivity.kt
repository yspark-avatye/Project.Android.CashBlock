package com.avatye.cashblock.base.presentation.view.terms

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.webkit.WebView
import com.avatye.cashblock.base.component.domain.entity.base.ActivityTransitionType
import com.avatye.cashblock.base.component.support.extraParcel
import com.avatye.cashblock.base.component.support.launch
import com.avatye.cashblock.base.component.widget.miscellaneous.ScrollWebView
import com.avatye.cashblock.base.presentation.AppBaseActivity
import com.avatye.cashblock.base.presentation.parcel.TermsParcel
import com.avatye.cashblock.databinding.AcbCommonActivityTermsViewBinding

internal class TermsViewActivity : AppBaseActivity() {

    companion object {
        fun open(activity: Activity, parcel: TermsParcel, close: Boolean = false) {
            activity.launch(
                intent = Intent(activity, TermsViewActivity::class.java).apply {
                    putExtra(TermsParcel.NAME, parcel)
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                },
                transition = ActivityTransitionType.NONE.value,
                close = close
            )
        }
    }

    private lateinit var parcel: TermsParcel

    private val vb: AcbCommonActivityTermsViewBinding by lazy {
        AcbCommonActivityTermsViewBinding.inflate(LayoutInflater.from(this))
    }

    override fun onResume() {
        super.onResume()
        vb.bannerLinearView.onResume()
    }

    override fun onPause() {
        super.onPause()
        vb.bannerLinearView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        vb.bannerLinearView.onResume()
    }

    override fun onBackPressed() {
        onWebViewBackPressed()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentViewWith(view = vb.root, logKey = "view:core:terms-view")
        extraParcel<TermsParcel>(TermsParcel.NAME)?.let {
            parcel = it
            vb.headerView.viewType = it.headerType
            vb.headerView.actionClose { finish() }
            vb.headerView.actionBack { finish() }
            // region # web-view
            vb.termsContent.scrollCallback = callbackScroll
            vb.termsContent.loadUrl(it.url)
            // endregion
        } ?: run {
            finish()
        }
    }

    private val callbackScroll = object : ScrollWebView.IScrollComputeCallback {
        override fun onScrollCompute(webView: WebView?, offSetY: Int, scrollRange: Int, scrollExtent: Int) {
            webView?.let {
                val progressMaxValue = scrollRange - scrollExtent
                vb.termsProgress.max = progressMaxValue
                vb.termsProgress.progress = offSetY
            }
        }
    }

    private fun onWebViewBackPressed() {
        if (vb.termsContent.canGoBack()) {
            vb.termsContent.goBack()
        } else {
            super.onBackPressed()
        }
    }

}