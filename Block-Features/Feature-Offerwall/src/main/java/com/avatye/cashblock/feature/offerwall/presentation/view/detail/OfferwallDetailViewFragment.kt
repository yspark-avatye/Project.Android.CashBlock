package com.avatye.cashblock.feature.offerwall.presentation.view.detail

import android.os.Bundle
import android.view.View
import com.avatye.cashblock.feature.offerwall.databinding.AcbsoFragmentOfferwallDetailViewBinding
import com.avatye.cashblock.feature.offerwall.presentation.AppBaseFragment

internal class OfferwallDetailViewFragment : AppBaseFragment<AcbsoFragmentOfferwallDetailViewBinding>(AcbsoFragmentOfferwallDetailViewBinding::inflate) {

    private val parentActivity: OfferwallDetailViewActivity by lazy {
        activity as OfferwallDetailViewActivity
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

    }
}