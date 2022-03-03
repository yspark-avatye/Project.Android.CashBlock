package com.avatye.cashblock.feature.offerwall.presentation.view.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.avatye.cashblock.base.component.contract.business.CoreContractor
import com.avatye.cashblock.base.component.contract.business.ViewOpenContractor
import com.avatye.cashblock.base.component.domain.entity.base.ActivityTransitionType
import com.avatye.cashblock.base.component.domain.entity.base.ServiceType
import com.avatye.cashblock.base.component.domain.entity.offerwall.OfferwallItemEntity
import com.avatye.cashblock.base.component.domain.entity.offerwall.OfferwallJourneyStateType
import com.avatye.cashblock.base.component.domain.entity.offerwall.OfferwallSectionEntity
import com.avatye.cashblock.base.component.domain.entity.offerwall.OfferwallTabEntity
import com.avatye.cashblock.base.component.domain.model.parcel.ServiceNameParcel
import com.avatye.cashblock.base.component.domain.model.sealed.ViewModelResult
import com.avatye.cashblock.base.component.support.MessageDialogHelper
import com.avatye.cashblock.base.component.support.launch
import com.avatye.cashblock.base.component.support.toPX
import com.avatye.cashblock.base.component.widget.banner.BannerLinearView
import com.avatye.cashblock.base.component.widget.miscellaneous.PlaceHolderRecyclerView
import com.avatye.cashblock.base.library.LogHandler
import com.avatye.cashblock.feature.offerwall.MODULE_NAME
import com.avatye.cashblock.feature.offerwall.OfferwallConfig.logger
import com.avatye.cashblock.feature.offerwall.R
import com.avatye.cashblock.feature.offerwall.component.controller.ADController
import com.avatye.cashblock.feature.offerwall.component.controller.AdvertiseListController
import com.avatye.cashblock.feature.offerwall.component.data.PreferenceData
import com.avatye.cashblock.feature.offerwall.databinding.AcbsoActivityOfferwallMainBinding
import com.avatye.cashblock.feature.offerwall.presentation.AppBaseActivity
import com.avatye.cashblock.feature.offerwall.presentation.parcel.OfferWallActionParcel
import com.avatye.cashblock.feature.offerwall.presentation.view.detail.OfferwallDetailViewActivity
import com.avatye.cashblock.feature.offerwall.presentation.view.setting.SettingActivity
import com.avatye.cashblock.feature.offerwall.presentation.viewmodel.list.OfferwallListModel
import org.joda.time.DateTime

internal class OfferwallMainActivity : AppBaseActivity() {

    lateinit var offerwallListPagerAdapter: OfferwallListPagerAdapter

    var sectionList = mutableListOf<OfferwallSectionEntity>()
    var tabList = mutableListOf<OfferwallTabEntity>()

    private var sectionButtonList = mutableListOf<Button>()
    private var currentPagePosition: Int = 0

    companion object {
        const val tagName: String = "OfferwallMainActivity"

        fun open(
            activity: Activity,
            serviceType: ServiceType,
            close: Boolean = false
        ) {
            activity.launch(
                intent = Intent(activity, OfferwallMainActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    putExtra(ServiceNameParcel.NAME, ServiceNameParcel(serviceType = serviceType))
                },
                transition = ActivityTransitionType.NONE.value,
                close = close
            )
        }
    }

    private val vb: AcbsoActivityOfferwallMainBinding by lazy {
        AcbsoActivityOfferwallMainBinding.inflate(LayoutInflater.from(this))
    }

    private val viewModel by lazy {
        OfferwallListModel.create(this)
    }

    override fun onResume() {
        super.onResume()
        // banners
        vb.bannerLinearView.onResume()
        if (AdvertiseListController.needRefreshList) {
            offerwallListPagerAdapter.listRefresh()
            AdvertiseListController.needRefreshList = false
        }
    }

    override fun onPause() {
        super.onPause()
        // banners
        vb.bannerLinearView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        // banner
        vb.bannerLinearView.onDestroy()
    }


