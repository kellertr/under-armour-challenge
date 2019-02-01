package com.underarmour.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.underarmour.network.model.Article
import javax.inject.Inject

/**
 * The article shared view model will manage any data that should be shared across multiple classes from within
 * the application
 */
class ArticleSharedViewModel @Inject constructor(): ViewModel() {
    val selectedArticle: MutableLiveData<Article> = MutableLiveData()
}