package com.guillaumewilmot.swoleai.util.storage

import androidx.datastore.preferences.core.Preferences
import com.guillaumewilmot.swoleai.model.Optional
import com.guillaumewilmot.swoleai.model.UserModel
import com.guillaumewilmot.swoleai.model.asOptional
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.subjects.BehaviorSubject
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class DataStorageMock : DataStorage {

    override fun <T> toStorage(dataDefinition: DataDefinition, obj: T): Single<Preferences>? {
        when (dataDefinition) {
            DataDefinition.USER -> dataHolder._userField.onNext((obj as UserModel).asOptional())
            DataDefinition.EXERCISES -> dataHolder._exercisesField.onNext(obj as List<Int>)
        }
        return null
    }

    /**
     * Data
     */

    override val dataHolder by lazy { DataHolderImpl() }

    inner class DataHolderImpl : DataHolder {
        val _userField: BehaviorSubject<Optional<UserModel>> by lazy {
            BehaviorSubject.createDefault(Optional(null))
        }
        val _exercisesField: BehaviorSubject<List<Int>> by lazy {
            BehaviorSubject.createDefault(listOf())
        }

        override val userField: Flowable<Optional<UserModel>> =
            _userField.toFlowable(BackpressureStrategy.BUFFER)
        override val exercisesField: Flowable<List<Int>> =
            _exercisesField.toFlowable(BackpressureStrategy.BUFFER)
    }
}