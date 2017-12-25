package name.dmx.androiddevguid.model

import java.io.Serializable

/**
 * App信息
 * Created by dmx on 17-12-20.
 */
data class AppInfo(val name: String, val packageName: String, val url: String, val imgUrl: String, val detail: String, val description: String, val downloadCount: Int) : Serializable {
    val downloadCountDescription: String
        get() =
            if (downloadCount >= 10000) {
                (downloadCount / 10000).toString() + "万下载"
            } else {
                downloadCount.toString() + "次下载"
            }
    val updateTime: String
        get() {
            val strArr = detail.split(" ")
            return if (strArr.size == 5) strArr[1] + " " + strArr[2] else strArr[1]
        }
}