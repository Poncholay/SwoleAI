package com.guillaumewilmot.swoleai.util.storage

import android.annotation.SuppressLint
import android.content.Context
import android.preference.PreferenceManager
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

object SimpleStorage {
    private val gson by lazy { Gson() }

    val user = UserStorage()

    inline fun <reified T> genericType(): Type = object : TypeToken<T>() {}.type

    private fun <T> fromJson(json: String, t: Type): T? = try {
        gson.fromJson<T>(json, t)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }

    fun fromStorage(context: Context, key: String, type: Type): Any? = try {
        val text = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
            .getString(key, "null")

        text?.let {
            Log.d("Storage", "Retrieved in shared preferences $key")
            fromJson<Any>(it, type)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }

    @SuppressLint("ApplySharedPref")
    @Synchronized
    fun <T> toStorage(context: Context, key: String, obj: T) = try {
        obj.apply {
            val text = gson.toJson(this)
            PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
                .edit()
                .putString(key, text)
                .commit()

            Log.d("Storage", "Stored $key: $text")
        }
    } catch (e: Exception) {
        e.printStackTrace()
        obj
    }

    fun <T> internalClone(obj: T, type: Type): T? = fromJson<T>(gson.toJson(obj), type)
    inline fun <reified T> clone(obj: T): T? = internalClone(obj, genericType<T>())
}

fun <T> T.toStorage(context: Context, key: String): T = try {
    SimpleStorage.toStorage(context, key, this)
} catch (e: Exception) {
    e.printStackTrace()
    this
}

/**
 * NEVER MODIFY AN OBJECT FROM THE STORAGE WITHOUT CLONING IT FIRST
 */
inline fun <reified T> Context.fromStorage(key: String): T? = try {
    SimpleStorage.fromStorage(this, key, SimpleStorage.genericType<T>()) as? T
} catch (e: Exception) {
    e.printStackTrace()
    null
}