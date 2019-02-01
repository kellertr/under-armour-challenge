package com.underarmour.challenge.fragments.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.underarmour.challenge.R
import com.underarmour.network.model.Article
import kotlinx.android.synthetic.main.article_row.view.*

class ArticleListAdapter(private val articleSelectedListener: ArticleSelectedListener): RecyclerView.Adapter<ArticleHolder>() {

    var articles: MutableList<Article> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleHolder =
         ArticleHolder(LayoutInflater.from(parent.context).inflate(R.layout.article_row, parent,false))

    override fun onBindViewHolder(holder: ArticleHolder, position: Int){
        val article = articles.get(position)
        holder.bind( article )
        holder.itemView.setOnClickListener {
            articleSelectedListener.articleSelected(article)
        }
    }

    override fun getItemCount(): Int = articles.size

    fun addArticles( newArticles: List<Article> ){
        articles.addAll(newArticles)
        notifyDataSetChanged()
    }

    fun replaceAllData( newArticles: List<Article> ){
        articles.clear()
        articles.addAll(newArticles)
        notifyDataSetChanged()
    }
}

class ArticleHolder(val view: View): RecyclerView.ViewHolder( view ){

    fun bind(article: Article?){
        itemView.article_title.text = article?.headline?.main?.trim()

        var category = article?.section_name

        article?.subsectoinName?.let {
            category = "$category-$it"
        }

        category?.let {
            itemView.article_category.text = it
        } ?: run { itemView.article_category.text = "" }

        val articleImageView = itemView.article_image
        val multimedia = article?.multimedia

        if( multimedia.isNullOrEmpty() ){
            return
        }

        multimedia?.get(0)?.url?.let { imageUrl ->
            articleImageView.visibility = View.VISIBLE
            Picasso.get().load( imageUrl ).into(articleImageView)
        } ?: run { articleImageView.visibility = View.GONE }
    }
}

interface ArticleSelectedListener{
    fun articleSelected( article: Article )
}