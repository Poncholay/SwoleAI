package com.guillaumewilmot.swoleai.model

import com.google.gson.annotations.SerializedName

data class Nullable<T>(
    @SerializedName("value")
    val value: T?
)

fun <T> T?.asNullable() = Nullable(this)