package com.guillaumewilmot.swoleai

import android.content.res.Resources
import android.text.Editable
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.guillaumewilmot.swoleai.util.storage.DataStorage
import com.guillaumewilmot.swoleai.util.storage.DataStorageMock
import io.mockk.*
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.functions.Consumer
import io.reactivex.rxjava3.observers.TestObserver
import io.reactivex.rxjava3.subscribers.TestSubscriber
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before

@ExperimentalCoroutinesApi
open class BaseUnitTest {

    protected lateinit var application: SwoleAiApplication
    protected lateinit var resources: Resources
    protected lateinit var dataStorage: DataStorage

    @Before
    fun initFields() {
        application = mockk(relaxed = true, relaxUnitFun = true)
        resources = mockk(relaxed = true, relaxUnitFun = true)
        dataStorage = spyk(DataStorageMock())
    }

    @Before
    fun mockLog() {
        mockkStatic(Log::class)
        every { Log.e(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0
        every { Log.d(any(), any()) } returns 0
        every { Log.d(any(), any(), any()) } returns 0
        every { Log.i(any(), any()) } returns 0
        every { Log.i(any(), any(), any()) } returns 0
        every { Log.v(any(), any()) } returns 0
        every { Log.v(any(), any(), any()) } returns 0
        every { Log.w(any(), any<String>()) } returns 0
        every { Log.w(any(), any<Throwable>()) } returns 0
        every { Log.w(any(), any(), any()) } returns 0
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    /**
     * Helpers
     */

    inline fun <reified T> fromJson(fileName: String): T = Gson().fromJson(
        readAsset(fileName), object : TypeToken<T>() {}.type
    )

    protected fun editableMockk(s: String): Editable = mockk(relaxed = true) {
        every { this@mockk.toString() } returns s
    }

    fun readAsset(fileName: String): String = this.javaClass.classLoader!!.getResourceAsStream(
        fileName
    ).bufferedReader().use {
        it.readText()
    }

    /**
     * Rx helpers
     */

    fun <T : Any> testObserver(flowable: Flowable<T>) = TestSubscriber<T>().apply {
        flowable.subscribe(this)
    }

    fun <T : Any> testObserver(observable: Observable<T>) = TestObserver<T>().apply {
        observable.subscribe(this)
    }

    fun <T : Any> spyConsumer(flowable: Flowable<T>) = spyk(object : Consumer<T> {
        override fun accept(t: T) {

        }
    }).apply {
        flowable.subscribe(this)
    }

    fun <T : Any> spyConsumer(observable: Observable<T>) = spyk(object : Consumer<T> {
        override fun accept(t: T) {

        }
    }).apply {
        observable.subscribe(this)
    }
}