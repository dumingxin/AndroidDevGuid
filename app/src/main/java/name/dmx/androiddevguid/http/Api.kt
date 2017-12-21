package name.dmx.readhubclient.http

import io.reactivex.Observable
import name.dmx.androiddevguid.model.AppInfo
import name.dmx.androiddevguid.model.LibInfo
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by dmx on 17-10-30.
 */
interface Api {
    /**
     * 查询App列表
     */
    @GET("cloudQuery")
    fun getAppList(@Query("bql") bql: String, @Query("values") values: String): Observable<ListResult<AppInfo>>
    /**
     * 查询Lib列表
     */
    @GET("cloudQuery")
    fun getLibList(@Query("bql") bql: String, @Query("values") values: String): Observable<ListResult<LibInfo>>
}