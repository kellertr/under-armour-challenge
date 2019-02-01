package com.underarmour.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.underarmour.network.model.Article
import javax.inject.Inject

/**
 * The ArticleDetailViewModel is used to expose specifi data from an article. This will prevent any UI classes from
 * managing business logic pertaining to an article. This class also exposes the article url for a customer to share
 * via a list of applicable applications.
 */
class ArticleDetailViewModel @Inject constructor(): ViewModel() {

    //Expose our private MutableLiveData objects to any observing classes
    fun getContributorLiveData(): LiveData<String> = showContributorLiveData
    fun getSnippetLiveData(): LiveData<String> = showSnippetLiveData
    fun getTitleLiveData(): LiveData<String> = showTitleLiveData
    fun getUrlLiveData(): LiveData<String> = showUrlLiveData

    //Hide these live data objects so observing classes don't accidentally overwrite this information
    private val showContributorLiveData = MutableLiveData<String>()
    private val showSnippetLiveData = MutableLiveData<String>()
    private val showTitleLiveData = MutableLiveData<String>()
    private val showUrlLiveData = MutableLiveData<String>()

    //The article thart we are exposing data for, upon setting of this field, we will update the live data objects
    //for our calling classes
    var article: Article? = null
        set(value) {
            if (field != value) {
                field = value
                updateArticleData()
            }
        }

    /**
     * This method will update article live data with a newly declared article object
     */
    private fun updateArticleData(){
        showSnippetLiveData.value = article?.snippet
        showContributorLiveData.value = article?.byline?.original
        showTitleLiveData.value = article?.headline?.main

        if( !article?.multimedia.isNullOrEmpty() ) {
            showUrlLiveData.value = article?.multimedia?.get(0)?.url
        }
    }

    /**
     * This method will expose the share url for a given article
     *
     * @return the NYTimes website article URL to be shared
     */
    fun getSocialShareUrl(): String? = article?.web_url

}