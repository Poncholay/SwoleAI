package com.guillaumewilmot.swoleai.util.storage.live

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.annotation.VisibleForTesting
import com.guillaumewilmot.swoleai.model.UserModel
import com.guillaumewilmot.swoleai.util.storage.SimpleStorage
import com.guillaumewilmot.swoleai.util.storage.UserStorage

object LiveStorage {
    /**
     * Implementation
     */

    lateinit var updateListener: SharedPreferences.OnSharedPreferenceChangeListener

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
    val dataHolder = DataHolder()

    class DataHolder {
        private val userField: StoredLiveData<UserModel?> = StoredLiveData(UserStorage.USER, SimpleStorage.user::user)
        private val exercisesField: StoredLiveData<List<Int>> = StoredLiveData(UserStorage.EXERCISES, SimpleStorage.user::exercises)

        fun user(context: Context) = userField.get(context)
        fun exercises(context: Context) = exercisesField.get(context)

        val dataList: List<StoredLiveData<*>> = listOf(
            userField,
            exercisesField
        )
    }
}
