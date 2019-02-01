package com.underarmour.challenge

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.underarmour.challenge.fragments.search.ArticleSearchFragment
import com.underarmour.viewmodel.ArticleSharedViewModel
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_article.*
import javax.inject.Inject


class ArticleActivity : AppCompatActivity(), HasSupportFragmentInjector {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var sharedViewModel: ArticleSharedViewModel

    @Inject
    lateinit var mDispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>
    override fun supportFragmentInjector(): AndroidInjector<Fragment> = mDispatchingAndroidInjector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article)

        sharedViewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(ArticleSharedViewModel::class.java)


        if( savedInstanceState == null ){
            supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer,
                ArticleSearchFragment.newInstance()).commit()
        }

        setSupportActionBar(toolbar)
    }
}
