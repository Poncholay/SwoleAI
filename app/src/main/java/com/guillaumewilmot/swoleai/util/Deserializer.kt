package com.guillaumewilmot.swoleai.util

import android.content.Context
import java.lang.reflect.Type

interface Deserializer {
    fun <T> fromJson(json: String, t: Type): T?
    fun <T> T?.toJson(): String?
    fun <T> deserializeAsset(context: Context, fileName: String, t: Type): T?
}