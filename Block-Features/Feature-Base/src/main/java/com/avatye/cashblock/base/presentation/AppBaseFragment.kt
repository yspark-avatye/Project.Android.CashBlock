package com.avatye.cashblock.base.presentation

import androidx.viewbinding.ViewBinding
import com.avatye.cashblock.base.component.domain.model.app.CoreBaseFragment
import com.avatye.cashblock.base.component.domain.model.app.Inflate

internal abstract class AppBaseFragment<VB : ViewBinding>(inflate: Inflate<VB>) : CoreBaseFragment<VB>(inflate) {}