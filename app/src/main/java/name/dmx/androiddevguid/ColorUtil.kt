package name.dmx.androiddevguid

import android.support.v4.graphics.ColorUtils
import java.util.*

/**
 * Created by dmx on 2017/12/22.
 */
object ColorUtil {
    /**
     * 根据字符串生成颜色值
     */
    fun generateColor(str: String): Int {
        val seed = strToLong(str)
        val random = Random()
        val value = random.nextInt(9)
        val choice: Float = if (value < 3) 0f else if (value < 6) (1 / 3.0).toFloat() else (2 / 3.0).toFloat()
        val h = generateDouble(seed, 0.02f, 0.31f) + choice
        val l = generateDouble(seed, 0.3f, 0.8f)
        val s = generateDouble(seed, 0.3f, 0.8f)
        println(str+":"+listOf(h, l, s).joinToString())
        return ColorUtils.HSLToColor(listOf(h, l, s).toFloatArray())
    }

    private fun strToLong(str: String): Long {
        var sum = 0
        for (ch in str) {
            sum += ch.toInt()
        }
        return sum.toLong()
    }

    private fun generateDouble(seed: Long, min: Float, max: Float): Float {
        val bound = max * 100
        val random = Random()
        val value = random.nextInt(bound.toInt())
        return min + value / 100.0f
    }
}