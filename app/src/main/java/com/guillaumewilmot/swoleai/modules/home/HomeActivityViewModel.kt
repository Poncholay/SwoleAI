package com.guillaumewilmot.swoleai.modules.home

import android.app.Application
import android.util.Log
import com.guillaumewilmot.swoleai.controller.ParentViewModel
import com.guillaumewilmot.swoleai.model.UserModel
import com.guillaumewilmot.swoleai.util.storage.DataStorage
import com.guillaumewilmot.swoleai.util.storage.UserStorage
import com.guillaumewilmot.swoleai.util.storage.rxlive.RxLiveStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class HomeActivityViewModel @Inject constructor(
    application: Application,
    val dataStorage: DataStorage
) : ParentViewModel(application) {

    private val _user = RxLiveStorage.dataHolder.user(application)

    /**
     * UI
     */

    val redirectToOnboarding: Observable<Boolean> = _user
        .subscribeOn(AndroidSchedulers.mainThread())
        .observeOn(AndroidSchedulers.mainThread())
        .map { it.value == null }
//        .filter { true }
        .filter { false }

//    init {
//        tryStuff()
//    }

//    fun tryStuff() {
//        val o1 = dataStorage.dataHolder.userField.subscribe {
//            Log.e(name(), "User ${it.value.toString()}")
//        }
////        dataStorage.dataHolder.exercisesField.subscribe {
////            Log.e(name(), "Exercises $it")
////        }
//
//        dataStorage.toStorage(UserStorage.USER, UserModel(name = "Message 1"))
//        dataStorage.toStorage(UserStorage.USER, UserModel(name = "Message 2"))
//        dataStorage.toStorage(UserStorage.USER, UserModel(name = "Message 3"))
//        dataStorage.toStorage(UserStorage.USER, UserModel(name = "Message 4"))
//        dataStorage.toStorage(UserStorage.USER, UserModel(name = "Message 5"))
//        dataStorage.toStorage(UserStorage.USER, UserModel(name = "Message 6"))
//        dataStorage.toStorage(UserStorage.USER, UserModel(name = "Message 7"))
//        dataStorage.toStorage(UserStorage.USER, UserModel(name = "Message 8"))
//        dataStorage.toStorage(UserStorage.USER, UserModel(name = "Message 9"))
//            ?.subscribe(
//                {
//                    o1.dispose()
//                    val o2 = dataStorage.dataHolder.userField.subscribe {
//                        Log.e(name(), "Latest User ${it.value.toString()}")
//                    }
//                },
//                {}
//            )
//
////        dataStorage.toStorage(UserStorage.USER, UserModel(name = "Message 1"))
////        dataStorage.toStorage(UserStorage.USER, null)
////        dataStorage.toStorage(UserStorage.USER, UserModel(name = "Message 3"))
//
////        dataStorage.toStorage(UserStorage.EXERCISES, listOf(1, 2, 3))
////        dataStorage.toStorage(UserStorage.EXERCISES, null)
////        dataStorage.toStorage(UserStorage.EXERCISES, listOf(1, 2, 3, 4))
//    }
}

//.doOnNext { Log.d(name(), it.toJson()) }