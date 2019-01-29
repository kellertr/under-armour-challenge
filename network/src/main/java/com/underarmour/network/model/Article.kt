package com.underarmour.network.model

data class Article(val web_url: String?,
                   val snippet: String?,
                   val source: String?,
                   val multimedia: List<ArticleMultimedia>?,
                   val headline: ArticleHeadline?,
                   val byline: ArticleAuthor?)