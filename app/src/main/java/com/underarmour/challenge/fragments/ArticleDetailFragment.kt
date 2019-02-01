package com.underarmour.challenge.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.squareup.picasso.Picasso
import com.underarmour.challenge.R
import com.underarmour.network.model.Article
import com.underarmour.viewmodel.ArticleDetailViewModel
import com.underarmour.viewmodel.ArticleSharedViewModel
import kotlinx.android.synthetic.main.article_detail_fragment.*
import javax.inject.Inject

class ArticleDetailFragment: Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var sharedViewModel: ArticleSharedViewModel
    lateinit var detailViewModel: ArticleDetailViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?) = inflater.inflate(
        R.layout.article_detail_fragment,
        container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        shareButton.setOnClickListener {
            initiateSocialShare()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        detailViewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(ArticleDetailViewModel::class.java)

        detailViewModel.getContributorLiveData().observe(viewLifecycleOwner, articleAuthorObserver)
        detailViewModel.getSnippetLiveData().observe(viewLifecycleOwner, articleSnippetObserver)
        detailViewModel.getTitleLiveData().observe(viewLifecycleOwner, articleTitleObserver)
        detailViewModel.getUrlLiveData().observe(viewLifecycleOwner, articleImageUrlObserver)

        activity?.let {
            sharedViewModel = ViewModelProviders.of(it, viewModelFactory).get(ArticleSharedViewModel::class.java)

            sharedViewModel.selectedArticle.observe(viewLifecycleOwner, articleObserver)
        }
    }

    private val articleObserver = Observer<Article?> { article ->
        detailViewModel.article = article
    }

    private val articleSnippetObserver = Observer<String?> { snippet ->
        snippet?.let{
            article_detail_snippet.visibility = View.VISIBLE
            article_detail_snippet.text = snippet
        } ?: run { article_detail_snippet.visibility = View.GONE }

        shareButton.visibility = View.VISIBLE
    }

    private val articleImageUrlObserver = Observer<String?> {imageUrl ->
        imageUrl?.let {
            Picasso.get().load(imageUrl).into(article_detail_image)
        } ?: run { article_detail_image.visibility = View.GONE }
    }

    private val articleAuthorObserver = Observer<String?> { author ->
        author?.let{
            article_detail_contributor.visibility = View.VISIBLE
            article_detail_contributor.text = author
        } ?: run { article_detail_contributor.visibility = View.GONE }
    }

    private val articleTitleObserver = Observer<String?> { title ->
        title?.let{
            article_detail_title.visibility = View.VISIBLE
            article_detail_title.text = title
        } ?: run { article_detail_title.visibility = View.GONE }
    }

    private fun initiateSocialShare(){
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_email_subject))
            putExtra(
                Intent.EXTRA_TEXT, getString(R.string.share_body,
                    detailViewModel.getSocialShareUrl()))
        }

        activity?.startActivity(
            Intent.createChooser(shareIntent, getString(R.string.share_send_via))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    companion object {

        fun newInstance(): ArticleDetailFragment {
            return ArticleDetailFragment()
        }
    }

}