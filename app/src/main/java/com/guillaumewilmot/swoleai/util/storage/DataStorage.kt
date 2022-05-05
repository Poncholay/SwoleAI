package com.guillaumewilmot.swoleai.util.storage

import io.reactivex.rxjava3.core.Completable
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
interface DataStorage {
    fun <T> toStorage(dataDefinition: DataDefinition, obj: T): Completable
    val dataHolder: DataHolder
}