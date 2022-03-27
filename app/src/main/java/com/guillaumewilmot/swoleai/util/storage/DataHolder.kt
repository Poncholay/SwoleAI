package com.guillaumewilmot.swoleai.util.storage

import com.guillaumewilmot.swoleai.model.Nullable
import com.guillaumewilmot.swoleai.model.SessionModel
import com.guillaumewilmot.swoleai.model.UserModel
import io.reactivex.rxjava3.core.Flowable

interface DataHolder {
    val userField: Flowable<Nullable<UserModel>>
    val currentSessionField: Flowable<Nullable<SessionModel>>
}