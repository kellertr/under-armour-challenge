package com.underarmour.challenge.fragments.search

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.underarmour.challenge.R
import com.underarmour.challenge.fragments.ArticleDetailFragment
import com.underarmour.challenge.util.ConnectionUtils
import com.underarmour.challenge.util.FragmentRunner
import com.underarmour.network.model.Article
import com.underarmour.viewmodel.ArticleSharedViewModel
import com.underarmour.viewmodel.articlesearch.ArticleListViewModel
import kotlinx.android.synthetic.main.article_search_fragment.*
import javax.inject.Inject

/**
 * The ArticleSearchFragment is the home page of the application. From this page, a user can initiate a type-ahead
 * search to view a list of articles. This Fragment works hand in hand with the ArticleListViewModel and ArticleAdapter
 * for retrieving and displaying data in a list to users.
 *
 */
class ArticleSearchFragment: Fragment(), ArticleSelectedListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var sharedViewModel: ArticleSharedViewModel
    lateinit var articleListViewModel: ArticleListViewModel
    lateinit var adapter: ArticleListAdapter

    var clearOnNextSuccessfulLoad = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?) = inflater.inflate(R.layout.article_search_fragment,
        container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Build out our recycler view
        adapter = ArticleListAdapter(this)
        val manager = LinearLayoutManager(view.context)
        articleList.layoutManager = manager
        articleList.adapter = adapter

        //Add a scroll listener to our recycler view to trigger our lazy loading mechanism
        articleList.addOnScrollListener( object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                articleListViewModel.loadMoreData()
            }
        })

        //Add a text change listener to our article search SearchView to perform typeahead searches
        articleSearch.setOnQueryTextListener( object: SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    clearOnNextSuccessfulLoad = true
                    articleListViewModel.getArticles( it )
                }

                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    articleListViewModel.getArticles( it )
                }

                return true
            }
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //Build our viewmodels and establish observers when we have an activity
        articleListViewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(ArticleListViewModel::class.java)

        articleListViewModel.getArticleLiveData().observe(viewLifecycleOwner, articleListUpdated)
        articleListViewModel.getLoadingLiveData().observe(viewLifecycleOwner, loadingObserver)
        articleListViewModel.getNetworkErrorLiveData().observe(viewLifecycleOwner, networkErrorObserver)

        activity?.let {
            sharedViewModel = ViewModelProviders.of(it, viewModelFactory).get(ArticleSharedViewModel::class.java)
        }

    }

    /**
     * This helper method will display an internet connection dialog to the customer. The user will have an option
     * to cancel out of the dialog OR will be able to go to Settings to triage their connectivity issues
     */
    private fun showInternetConnectionDialog(){
        context?.let { appContext ->
            AlertDialog.Builder(appContext)
                .setTitle(getString(R.string.no_internet_connection_title))
                .setMessage(getString(R.string.no_internet_connection_message))
                .setPositiveButton(getString(R.string.settings)) { _, _ -> appContext.startActivity(Intent(Settings.ACTION_WIFI_SETTINGS)) }
                .setNegativeButton(getString(R.string.cancel)) { dialog, _ -> dialog.dismiss() }
                .create().also { it.show() }
        }
    }

    /**
     * This helper method will show an error dialog to the customer. If a user has no previous search results,
     * we will show this dialog that gives the customer the option to reload data or try a different search term
     * upon dismissing the AlertDialog
     */
    private fun showErrorDialog(){
        context?.let { appContext ->
            AlertDialog.Builder(appContext)
                .setTitle(getString(R.string.network_error_title))
                .setMessage(getString(R.string.network_error_body))
                .setPositiveButton(getString(R.string.retry)) { _, _ ->
                    articleListViewModel.reset()
                    articleListViewModel.loadMoreData()
                }
                .setNegativeButton(getString(R.string.cancel)) { dialog, _ -> dialog.dismiss() }
                .create().also { it.show() }
        }
    }

    //Build our observers out for observing LiveData objects provided by the ViewModels
    private val networkErrorObserver = Observer<Boolean> { hasError ->
        loadingBar.visibility = View.GONE

        if( hasError && !ConnectionUtils.isOnline(context) ){
            showInternetConnectionDialog()
        } else if( hasError && adapter.itemCount == 0 ){
            showErrorDialog()
        }
    }

    private val loadingObserver = Observer<Boolean> { isLoading ->
        if( isLoading && adapter.itemCount == 0 ){
            loadingBar.visibility = View.VISIBLE
        }
    }

    private val articleListUpdated = Observer<List<Article>> { articles ->

        loadingBar.visibility = View.GONE

        if( clearOnNextSuccessfulLoad ){
            clearOnNextSuccessfulLoad = false
            adapter.replaceAllData(articles)
        } else {
            adapter.addArticles( articles )
        }
    }

    /**
     * Interface method passed to ArticleAdapter that exposes which article was engaged with so we can navigate
     * to the appropriate article detail page. Also, we set the selected article of the shared view model in this
     * method so that the ArticleDetailFragment knows which article to display information for
     */
    override fun articleSelected(article: Article) {
        sharedViewModel.selectedArticle.value = article

        FragmentRunner.activateNewFragment(activity, ArticleDetailFragment.newInstance())
    }

    companion object {

        /**
         * @return a new instance of the ArticleSearchFragment
         */
        fun newInstance(): ArticleSearchFragment {
            return ArticleSearchFragment()
        }
    }

}