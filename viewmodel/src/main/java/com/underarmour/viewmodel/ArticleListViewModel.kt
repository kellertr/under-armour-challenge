package com.underarmour.viewmodel

import androidx.lifecycle.ViewModel
import com.underarmour.network.NYTimesApi
import javax.inject.Inject

class ArticleListViewModel @Inject constructor( nyTimesApi: NYTimesApi ): ViewModel() {
}