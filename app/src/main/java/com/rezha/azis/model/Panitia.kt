package com.rezha.azis.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Panitia(
        var mesjid:String?="",
        var alamat_mesjid:String?="",
        var tahun:String?="",
        var nama:String?="",
        var harga_beras: String?="",
        var fidyah:String?="",
        var id:String?="",
): Parcelable