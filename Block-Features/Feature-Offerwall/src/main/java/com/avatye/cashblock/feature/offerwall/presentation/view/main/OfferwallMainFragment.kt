package com.avatye.cashblock.feature.offerwall.presentation.view.main

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.avatye.cashblock.base.component.contract.api.OfferwallApiContractor
import com.avatye.cashblock.base.component.domain.entity.base.ServiceType
import com.avatye.cashblock.base.component.domain.entity.offerwall.OfferWallTabEntity
import com.avatye.cashblock.base.component.domain.entity.offerwall.OfferwallItemEntity
import com.avatye.cashblock.base.component.domain.model.contract.ContractResult
import com.avatye.cashblock.base.component.widget.miscellaneous.PlaceHolderRecyclerView
import com.avatye.cashblock.feature.offerwall.OfferwallConfig
import com.avatye.cashblock.feature.offerwall.component.controller.AdvertiseController
import com.avatye.cashblock.feature.offerwall.databinding.AcbsoFragmentOfferwallMainBinding
import com.avatye.cashblock.feature.offerwall.presentation.AppBaseFragment

internal class OfferwallMainFragment : AppBaseFragment<AcbsoFragmentOfferwallMainBinding>(AcbsoFragmentOfferwallMainBinding::inflate) {

    private val parentActivity: OfferwallMainActivity by lazy {
        activity as OfferwallMainActivity
    }

    private val api: OfferwallApiContractor by lazy {
        OfferwallApiContractor(blockType = OfferwallConfig.blockType)
    }

    lateinit var offerwallListPagerAdapter: OfferwallListPagerAdapter

    val tabList = listOf<OfferWallTabEntity>()
    private var currentPagePosition: Int = 0


    override fun onResume() {
        super.onResume()
        binding.bannerLinearView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.bannerLinearView.onPause()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        offerwallListPagerAdapter = OfferwallListPagerAdapter()

        initViewPager()

        // region # Tab
        //TODO Tab API 다되면~
        //endregion

        // region { banner }
        binding.bannerLinearView.bannerData = AdvertiseController.createBannerData()
        binding.bannerLinearView.requestBanner()
        // endregion
    }


    override fun onHiddenChanged(hidden: Boolean) {
        // 복구하기 시 특정 프래그먼트의 hidden 상태 여부
        super.onHiddenChanged(hidden)
        if (!hidden && isRefreshOfferwallList) {
            offerwallListPagerAdapter.listRefresh()
            isRefreshOfferwallList = false
        }
    }


    internal fun requestOfferWalls(isRetry: Boolean = false, callback: () -> Unit = {}) {

        parentActivity.loadingDialog?.show(cancelable = false)

        api.retrieveList(deviceADID = "", tabID = "", serviceID = serviceType ?: ServiceType.OFFERWALL) {
            when (it) {
                is ContractResult.Success -> {
                    if (isAvailable) {
                        callback()
                        offerwallListPagerAdapter.setData(isRetry, it.contract)
                        parentActivity.loadingDialog?.dismiss()
                    }
                }
                is ContractResult.Failure -> {
                    if (isAvailable) {
                        offerwallListPagerAdapter.setStatusView(PlaceHolderRecyclerView.Status.ERROR)
                        parentActivity.loadingDialog?.dismiss()
                    }
                }
            }
        }
    }


    private fun initViewPager() {
        binding.vpViewpager2.isUserInputEnabled = false
        binding.vpViewpager2.adapter = offerwallListPagerAdapter
        binding.vpViewpager2.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.vpViewpager2.offscreenPageLimit = tabList.size - 1
        binding.vpViewpager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                // TODO Tab background 설정
//                setTabBackground(position)

                currentPagePosition = position
            }
        })
    }

    private fun initOfferwallList() {
        // TODO isAvailabe
        if (tabList.isNotEmpty()) {
            tabList.forEachIndexed { index, tab ->
                context?.let {
                    addTapButton()
                }
            }
            initViewPager()
            requestOfferWalls()
        }
    }

    private fun addTapButton() {
        // TODO Tab-button Add
    }


    inner class OfferwallListPagerAdapter : FragmentStateAdapter(this) {
        private var fragmentList = hashMapOf<Int, OfferWallListFragment>()
        private val currentFragment
            get() = fragmentList[currentPagePosition]

        override fun getItemCount(): Int = tabList.size

        override fun createFragment(position: Int): Fragment {
            val fragment = OfferWallListFragment()
            fragmentList[position] = fragment
            return fragment
        }


        fun setData(isRetry: Boolean = false, data: OfferwallItemEntity) {

        }

        fun setStatusView(status: PlaceHolderRecyclerView.Status) {
            currentFragment?.binding?.placeHolderRecyclerView?.status = status
        }


        fun listRefresh(position: Int = 0, isPositionScroll: Boolean = false) {

        }


    }
}