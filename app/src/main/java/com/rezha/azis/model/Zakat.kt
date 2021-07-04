package com.rezha.azis.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Zakat(
    var panitia:String?="",
    var nama:String?="",
    var alamat:String?="",
    var anggota:String?="",
    var harga_beras:String?="",
    var keterangan:String?="",
    var zakat_harta:String?="",
    var fidyah:String?="",
    var beras: String?="",
    var uang:String?="",
    var tanggal:String?="",
    var jenis:String?="",
    var id:String?="",
): Parcelable