package com.avatye.cashblock.feature.offerwall.presentation.view.inquiry

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.avatye.cashblock.base.component.domain.entity.base.ActivityTransitionType
import com.avatye.cashblock.base.component.domain.entity.base.ServiceType
import com.avatye.cashblock.base.component.domain.entity.offerwall.OfferwallContactRewardEntity
import com.avatye.cashblock.base.component.domain.model.parcel.ServiceNameParcel
import com.avatye.cashblock.base.component.domain.model.sealed.ViewModelResult
import com.avatye.cashblock.base.component.support.launch
import com.avatye.cashblock.base.component.widget.miscellaneous.PlaceHolderRecyclerView
import com.avatye.cashblock.feature.offerwall.R
import com.avatye.cashblock.feature.offerwall.databinding.AcbsoActivityInquiryListBinding
import com.avatye.cashblock.feature.offerwall.databinding.AcbsoItemInquiryListBinding
import com.avatye.cashblock.feature.offerwall.presentation.AppBaseActivity
import com.avatye.cashblock.feature.offerwall.presentation.parcel.InquiryParcel
import com.avatye.cashblock.feature.offerwall.presentation.viewmodel.inquiry.InquiryListViewModel

internal class InquiryListActivity : AppBaseActivity() {

    companion object {
        fun open(activity: Activity, serviceType: ServiceType, close: Boolean = false) {
            activity.launch(
                Intent(activity, InquiryListActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    putExtra(ServiceNameParcel.NAME, ServiceNameParcel(serviceType = serviceType))
                },
                transition = ActivityTransitionType.NONE.value,
                close = close
            )
        }
    }

    private val vb: AcbsoActivityInquiryListBinding by lazy {
        AcbsoActivityInquiryListBinding.inflate(LayoutInflater.from(this))
    }

    private val viewModel = InquiryListViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentViewWith(view = vb.root, logKey = "view:offerwall:inquiry-list")
        vb.placeHolderList.setLayoutManager(LinearLayoutManager(this))
        vb.placeHolderList.status = PlaceHolderRecyclerView.Status.LOADING
        vb.placeHolderList.actionRetry { viewModel.request() }
        viewModel.result.observe(this) {
            when (it) {
                is ViewModelResult.InProgress -> {
                    loadingView?.show(cancelable = false)
                    vb.placeHolderList.status = PlaceHolderRecyclerView.Status.LOADING
                }
                is ViewModelResult.Error -> {
                    loadingView?.dismiss()
                    vb.placeHolderList.status = PlaceHolderRecyclerView.Status.ERROR
                }
                is ViewModelResult.Complete -> {
                    loadingView?.dismiss()
                    vb.placeHolderList.setAdapter(InquiryListAdapter(items = it.result))
                }
            }
        }
        viewModel.request()
    }

    private fun createBadgeColor(resultMessageType: Int): Int {
        return when (resultMessageType) {
            0 -> R.drawable.acb_common_tag_ff9100
            1 -> R.drawable.acb_common_tag_0091ea
            8 -> R.drawable.acb_common_tag_01579b
            else -> R.drawable.acb_common_tag_ef5350
        }
    }

    private fun createBadgeText(resultMessageType: Int): String {
        return when (resultMessageType) {
            0 -> "확인중"
            1 -> "적립완료"
            8 -> "정보요청"
            else -> "적립불가"
        }
    }

    private fun createAnswerText(resultMessageType: Int): String {
        return when (resultMessageType) {
            0 -> "현재 광고 업체로 전달하여 확인 요청 중에 있습니다.\n사안에 따라 광고주 확인이 필요하며 확인까지 짧게는 1 - 3일, 길게는 1주 가량의 기간이 소요될 수 있는 점 양해 부탁드립니다.\n미적립 관련 확인되는 즉시, 재안내 드리겠습니다.\n감사합니다."
            1 -> "캐시 적립 완료되었습니다.\n적립내역을 확인 부탁드립니다.\n감사합니다."
            2 -> "광고 업체를 통해 확인 시 고객님은 해당 광고에 참여한 이력이 확인되지 않습니다.\n감사합니다."
            3 -> "광고 업체를 통해 확인 시 고객님은 이미 해당 광고에 참여한 것으로 확인됩니다.\n*타 리워드앱 등을 통해 기존에 참여한 이력이 있는 경우, 캐시가 지급되지 않습니다.\n감사합니다."
            4 -> "해당 광고는 참여가 불가한 광고로 미적립 되었습니다.\n감사합니다."
            5 -> "해당 광고는 이미 소진 및 종료된 광고로 포인트 적립이 불가능합니다.\n감사합니다."
            6 -> "광고 업체를 통해 확인 시 고객님은 최초설치/실행이 아닌 것으로 확인됩니다.\n*충전소를 통해 광고 참여하기 이전에 설치를 하였거나,\n타 리워드앱 등을 통해 설치/실행 이력이 있는 경우 캐시 적립이 불가능합니다.\n감사합니다"
            7 -> "광고 업체를 통해 확인 시 고객님은 광고 미션 미완료로 확인됩니다.\n해당 광고 선택 시 안내 드리고 있는 포인트 지급에 대한 상세내용 확인을 부탁드립니다.\n감사합니다."
            8 -> "정확한 확인을 위해 입력란의 참여 정보를 모두 작성하신 후 재문의 부탁드립니다.\n감사합니다."
            else -> ""
        }
    }


    private inner class InquiryListAdapter(private val items: MutableList<OfferwallContactRewardEntity>) : RecyclerView.Adapter<InquiryListAdapter.ItemViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
            val itemBinding = AcbsoItemInquiryListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ItemViewHolder(itemBinding)
        }

        override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
            holder.itemViewBind(entity = items[position])
        }

        override fun getItemCount(): Int = items.size


        inner class ItemViewHolder(private val ib: AcbsoItemInquiryListBinding) : RecyclerView.ViewHolder(ib.root) {
            fun itemViewBind(entity: OfferwallContactRewardEntity) {
                ib.inquiryTag.text = createBadgeText(entity.resultMsgType)
                ib.inquiryTag.setBackgroundResource(createBadgeColor(entity.resultMsgType))
                ib.inquiryTitle.text = entity.title
                ib.inquiryAnswer.text = entity.customMsg.ifBlank { createAnswerText(entity.resultMsgType) }
                with(ib.inquiryRetry) {
                    isVisible = entity.state == 8
                    // click
                    setOnClickListener {
                        if (entity.state == 8) {
                            InquiryRewardActivity.open(
                                activity = this@InquiryListActivity,
                                parcel = InquiryParcel(advertiseId = entity.advertiseID, contactId = entity.contactID, title = entity.title, state = 1),
                                serviceType = serviceType
                            )
                        }
                    }
                }
                ib.inquiryContent.setOnClickListener {
                    if (ib.inquiryAnswerContainer.isVisible) {
                        ib.inquiryAnswerContainer.isVisible = false
                        ib.inquiryFold.setImageResource(R.drawable.acb_common_ic_arrow_fold_down_12x6)
                    } else {
                        ib.inquiryAnswerContainer.isVisible = true
                        ib.inquiryFold.setImageResource(R.drawable.acb_common_ic_arrow_fold_up_12x6)
                    }
                }
            }
        }
    }
}