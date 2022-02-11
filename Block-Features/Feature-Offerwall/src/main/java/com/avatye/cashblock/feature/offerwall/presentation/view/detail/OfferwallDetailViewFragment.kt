package com.avatye.cashblock.feature.offerwall.presentation.view.detail

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.core.view.isVisible
import com.avatye.cashblock.base.component.contract.api.OfferwallApiContractor
import com.avatye.cashblock.base.component.contract.business.CoreContractor
import com.avatye.cashblock.base.component.domain.entity.base.ServiceType
import com.avatye.cashblock.base.component.domain.entity.offerwall.OfferWallProductType
import com.avatye.cashblock.base.component.domain.entity.offerwall.OfferwallImpressionItemEntity
import com.avatye.cashblock.base.component.domain.entity.offerwall.OfferwallJourneyStateType
import com.avatye.cashblock.base.component.domain.model.contract.ContractResult
import com.avatye.cashblock.base.component.support.MessageDialogHelper
import com.avatye.cashblock.base.component.support.toLocale
import com.avatye.cashblock.feature.offerwall.OfferwallConfig
import com.avatye.cashblock.feature.offerwall.R
import com.avatye.cashblock.feature.offerwall.component.controller.AdvertiseController
import com.avatye.cashblock.feature.offerwall.databinding.AcbsoFragmentOfferwallDetailViewBinding
import com.avatye.cashblock.feature.offerwall.presentation.AppBaseFragment
import com.avatye.cashblock.feature.offerwall.presentation.parcel.OfferWallActionParcel
import com.avatye.cashblock.feature.offerwall.presentation.parcel.OfferWallViewParcel
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

internal class OfferwallDetailViewFragment : AppBaseFragment<AcbsoFragmentOfferwallDetailViewBinding>(AcbsoFragmentOfferwallDetailViewBinding::inflate) {

    private val parentActivity: OfferwallDetailViewActivity by lazy {
        activity as OfferwallDetailViewActivity
    }

    private val api: OfferwallApiContractor by lazy {
        OfferwallApiContractor(blockType = OfferwallConfig.blockType)
    }

    var isRequestConfiirm: Boolean = false

    var parcel: OfferWallViewParcel? = null


    override fun onResume() {
        super.onResume()
        binding.bannerLinearView.onResume()
        if (isRequestConfiirm) {
            isRequestConfiirm = false
            requestImpression()
        }
    }

    override fun onPause() {
        super.onPause()
        binding.bannerLinearView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
//        binding.bannerLinearView.onDestroy()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parcel = arguments?.getParcelable(OfferWallViewParcel.NAME)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        with(binding.confirmButton) {

        }

        with(binding.validateButton) {

        }

        // region # banner
        binding.bannerLinearView.bannerData = AdvertiseController.createBannerData()
        binding.bannerLinearView.requestBanner()
        // endregion

        requestImpression()

    }


    private fun requestImpression() {
        loadingView?.show(cancelable = false)
        CoreContractor.DeviceSetting.retrieveAAID { aaidEntity ->
            api.postImpression(
                deviceADID = aaidEntity.aaid,
                advertiseID = parcel?.advertiseID ?: "",
                service = serviceType ?: ServiceType.OFFERWALL,
            ) {
                when (it) {
                    is ContractResult.Success -> {
                        loadingView?.dismiss()
                        AdvertiseController.impressionItemEntity = it.contract
                        // delay
                        Handler(Looper.getMainLooper()).postDelayed({
                            loadingView?.dismiss()
                        }, 1000)

                        bindView(it.contract)
                    }
                    is ContractResult.Failure -> {
                        loadingView?.dismiss()
                        MessageDialogHelper.confirm(
                            activity = parentActivity,
                            message = getString(R.string.acb_common_message_error),
                            onConfirm = { parentActivity.finish() }
                        ).show(false)
                    }
                }
            }
        }
    }


    private fun bindView(impressionItem: OfferwallImpressionItemEntity) {
        with(impressionItem) {
            // region # icon
            glider?.let {
                it.load(iconUrl)
                    .apply(RequestOptions().transform(RoundedCorners(24)))
                    .error(R.drawable.acbso_ic_coin_error)
                    .into(binding.iconImage)
            }
            // endregion

            // region # text
            binding.title.text = if (displayTitle.isEmpty()) title else displayTitle
            binding.actionType.text = actionName
            binding.description.text = actionGuide
            binding.reward.text = reward?.ImpressionReward?.toLocale()
            binding.detailDescription.text = userGuide
            binding.confirmButton.text = if (productID == OfferWallProductType.CPI) {
                getString(R.string.acbso_offerwall_button_confirm_cpi)
            } else {
                getString(R.string.acbso_offerwall_button_confirm).format(reward?.ImpressionReward)
            }
            binding.validateButton.text = getString(R.string.acbso_offerwall_button_validate).format(reward?.ImpressionReward)
            // endregion

            // region # visible
            binding.iconBadge.isVisible = (journeyState == OfferwallJourneyStateType.PARTICIPATE)
            binding.validateButton.isVisible = (productID == OfferWallProductType.CPI)
            // endregion

            // region # onClick
            binding.confirmButton.setOnClickListener { requestConfirm(impressionItem = this) }
            binding.validateButton.setOnClickListener {

            }
            // endregion
        }


    }


    private fun requestConfirm(impressionItem: OfferwallImpressionItemEntity) {
        // TODO 연령심사

        loadingView?.show(false)
        CoreContractor.DeviceSetting.retrieveAAID { aaidEntity ->
            api.postClick(
                deviceADID = aaidEntity.aaid,
                advertiseID = parcel?.advertiseID ?: "",
                service = serviceType ?: ServiceType.OFFERWALL,
            ) {
                when (it) {
                    is ContractResult.Success -> {
                        runCatching {
                            val actionParcel = OfferWallActionParcel(
                                currentPosition = parcel?.currentPos ?: 0,
                                journeyType = OfferwallJourneyStateType.PARTICIPATE,
                                forceRefresh = false
                            )
                            parentActivity.setResult(Activity.RESULT_OK, Intent().putExtra(OfferWallActionParcel.NAME, actionParcel))
                            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it.contract.landingUrl)))
                        }.onSuccess {
                            isRequestConfiirm = true
                        }.onFailure {
                            isRequestConfiirm = false
                            MessageDialogHelper.confirm(
                                activity = parentActivity,
                                message = R.string.acbso_offerwall_failed_to_participate_please_try_again,
                                onConfirm = { parentActivity.finish() }
                            )
                        }.also {
                            loadingView?.dismiss()
                        }
                    }

                    is ContractResult.Failure -> {
                        loadingView?.dismiss()
                        val actionParcel = OfferWallActionParcel(
                            currentPosition = 0,
                            journeyType = OfferwallJourneyStateType.NONE,
                            forceRefresh = true
                        )
                        parentActivity.setResult(Activity.RESULT_OK, Intent().putExtra(OfferWallActionParcel.NAME, actionParcel))
                        MessageDialogHelper.confirm(
                            activity = parentActivity,
                            message = getString(R.string.acb_common_message_error),
                            onConfirm = { parentActivity.finish() }
                        ).show(false)
                    }
                }
            }
        }

    }
}