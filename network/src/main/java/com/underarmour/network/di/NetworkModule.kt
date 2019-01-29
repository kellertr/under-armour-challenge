package com.underarmour.network.di

import com.underarmour.network.NYTimesApi
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class NetworkModule {

    @Provides
    @Singleton
    fun provideNYTimesApi(): NYTimesApi = NYTimesApi.create()
}