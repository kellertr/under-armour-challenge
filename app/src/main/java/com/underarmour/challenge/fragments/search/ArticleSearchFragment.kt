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
import com.underarmour.network.model.Article
import com.underarmour.viewmodel.articlesearch.ArticleListViewModel
import kotlinx.android.synthetic.main.article_search_fragment.*
import javax.inject.Inject

class ArticleSearchFragment: Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var articleListViewModel: ArticleListViewModel
    lateinit var adapter: ArticleListAdapter

    var clearOnNextSuccessfulLoad = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?) = inflater.inflate(R.layout.article_search_fragment,
        container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ArticleListAdapter()
        val manager = LinearLayoutManager(view.context)
        articleList.layoutManager = manager
        articleList.adapter = adapter

        articleList.addOnScrollListener( object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                articleListViewModel.loadMoreData()
            }
        })

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

        articleListViewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(ArticleListViewModel::class.java)

        articleListViewModel.getArticleLiveData().observe(viewLifecycleOwner, articleListUpdated)

    }

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

    private val articleListUpdated = Observer<List<Article>> { articles ->
        if( clearOnNextSuccessfulLoad ){
            clearOnNextSuccessfulLoad = false
            adapter.replaceAllData(articles)
        } else {
            adapter.addArticles( articles )
        }
    }

    companion object {

        fun newInstance(): ArticleSearchFragment {
            return ArticleSearchFragment()
        }
    }

}