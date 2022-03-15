package com.guillaumewilmot.swoleai.util.storage

import android.content.Context
import com.guillaumewilmot.swoleai.model.UserModel

class UserStorage {
    fun user(context: Context?) = context?.fromStorage<UserModel>(USER)
    fun exercises(context: Context?) = context?.fromStorage<List<Int>>(EXERCISES) ?: listOf() //TODO : Fix

    companion object {
        const val USER = "user"
        const val EXERCISES = "exercises"
    }
}