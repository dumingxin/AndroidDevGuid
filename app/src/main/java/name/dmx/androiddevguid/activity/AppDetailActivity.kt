package name.dmx.androiddevguid.activity

import android.content.Context
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_app_detail.*
import labelnet.cn.patterncolordemo.PaletteUtil
import name.dmx.androiddevguid.R
import name.dmx.androiddevguid.adapter.AppInfoAdapter
import name.dmx.androiddevguid.http.ListResult
import name.dmx.androiddevguid.http.transformer.SchedulerTransformer
import name.dmx.androiddevguid.listener.AppBarStateChangeListener
import name.dmx.androiddevguid.model.AppInfo
import name.dmx.androiddevguid.model.RelationApkLib
import name.dmx.androiddevguid.repository.DataRepository
/**
 * Created by dmx on 2017/12/25.
 */
class AppDetailActivity : AppCompatActivity(), Callback {

    private lateinit var appInfo: AppInfo
    private var statusBarColor = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_detail)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        appInfo = intent.getSerializableExtra(KEY_APP_INFO) as AppInfo
        initView()
        initData(appInfo)

    }

    private fun initView() {
        toolbar.setNavigationOnClickListener {
            this.finish()
        }
        //监听toolbar的折叠状态
        appBarLayout.addOnOffsetChangedListener(object : AppBarStateChangeListener() {
            override fun onStateChanged(appBarLayout: AppBarLayout, state: State) {
                if (state == State.COLLAPSED) {
                    collapsingToolbarLayout.setContentScrimColor(ContextCompat.getColor(this@AppDetailActivity, R.color.colorPrimary))
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        window.statusBarColor = ContextCompat.getColor(this@AppDetailActivity, R.color.colorPrimaryDark)
                    }
                } else {
                    collapsingToolbarLayout.setContentScrimColor(ContextCompat.getColor(this@AppDetailActivity, android.R.color.transparent))
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        window.statusBarColor = statusBarColor
                    }
                }
            }
        })
    }

    private fun initData(appInfo: AppInfo) {
        Picasso.with(this).load(appInfo.imgUrl)
                .into(sdvLauncher, this)
        tvName.text = appInfo.name
        collapsingToolbarLayout.title = appInfo.name
        tvDownloadCount.text = appInfo.downloadCountDescription
        tvUpdateTime.text = appInfo.updateTime

        val observable = DataRepository.getInstance(this).getLibListByApp(appInfo.packageName)
        observable.compose(SchedulerTransformer()).subscribe({ listResult: ListResult<RelationApkLib> ->
            val appInfoAdapter = AppInfoAdapter(this@AppDetailActivity, appInfo, listResult.results?.toList()!!)
            recyclerView.layoutManager = LinearLayoutManager(this@AppDetailActivity)
            recyclerView.adapter = appInfoAdapter
            appInfoAdapter.notifyDataSetChanged()
            appInfoAdapter.onItemClickListener = object : AppInfoAdapter.OnItemClickListener {
                override fun onItemClick(view: View, position: Int) {
                    val item = listResult.results?.get(position)
                    DataRepository.getInstance(this@AppDetailActivity).getLibByPackageName(item!!.libPackageName)
                            .compose(SchedulerTransformer())
                            .subscribe({ result ->
                                val intent = LibDetailActivity.makeIntent(this@AppDetailActivity, result.results?.get(0)!!, position)
                                this@AppDetailActivity.startActivity(intent)
                            })
                }

            }
        }, { error ->
            error.printStackTrace()
        })
    }

    override fun onSuccess() {
        val bitmap = (sdvLauncher.drawable as BitmapDrawable).bitmap
        PaletteUtil.instance.init(
                bitmap,
                object : PaletteUtil.PatternCallBack {
                    override fun onCallBack(drawable: Drawable, burnColor: Int) {
                        clTitleContainer.background = drawable
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            window.statusBarColor = burnColor
                            statusBarColor = burnColor
                        }
                    }
                }
        )
    }

    override fun onError() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {
        private val KEY_APP_INFO = "KEY_APP_INFO"
        fun makeIntent(context: Context, appInfo: AppInfo): Intent {
            val intent = Intent(context, AppDetailActivity::class.java)
            intent.putExtra(KEY_APP_INFO, appInfo)
            return intent
        }
    }
}