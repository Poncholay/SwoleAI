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
import com.guillaumewilmot.swoleai.model.Nullable
import com.guillaumewilmot.swoleai.model.SessionModel
import com.guillaumewilmot.swoleai.model.UserModel
import com.guillaumewilmot.swoleai.model.asNullable
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.lang.reflect.Type

@ExperimentalCoroutinesApi
class DataStorageImpl constructor(
    private val applicationContext: Context
) : DataStorage {
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

    private fun <T> T?.toJson(): String? = try {
        gson.toJson(this)
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
        null
    }

    /**
     * Get
     */

    private fun getString(key: String): Flowable<Nullable<String>> {
        return applicationContext.rxDataStore
            .data()
            .onErrorReturn {
                //FIXME: Will have to handle this case at some point
                it.printStackTrace()
                emptyPreferences()
            }
            .map { preferences ->
                preferences[stringPreferencesKey(key)].asNullable()
            }
            .distinctUntilChanged()
            .doOnNext {
                Log.d("DataStorage", "Sending new value downstream for $key: ${it.value.toJson()}")
            }
            .subscribeOn(Schedulers.io())
    }

    private fun <T : Any> fromStorageWithType(
        key: String,
        type: Type
    ): Flowable<Nullable<T>> {
        return getString(key).map {
            it.value?.let { stringValue ->
                fromJson<T>(stringValue, type)
            }.asNullable()
        }
    }

    private inline fun <reified T : Any> fromStorage(
        dataDefinition: DataDefinition
    ): Flowable<Nullable<T>> {
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

//    private fun putStringRx(key: String, value: String): Single<Preferences> {
    private fun putStringRx(key: String, value: String): Completable {
        return applicationContext.rxDataStore
            .updateDataAsync { preferences ->
                val mutablePreferences = preferences.toMutablePreferences()
                mutablePreferences[stringPreferencesKey(key)] = value
                Single.just(mutablePreferences)
            }
            .also {
                Log.d("DataStorage", "Send for storage \"$key\": \"$value\"")
            }
            .flatMapCompletable {
                Log.d("DataStorage", "Stored \"$key\": \"$value\"")
                Completable.complete()
            }
    }

    //    override fun <T> toStorage(dataDefinition: DataDefinition, obj: T): Single<Preferences> {
    override fun <T> toStorage(dataDefinition: DataDefinition, obj: T): Completable {
        val jsonValue = obj.toJson() ?: ""
        return putStringRx(dataDefinition.key, jsonValue)
    }

    /**
     * Data
     */

    override val dataHolder by lazy { DataHolderImpl() }

    inner class DataHolderImpl : DataHolder {
        override val userField: Flowable<Nullable<UserModel>> by lazy {
            this@DataStorageImpl.fromStorage(DataDefinition.USER)
        }

        override val currentSessionField: Flowable<Nullable<SessionModel>> by lazy {
            this@DataStorageImpl.fromStorage(DataDefinition.CURRENT_SESSION)
        }
    }
}