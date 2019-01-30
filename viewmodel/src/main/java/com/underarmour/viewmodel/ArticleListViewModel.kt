package com.underarmour.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.underarmour.network.NYTimesManager
import com.underarmour.network.model.Article
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class ArticleListViewModel @Inject constructor(val nyTimesManager: NYTimesManager ): ViewModel() {

    private val articleListLiveData = MutableLiveData<List<Article>>()
    fun getArticleListLiveData(): LiveData<List<Article>> = articleListLiveData

    //TO-DO Network Error, ConnectivityError

    protected val disposables = CompositeDisposable()

    // Dispose RxJava Subscribers
    public override fun onCleared() = disposables.clear()

    fun getArticles( searchTerm: String, page: Int ){

        disposables.add(nyTimesManager.getArticles(page, searchTerm).subscribeOn( Schedulers.io() )
            .observeOn( AndroidSchedulers.mainThread() ).subscribe( { articles ->
                articleListLiveData.value = articles
            }, {
                //TODO error handling
            } ))
    }

}