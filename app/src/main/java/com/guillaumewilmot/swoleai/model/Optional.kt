package com.guillaumewilmot.swoleai.model

import com.google.gson.annotations.SerializedName

data class Optional<T>(
    @SerializedName("value")
    val value: T?
)

fun <T> T?.asOptional() = Optional(this)