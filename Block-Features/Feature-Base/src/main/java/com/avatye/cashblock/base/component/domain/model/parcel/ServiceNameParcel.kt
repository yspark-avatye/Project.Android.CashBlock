package com.avatye.cashblock.base.component.domain.model.parcel

import android.os.Parcelable
import com.avatye.cashblock.base.component.domain.entity.base.ServiceType

import kotlinx.parcelize.Parcelize

@Parcelize
data class ServiceNameParcel(val serviceType: ServiceType) : Parcelable {
    companion object {
        const val NAME = "parcel:service-type"
    }
}