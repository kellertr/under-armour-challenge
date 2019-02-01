package com.underarmour.viewmodel.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.underarmour.viewmodel.ArticleDetailViewModel
import com.underarmour.viewmodel.ArticleSharedViewModel
import com.underarmour.viewmodel.articlesearch.ArticleListViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

/**
 * The ViewModelModule is used to build the graph for the ViewModel layer. We will use multibinding to
 * bind all of our ViewModel classes. These viewModel classes will be utilized to create the
 * ViewModelFactory which will be injected in each fragment.
 */
@Module
abstract class ViewModelModule {
    @Binds
    abstract fun bindViewModelFactoryModule(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(ArticleSharedViewModel::class)
    abstract fun bindSharedtViewModel(viewModel: ArticleSharedViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ArticleListViewModel::class)
    abstract fun bindListlViewModel(viewModel: ArticleListViewModel): ViewModel


    @Binds
    @IntoMap
    @ViewModelKey(ArticleDetailViewModel::class)
    abstract fun bindArticleDetailViewModel(viewModel: ArticleDetailViewModel): ViewModel
}