package com.guillaumewilmot.swoleai.util.storage.rxlive

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.annotation.VisibleForTesting
import com.guillaumewilmot.swoleai.model.UserModel
import com.guillaumewilmot.swoleai.util.storage.SimpleStorage
import com.guillaumewilmot.swoleai.util.storage.UserStorage

object RxLiveStorage {
    /**
     * Implementation
     */

    //Keep reference to avoid garbage collection
    private lateinit var updateListener: SharedPreferences.OnSharedPreferenceChangeListener

    fun registerUpdateListener(context: Context) {
        updateListener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            reloadStorage(context, key)
        }
        PreferenceManager.getDefaultSharedPreferences(context).registerOnSharedPreferenceChangeListener(updateListener)
    }

    private fun reloadStorage(context: Context, key: String) {
        dataHolder.dataList.forEach {
            if (it.key == key) {
                it.reload(context)
            }
        }
    }

    /**
     * Data
     */

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val dataHolder by lazy { DataHolder() }

    class DataHolder {
        private val userField: RxStoredLiveDataNullable<UserModel> = RxStoredLiveDataNullable(UserStorage.USER, SimpleStorage.user::user)
        private val exercisesField: RxStoredLiveData<List<Int>> = RxStoredLiveData(UserStorage.EXERCISES, SimpleStorage.user::exercises)

        fun user(context: Context) = userField.get(context)
        fun exercises(context: Context) = exercisesField.get(context)

        val dataList: List<RxStoredLiveData<*>> = listOf(
            userField,
            exercisesField
        )
    }
}
