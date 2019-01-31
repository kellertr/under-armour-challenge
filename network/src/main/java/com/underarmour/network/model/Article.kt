package com.underarmour.network.model

data class Article(val web_url: String?,
                   val snippet: String?,
                   val source: String?,
                   var multimedia: List<ArticleMultimedia>?,
                   val headline: ArticleHeadline?,
                   val byline: ArticleAuthor?,
                   val section_name: String?,
                   val subsectoinName: String?)