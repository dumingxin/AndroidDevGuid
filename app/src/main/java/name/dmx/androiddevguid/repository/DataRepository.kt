package name.dmx.androiddevguid.repository

import android.content.Context
import com.google.gson.GsonBuilder
import com.hzzh.baselibrary.net.DefaultOkHttpClient
import io.reactivex.Observable
import name.dmx.androiddevguid.http.Api
import name.dmx.androiddevguid.http.ListResult
import name.dmx.androiddevguid.model.AppInfo
import name.dmx.androiddevguid.model.LibInfo
import name.dmx.androiddevguid.model.RelationApkLib
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

    fun getTopAppList(keyword: String?, pageIndex: Int, pageSize: Int): Observable<ListResult<AppInfo>> {
        if (keyword == null) {
            val bql = "select * from app_info  limit ?,? order by downloadCount desc"
            val offset = pageIndex * pageSize
            val values = "[$offset,$pageSize]"
            return httpService.getAppList(bql, values)
        } else {
            val bql = "select * from app_info where name = ? limit ?,? order by downloadCount desc"
            val offset = pageIndex * pageSize
            val values = "[\'$keyword\',$offset,$pageSize]"
            return httpService.getAppList(bql, values)
        }

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

    /**
     * 根据lib库查询App列表
     */
    fun getAppListByLib(lib: String, pageIndex: Int, pageSize: Int): Observable<ListResult<AppInfo>> {
        val bql = "select * from r_apk_lib where libPackageName=? limit ?,?"
        val offset = pageIndex * pageSize
        val values = "[\'$lib\',$offset,$pageSize]"
        return httpService.getApk_LibList(bql, values).flatMap { list: ListResult<RelationApkLib> ->
            val size = list.results?.size!!
            if (size > 0) {
                val bql = "select * from app_info where packageName in (" + Array(size, { "?" }).joinToString(",") + ")"
                val sb = StringBuilder()
                if (list.results != null) {
                    for (item in list.results!!) {
                        sb.append(",\'" + item.apkPackageName + "\'")
                    }
                }
                val values = if (sb.isNotEmpty()) sb.substring(1) else sb.toString()
                return@flatMap httpService.getAppList(bql, "[$values]")
            } else {
                return@flatMap Observable.generate<ListResult<AppInfo>> { generator ->
                    generator.onNext(ListResult<AppInfo>())
                }
            }
        }
    }

    /**
     * 根据packageName查询App详情
     */
    fun getAppByPackageName(packageName: String): Observable<ListResult<AppInfo>> {
        val bql = "select * from app_info where packageName=?"
        val values = "[\'$packageName\']"
        return httpService.getAppList(bql, values)
    }

    /**
     * 根据packageName查询lib详情
     */
    fun getLibByPackageName(packageName: String): Observable<ListResult<LibInfo>> {
        val bql = "select count(*) from r_apk_lib where libPackageName=? group by libPackageName"
        val values = "[\'$packageName\']"
        return httpService.getApk_LibList(bql, values).flatMap { result ->
            val bql = "select * from lib_info where packageName=?"
            val count = result.results?.get(0)?._count
            return@flatMap httpService.getLibList(bql, values).map { result ->
                result.results?.get(0)?._count = count!!
                return@map result
            }
        }

    }

    /**
     * 应用搜索
     */
    fun searchAppByKeyword(keyword: String, pageIndex: Int, pageSize: Int): Observable<ListResult<AppInfo>> {
        val bql = "select * from app_info where name like \'%?%\' limit ?,? order by downloadCount desc"
        val offset = pageIndex * pageSize
        val values = "[\'$keyword\',$offset,$pageSize]"
        return httpService.getAppList(bql, values)
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