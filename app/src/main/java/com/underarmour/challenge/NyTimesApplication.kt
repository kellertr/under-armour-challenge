package com.underarmour.challenge

import android.app.Activity
import android.app.Application
import android.util.Log
import com.underarmour.challenge.di.AppInjector
import com.underarmour.challenge.di.DaggerApplicationComponent
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.plugins.RxJavaPlugins
import javax.inject.Inject

/**
 * The NYTimesApplication class is a class that is utilized to spin up Dagger for our Application and Activity. This
 * class will also define a global RX error handler in the event that a disposable gets disposed before an error returns
 */
class NyTimesApplication : Application(), HasActivityInjector {

    val TAG = NyTimesApplication::class.java.simpleName

    //Add An Activity Injector so we can utilize Dagger Android for Activity/Fragment injection
    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Activity>

    override fun activityInjector(): AndroidInjector<Activity> {
        return androidInjector
    }

    override fun onCreate() {
        super.onCreate()

        DaggerApplicationComponent
            .builder()
            .application(this)
            .build()
            .inject(this)

        AppInjector.init(this)

        RxJavaPlugins.setErrorHandler {
            var throwable = it
            if (it is UndeliverableException) {
                throwable = throwable.cause
            }

            Log.e(TAG, "Handled unhandled RXJava network error From" +
                    " a disposable that has already been disposed", throwable)
        }
    }
}