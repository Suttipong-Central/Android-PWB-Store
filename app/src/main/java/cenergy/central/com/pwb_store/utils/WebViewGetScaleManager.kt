package cenergy.central.com.pwb_store.utils

import android.content.Context
import android.graphics.Point
import android.view.WindowManager

class WebViewGetScaleManager(val context: Context) {
    fun getScale(): Int {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val size = Point()
        display.getSize(size)
        val width = size.x
        val valueWidth = width.toDouble() / 800.toDouble()
        return (valueWidth * 100).toInt()
    }
}
