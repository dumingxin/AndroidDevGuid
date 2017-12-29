package name.dmx.androiddevguid.activity

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import kotlinx.android.synthetic.main.activity_lib_detail.*
import labelnet.cn.patterncolordemo.PaletteUtil
import name.dmx.androiddevguid.R
import name.dmx.androiddevguid.adapter.LibInfoAdapter
import name.dmx.androiddevguid.http.ListResult
import name.dmx.androiddevguid.http.transformer.SchedulerTransformer
import name.dmx.androiddevguid.listener.AppBarStateChangeListener
import name.dmx.androiddevguid.model.AppInfo
import name.dmx.androiddevguid.model.LibInfo
import name.dmx.androiddevguid.repository.DataRepository

/**
 * Created by dmx on 2017/12/26.
 */
class LibDetailActivity : AppCompatActivity() {
    private val PAGE_SIZE = 20

    private var colorIndex = 0
    private lateinit var libInfo: LibInfo
    private var pageIndex = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lib_detail)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        libInfo = intent.getSerializableExtra(KEY_LIB_INFO) as LibInfo
        colorIndex = intent.getIntExtra(KEY_COLOR_INDEX, 0)
        initView()
        initData(libInfo)
    }

    private fun initView() {
        toolbar.setNavigationOnClickListener {
            this.finish()
        }
        val colorArray = resources.getIntArray(R.array.randomColor)
        val color = colorArray[colorIndex % colorArray.size]
        val easyColor = PaletteUtil.instance.colorEasy(color)
        val burnColor = PaletteUtil.instance.colorBurn(color)
        clTitleContainer.background = PaletteUtil.instance.generateGradientDrawable(color, easyColor)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = burnColor
        }
        //监听toolbar的折叠状态
        appBarLayout.addOnOffsetChangedListener(object : AppBarStateChangeListener() {
            override fun onStateChanged(appBarLayout: AppBarLayout, state: State) {
                if (state == State.COLLAPSED) {
                    collapsingToolbarLayout.setContentScrimColor(ContextCompat.getColor(this@LibDetailActivity, R.color.colorPrimary))
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        window.statusBarColor = ContextCompat.getColor(this@LibDetailActivity, R.color.colorPrimaryDark)
                    }
                } else {
                    collapsingToolbarLayout.setContentScrimColor(ContextCompat.getColor(this@LibDetailActivity, android.R.color.transparent))
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        window.statusBarColor = burnColor
                    }
                }
            }
        })
        smartRefreshLayout.setOnLoadmoreListener {
            pageIndex++
            getAppListByLib(libInfo.packageName, pageIndex, PAGE_SIZE)
        }
    }

    private fun initData(lib: LibInfo) {
        tvName.text = lib.packageName
        collapsingToolbarLayout.title = lib.packageName
        tvDownloadCount.text = lib.countDescription
        getAppListByLib(lib.packageName, pageIndex, PAGE_SIZE)
    }

    private fun getAppListByLib(libPackageName: String, pageIndex: Int, pageSize: Int) {
        DataRepository.getInstance(this).getAppListByLib(libPackageName, pageIndex, pageSize)
                .compose(SchedulerTransformer())
                .subscribe({ listResult: ListResult<AppInfo> ->
                    if (pageIndex == 0) {
                        val libInfoAdapter = LibInfoAdapter(this@LibDetailActivity, libInfo, (listResult.results?.toList() as MutableList<AppInfo>?)!!)
                        recyclerView.layoutManager = LinearLayoutManager(this@LibDetailActivity)
                        recyclerView.adapter = libInfoAdapter
                        libInfoAdapter.notifyDataSetChanged()
                        libInfoAdapter.onItemClickListener = object : LibInfoAdapter.OnItemClickListener {
                            override fun onItemClick(view: View, position: Int) {
                                val item = libInfoAdapter.getItem(position)
                                DataRepository.getInstance(this@LibDetailActivity).getAppByPackageName(item?.packageName!!)
                                        .compose(SchedulerTransformer())
                                        .subscribe({ result ->
                                            val intent = AppDetailActivity.makeIntent(this@LibDetailActivity, result.results?.get(0)!!)
                                            this@LibDetailActivity.startActivity(intent)
                                        })
                            }

                        }
                    } else {
                        val libInfoAdapter = recyclerView.adapter as LibInfoAdapter
                        libInfoAdapter.appendAppList(listResult.results?.toList()!!)
                        libInfoAdapter.notifyDataSetChanged()
                        smartRefreshLayout.finishLoadmore()
                    }

                }, { error ->
                    error.printStackTrace()
                    smartRefreshLayout.finishLoadmore()
                })
    }

    companion object {
        val KEY_LIB_INFO = "KEY_LIB_INFO"
        val KEY_COLOR_INDEX = "KEY_COLOR_INDEX"
        fun makeIntent(context: Context, lib: LibInfo, position: Int): Intent {
            val intent = Intent(context, LibDetailActivity::class.java)
            intent.putExtra(KEY_LIB_INFO, lib)
            intent.putExtra(KEY_COLOR_INDEX, position)
            return intent
        }
    }
}