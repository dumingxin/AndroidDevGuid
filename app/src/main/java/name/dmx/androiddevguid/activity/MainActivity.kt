package name.dmx.androiddevguid.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v7.app.ActionBarDrawerToggle
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import name.dmx.androiddevguid.R
import name.dmx.androiddevguid.adapter.TabFragmentAdapter
import name.dmx.androiddevguid.fragment.AppFragment
import name.dmx.androiddevguid.fragment.EmptyFragment
import name.dmx.readhubclient.repository.DataRepository

class MainActivity : AppCompatActivity() , NavigationView.OnNavigationItemSelectedListener{

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
        val fragmentList = arrayListOf(AppFragment.newInstance(), EmptyFragment.newInstance())
        val tabFragmentAdapter = TabFragmentAdapter(supportFragmentManager, fragmentList, titleList)
        viewPager.adapter = tabFragmentAdapter
        tabLayout.setupWithViewPager(viewPager)
//        DataRepository.getInstance(this).getAppList(1,20)
    }
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}
