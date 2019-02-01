package com.underarmour.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.underarmour.network.model.Article
import javax.inject.Inject

class ArticleDetailViewModel @Inject constructor(): ViewModel() {

    fun getContributorLiveData(): LiveData<String> = showContributorLiveData
    fun getSnippetLiveData(): LiveData<String> = showSnippetLiveData
    fun getTitleLiveData(): LiveData<String> = showTitleLiveData
    fun getUrlLiveData(): LiveData<String> = showUrlLiveData


    private val showContributorLiveData = MutableLiveData<String>()
    private val showSnippetLiveData = MutableLiveData<String>()
    private val showTitleLiveData = MutableLiveData<String>()
    private val showUrlLiveData = MutableLiveData<String>()


    var article: Article? = null
        set(value) {
            if (field != value) {
                field = value
                updateArticleData()
            }
        }

    private fun updateArticleData(){
        showSnippetLiveData.value = article?.snippet
        showContributorLiveData.value = article?.byline?.original
        showTitleLiveData.value = article?.headline?.main

        if( !article?.multimedia.isNullOrEmpty() ) {
            showUrlLiveData.value = article?.multimedia?.get(0)?.url
        }
    }

    fun getSocialShareUrl(): String? = article?.web_url

}