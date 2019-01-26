package com.underarmour.network

import io.reactivex.Single
import okhttp3.OkHttpClient
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import javax.security.cert.CertificateException

interface NYTimesApi {

    @GET("search/v2/articlesearch.json")
    fun getArticles(
        @Query("api-key") apiKey: String = API_KEY,
        @Query("q") searchTerm: String,
        @Query("page") page: String
    ): Single<NYTimesResponseContainer>

    companion object Factory {

        const val BASE_URL = "https://api.nytimes.com/svc/"
        const val API_KEY = "d31fe793adf546658bd67e2b6a7fd11a"

        /**
         * Create an instance of the FourSquareAPI utilizing RX and Gson.
         *
         * @return a new instance of the FourSquare API
         */
        fun create(): NYTimesApi {
            return create(BASE_URL)
        }

        /**
         * Create an instance of the FourSquareAPI utilizing RX and Gson.
         *
         * @param baseUrl is the baseURL for the FourSquareAPI
         * @return a new instance of the FourSqaure API
         */
        fun create( baseUrl: String ): NYTimesApi {

            //Trust all certificates, typically we would only want a limited subset of certificates
            //to prevent man in the middle attacks
            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                override fun getAcceptedIssuers(): Array<X509Certificate> {
                    return arrayOf()
                }

                @Throws(CertificateException::class)
                override fun checkClientTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {
                    //No-op
                }

                @Throws(CertificateException::class)
                override fun checkServerTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {
                    //No-op
                }
            })
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, java.security.SecureRandom())
            val okHttpBuilder = OkHttpClient.Builder()
            okHttpBuilder.sslSocketFactory(sslContext.socketFactory)
            okHttpBuilder.hostnameVerifier { _ , _ ->
                true
            }

            val retrofit = retrofit2.Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpBuilder.build())
                .baseUrl(baseUrl)
                .build()

            return retrofit.create(NYTimesApi::class.java)
        }
    }

}