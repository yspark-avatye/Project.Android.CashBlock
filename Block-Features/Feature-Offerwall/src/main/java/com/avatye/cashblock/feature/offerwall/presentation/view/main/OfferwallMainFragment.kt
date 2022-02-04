package com.avatye.cashblock.feature.offerwall.presentation.view.main

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.ContextThemeWrapper
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.avatye.cashblock.base.component.contract.api.OfferwallApiContractor
import com.avatye.cashblock.base.component.contract.business.CoreContractor
import com.avatye.cashblock.base.component.domain.entity.base.ServiceType
import com.avatye.cashblock.base.component.domain.entity.offerwall.OfferWallTabEntity
import com.avatye.cashblock.base.component.domain.entity.offerwall.OfferwallItemEntity
import com.avatye.cashblock.base.component.domain.entity.offerwall.OfferwallSectionEntity
import com.avatye.cashblock.base.component.domain.model.contract.ContractResult
import com.avatye.cashblock.base.component.support.MessageDialogHelper
import com.avatye.cashblock.base.component.support.toPX
import com.avatye.cashblock.base.component.widget.miscellaneous.PlaceHolderRecyclerView
import com.avatye.cashblock.base.library.LogHandler
import com.avatye.cashblock.feature.offerwall.MODULE_NAME
import com.avatye.cashblock.feature.offerwall.OfferwallConfig
import com.avatye.cashblock.feature.offerwall.R
import com.avatye.cashblock.feature.offerwall.component.controller.AdvertiseController
import com.avatye.cashblock.feature.offerwall.component.controller.AdvertiseListController
import com.avatye.cashblock.feature.offerwall.component.data.PreferenceData
import com.avatye.cashblock.feature.offerwall.databinding.AcbsoFragmentOfferwallMainBinding
import com.avatye.cashblock.feature.offerwall.presentation.AppBaseFragment
import org.joda.time.DateTime

internal class OfferwallMainFragment : AppBaseFragment<AcbsoFragmentOfferwallMainBinding>(AcbsoFragmentOfferwallMainBinding::inflate) {

    companion object {
        const val tagName: String = "OfferwallMainFragment"
    }

    private val parentActivity: OfferwallMainActivity by lazy {
        activity as OfferwallMainActivity
    }

    private val api: OfferwallApiContractor by lazy {
        OfferwallApiContractor(blockType = OfferwallConfig.blockType)
    }

    lateinit var offerwallListPagerAdapter: OfferwallListPagerAdapter

    var sectionList = mutableListOf<OfferwallSectionEntity>()
    var tabList = mutableListOf<OfferWallTabEntity>()


    private var sectionButtonList = mutableListOf<Button>()

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

        // region # Tab
        requestOfferwallTabs {
            initOfferwallList()
        }
        //endregion

        // region # banner
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


    fun requestOfferwallTabs(callback: () -> Unit) {
        api.retrieveTabs {
            when (it) {
                is ContractResult.Success -> {
                    val conditionTime = if (DateTime().toString("HH").toLong() >= 12) {
                        DateTime().plusDays(1).toString("yyyyMMdd00")
                    } else {
                        DateTime().toString("yyyyMMdd12")
                    }.toLong()
                    PreferenceData.Tab.conditionTime = conditionTime

                    tabList = it.contract
                    callback()
                }

                is ContractResult.Failure -> {
                    tabList = mutableListOf()
                    callback()
                    LogHandler.e(moduleName = MODULE_NAME) {
                        "$tagName -> requestOfferwallTabs() { statusCode:${it.statusCode}, code: ${it.errorCode}, message: ${it.message} }"
                    }
                }
            }
        }

    }


    fun requestOfferWallList(isRetry: Boolean = false, callback: () -> Unit = {}) {
        loadingView?.show(cancelable = false)

        CoreContractor.DeviceSetting.retrieveAAID { AAIDEntity ->
            api.retrieveList(deviceADID = AAIDEntity.aaid, serviceID = serviceType ?: ServiceType.OFFERWALL) {
                when (it) {
                    is ContractResult.Success -> {
                        if (isAvailable) {
                            callback()
                            sectionList = it.contract

                            it.contract.forEach { sectionEntity ->
                                sectionEntity.items.forEach { items ->
                                    offerwallListPagerAdapter.setData(
                                        isRetry = isRetry,
                                        data = items,
                                    )
                                }
                            }


                            loadingView?.dismiss()
                        }
                    }
                    is ContractResult.Failure -> {
                        if (isAvailable) {
                            offerwallListPagerAdapter.setStatusView(PlaceHolderRecyclerView.Status.ERROR)
                            loadingView?.dismiss()
                        }
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
                setTabBackground(position)
                currentPagePosition = position
            }
        })
    }

    private fun initOfferwallList() {
        if (isAvailable) {
            if (tabList.isNotEmpty()) {
                tabList.forEachIndexed { index, tab ->
                    addTapButton(parentActivity, index, tab.tabName)
                }
                setTabBackground(0)
                initViewPager()
                requestOfferWallList()
            } else {
                MessageDialogHelper.confirm(
                    activity = parentActivity,
                    message = getString(R.string.acb_common_message_error),
                    onConfirm = { parentActivity.finish() }
                ).show(false)
            }
        }

    }

    private fun addTapButton(context: Context, index: Int, title: String) {
        val tabContext = ContextThemeWrapper(context, R.style.CashBlock_Widget_Button)
        val tab = Button(tabContext)

        tab.layoutParams = LinearLayout.LayoutParams(0, 36.toPX.toInt(), 1F)
        tab.setPadding(5.toPX.toInt(), 0, 5.toPX.toInt(), 0)
        tab.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14F)
        tab.text = title
        tab.setTextColor(ContextCompat.getColorStateList(context, R.color.acbso_color_button_0091ea_99a6b6))
        sectionButtonList.add(tab)

        binding.tabButton.addView(tab)

        tab.setOnClickListener {
            binding.vpViewpager2.setCurrentItem(index, true)
        }
    }


