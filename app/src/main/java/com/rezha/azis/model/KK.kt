package com.rezha.azis.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class KK(
        var nama:String?="",
        var alamat:String?="",
        var status:String?="",
        var hubungan:String?="",
        var kk:String?="",
        var jlh_anggota:String?="",
        var id:String?="",
):Parcelable


