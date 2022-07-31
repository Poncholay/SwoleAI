package com.guillaumewilmot.swoleai.util

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

object DeserializerImpl : Deserializer {
    private val gson by lazy { Gson() }

    inline fun <reified T> gsonDeserializeType(): Type = object : TypeToken<T>() {}.type

    override fun <T> fromJson(json: String, t: Type): T? = try {
        gson.fromJson<T>(json, t)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }

    override fun <T> T?.toJson(): String? = try {
        gson.toJson(this)
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
        null
    }

    override fun <T> deserializeAsset(
        context: Context,
        fileName: String,
        t: Type
    ): T? = readAsset(context, fileName)?.let { json ->
        fromJson<T>(json, t)
    }

    private fun readAsset(context: Context, fileName: String): String? = try {
        context.assets
            .open(fileName)
            .bufferedReader()
            .use {
                it.readText()
            }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}