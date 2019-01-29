package com.underarmour.viewmodel.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.underarmour.viewmodel.ArticleListViewModel
import com.underarmour.viewmodel.ArticleSharedViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    abstract fun bindViewModelFactoryModule(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(ArticleListViewModel::class)
    abstract fun bindArticleListViewModel(viewModel: ArticleListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ArticleSharedViewModel::class)
    abstract fun bindArticleSharedViewModel(viewModel: ArticleSharedViewModel): ViewModel

}