package com.underarmour.network

import com.underarmour.network.util.TestUtil
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class NYTimesAPITest {

    lateinit var api: NYTimesApi
    private val mockWebServer = MockWebServer()

    @Before
    fun setup(){
        mockWebServer.start()

        //Initialize a custom instance of the nytimes api so we can utilize a mock web server and
        // avoid hitting apis real time
        val retrofit = retrofit2.Retrofit.Builder()
            .baseUrl(mockWebServer.url("").toString())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(NYTimesApi::class.java)
    }

    @After
    fun teardown(){
        mockWebServer.shutdown()
    }

    @Test
    fun articleLookupTest(){
        mockWebServer.enqueue(MockResponse().setBody(TestUtil.getContentStringFromFile("/NYTimesLookup.json")))

        val searchTerm = "searchTerm"
        val page = "1"
        api.getArticles( searchTerm = searchTerm, page = page ).test().assertNoErrors().assertValue { result ->
            Assert.assertNotNull( "Article lookup returns null", result )
            Assert.assertNotNull( "Article inner response is null", result.response )
            Assert.assertNotNull( "Article response docs are null", result.response?.docs )
            Assert.assertEquals("Article list size incorrect", 10, result.response?.docs?.size)

            val article = result.response?.docs?.get(0)

            Assert.assertEquals( "Article web url incorrect",
                "https://www.nytimes.com/2019/01/21/us/politics/giuliani-trump-tower-russia.html",
                 article?.web_url)

            Assert.assertEquals( "Article source incorrect", "The New York Times", article?.source)

            Assert.assertEquals("Article main headline not as expected",
                "Giuliani Says His Moscow Trump Tower Comments Were ‘Hypothetical’",
                article?.headline?.main)

            Assert.assertEquals("Article print headline not as expected", "Giuliani Calls Comments On Trump ‘Hypothetical’",
                article?.headline?.print_headline)

            Assert.assertEquals("Article author not as expected","By MAGGIE HABERMAN" ,
                article?.byline?.original)

            true
        }

        val request = mockWebServer.takeRequest()
        Assert.assertEquals( "Request method not equal", "GET", request.method )
        Assert.assertEquals("Request path not equal", "/search/v2/articlesearch.json?" +
                "api-key=d31fe793adf546658bd67e2b6a7fd11a&" +
                "q=$searchTerm&page=$page", request.path)
    }


}