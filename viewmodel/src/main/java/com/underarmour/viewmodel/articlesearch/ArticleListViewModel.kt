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

const val PAGE_SIZE = 10

class ArticleListViewModel @Inject constructor(val nyTimesManager: NYTimesManager) : ViewModel() {

    val disposables: CompositeDisposable = CompositeDisposable()

    private val articles: MutableLiveData<List<Article>> = MutableLiveData()
    private val networkError: MutableLiveData<Boolean> = MutableLiveData()
    private val isLoadingData: MutableLiveData<Boolean> = MutableLiveData()

    private var pageNumber = 0
    private var currentSearchTerm: String = ""
    private var allDataLoaded = false

    fun getArticleLiveData(): LiveData<List<Article>> = articles
    fun getNetworkErrorLiveData(): LiveData<Boolean> = networkError
    fun getLoadingLiveData(): LiveData<Boolean> = isLoadingData



    //TO-DO Network Error, ConnectivityError

    public override fun onCleared() {
        disposables.dispose()
    }

    fun getArticles(searchTerm: String) {

        if( searchTerm.length > 2 ){
            return
        }

        if(currentSearchTerm != searchTerm){
            disposables.clear()
            pageNumber = 0
            currentSearchTerm = searchTerm
            allDataLoaded = false
        }

        if( allDataLoaded ){
            return
        }

        isLoadingData.value = true
        isLoadingData.value = false

        disposables.add(nyTimesManager.getArticles(pageNumber, searchTerm).subscribeOn( Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe(
                {
                    if( it.size < PAGE_SIZE ){
                        allDataLoaded = true
                    }

                    articles.value = it
                }, {
                    networkError.value = true
                    networkError.value = false
                }
            ))

        pageNumber++
    }

    fun loadMoreData(  ){
        if( allDataLoaded ) {
            getArticles(currentSearchTerm)
        }
    }

    fun reset(){
        pageNumber = 0
    }
}
