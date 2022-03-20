package com.guillaumewilmot.swoleai.util.storage

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.rxjava3.rxPreferencesDataStore
import androidx.datastore.rxjava3.RxDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.guillaumewilmot.swoleai.model.UserModel
import com.guillaumewilmot.swoleai.util.storage.rxlive.Optional
import com.guillaumewilmot.swoleai.util.storage.rxlive.asOptional
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.lang.reflect.Type

@ExperimentalCoroutinesApi
class DataStorage constructor(
    private val applicationContext: Context
) {
    private val Context.rxDataStore: RxDataStore<Preferences> by rxPreferencesDataStore(name = "AppStorage")
    private val gson by lazy { Gson() }

    private inline fun <reified T> gsonDeserializeType(): Type = object : TypeToken<T>() {}.type
    private fun <T> fromJson(json: String, t: Type): T? = try {
        gson.fromJson<T>(json, t)
    } catch (e: Exception) {
//        e.printStackTrace()
        null
    }

    /**
     * Get
     */

    private fun getStringRx(key: String): Flowable<Optional<String>> {
        return applicationContext.rxDataStore
            .data()
            .onErrorReturn {
                it.printStackTrace()
                emptyPreferences()
            }
            .map { preferences ->
                preferences[stringPreferencesKey(key)].asOptional()
            }
            .distinctUntilChanged()
            .doOnNext {
                Log.d("DataStorage", "Sending new value downstream for $key")
            }
    }

    private inline fun <reified T : Any> fromStorage(
        dataDefinition: DataDefinition
    ): Flowable<Optional<T>> {
        return getStringRx(dataDefinition.key).map {
            it.value?.let { stringValue ->
                fromJson<T>(stringValue, gsonDeserializeType<T>())
            }.asOptional()
        }
    }

    private inline fun <reified T : Any> fromStorageOrDefault(
        dataDefinition: DataDefinition,
        default: T
    ): Flowable<T> {
        return fromStorage<T>(dataDefinition).map {
            it.value ?: default
        }
    }

    /**
     * Set
     */

    private fun putStringRx(key: String, value: String): Single<Preferences> {
        return applicationContext.rxDataStore.updateDataAsync { preferences ->
            val mutablePreferences = preferences.toMutablePreferences()
            mutablePreferences[stringPreferencesKey(key)] = value
            Single.just(mutablePreferences)
        }.also { result ->
            Log.d("DataStorage", "Send for storage \"$key\": \"$value\"")
            result.subscribe(
                {
                    Log.d("DataStorage", "Stored \"$key\": \"$value\"")
                },
                {
                    it.printStackTrace()
                    Log.d("DataStorage", "Error while Storing \"$key\": \"$value\"")
                }
            )
        }
    }

    fun <T> toStorage(key: String, obj: T): Single<Preferences>? = try {
        val value = gson.toJson(obj)
        putStringRx(key, value)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }

    /**
     * Data
     */

    val dataHolder by lazy { DataHolder() }

    inner class DataHolder {
        val userField: Flowable<Optional<UserModel>> by lazy {
            this@DataStorage.fromStorage(DataDefinition.USER)
        }
        val exercisesField: Flowable<List<Int>> by lazy {
            this@DataStorage.fromStorageOrDefault(DataDefinition.EXERCISES, listOf())
        }

    }

    enum class DataDefinition(
        val key: String
    ) {
        USER("storage_user"),
        EXERCISES("storage_exercises");
    }
}