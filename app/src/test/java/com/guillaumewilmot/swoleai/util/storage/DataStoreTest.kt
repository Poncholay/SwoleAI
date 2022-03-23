package com.guillaumewilmot.swoleai.util.storage

import com.guillaumewilmot.swoleai.BaseUnitTest
import org.junit.Test

class DataStoreTest : BaseUnitTest() {
    @Test
    fun testFakeDataStorage() {
        dataStorage.dataHolder.userField.subscribe()
    }
}