package com.avatye.cashblock.feature.offerwall.presentation.view.detail

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import com.avatye.cashblock.base.component.domain.entity.base.ActivityTransitionType
import com.avatye.cashblock.base.component.domain.entity.base.ServiceType
import com.avatye.cashblock.base.component.domain.model.parcel.ServiceNameParcel
import com.avatye.cashblock.base.component.support.extraFloat
import com.avatye.cashblock.base.component.support.extraParcel
import com.avatye.cashblock.base.component.support.launchFortResult
import com.avatye.cashblock.base.component.widget.dialog.DialogMessageView
import com.avatye.cashblock.feature.offerwall.R
import com.avatye.cashblock.feature.offerwall.databinding.AcbsoActivityOfferwallDetailViewBinding
import com.avatye.cashblock.feature.offerwall.presentation.AppBaseActivity
import com.avatye.cashblock.feature.offerwall.presentation.parcel.OfferWallParcel

internal class OfferwallDetailViewActivity : AppBaseActivity() {

    companion object {
        /** this activity code */
        const val REQUEST_CODE = 11001

        fun open(activity: Activity, serviceType: ServiceType, parcel: OfferWallParcel, options: Bundle?) {
            activity.launchFortResult(
                intent = Intent(activity, OfferwallDetailViewActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    putExtra(OfferWallParcel.NAME, parcel)
                    putExtra(ServiceNameParcel.NAME, ServiceNameParcel(serviceType = serviceType))
                },
                transition = ActivityTransitionType.NONE.value,
                requestCode = REQUEST_CODE,
                options = options,
            )
        }
    }

    private val vb: AcbsoActivityOfferwallDetailViewBinding by lazy {
        AcbsoActivityOfferwallDetailViewBinding.inflate(LayoutInflater.from(this))
    }


    override fun onBackPressed() {
        supportFinishAfterTransition()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentViewWith(vb.root)

        val advertiseID = extraParcel<OfferWallParcel>(OfferWallParcel.NAME)?.advertiseID






        with(vb.headerView) {

            // region # 뒤로가기
            actionBack { finish() }
            // endregion

            // region # 문의하기
            this.updateOptionFirst(true, R.drawable.acbso_ic_inquiry_reward) {

            }
            // endregion

            // region # 광고숨김
            this.updateOptionFirst(true, R.drawable.acbso_ic_hide) {
                DialogMessageView.create(ownerActivity = this@OfferwallDetailViewActivity).apply {
                    setMessage(R.string.acbso_offerwall_do_you_want_to_hide_from_the_participating_list)
                    setPositiveButton {
                        //TODO 광고숨김 기능
                    }
                    setNegativeButton()
                }
            }
            // endregion

            // region # 광고제거
            this.updateOptionSecond(true, R.drawable.acbso_ic_remove_bar) {
                DialogMessageView.create(ownerActivity = this@OfferwallDetailViewActivity).apply {
                    setMessage(R.string.acbso_offerwall_do_you_want_to_remove_from_the_participating_list)
                    setPositiveButton {
                        //TODO 광고제거 기능
                    }
                    setNegativeButton()
                }
            }
            // endregion
        }

        transactionFragment(fragment = OfferwallDetailViewFragment().apply {
            val bundle = Bundle()
            bundle.putString("advertiseID", advertiseID)
            arguments = bundle
        })


    }

    private fun transactionFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.ad_detail_view_container, fragment)
            commit()
        }
    }
}