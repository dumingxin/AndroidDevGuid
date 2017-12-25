package name.dmx.readhubclient.repository

import android.content.Context
import com.google.gson.GsonBuilder
import com.hzzh.baselibrary.net.DefaultOkHttpClient
import io.reactivex.Observable
import name.dmx.androiddevguid.model.AppInfo
import name.dmx.androiddevguid.model.LibInfo
import name.dmx.androiddevguid.model.RelationApkLib
import name.dmx.readhubclient.http.Api
import name.dmx.readhubclient.http.ListResult
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by dmx on 17-11-7.
 */
class DataRepository private constructor(private val context: Context) {
    private val SERVER_ADDRESS = "https://api.bmob.cn/1/"
    private val httpService: Api

    init {
        val builder = Retrofit.Builder()
        builder.baseUrl(SERVER_ADDRESS)
        builder.client(DefaultOkHttpClient.getOkHttpClient(context))
        builder.addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
        builder.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        val retrofit = builder.build()
        httpService = retrofit.create(Api::class.java)
    }

    fun getTopAppList(pageIndex: Int, pageSize: Int): Observable<ListResult<AppInfo>> {
        val bql = "select * from app_info limit ?,? order by downloadCount desc"
        val offset = pageIndex * pageSize
        val values = "[$offset,$pageSize]"
        return httpService.getAppList(bql, values)
    }

    fun getTopLibList(pageIndex: Int, pageSize: Int): Observable<ListResult<LibInfo>> {
        val bql = "select count(*) as count from r_apk_lib group by libPackageName limit ?,? order by _count desc"
        val offset = pageIndex * pageSize
        val values = "[$offset,$pageSize]"
        return httpService.getApk_LibList(bql, values).flatMap { list: ListResult<RelationApkLib> ->
            val bql = "select * from lib_info where packageName in (" + Array(pageSize, { "?" }).joinToString(",") + ")"
            val mapLibCount = HashMap<String, Int>()
            val sb = StringBuilder()
            if (list.results != null) {
                for (item in list.results!!) {
                    sb.append(",\'" + item.libPackageName + "\'")
                    mapLibCount.put(item.libPackageName, item._count)
                }
            }
            val values = if (sb.isNotEmpty()) sb.substring(1) else sb.toString()
            return@flatMap httpService.getLibList(bql, "[$values]").map { t: ListResult<LibInfo> ->
                if (t.results != null) {
                    for (item in t.results!!) {
                        item._count = mapLibCount[item.packageName]!!
                    }
                }
                return@map t
            }
        }
    }

    /**
     * 获取App引用的lib
     */
    fun getLibListByApp(app: String): Observable<ListResult<RelationApkLib>> {
        val bql = "select * from r_apk_lib  where apkPackageName=?"
        val values = "[\'$app\']"
        return httpService.getApk_LibList(bql, values)
    }

    companion object {
        private var instance: DataRepository? = null
        fun getInstance(context: Context): DataRepository {
            if (instance == null) {
                synchronized(DataRepository::class.java) {
                    if (instance == null) {
                        instance = DataRepository(context)
                    }
                }
            }
            return instance!!
        }
    }
}