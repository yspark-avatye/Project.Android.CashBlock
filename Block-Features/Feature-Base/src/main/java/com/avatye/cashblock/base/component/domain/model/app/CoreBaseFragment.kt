package com.avatye.cashblock.base.component.domain.model.app

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.avatye.cashblock.base.component.widget.dialog.DialogLoadingView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager

typealias Inflate<T> = (LayoutInflater, ViewGroup?, Boolean) -> T

abstract class CoreBaseFragment<VB : ViewBinding>(private val inflate: Inflate<VB>) : Fragment() {

    protected var glider: RequestManager? = null
    protected var loadingView: DialogLoadingView? = null

    private var _binding: VB? = null
    val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = inflate.invoke(inflater, container, false)
        glider = Glide.with(this)
        loadingView = parentFragment?.activity?.let { DialogLoadingView.create(ownerActivity = it) }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        glider = null
    }

}