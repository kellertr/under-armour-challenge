package com.underarmour.challenge.di

import com.underarmour.challenge.ArticleActivity
import com.underarmour.challenge.fragments.ArticleDetailFragment
import com.underarmour.challenge.fragments.search.ArticleSearchFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class ActivitiesModule {

    @ContributesAndroidInjector
    abstract fun contributeArticleActivity(): ArticleActivity

    @ContributesAndroidInjector
    abstract fun contributeArticleSearchFragment(): ArticleSearchFragment

    @ContributesAndroidInjector
    abstract fun contributeArticleDetailFragment(): ArticleDetailFragment
}