package com.guillaumewilmot.swoleai

import android.content.SharedPreferences
import android.content.res.Resources
import android.preference.PreferenceManager
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.guillaumewilmot.swoleai.util.storage.rxlive.RxLiveStorage
import io.mockk.*
import org.junit.After
import org.junit.Before
import java.util.*

open class BaseUnitTest {

    protected lateinit var application: SwoleAiApplication
    protected lateinit var resources: Resources

    @Before
    fun initFields() {
        application = mockk(relaxed = true, relaxUnitFun = true)
        resources = mockk(relaxed = true, relaxUnitFun = true)
    }

    lateinit var dataHolder: RxLiveStorage.DataHolder

    @Before
    fun mockLiveStorage() {
        dataHolder = spyk(RxLiveStorage.DataHolder(), recordPrivateCalls = true)
        mockkObject(RxLiveStorage)
        every { RxLiveStorage.dataHolder } returns dataHolder
    }

    @Before
    fun mockSharedPreferences() {
        mockkStatic(PreferenceManager::class)

        val fakeStorage: MutableMap<String, String> = Collections.synchronizedMap(mutableMapOf())

        val listenerCaptor = CapturingSlot<SharedPreferences.OnSharedPreferenceChangeListener>()
        val mockPreferenceManager = mockk<SharedPreferences> {
            val getCaptor = CapturingSlot<String>()
            every { getString(capture(getCaptor), any()) } answers {
                fakeStorage[getCaptor.captured]
            }
            every { registerOnSharedPreferenceChangeListener(capture(listenerCaptor)) } just Runs
        }

        every { mockPreferenceManager.edit() } returns mockk {
            val putCaptorKey = CapturingSlot<String>()
            val putCaptorValue = CapturingSlot<String>()
            every { putString(capture(putCaptorKey), capture(putCaptorValue)) } answers {
                fakeStorage[putCaptorKey.captured] = putCaptorValue.captured
                listenerCaptor.captured.onSharedPreferenceChanged(mockPreferenceManager, putCaptorKey.captured)
                this@mockk
            }
            every { commit() } returns true
        }

        every { PreferenceManager.getDefaultSharedPreferences(any()) } returns mockPreferenceManager

        RxLiveStorage.registerUpdateListener(application)
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

    inline fun <reified T> fromJson(fileName: String): T = Gson().fromJson(
        readAsset(fileName), object : TypeToken<T>() {}.type
    )

    fun readAsset(fileName: String): String = this.javaClass.classLoader!!.getResourceAsStream(
        fileName
    ).bufferedReader().use {
        it.readText()
    }
}