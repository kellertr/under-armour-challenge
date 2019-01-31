package com.underarmour.network

import com.underarmour.network.model.Article
import com.underarmour.network.model.ArticleMultimedia
import io.reactivex.Single
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

const val IMAGE_TYPE = "image"
const val IMAGE_SUB_TYPE = "xlarge"
const val NY_TIMES_IMAGE_URL_PREFIX = "https://static01.nyt.com/"

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
            val response = container.response?.docs?.filter { !it.headline?.main.isNullOrEmpty() }
                ?: Collections.emptyList<Article>()

            for( article in response ){
                val mutlimedia = article.multimedia?.filter { it.type == IMAGE_TYPE && it.subType == IMAGE_SUB_TYPE }
                    ?: Collections.emptyList<ArticleMultimedia>()

                for( image in mutlimedia ){
                    image.url = NY_TIMES_IMAGE_URL_PREFIX.plus(image.url)
                }

                article.multimedia = mutlimedia
            }

            return@map response
        }
    }
}

data class NYTimesResponseContainer( val response: NYTimesResponse? )
data class NYTimesResponse( val docs: List<Article>? )