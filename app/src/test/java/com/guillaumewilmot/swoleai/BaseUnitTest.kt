package com.guillaumewilmot.swoleai

import android.content.res.Resources
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.guillaumewilmot.swoleai.util.storage.live.LiveStorage
import io.mockk.*
import org.junit.After
import org.junit.Before

open class BaseUnitTest {

    protected lateinit var application: SwoleAiApplication
    protected lateinit var resources: Resources

    @Before
    fun initFields() {
        application = mockk(relaxed = true, relaxUnitFun = true)
        resources = mockk(relaxed = true, relaxUnitFun = true)
    }

    @Before
    fun mockLiveStorage() {
        mockkObject(LiveStorage)
        val dataHolder = spyk(LiveStorage.DataHolder(), recordPrivateCalls = true)
        every { LiveStorage.dataHolder } returns dataHolder
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