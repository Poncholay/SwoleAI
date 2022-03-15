package com.guillaumewilmot.swoleai.model

import com.google.gson.annotations.SerializedName

data class UserModel(
    @SerializedName("name")
    val name: String? = null
)