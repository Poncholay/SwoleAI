package com.guillaumewilmot.swoleai.util.storage

import com.guillaumewilmot.swoleai.model.Nullable
import com.guillaumewilmot.swoleai.model.SessionModel
import com.guillaumewilmot.swoleai.model.UserModel
import com.guillaumewilmot.swoleai.model.asNullable
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class DataStorageMock : DataStorage {

    override fun <T> toStorage(dataDefinition: DataDefinition, obj: T): Completable {
        when (dataDefinition) {
            DataDefinition.USER -> dataHolder._userField.onNext((obj as UserModel).asNullable())
            DataDefinition.CURRENT_SESSION -> dataHolder._currentSessionField.onNext((obj as SessionModel).asNullable())
        }
        return Completable.complete()
    }

    /**
     * Data
     */

    override val dataHolder by lazy { DataHolderImpl() }

    @Suppress("PropertyName")
    inner class DataHolderImpl : DataHolder {
        val _userField: BehaviorSubject<Nullable<UserModel>> by lazy {
            BehaviorSubject.createDefault(Nullable(null))
        }
        val _currentSessionField: BehaviorSubject<Nullable<SessionModel>> by lazy {
            BehaviorSubject.createDefault(Nullable(null))
        }

        override val userField: Flowable<Nullable<UserModel>> =
            _userField.toFlowable(BackpressureStrategy.BUFFER)
        override val currentSessionField: Flowable<Nullable<SessionModel>> =
            _currentSessionField.toFlowable(BackpressureStrategy.BUFFER)
    }
}