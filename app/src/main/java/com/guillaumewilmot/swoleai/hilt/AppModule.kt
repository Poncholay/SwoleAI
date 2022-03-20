package com.guillaumewilmot.swoleai.hilt

import android.content.Context
import com.guillaumewilmot.swoleai.util.storage.DataStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi

@Module
@InstallIn(SingletonComponent::class)
@ExperimentalCoroutinesApi
class AppModule {

    @Module
    @InstallIn(SingletonComponent::class)
    object DataStorageModule {
        @Provides
        fun provideDataStorage(
            @ApplicationContext applicationContext: Context
        ): DataStorage = DataStorage(applicationContext)
    }
}