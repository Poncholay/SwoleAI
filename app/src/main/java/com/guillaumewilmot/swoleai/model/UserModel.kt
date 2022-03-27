package com.guillaumewilmot.swoleai.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class UserModel(
    @SerializedName("name")
    var username: String? = null
) : Serializable {
    companion object {
        private const val serialVersionUID = 7L
    }
}