package name.dmx.androiddevguid.model

import java.io.Serializable

/**
 * 三方库信息
 * Created by dmx on 17-12-20.
 */
data class LibInfo(val packageName: String, var _count: Int, val description: String, val tag: String, val gitHubUrl: String?) : Serializable {
    val countDescription: String
        get() = _count.toString() + "次引用"
}