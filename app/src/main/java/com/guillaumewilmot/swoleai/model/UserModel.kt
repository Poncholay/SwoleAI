package com.guillaumewilmot.swoleai.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class UserModel(
    @SerializedName("name")
    var username: String? = null,
    @SerializedName("isMale")
    var isMale: Boolean? = null,
    @SerializedName("height")
    var height: Int? = null,
    @SerializedName("weight")
    var weight: Int? = null
) : Serializable {
    companion object {
        private const val serialVersionUID = 7L
    }
}