package cenergy.central.com.pwb_store.utils

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import java.util.*

object BarcodeUtils {

    private val WHITE = 0xFFFFFFFF.toInt()
    private const val TRANSPARENT = 0x00FFFFFF
    private const val BLACK = 0xFF000000.toInt()
    private const val WIDTH = 512
    private const val HEIGHT = 512

    fun createQRCode(str: String): Bitmap? {
        val result: BitMatrix
        try {
            val hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java)
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8")
            hints.put(EncodeHintType.MARGIN, 0)
            result = MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, WIDTH, HEIGHT, hints)
        } catch (iae: IllegalArgumentException) {
            // Unsupported format
            return null
        }

        val width = result.width
        val height = result.height
        val pixels = IntArray(width * height)
        for (y in 0 until height) {
            val offset = y * width
            for (x in 0 until width) {
                pixels[offset + x] = if (result.get(x, y)) BLACK else TRANSPARENT
            }
        }

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return bitmap
    }
}

