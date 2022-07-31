package com.guillaumewilmot.swoleai.hilt

import android.content.Context
import com.guillaumewilmot.swoleai.util.fragmentBackstack.FragmentBackstack
import com.guillaumewilmot.swoleai.util.fragmentBackstack.FragmentBackstackImpl
import com.guillaumewilmot.swoleai.util.storage.DataStorage
import com.guillaumewilmot.swoleai.util.storage.DataStorageImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
@ExperimentalCoroutinesApi
class AppModule {

    /**
     * DataStorage
     */

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

    /**
     * FragmentBackstack
     */

    @Module
    @InstallIn(ActivityRetainedComponent::class)
    abstract class FragmentBackstackModule {
        @Binds
        abstract fun bindFragmentBackstack(
            fragmentBackstackImpl: FragmentBackstackImpl
        ): FragmentBackstack
    }

    @Module
    @InstallIn(ActivityRetainedComponent::class)
    object FragmentBackstackImplModule {
        @Provides
        @ActivityRetainedScoped
        fun provideFragmentBackstackImpl(): FragmentBackstackImpl = FragmentBackstackImpl()
    }
}