package com.underarmour.network

import com.underarmour.network.model.Article
import io.reactivex.Single
import java.util.*
import javax.inject.Inject

/**
 * The NYTimesManager class will handle any business logic that is required when parsing the response that comes from the
 * NYTimesApi class.
 *
 * @param nyTimesApi is injected to interact with the NYTimes API utilizing retrofit
 */
class NYTimesManager @Inject constructor( val nyTimesApi: NYTimesApi ){

    /**
     * This method will return a Single that will manage interactionms with the FourSquareAPI. This single will emit a
     * list of articles to any calling classes.
     *
     * @param page the current page we are requesting data for
     * @param searchTerm the current search term we are searching the ny times for
     *
     *
     * @return a single that contains the list of articles returned from the NYTimes API
     */
    fun getArticles( page: Int, searchTerm: String ): Single<List<Article>> {
        return nyTimesApi.getArticles( page = page.toString(), searchTerm = searchTerm ).map { container ->
            return@map container.response?.docs ?: Collections.emptyList<Article>()
        }
    }
}

data class NYTimesResponseContainer( val response: NYTimesResponse? )
data class NYTimesResponse( val docs: List<Article>? )