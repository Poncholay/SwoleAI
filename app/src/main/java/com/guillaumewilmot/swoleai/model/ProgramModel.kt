package com.guillaumewilmot.swoleai.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ProgramModel(
    @SerializedName("id")
    val id: Int
) : Serializable {
    companion object {
        private const val serialVersionUID = 3L
    }
}