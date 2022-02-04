package com.avatye.cashblock.feature.offerwall.presentation.view.detail

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.core.view.isVisible
import com.avatye.cashblock.base.component.contract.api.OfferwallApiContractor
import com.avatye.cashblock.base.component.contract.business.CoreContractor
import com.avatye.cashblock.base.component.domain.entity.ad.AAIDEntity
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
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

internal class OfferwallDetailViewFragment : AppBaseFragment<AcbsoFragmentOfferwallDetailViewBinding>(AcbsoFragmentOfferwallDetailViewBinding::inflate), View.OnClickListener {

    private val parentActivity: OfferwallDetailViewActivity by lazy {
        activity as OfferwallDetailViewActivity
    }

    private val api: OfferwallApiContractor by lazy {
        OfferwallApiContractor(blockType = OfferwallConfig.blockType)
    }

    var advertiseID: String? = null


    override fun onResume() {
        super.onResume()
        binding.bannerLinearView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.bannerLinearView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.bannerLinearView.onDestroy()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        advertiseID = arguments?.getString("advertiseID")
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding.confirmButton) {
            setOnClickListener {

            }
        }

        with(binding.validateButton) {
            setOnClickListener {

            }
        }


        // region { banner }
        binding.bannerLinearView.bannerData = AdvertiseController.createBannerData()
        binding.bannerLinearView.requestBanner()
        // endregion

    }


    private fun requestImpression() {
        CoreContractor.DeviceSetting.retrieveAAID {aaidEntity ->
            api.postImpression(
                deviceADID = aaidEntity.aaid,
                advertiseID = advertiseID ?: "",
                service = serviceType ?: ServiceType.OFFERWALL,
            ) {
                when (it) {
                    is ContractResult.Success -> {
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
            glider?.let {
                it.load(iconUrl)
                    .apply(RequestOptions().transform(RoundedCorners(24)))
                    .error(R.drawable.acbso_ic_coin_error)
                    .into(binding.iconImage)
            }

            // text
            binding.title.text = if(displayTitle.isEmpty()) {
                title
            } else {
                displayTitle
            }
            binding.description.text = userGuide
            binding.reward.text = reward.toLocale()


            // badge
            binding.iconBadge.isVisible = (journeyState == OfferwallJourneyStateType.PARTICIPATE.value)


            // button
            binding.confirmButton.text = if(productID == OfferWallProductType.CPI.value) {
                getString(R.string.acbso_offerwall_button_confirm_cpi)
            } else {
                getString(R.string.acbso_offerwall_button_confirm).format(reward)
            }
            binding.validateButton.text = getString(R.string.acbso_offerwall_button_validate) .format(reward)
        }


        binding.confirmButton.setOnClickListener(this)
        binding.validateButton.setOnClickListener(this)


    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.confirmButton.id -> {

            }
            binding.validateButton.id -> {

            }
        }
    }
}