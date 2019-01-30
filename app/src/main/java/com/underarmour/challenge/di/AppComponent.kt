package com.underarmour.challenge.di

import android.app.Application
import com.underarmour.challenge.NyTimesApplication
import dagger.BindsInstance
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

/**
 * Dagger Component needed to build the Dagger Graph for required dependencies
 */
@Singleton
@Component(modules = [
    AndroidSupportInjectionModule::class,
    ApplicationModule::class])
interface ApplicationComponent {

    fun inject(app: NyTimesApplication)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): ApplicationComponent
    }
}