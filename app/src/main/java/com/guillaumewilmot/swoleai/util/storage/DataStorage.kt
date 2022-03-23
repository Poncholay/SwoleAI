package com.guillaumewilmot.swoleai.util.storage

import androidx.datastore.preferences.core.Preferences
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
interface DataStorage {
    fun <T> toStorage(dataDefinition: DataDefinition, obj: T): Single<Preferences>?
    val dataHolder: DataHolder
}