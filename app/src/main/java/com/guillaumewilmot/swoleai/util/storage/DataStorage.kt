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
import com.guillaumewilmot.swoleai.model.Optional
import com.guillaumewilmot.swoleai.model.UserModel
import com.guillaumewilmot.swoleai.model.asOptional
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.lang.reflect.Type

@ExperimentalCoroutinesApi
class DataStorage constructor(
    private val applicationContext: Context
) {
    private val Context.rxDataStore: RxDataStore<Preferences> by rxPreferencesDataStore(
        name = "AppStorage", //Down the road it might be worth it to open more than one file
        scheduler = Schedulers.single() //Allows save operation to be done in order
    )
    private val gson by lazy { Gson() }

    private inline fun <reified T> gsonDeserializeType(): Type = object : TypeToken<T>() {}.type
    private fun <T> fromJson(json: String, t: Type): T? = try {
        gson.fromJson<T>(json, t)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }

    /**
     * Get
     */

    private fun getStringRx(key: String): Flowable<Optional<String>> {
        return applicationContext.rxDataStore
            .data()
            .onErrorReturn {
                //FIXME: Will have to handle this case at some point
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

    private fun <T : Any> fromStorageWithType(
        key: String,
        type: Type
    ): Flowable<Optional<T>> {
        return getStringRx(key).map {
            it.value?.let { stringValue ->
                fromJson<T>(stringValue, type)
            }.asOptional()
        }
    }

    private inline fun <reified T : Any> fromStorage(
        dataDefinition: DataDefinition
    ): Flowable<Optional<T>> {
        return fromStorageWithType(dataDefinition.key, gsonDeserializeType<T>())
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
                    Log.d("DataStorage", "Error while Storing \"$key\": \"$value\"")
                    it.printStackTrace()
                }
            )
        }
    }

    fun <T> toStorage(dataDefinition: DataDefinition, obj: T): Single<Preferences>? = try {
        val value = gson.toJson(obj)
        putStringRx(dataDefinition.key, value)
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