package com.guillaumewilmot.swoleai.model

import com.google.gson.annotations.SerializedName

data class UserModel(
    @SerializedName("name")
    var username: String? = null
)