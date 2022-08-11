package com.guillaumewilmot.swoleai.util.storage

import com.guillaumewilmot.swoleai.model.*
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class DataStorageMock : DataStorage {

    override fun <T> toStorage(dataDefinition: DataDefinition, obj: T): Completable {
        when (dataDefinition) {
            DataDefinition.USER -> dataHolder._userField.onNext(
                (obj as UserModel).asNullable()
            )
            DataDefinition.PROGRAM -> dataHolder._programField.onNext(
                (obj as ProgramModel).asNullable()
            )
            DataDefinition.SELECTED_SESSION_ID -> dataHolder._selectedSessionIdField.onNext(
                obj as Int
            )
            DataDefinition.EXERCISE_BOOK -> dataHolder._exerciseBookField.onNext(
                obj as ExerciseBookModel
            )
        }
        return Completable.complete()
    }

    /**
     * Data
     */

    override val dataHolder by lazy {
        DataHolderImpl()
    }

    @Suppress("PropertyName")
    inner class DataHolderImpl : DataHolder {
        val _userField: BehaviorSubject<Nullable<UserModel>> by lazy {
            BehaviorSubject.createDefault(Nullable(null))
        }
        val _programField: BehaviorSubject<Nullable<ProgramModel>> by lazy {
            BehaviorSubject.createDefault(Nullable(null))
        }
        val _selectedSessionIdField: BehaviorSubject<Int> by lazy {
            BehaviorSubject.createDefault(1)
        }
        val _exerciseBookField: BehaviorSubject<ExerciseBookModel> by lazy {
            BehaviorSubject.createDefault(ExerciseBookModel(id = 1, exercises = listOf()))
        }

        override val userField: Flowable<Nullable<UserModel>> =
            _userField.toFlowable(BackpressureStrategy.BUFFER)
        override val programField: Flowable<Nullable<ProgramModel>> =
            _programField.toFlowable(BackpressureStrategy.BUFFER)
        override val selectedSessionIdField: Flowable<Int> =
            _selectedSessionIdField.toFlowable(BackpressureStrategy.BUFFER)
        override val exerciseBookField: Flowable<ExerciseBookModel> =
            _exerciseBookField.toFlowable(BackpressureStrategy.BUFFER)
    }
}