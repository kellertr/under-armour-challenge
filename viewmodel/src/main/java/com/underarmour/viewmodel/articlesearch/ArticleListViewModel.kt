package com.underarmour.viewmodel.articlesearch

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.underarmour.network.model.Article
import javax.inject.Inject
import com.underarmour.network.NYTimesManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class ArticleListViewModel @Inject constructor(val nyTimesManager: NYTimesManager) : ViewModel() {

    val disposables: CompositeDisposable = CompositeDisposable()

    private val articles: MutableLiveData<List<Article>> = MutableLiveData()

    private var pageNumber = 0
    private var currentSearchTerm: String = ""

    fun getArticleLiveData(): LiveData<List<Article>> = articles


    //TO-DO Network Error, ConnectivityError

    public override fun onCleared() {
        disposables.dispose()
    }

    fun getArticles(searchTerm: String) {

        if( searchTerm.length > 2 && currentSearchTerm != searchTerm){
            disposables.clear()
            pageNumber = 0
            currentSearchTerm = searchTerm
        }

        disposables.add(nyTimesManager.getArticles(pageNumber, searchTerm).subscribeOn( Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe(
                {
                    articles.value = it
                }, {
                    //TODO error live data
                }
            ))

        pageNumber++
    }

    fun loadMoreData(  ){
        getArticles( currentSearchTerm )
    }
}
