package com.guillaumewilmot.swoleai.util.storage.live

import androidx.lifecycle.Observer
import com.github.kxfeng.livedata.observeForeverFreshly
import com.guillaumewilmot.swoleai.BaseUnitTest
import com.guillaumewilmot.swoleai.model.UserModel
import com.guillaumewilmot.swoleai.util.storage.SimpleStorage
import com.guillaumewilmot.swoleai.util.storage.UserStorage
import com.guillaumewilmot.swoleai.util.storage.toStorage
import io.mockk.*
import org.hamcrest.CoreMatchers
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class LiveStorageTest : BaseUnitTest() {

//    @Rule
//    @JvmField
//    val instantExecutorRule = InstantTaskExecutorRule()

    private val mockUser = UserModel()
    private val nullUser: UserModel? = null

    private val mockExercises = listOf<Int>()

    private val userObserver: Observer<UserModel?> = spyk()
    private val exercisesObserver: Observer<List<Int>> = spyk()

    @Before
    fun setup() {
        mockkObject(SimpleStorage)

        every { SimpleStorage.user.user(any()) } returns mockUser
        val storeCaptor = CapturingSlot<Any>()
        every {
            SimpleStorage.toStorage(
                any(),
                any(),
                capture(storeCaptor)
            )
        } returns { storeCaptor.captured }

        mockLiveStorage()
    }

    @Test
    fun testInitialization() {
        val liveUser = LiveStorage.dataHolder.user(application)
        val liveSettings = LiveStorage.dataHolder.exercises(application)

        liveUser.observeForever(userObserver)
        liveSettings.observeForever(exercisesObserver)

        Assert.assertThat(liveUser.value, CoreMatchers.equalTo(mockUser))
        Assert.assertThat(liveSettings.value, CoreMatchers.equalTo(mockExercises))

        verifySequence {
            LiveStorage.dataHolder.user(application)
            LiveStorage.dataHolder.exercises(application)
            userObserver.onChanged(mockUser)
            exercisesObserver.onChanged(mockExercises)
        }
    }

    @Test
    fun testToStoragePropagation() {
        val liveUser = LiveStorage.dataHolder.user(application)
        val liveSettings = LiveStorage.dataHolder.exercises(application)

        liveUser.observeForeverFreshly(userObserver)
        liveSettings.observeForeverFreshly(exercisesObserver)

        nullUser.toStorage(application, UserStorage.USER)
        mockExercises.toStorage(application, UserStorage.EXERCISES)

        Assert.assertThat(liveUser.value, CoreMatchers.equalTo(nullUser))
        Assert.assertThat(liveSettings.value, CoreMatchers.equalTo(mockExercises))

        null.toStorage(application, UserStorage.USER)
        null.toStorage(application, UserStorage.EXERCISES)

        Assert.assertThat(liveUser.value, CoreMatchers.equalTo(null))
        Assert.assertThat(liveSettings.value?.size, CoreMatchers.equalTo(0))
        Assert.assertThat(liveSettings.value?.isEmpty(), CoreMatchers.equalTo(true))

        verifySequence {
            LiveStorage.dataHolder.user(application)
            LiveStorage.dataHolder.exercises(application)

            userObserver.onChanged(nullUser)

            exercisesObserver.onChanged(mockExercises)

            userObserver.onChanged(null)

            exercisesObserver.onChanged(listOf())
        }
    }
}