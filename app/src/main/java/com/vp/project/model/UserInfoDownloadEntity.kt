package com.vp.project.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize // 인텐트로 넘길라고
data class UserInfoDownloadEntity (
    var uid :String? = null,
    var email :String? = null,
    var name :String? = null,
    var username :String? = null,
    var text :String? = null
) : Parcelable