package name.dmx.androiddevguid.http

import io.reactivex.Observable
import name.dmx.androiddevguid.model.AppInfo
import name.dmx.androiddevguid.model.LibInfo
import name.dmx.androiddevguid.model.RelationApkLib
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
     * 查询r_apk_lib关系表
     */
    @GET("cloudQuery")
    fun getApkLibList(@Query("bql") bql: String, @Query("values") values: String): Observable<ListResult<RelationApkLib>>

    /**
     * 查询Lib信息
     */
    @GET("cloudQuery")
    fun getLibList(@Query("bql") bql:String,@Query("values") values: String):Observable<ListResult<LibInfo>>
    /**
     * 查询统计信息
     */
    @GET("cloudQuery")
    fun getCount(@Query("bql") bql:String):Observable<ListResult<Map<String,String>>>
}