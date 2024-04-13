package com.ifs21049.lostandfound.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.File

@Parcelize
data class DelcomLostFound(
    val id: Int,
    val title: String,
    val description: String,
    var isCompleted: Int?,
    val cover:  File,
    var status: String?,
) : Parcelable