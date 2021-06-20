package com.rezha.azis.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Transaksi(
    var nama:String?="",
    var alamat:String?="",
    var keterangan:String?="",
    var beras: String?="",
    var uang:String?="",
    var tanggal:String?="",
    var jenis:String?="",
    var id:String?="",
):Parcelable


