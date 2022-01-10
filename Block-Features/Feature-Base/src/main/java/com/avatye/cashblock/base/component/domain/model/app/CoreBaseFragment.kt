package com.avatye.cashblock.base.component.domain.model.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.zoyi.com.bumptech.glide.Glide
import com.zoyi.com.bumptech.glide.RequestManager

typealias Inflate<T> = (LayoutInflater, ViewGroup?, Boolean) -> T

abstract class CoreBaseFragment<VB : ViewBinding>(private val inflate: Inflate<VB>) : Fragment() {

    protected var glider: RequestManager? = null

    private var _binding: VB? = null
    val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = inflate.invoke(inflater, container, false)
        glider = Glide.with(this)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        glider = null
    }

}