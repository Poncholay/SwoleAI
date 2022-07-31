package com.guillaumewilmot.swoleai.util.storage

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.rxjava3.rxPreferencesDataStore
import androidx.datastore.rxjava3.RxDataStore
import com.guillaumewilmot.swoleai.model.*
import com.guillaumewilmot.swoleai.util.DeserializerImpl.fromJson
import com.guillaumewilmot.swoleai.util.DeserializerImpl.gsonDeserializeType
import com.guillaumewilmot.swoleai.util.DeserializerImpl.toJson
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
        name = "AppStorage",
        scheduler = Schedulers.single() //Allows save operation to be done in order
    )

    /**
     * Get
     */

    private fun getString(key: String): Flowable<Nullable<String>> = applicationContext.rxDataStore
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

    private fun <T : Any> fromStorageWithType(
        key: String,
        type: Type
    ): Flowable<Nullable<T>> = getString(key).map {
        it.value?.let { stringValue ->
            fromJson<T>(stringValue, type)
        }.asNullable()
    }

    private inline fun <reified T : Any> fromStorage(
        dataDefinition: DataDefinition
    ): Flowable<Nullable<T>> = fromStorageWithType(
        dataDefinition.key,
        gsonDeserializeType<T>()
    )

    private inline fun <reified T : Any> fromStorageOrDefault(
        dataDefinition: DataDefinition,
        default: T,
        saveIfDefault: Boolean
    ): Flowable<T> = fromStorage<T>(dataDefinition).map {
        it.value ?: default.also { default ->
            if (saveIfDefault) {
                toStorage(dataDefinition, default)
            }
        }
    }

    /**
     * Set
     */

    private fun putString(key: String, value: String): Completable {
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

    override fun <T> toStorage(dataDefinition: DataDefinition, obj: T): Completable {
        val jsonValue = obj.toJson() ?: ""
        return putString(dataDefinition.key, jsonValue)
    }

    /**
     * Data
     */

    override val dataHolder by lazy { DataHolderImpl() }

    inner class DataHolderImpl : DataHolder {
        private fun <T : Any> cacheLatest(flowable: Flowable<T>) = flowable
            .replay(1)
            .refCount()

        override val userField: Flowable<Nullable<UserModel>> by lazy {
            cacheLatest(fromStorage(DataDefinition.USER))
        }

        override val programField: Flowable<Nullable<ProgramModel>> by lazy {
            cacheLatest(fromStorage(DataDefinition.PROGRAM))
        }

        override val exerciseBookField: Flowable<ExerciseBookModel> by lazy {
            cacheLatest(
                fromStorageOrDefault(
                    DataDefinition.EXERCISE_BOOK,
                    default = ExerciseBookModel.loadDefault(applicationContext),
                    saveIfDefault = true
                )
            )
        }

        override val selectedSessionIdField: Flowable<Int> by lazy {
            cacheLatest(
                fromStorageOrDefault(
                    DataDefinition.SELECTED_SESSION_ID,
                    default = 0,
                    saveIfDefault = false
                )
            )
        }
    }
}