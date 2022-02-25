package com.avatye.cashblock.feature.offerwall.presentation.view.main

import android.app.ActivityOptions
import android.content.Context
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.avatye.cashblock.base.component.domain.entity.base.ServiceType
import com.avatye.cashblock.base.component.domain.entity.offerwall.*
import com.avatye.cashblock.base.component.support.CoreUtil.showToast
import com.avatye.cashblock.base.component.support.compoundDrawablesWithIntrinsicBounds
import com.avatye.cashblock.base.component.support.toLocale
import com.avatye.cashblock.base.component.widget.miscellaneous.PlaceHolderRecyclerView
import com.avatye.cashblock.feature.offerwall.R
import com.avatye.cashblock.feature.offerwall.component.controller.AdvertiseListController
import com.avatye.cashblock.feature.offerwall.component.data.PreferenceData
import com.avatye.cashblock.feature.offerwall.databinding.AcbsoFragmentOfferwallListBinding
import com.avatye.cashblock.feature.offerwall.databinding.AcbsoItemOfferwallCategoryBinding
import com.avatye.cashblock.feature.offerwall.databinding.AcbsoItemOfferwallListBinding
import com.avatye.cashblock.feature.offerwall.databinding.AcbsoItemOfferwallSectionBinding
import com.avatye.cashblock.feature.offerwall.presentation.AppBaseFragment
import com.avatye.cashblock.feature.offerwall.presentation.parcel.OfferWallViewParcel
import com.avatye.cashblock.feature.offerwall.presentation.view.detail.OfferwallDetailViewActivity
import com.avatye.sdk.cashbutton.core.widget.stickylayout.StickyHeaders
import com.avatye.sdk.cashbutton.core.widget.stickylayout.StickyHeadersLinearLayoutManager
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

internal class OfferWallListFragment : AppBaseFragment<AcbsoFragmentOfferwallListBinding>(AcbsoFragmentOfferwallListBinding::inflate) {

    private val parentActivity: OfferwallMainActivity by lazy {
        activity as OfferwallMainActivity
    }

    internal companion object {
        fun open(position: Int): OfferWallListFragment {
            val args = Bundle()
            args.putInt("position", position)

            val instance = OfferWallListFragment()
            instance.arguments = args

            return instance
        }
    }


    val offerWallAdapter: OfferWallAdapter = OfferWallAdapter()
    private lateinit var tab: OfferwallTabEntity
    private var tabPosition = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { tabPosition = it.getInt("position") }

