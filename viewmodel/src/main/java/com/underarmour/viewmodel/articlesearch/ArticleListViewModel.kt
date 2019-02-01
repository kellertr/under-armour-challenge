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

/**
 * The Article ListView Model contains all interactions needed to display an Article List. This view model is
 * responsible for interacting with the NYTimesManager to expose articles to a calling class. It will also expose
 * the loading state as well as network error states.
 */
class ArticleListViewModel @Inject constructor(val nyTimesManager: NYTimesManager) : ViewModel() {

    val disposables: CompositeDisposable = CompositeDisposable()

    //Make the LiveData objects private so we don't accidentally update them outside of this class
    private val articles: MutableLiveData<List<Article>> = MutableLiveData()
    private val networkError: MutableLiveData<Boolean> = MutableLiveData()
    private val isLoadingData: MutableLiveData<Boolean> = MutableLiveData()

    private var pageNumber = 0
    private var currentSearchTerm: String = ""
    private var allDataLoaded = false

    //Getters for our private MutableLiveData objects
    fun getArticleLiveData(): LiveData<List<Article>> = articles
    fun getNetworkErrorLiveData(): LiveData<Boolean> = networkError
    fun getLoadingLiveData(): LiveData<Boolean> = isLoadingData


    public override fun onCleared() {
        //Dispose all pending disposable to avoid leaking this view model
        disposables.dispose()
    }

    /**
     * This method will interact with the NYTimesManage to retrieve a list of articles for a given page, upon
     * successfully retrieving articles, we will update our live data object so any observing classes receive our
     * updates. If we encounter an error, we will update the live data objects there as well.
     *
     * @param searchTerm is our new search term
     */
    fun getArticles(searchTerm: String) {

        //To give customers a chance to enter text before we try and load, we don't make an article search
        //request until they have entered 3 or more characters
        if( searchTerm.length > 2 ){
            return
        }

        //If we encounter a new search term, reset the page number, clear any pending transactions and re-enable
        //loading
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

    /**
     * This method is called to load more articles for the same search term
     */
    fun loadMoreData(  ){
        if( !allDataLoaded ) {
            getArticles(currentSearchTerm)
        }
    }

    /**
     * This method is called when we want to reset the page number and reload using the same search term
     */
    fun reset(){
        pageNumber = 0
    }
}
