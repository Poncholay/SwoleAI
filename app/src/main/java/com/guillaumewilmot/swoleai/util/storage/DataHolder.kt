package com.guillaumewilmot.swoleai.util.storage

import com.guillaumewilmot.swoleai.model.Optional
import com.guillaumewilmot.swoleai.model.UserModel
import io.reactivex.rxjava3.core.Flowable

interface DataHolder {
    val userField: Flowable<Optional<UserModel>>
    val exercisesField: Flowable<List<Int>>
}