        if(parentActivity.tabList.isNotEmpty()) {
            tab = parentActivity.tabList[tabPosition]
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding.placeHolderRecyclerView) {
            setHasFixedSize(false)
            setAdapter(offerWallAdapter)
            setLayoutManager(WrapContentLinearLayoutManager(parentActivity))
            status = PlaceHolderRecyclerView.Status.LOADING
            actionRetry {
                if (isAvailable) {
                    parentActivity.requestOfferWallList()
                }
            }
        }
    }


    private fun foldingData(hiddenSectionID: String, position: Int = 0) {
        val hiddenSections: List<String>? = PreferenceData.Hidden.hiddenSections

        if ((hiddenSections ?: listOf()).contains(hiddenSectionID)) {
            val resultItem = hiddenSections?.filterNot { it.contains(hiddenSectionID) }
            PreferenceData.Hidden.update(hiddenSections = resultItem)
        } else {
            val resultItem = hiddenSections?.plus(hiddenSectionID)
            PreferenceData.Hidden.update(hiddenSections = resultItem)
        }
        parentActivity.offerwallListPagerAdapter.listRefresh(position, true)
    }


    private fun getItemTitle(entity: OfferwallItemEntity, listType: OfferwallBindItemListType): String {
        val title = when (listType) {
            OfferwallBindItemListType.SECTION, OfferwallBindItemListType.CATEGORY -> {
                entity.displayTitle.ifEmpty {
                    entity.sectionTitle
                }
            }
            else -> {
                entity.displayTitle.ifEmpty {
                    entity.title
                }
            }
        }

        return title
    }


    inner class OfferWallAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(), StickyHeaders, StickyHeaders.ViewSetup {
        var offerwalls: MutableList<OfferwallItemEntity> = mutableListOf()

        // region # Sticky-Method
        override fun isStickyHeader(position: Int): Boolean = (offerwalls[position].viewType == OfferwallViewStateType.VIEW_TYPE_SECTION)
        override fun setupStickyHeaderView(header: View) {
            header.findViewById<FrameLayout>(R.id.section_sticky_line).isVisible = false
        }

        override fun teardownStickyHeaderView(header: View) {}
        // endregion


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return when (viewType) {
                OfferwallViewStateType.VIEW_TYPE_SECTION.value -> {
                    val itemSectionBinding = AcbsoItemOfferwallSectionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                    OfferWallSectionViewHolder(itemSectionBinding)
                }
                OfferwallViewStateType.VIEW_TYPE_CATEGORY.value -> {
                    val itemCategoryBinding = AcbsoItemOfferwallCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                    OfferwallCategoryViewHolder(itemCategoryBinding)
                }
                else -> {
                    val itemListBinding = AcbsoItemOfferwallListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                    OfferwallListViewHolder(itemListBinding)
                }
            }
        }

        override fun getItemViewType(position: Int): Int {
            return offerwalls[position].viewType.value
        }

        override fun getItemCount(): Int {
            return offerwalls.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if (isAvailable) {
                when (getItemViewType(position)) {
                    OfferwallViewStateType.VIEW_TYPE_SECTION.value -> (holder as OfferWallSectionViewHolder).bindSection()
                    OfferwallViewStateType.VIEW_TYPE_CATEGORY.value -> (holder as OfferwallCategoryViewHolder).bindCategory()
                    else -> (holder as OfferwallListViewHolder).bindList()
                }
            }
        }


        fun refreshList(positionStart: Int, isPositionScroll: Boolean) {
            notifyItemRangeChanged(positionStart, offerwalls.size)

            if (isPositionScroll) {
                binding.placeHolderRecyclerView.setScrollToPosition(position = positionStart)
            }
        }


        inner class OfferWallSectionViewHolder(private val itemBinding: AcbsoItemOfferwallSectionBinding) : RecyclerView.ViewHolder(itemBinding.root) {
            fun bindSection() {
                val entity = offerwalls[adapterPosition]
                val title = getItemTitle(entity, OfferwallBindItemListType.SECTION)

                bindHiddenSection(adapterPosition, entity)
                itemBinding.sectionTitle.text = "${title}(${entity.sectionPos})"
                itemBinding.sectionStickyLine.isVisible = adapterPosition != 0
            }

            private fun bindHiddenSection(position: Int, entity: OfferwallItemEntity) {
                val hiddenSections: List<String>? = PreferenceData.Hidden.hiddenSections
                val hiddenSectionID = AdvertiseListController.getHiddenSectionID(
                    tabEntity = tab,
                    entity = entity
                )

                if ((hiddenSections ?: listOf()).contains(hiddenSectionID)) {
                    itemBinding.sectionFoldingArrow.rotation = 180F
                } else {
                    itemBinding.sectionFoldingArrow.rotation = 0F
                }

                itemBinding.listItemSectionContainer.setOnClickListener {
                    foldingData(hiddenSectionID, position)
                }
            }
        }

        inner class OfferwallCategoryViewHolder(private val itemBinding: AcbsoItemOfferwallCategoryBinding) : RecyclerView.ViewHolder(itemBinding.root) {
            fun bindCategory() {
                val entity = offerwalls[adapterPosition]
                val title = getItemTitle(entity, OfferwallBindItemListType.CATEGORY)

                when (tab.listType) {
                    OfferwallListType.ONLY_CATEGORY.value -> {
                        itemBinding.categoryFoldingArrow.isVisible = true
                        itemBinding.categoryTitle.text = "${title}(${entity.categoryPos})"
                        bindHiddenCategory(adapterPosition,entity)
                    }
                    else -> {
                        itemBinding.categoryFoldingArrow.isVisible = false
                        itemBinding.categoryTitle.text = title
                    }
                }
            }

            private fun bindHiddenCategory(position: Int, entity: OfferwallItemEntity) {
                val hiddenSections: List<String>? = PreferenceData.Hidden.hiddenSections
                val hiddenSectionID = AdvertiseListController.getHiddenSectionID(
                    tabEntity = tab,
                    entity = entity
                )

                if ((hiddenSections ?: listOf()).contains(hiddenSectionID)) {
                    itemBinding.categoryFoldingArrow.rotation = 180F
                } else {
                    itemBinding.categoryFoldingArrow.rotation = 0F
                }

                itemBinding.listItemCategoryContainer.setOnClickListener {
                    foldingData(hiddenSectionID, position)
                }
            }
        }


        inner class OfferwallListViewHolder(private val itemBinding: AcbsoItemOfferwallListBinding) : RecyclerView.ViewHolder(itemBinding.root) {

            fun bindList() {
                val entity = offerwalls[adapterPosition]
                val title = getItemTitle(entity, OfferwallBindItemListType.BASE)

                setView(entity)

                itemBinding.listItemContainer.setOnClickListener {
                    if (entity.journeyState == OfferwallJourneyStateType.COMPLETED_REWARDED || entity.journeyState == OfferwallJourneyStateType.COMPLETED_FAILED) {
                        showToast(getString(R.string.acbso_offerwall_rewarded_completed))
                    } else {
                        OfferwallDetailViewActivity.open(
                            activity = parentActivity,
                            serviceType = serviceType ?: ServiceType.OFFERWALL,
                            parcel = OfferWallViewParcel(entity.advertiseID, adapterPosition, title, entity.reward),
                            options = getActivityOptions(entity)
                        )

                    }
                }
            }

            private fun setView(entity: OfferwallItemEntity) {
                // region # Icon
                glider?.load(entity.iconUrl)?.apply(RequestOptions().transform(RoundedCorners(24)))?.error(R.drawable.acbso_ic_coin_error)?.into(itemBinding.iconImage)
                // endregion

                // region # Text
                itemBinding.itemRewardAmount.text = entity.reward.toLocale()
                itemBinding.itemActionType.text = entity.actionName
                itemBinding.itemTitle.text = getItemTitle(entity, OfferwallBindItemListType.BASE)
                itemBinding.itemDescription.text = if (entity.additionalDescription.isEmpty()) {
                    entity.userGuide
                } else {
                    entity.additionalDescription
                }
                // endregion

                // region # Visible
                itemBinding.itemBottomLine.isVisible = !entity.isLast
                itemBinding.iconBadge.isVisible = (entity.journeyState == OfferwallJourneyStateType.PARTICIPATE)
                // endregion

                // region # Resource
                if (entity.journeyState == OfferwallJourneyStateType.COMPLETED_REWARDED || entity.journeyState == OfferwallJourneyStateType.COMPLETED_FAILED) {
                    itemBinding.listItemContainer.alpha = 0.2f
                    itemBinding.iconImage.colorFilter = ColorMatrixColorFilter(ColorMatrix().apply { setSaturation(0F) })
                    itemBinding.itemRewardAmount.setTextColor(ActivityCompat.getColor(parentActivity, R.color.acb_core_color_silver))
                    itemBinding.itemActionType.setBackgroundResource(R.drawable.acb_common_rectangle_cccccc_r50)
                    itemBinding.itemRewardAmount.compoundDrawablesWithIntrinsicBounds(end = R.drawable.acbso_ic_coin_off)
                } else {
                    itemBinding.listItemContainer.alpha = 1f
                    itemBinding.iconImage.colorFilter = null
                    itemBinding.itemRewardAmount.setTextColor(ActivityCompat.getColor(parentActivity, R.color.acb_core_color_main_text))
                    itemBinding.itemActionType.setBackgroundResource(R.drawable.acb_common_rectangle_f57c00_r4)

                    val bgShape = itemBinding.itemActionType.background as GradientDrawable
                    entity.actionBGColor.let { bgShape.setColor(Color.parseColor(it)) }
                    itemBinding.itemRewardAmount.compoundDrawablesWithIntrinsicBounds(end = R.drawable.acbso_ic_coin_blue)
                }
                // endregion
            }

            private fun getActivityOptions(entity: OfferwallItemEntity): Bundle? {
                val pairArray = arrayListOf<Pair<View, String>>(
                    Pair.create(itemBinding.iconImage, parentActivity.getString(R.string.acbso_transition_name_offerwall_image)),
                    Pair.create(itemBinding.itemTitle, parentActivity.getString(R.string.acbso_transition_name_offerwall_name)),
                    Pair.create(itemBinding.itemDescription, parentActivity.getString(R.string.acbso_transition_name_offerwall_description)),
                    Pair.create(itemBinding.itemRewardAmount, parentActivity.getString(R.string.acbso_transition_name_offerwall_reward)),
                )

                if (entity.journeyState == OfferwallJourneyStateType.PARTICIPATE) {
                    pairArray.add(Pair.create(itemBinding.iconBadge, parentActivity.getString(R.string.acbso_transition_name_offerwall_badge)))
                }

                val activityOptions = ActivityOptions.makeSceneTransitionAnimation(parentActivity, *pairArray.toTypedArray())

                return activityOptions?.toBundle()
            }
        }
    }


    // region # Sticky-Header
    inner class WrapContentLinearLayoutManager(context: Context) : StickyHeadersLinearLayoutManager<OfferWallAdapter>(context) {

        override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
            try {
                super.onLayoutChildren(recycler, state)
            } catch (e: IndexOutOfBoundsException) {
            }
        }
    }
    // endregion
}

