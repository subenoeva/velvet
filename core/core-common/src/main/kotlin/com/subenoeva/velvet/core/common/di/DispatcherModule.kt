package com.subenoeva.velvet.core.common.di

import com.subenoeva.velvet.core.common.dispatcher.DispatcherProvider
import com.subenoeva.velvet.core.common.dispatcher.DefaultDispatcherProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DispatcherModule {

    @Binds
    @Singleton
    abstract fun bindDispatcherProvider(
        impl: DefaultDispatcherProvider
    ): DispatcherProvider
}