    private fun setTabBackground(currentIndex: Int) {
        context?.let {
            when (currentIndex) {
                0 -> {
                    // 첫 번째 탭일 때, 자신 탭과 우측 탭 설정
                    ViewCompat.setBackground(sectionButtonList[currentIndex], ContextCompat.getDrawable(it, R.drawable.acbso_shp_left_select_tab_background))
                    ViewCompat.setBackground(
                        sectionButtonList[currentIndex + 1],
                        ContextCompat.getDrawable(it, R.drawable.acbso_shp_center_unselect_tab_background)
                    )
                }
                sectionButtonList.size - 1 -> {
                    // 마지막 탭일 때, 자신 탭과 좌측 탭 설정
                    ViewCompat.setBackground(
                        sectionButtonList[currentIndex - 1],
                        ContextCompat.getDrawable(it, R.drawable.acbso_shp_center_unselect_tab_background)
                    )
                    ViewCompat.setBackground(sectionButtonList[currentIndex], ContextCompat.getDrawable(it, R.drawable.acbso_shp_right_select_tab_background))
                }
                else -> {
                    // 자신 탭과 좌우 탭 설정
                    ViewCompat.setBackground(
                        sectionButtonList[currentIndex - 1],
                        ContextCompat.getDrawable(it, R.drawable.acbso_shp_center_unselect_tab_background)
                    )
                    ViewCompat.setBackground(sectionButtonList[currentIndex], ContextCompat.getDrawable(it, R.drawable.acbso_shp_center_select_tab_background))
                    ViewCompat.setBackground(
                        sectionButtonList[currentIndex + 1],
                        ContextCompat.getDrawable(it, R.drawable.acbso_shp_center_unselect_tab_background)
                    )
                }
            }

            // 나머지 좌측 탭들 설정
            for (i in 0 until currentIndex - 1) {
                ViewCompat.setBackground(sectionButtonList[i], ContextCompat.getDrawable(it, R.drawable.acbso_shp_left_unselect_tab_background))
            }

            // 나머지 우측 탭들 설정
            for (i in currentIndex + 2 until sectionButtonList.size) {
                ViewCompat.setBackground(sectionButtonList[i], ContextCompat.getDrawable(it, R.drawable.acbso_shp_right_unselect_tab_background))
            }
        }

        for (i in 0 until sectionButtonList.size) {
            sectionButtonList[i].isEnabled = currentIndex != i
        }

        binding.tabButton.requestLayout()
    }


    inner class OfferwallListPagerAdapter : FragmentStateAdapter(this) {
        private var itemEntity: OfferwallItemEntity = OfferwallItemEntity()
        private var offerwalls: MutableList<OfferwallItemEntity> = mutableListOf()

        private var fragmentList = hashMapOf<Int, OfferWallListFragment>()
        private val currentFragment
            get() = fragmentList[currentPagePosition]

        override fun getItemCount(): Int = tabList.size

        override fun createFragment(position: Int): Fragment {
            val fragment = OfferWallListFragment.open(position = position)
            fragmentList[position] = fragment
            return fragment
        }


        fun setData(isRetry: Boolean = false, data: OfferwallItemEntity) {
            itemEntity = data

            offerwalls = AdvertiseListController.makeOfferwallList(
                sectionList = sectionList,
                tabEntity = tabList[currentPagePosition]
            )

            currentFragment?.let {
                it.offerWallAdapter.offerwalls = offerwalls
                it.offerWallAdapter.notifyDataSetChanged()
            }
        }


        fun listRefresh(position: Int = 0, isPositionScroll: Boolean = false) {
            currentFragment?.let {
                offerwalls = AdvertiseListController.makeOfferwallList(
                    sectionList = sectionList,
                    tabEntity = tabList[currentPagePosition]
                )
                it.offerWallAdapter.offerwalls = offerwalls
                // refresh

            }
        }


        fun setStatusView(status: PlaceHolderRecyclerView.Status) {
            currentFragment?.binding?.placeHolderRecyclerView?.status = status
        }
    }
}