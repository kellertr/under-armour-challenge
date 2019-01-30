package com.underarmour.challenge.di

import android.app.Application
import android.content.Context
import com.underarmour.network.di.NetworkModule
import com.underarmour.viewmodel.di.ViewModelModule
import dagger.Module
import dagger.Provides

@Module(includes = [ViewModelModule::class, ActivitiesModule::class, NetworkModule::class])
class ApplicationModule {

    @Provides
    fun providesContext(application: Application): Context = application.applicationContext
}