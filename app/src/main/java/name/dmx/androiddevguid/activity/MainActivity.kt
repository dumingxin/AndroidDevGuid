package name.dmx.androiddevguid.activity

import android.app.SearchManager
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import com.tencent.bugly.beta.Beta
import kotlinx.android.synthetic.main.activity_main.*
import name.dmx.androiddevguid.R
import name.dmx.androiddevguid.adapter.TabFragmentAdapter
import name.dmx.androiddevguid.fragment.AppFragment
import name.dmx.androiddevguid.fragment.LibFragment
import name.dmx.androiddevguid.viewmodel.AppViewModel


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var appFragment: AppFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        toolbar.setTitle(R.string.app_name)
        val actionBarToggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(actionBarToggle)
        navigationView.setNavigationItemSelectedListener(this)
        actionBarToggle.syncState()
        init()
    }

    private fun init() {
        val titleList = arrayListOf("应用排行", "框架排行")
        appFragment = AppFragment.newInstance()
        val fragmentList = arrayListOf(appFragment, LibFragment.newInstance())
        val tabFragmentAdapter = TabFragmentAdapter(supportFragmentManager, fragmentList, titleList)
        viewPager.adapter = tabFragmentAdapter
        tabLayout.setupWithViewPager(viewPager)
//        DataRepository.getInstance(this).getTopAppList(1,20)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.navigation_item_update -> {
                Beta.checkUpgrade()
            }
            R.id.navigation_item_readhub -> {
                startActivity(WebViewActivity.makeIntent(this, "https://github.com/dumingxin/AndroidDevGuid/blob/master/README.md", "关于应用", ""))
            }
            R.id.navigation_item_me->{
                startActivity(WebViewActivity.makeIntent(this, "https://github.com/dumingxin", "关于作者", ""))

            }
            else -> Snackbar.make(toolbar, item!!.title, Snackbar.LENGTH_SHORT).show()
        }
        drawerLayout.closeDrawers()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.ab_search).actionView as SearchView
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(componentName))
        // 设置搜索文本监听
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            // 当点击搜索按钮时触发该方法
            override fun onQueryTextSubmit(query: String): Boolean {
                val appViewModel = ViewModelProviders.of(appFragment).get(AppViewModel::class.java)
                appViewModel.keyword = query
                return false
            }

            // 当搜索内容改变时触发该方法
            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })
        searchView.setOnCloseListener {
            val appViewModel = ViewModelProviders.of(appFragment).get(AppViewModel::class.java)
            appViewModel.keyword = null
            return@setOnCloseListener false
        }
        return true
    }
}
