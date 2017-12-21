package name.dmx.androiddevguid

import android.app.Application
import android.content.Context
import com.facebook.drawee.backends.pipeline.Fresco
import com.scwang.smartrefresh.header.MaterialHeader
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.api.*
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.tencent.bugly.Bugly

/**
 * Created by dmx on 2017/12/12.
 */
class MyApplication : Application() {
    init {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreater(object : DefaultRefreshHeaderCreater {
            override fun createRefreshHeader(context: Context, layout: RefreshLayout): RefreshHeader {
                layout.setPrimaryColorsId(R.color.colorPrimary, android.R.color.white)//全局设置主题颜色
                return MaterialHeader(context)
            }
        })
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreater(object : DefaultRefreshFooterCreater {
            override fun createRefreshFooter(context: Context, layout: RefreshLayout): RefreshFooter {
                //指定为经典Footer，默认是 BallPulseFooter
                return ClassicsFooter(context).setDrawableSize(20f)
            }
        })
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        Fresco.initialize(this)
        Bugly.init(applicationContext, BuildConfig.buglyAppId, false)
    }

    companion object {
        lateinit var instance: MyApplication
    }
}