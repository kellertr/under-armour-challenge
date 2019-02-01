package com.underarmour.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.underarmour.network.model.Article
import javax.inject.Inject

class ArticleSharedViewModel @Inject constructor(): ViewModel() {
    val selectedArticle: MutableLiveData<Article> = MutableLiveData()
}