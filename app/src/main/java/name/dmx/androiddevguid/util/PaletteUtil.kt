package labelnet.cn.patterncolordemo

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.support.v7.graphics.Palette

/**
 * Created by yuan on 2016/8/28.
 * 获取图片背景色
 */
class PaletteUtil : Palette.PaletteAsyncListener {

    private var patternCallBack: PatternCallBack? = null

    @Synchronized
    fun init(bitmap: Bitmap, patternCallBack: PatternCallBack) {
        Palette.from(bitmap).generate(this)
        this.patternCallBack = patternCallBack
    }

    @Synchronized
    fun init(resources: Resources, resourceId: Int, patternCallBack: PatternCallBack) {
        val bitmap = BitmapFactory.decodeResource(resources, resourceId)
        Palette.from(bitmap).generate(this)
        this.patternCallBack = patternCallBack
    }

    @Synchronized
    override fun onGenerated(palette: Palette) {
        val a = palette.getVibrantSwatch()
        val b = palette.getLightVibrantSwatch()
        var colorEasy = 0
        if (b != null) {
            colorEasy = b!!.getRgb()
        }
        patternCallBack!!.onCallBack(generateGradientDrawable(a!!.getRgb(), colorEasy), colorBurn(a.rgb))
    }

    /**
     * 创建线性渐变的Drawable对象
     * @param RGBValues
     * @param two
     * @return
     */
    fun generateGradientDrawable(RGBValues: Int, two: Int): Drawable {
        var two = two
        if (two == 0) {
            two = colorEasy(RGBValues)
        } else {
            two = colorBurn(two)
        }
        val shape = GradientDrawable(GradientDrawable.Orientation.TL_BR, intArrayOf(RGBValues, two))
        shape.shape = GradientDrawable.RECTANGLE
        //设置渐变方式
        shape.gradientType = GradientDrawable.LINEAR_GRADIENT
        //圆角
        shape.cornerRadius = 8f
        return shape
    }


    /**
     * 颜色变浅处理
     * @param RGBValues
     * @return
     */
    fun colorEasy(RGBValues: Int): Int {
        var red = RGBValues shr 16 and 0xff
        var green = RGBValues shr 8 and 0xff
        var blue = RGBValues and 0xff
        if (red == 0) {
            red = 10
        }
        if (green == 0) {
            green = 10
        }
        if (blue == 0) {
            blue = 10
        }
        red = Math.floor(red * (1 + 0.1)).toInt()
        green = Math.floor(green * (1 + 0.1)).toInt()
        blue = Math.floor(blue * (1 + 0.1)).toInt()
        return Color.rgb(red, green, blue)
    }

    /**
     * 颜色加深处理
     * @param RGBValues
     * @return
     */
    fun colorBurn(RGBValues: Int): Int {
        var red = RGBValues shr 16 and 0xff
        var green = RGBValues shr 8 and 0xff
        var blue = RGBValues and 0xff
        red = Math.floor(red * (1 - 0.1)).toInt()
        green = Math.floor(green * (1 - 0.1)).toInt()
        blue = Math.floor(blue * (1 - 0.1)).toInt()
        return Color.rgb(red, green, blue)
    }


    interface PatternCallBack {
        fun onCallBack(drawable: Drawable, burnColor: Int)
    }

    companion object {
        val instance: PaletteUtil = PaletteUtil()
    }

}
