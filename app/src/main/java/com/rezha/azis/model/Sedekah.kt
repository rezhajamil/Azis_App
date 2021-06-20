package com.rezha.azis.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Sedekah(
        var nama:String?="",
        var alamat:String?="",
//        var hubungan_keluarga:String?="",
        var keterangan:String?="",
        var beras: String?="",
        var uang:String?="",
        var tanggal:String?="",
        var jenis:String?="",
        var id:String?="",
): Parcelable