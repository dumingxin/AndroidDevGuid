package name.dmx.androiddevguid.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.github.florent37.picassopalette.PicassoPalette
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_app_detail.*
import name.dmx.androiddevguid.R
import name.dmx.androiddevguid.adapter.AppInfoAdapter
import name.dmx.androiddevguid.http.transformer.SchedulerTransformer
import name.dmx.androiddevguid.model.AppInfo
import name.dmx.androiddevguid.model.RelationApkLib
import name.dmx.readhubclient.http.ListResult
import name.dmx.readhubclient.repository.DataRepository

/**
 * Created by dmx on 2017/12/25.
 */
class AppDetailActivity : AppCompatActivity() {
    private lateinit var appInfo: AppInfo
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_detail)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        appInfo = intent.getSerializableExtra(KEY_APP_INFO) as AppInfo
        initData(appInfo)
    }

    private fun initData(appInfo: AppInfo) {
        Picasso.with(this).load(appInfo.imgUrl)
                .into(sdvLauncher,
                        PicassoPalette.with(appInfo.imgUrl, sdvLauncher)
                                .use(PicassoPalette.Profile.VIBRANT).intoBackground(clTitleContainer, PicassoPalette.Swatch.RGB))
        tvName.text = appInfo.name
        ctl.title = appInfo.name
        supportActionBar?.title = appInfo.name
        tvDownloadCount.text = appInfo.downloadCountDescription
        tvUpdateTime.text = appInfo.updateTime

        val observable = DataRepository.getInstance(this).getLibListByApp(appInfo.packageName)
        observable.compose(SchedulerTransformer()).subscribe({ listResult: ListResult<RelationApkLib> ->
            val appInfoAdapter = AppInfoAdapter(this@AppDetailActivity, appInfo, listResult.results?.toList()!!)
            recyclerView.layoutManager = LinearLayoutManager(this@AppDetailActivity)
            recyclerView.adapter = appInfoAdapter
            appInfoAdapter.notifyDataSetChanged()
        }, { error ->
            error.printStackTrace()
        })
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