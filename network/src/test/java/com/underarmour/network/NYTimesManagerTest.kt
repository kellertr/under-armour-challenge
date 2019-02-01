package com.underarmour.network

import com.google.gson.Gson
import com.nhaarman.mockitokotlin2.*
import com.underarmour.network.util.TestUtil
import io.reactivex.Single
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class NYTimesManagerTest {

    lateinit var nyTimesManager: NYTimesManager
    val api: NYTimesApi = mock()
    val gson = Gson()

    @Before
    fun setup(){
        nyTimesManager = NYTimesManager(api)
    }

    @Test
    fun getArticlesTest() {

        val articles = gson.fromJson(
            TestUtil.getContentStringFromFile("/NYTimesLookup.json"),
            NYTimesResponseContainer::class.java)

        //Mock a response from the NYTimes api
        whenever( api.getArticles(any(), any(), any()) ).thenReturn( Single.just(articles) )

        nyTimesManager.getArticles(1, "under armour").test().assertNoErrors().assertValue { articleList ->

            //Validate that the correct mappings of the NYTimesApi class
            Assert.assertTrue("Incorrect article list size", articleList.size == 10  )

            articleList[0].apply {
                Assert.assertEquals( "Article web url incorrect",
                    "https://www.nytimes.com/2019/01/21/us/politics/giuliani-trump-tower-russia.html",
                    web_url)

                Assert.assertEquals( "Article source incorrect", "The New York Times", source)

                Assert.assertEquals("Article main headline not as expected",
                    "Giuliani Says His Moscow Trump Tower Comments Were ‘Hypothetical’",
                    headline?.main)

                Assert.assertEquals("Article print headline not as expected", "Giuliani Calls Comments On Trump ‘Hypothetical’",
                    headline?.print_headline)

                Assert.assertEquals("Article author not as expected","By MAGGIE HABERMAN" ,
                    byline?.original)
            }

            true
        }

        //Validate that the NYTimes manager has correct integration with the NYTimesApi class
        verify( api, times(1)).getArticles(any(), any(), any())
    }
}