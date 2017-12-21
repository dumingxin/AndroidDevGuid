package name.dmx.readhubclient.repository

import android.content.Context
import com.google.gson.GsonBuilder
import com.hzzh.baselibrary.net.DefaultOkHttpClient
import com.hzzh.baselibrary.net.transformer.SchedulerTransformer
import io.reactivex.Observable
import name.dmx.androiddevguid.model.AppInfo
import name.dmx.androiddevguid.model.LibInfo
import name.dmx.readhubclient.http.Api
import name.dmx.readhubclient.http.ListResult
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

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

    fun getAppList(pageIndex: Int, pageSize: Int):Observable<ListResult<AppInfo>> {
        val bql = "select * from app_info limit ?,? order by downloadCount desc"
        val offset = pageIndex * pageSize
        val values = "[$offset,$pageSize]"
        return httpService.getAppList(bql, values)
    }

    fun getLibList(pageIndex: Int,pageSize: Int):Observable<ListResult<LibInfo>>{
        val bql="select libPackageName,count(*) as count from r_lib_apk group by libPackageName limit ?,? order by count desc"
        val offset=pageIndex*pageSize
        val values = "[$offset,$pageSize]"
        return httpService.getLibList(bql, values)
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