package com.guillaumewilmot.swoleai.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class UserModel(
    @SerializedName("name")
    val username: String? = null,
    @SerializedName("isMale")
    val isMale: Boolean? = null,
    @SerializedName("height")
    val height: Int? = null,
    @SerializedName("weight")
    val weight: Int? = null
) : Serializable {
    companion object {
        private const val serialVersionUID = 7L
    }
}