package com.guillaumewilmot.swoleai.rxlive

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.guillaumewilmot.swoleai.BaseUnitTest
import com.guillaumewilmot.swoleai.model.UserModel
import com.guillaumewilmot.swoleai.util.storage.UserStorage
import com.guillaumewilmot.swoleai.util.storage.rxlive.Optional
import com.guillaumewilmot.swoleai.util.storage.rxlive.RxLiveStorage
import com.guillaumewilmot.swoleai.util.storage.rxlive.asOptional
import com.guillaumewilmot.swoleai.util.storage.toStorage
import io.mockk.spyk
import io.mockk.verifySequence
import io.reactivex.rxjava3.core.Observer
import org.hamcrest.CoreMatchers
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

class RxLiveStorageTest : BaseUnitTest() {

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    private val mockUser: UserModel = UserModel()
    private val mockUserOptional: Optional<UserModel> = mockUser.asOptional()

    private val nullUser: UserModel? = null
    private val nullUserOptional: Optional<UserModel> = null.asOptional()

    private val mockExercises = listOf(1)

    private val userObserver: Observer<Optional<UserModel>> = spyk()
    private val exercisesObserver: Observer<List<Int>> = spyk()

    @Test
    fun testInitialization() {
        mockUser.toStorage(application, UserStorage.USER)
        mockExercises.toStorage(application, UserStorage.EXERCISES)

        val liveUser = RxLiveStorage.dataHolder.user(application)
        val liveExercises = RxLiveStorage.dataHolder.exercises(application)

        liveUser.subscribe(userObserver)
        liveExercises.subscribe(exercisesObserver)

        Assert.assertThat(liveUser.value, CoreMatchers.equalTo(mockUserOptional))
        Assert.assertThat(liveUser.value?.value, CoreMatchers.equalTo(mockUser))
        Assert.assertThat(liveExercises.value, CoreMatchers.equalTo(mockExercises))

        verifySequence {
            userObserver.onSubscribe(any())
            userObserver.onNext(mockUserOptional)

            exercisesObserver.onSubscribe(any())
            exercisesObserver.onNext(mockExercises)
        }
    }

    @Test
    fun testToStoragePropagation() {
        val liveUser = RxLiveStorage.dataHolder.user(application)
        val liveSettings = RxLiveStorage.dataHolder.exercises(application)

        Assert.assertThat(liveUser.value, CoreMatchers.equalTo(Optional(null)))
        Assert.assertThat(liveSettings.value, CoreMatchers.equalTo(listOf()))

        liveUser.subscribe(userObserver)
        liveSettings.subscribe(exercisesObserver)

        Assert.assertThat(liveUser.value, CoreMatchers.equalTo(Optional(null)))
        Assert.assertThat(liveSettings.value, CoreMatchers.equalTo(listOf()))

        nullUser.toStorage(application, UserStorage.USER)
        mockExercises.toStorage(application, UserStorage.EXERCISES)

        Assert.assertThat(liveUser.value, CoreMatchers.equalTo(nullUserOptional))
        Assert.assertThat(liveSettings.value, CoreMatchers.equalTo(mockExercises))

        null.toStorage(application, UserStorage.USER)
        null.toStorage(application, UserStorage.EXERCISES)

        Assert.assertThat(liveUser.value, CoreMatchers.equalTo(Optional(null)))
        Assert.assertThat(liveSettings.value?.size, CoreMatchers.equalTo(0))
        Assert.assertThat(liveSettings.value?.isEmpty(), CoreMatchers.equalTo(true))

        verifySequence {
            userObserver.onSubscribe(any())
            userObserver.onNext(Optional(null))

            exercisesObserver.onSubscribe(any())
            exercisesObserver.onNext(listOf())

            userObserver.onNext(nullUserOptional)

            //Not sure why this is needed...
            userObserver
            exercisesObserver

            exercisesObserver.onNext(mockExercises)
            userObserver.onNext(Optional(null))
            exercisesObserver.onNext(listOf())
        }
    }
}