    override fun receiveActionInspection() {
        leakHandler.post {
            ViewOpenContractor.openInspectionView(
                activity = this@OfferwallMainActivity,
                blockType = getBlockType(),
                close = true
            )
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_CANCELED) {
            return
        }

        if (requestCode == OfferwallDetailViewActivity.REQUEST_CODE) {
            data?.extras?.getParcelable<OfferWallActionParcel>(OfferWallActionParcel.NAME)?.let {
                if (it.journeyType == OfferwallJourneyStateType.NONE) {
                    if (it.forceRefresh) {
                        requestList()
                    }
                } else {
                    offerwallListPagerAdapter.changeAllList(it.currentPosition, it.journeyType)
                }
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentViewWith(vb.root)

        offerwallListPagerAdapter = OfferwallListPagerAdapter()

        // region # headerView
        with(vb.headerView) {
            actionMore { SettingActivity.open(activity = this@OfferwallMainActivity, serviceType = serviceType, close = false) }
            actionClose { finish() }
        }
        // endregion

        // region # banner
        vb.bannerLinearView.bannerData = ADController.createBannerData()
        vb.bannerLinearView.sourceType = BannerLinearView.SourceType.OFFERWALL
        vb.bannerLinearView.requestBanner()
        // endregion

        // region # deviceAAID
        CoreContractor.DeviceSetting.fetchAAID {
            logger.i(viewName = viewTag) {
                "CoreContractor.DeviceSetting.fetchAAID { complete: $it }"
            }
        }
        // endregion

        // region # observe
        observeViewModel {
            init()
        }
        // endregion
    }


    private fun observeViewModel(callback: () -> Unit) {
        // region # tab
        viewModel.tabResult.observe(this) {
            when (it) {
                is ViewModelResult.InProgress -> loadingView?.show(cancelable = false)
                is ViewModelResult.Error -> {
                    loadingView?.dismiss()
                    tabList = mutableListOf()
                    LogHandler.e(moduleName = MODULE_NAME) {
                        "$tagName -> requestTabs() { statusCode:${it.statusCode}, code: ${it.errorCode}, message: ${it.message} }"
                    }
                    callback()
                }
                is ViewModelResult.Complete -> {
                    loadingView?.dismiss()
                    val conditionTime = if (DateTime().toString("HH").toLong() >= 12) {
                        DateTime().plusDays(1).toString("yyyyMMdd00")
                    } else {
                        DateTime().toString("yyyyMMdd12")
                    }.toLong()
                    PreferenceData.Tab.update(conditionTime = conditionTime)
                    tabList = it.result
                    callback()
                }
            }
        }
        requestTabs()
        // endregion

        // region # item-list
        viewModel.listResult.observe(this) {
            when (it) {
                is ViewModelResult.InProgress -> loadingView?.show(cancelable = false)
                is ViewModelResult.Error -> {
                    loadingView?.dismiss()
                    offerwallListPagerAdapter.setStatusView(PlaceHolderRecyclerView.Status.ERROR)
                    LogHandler.e(moduleName = MODULE_NAME) {
                        "$tagName -> requestList() { statusCode:${it.statusCode}, code: ${it.errorCode}, message: ${it.message} }"
                    }
                }
                is ViewModelResult.Complete -> {
                    loadingView?.dismiss()
                    sectionList = it.result
                    it.result.forEach { sectionEntity ->
                        sectionEntity.items.forEach { item ->
                            offerwallListPagerAdapter.setData()
                        }
                    }
                }
            }
        }
        // endregion
    }


    private fun requestTabs() = viewModel.requestTab()

    fun requestList() {
        CoreContractor.DeviceSetting.retrieveAAID { aaidEntity ->
            viewModel.requestList(deviceADID = aaidEntity.aaid, service = serviceType)
        }
    }


    private fun init() {
        if (tabList.isNotEmpty()) {
            tabList.forEachIndexed { index, tab ->
                addTapButton(this, index, tab.tabName)
            }
            setTabBackground(0)
            initViewPager()
            requestList()
        } else {
            MessageDialogHelper.confirm(
                activity = this,
                message = getString(R.string.acb_common_message_error),
                onConfirm = { finish() }
            ).show(false)
        }
    }


    private fun initViewPager() {
        with(vb.vpViewpager2) {
            isUserInputEnabled = false
            adapter = offerwallListPagerAdapter
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            offscreenPageLimit = tabList.size - 1
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageScrollStateChanged(state: Int) {}

                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

                override fun onPageSelected(position: Int) {
                    setTabBackground(position)
                    currentPagePosition = position
                }
            })
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

        vb.tabButton.addView(tab)

        tab.setOnClickListener {
            vb.vpViewpager2.setCurrentItem(index, true)
            offerwallListPagerAdapter.setData()
        }
    }


    private fun setTabBackground(currentIndex: Int) {
        when (currentIndex) {
            0 -> {
                // 첫 번째 탭일 때, 자신 탭과 우측 탭 설정
                ViewCompat.setBackground(sectionButtonList[currentIndex], ContextCompat.getDrawable(this, R.drawable.acbso_shp_left_select_tab_background))
                ViewCompat.setBackground(
                    sectionButtonList[currentIndex + 1],
                    ContextCompat.getDrawable(this, R.drawable.acbso_shp_center_unselect_tab_background)
                )
            }
            sectionButtonList.size - 1 -> {
                // 마지막 탭일 때, 자신 탭과 좌측 탭 설정
                ViewCompat.setBackground(
                    sectionButtonList[currentIndex - 1],
                    ContextCompat.getDrawable(this, R.drawable.acbso_shp_center_unselect_tab_background)
                )
                ViewCompat.setBackground(sectionButtonList[currentIndex], ContextCompat.getDrawable(this, R.drawable.acbso_shp_right_select_tab_background))
            }
            else -> {
                // 자신 탭과 좌우 탭 설정
                ViewCompat.setBackground(
                    sectionButtonList[currentIndex - 1],
                    ContextCompat.getDrawable(this, R.drawable.acbso_shp_center_unselect_tab_background)
                )
                ViewCompat.setBackground(sectionButtonList[currentIndex], ContextCompat.getDrawable(this, R.drawable.acbso_shp_center_select_tab_background))
                ViewCompat.setBackground(
                    sectionButtonList[currentIndex + 1],
                    ContextCompat.getDrawable(this, R.drawable.acbso_shp_center_unselect_tab_background)
                )
            }
        }

        // 나머지 좌측 탭들 설정
        for (i in 0 until currentIndex - 1) {
            ViewCompat.setBackground(sectionButtonList[i], ContextCompat.getDrawable(this, R.drawable.acbso_shp_left_unselect_tab_background))
        }

        // 나머지 우측 탭들 설정
        for (i in currentIndex + 2 until sectionButtonList.size) {
            ViewCompat.setBackground(sectionButtonList[i], ContextCompat.getDrawable(this, R.drawable.acbso_shp_right_unselect_tab_background))
        }


        for (i in 0 until sectionButtonList.size) {
            sectionButtonList[i].isEnabled = currentIndex != i
        }

        vb.tabButton.requestLayout()
    }


    inner class OfferwallListPagerAdapter : FragmentStateAdapter(this) {
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


        fun setStatusView(status: PlaceHolderRecyclerView.Status) {
            currentFragment?.binding?.placeHolderRecyclerView?.status = status
        }


        fun setData() {
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
            offerwalls = AdvertiseListController.makeOfferwallList(
                sectionList = sectionList,
                tabEntity = tabList[currentPagePosition]
            )
            currentFragment?.let {
                it.offerWallAdapter.offerwalls = offerwalls
                it.offerWallAdapter.refreshList(position, isPositionScroll)
            }
        }


        fun changeAllList(currentPosition: Int, journeyStateType: OfferwallJourneyStateType) {
            if (offerwalls.size - 1 < currentPosition) {
                requestList()
                return
            }
            AdvertiseListController.changeAllList(
                sections = sectionList,
                entity = offerwalls[currentPosition],
                journeyStateType = journeyStateType
            )
            listRefresh()
        }
    }
}