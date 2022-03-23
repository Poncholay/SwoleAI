package com.guillaumewilmot.swoleai.hilt

import android.content.Context
import com.guillaumewilmot.swoleai.util.fragmentBackstack.FragmentBackstack
import com.guillaumewilmot.swoleai.util.storage.DataStorage
import com.guillaumewilmot.swoleai.util.storage.DataStorageImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
@ExperimentalCoroutinesApi
class AppModule {

    @Module
    @InstallIn(SingletonComponent::class)
    abstract class DataStorageModule {

        @Binds
        abstract fun bindDataStorage(
            dataStorageImpl: DataStorageImpl
        ): DataStorage
    }

    @Module
    @InstallIn(SingletonComponent::class)
    object DataStorageImplModule {

        @Provides
        @Singleton
        fun provideDataStorageImpl(
            @ApplicationContext applicationContext: Context
        ): DataStorageImpl = DataStorageImpl(applicationContext)
    }

    @Module
    @InstallIn(SingletonComponent::class)
    object FragmentBackstackModule {

        @Provides
        @Singleton
        fun provideFragmentBackstack() = FragmentBackstack()
    }
}