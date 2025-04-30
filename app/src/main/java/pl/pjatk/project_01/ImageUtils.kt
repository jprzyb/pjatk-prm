package pl.pjatk.project_01

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import androidx.core.content.ContextCompat
import java.io.ByteArrayOutputStream
import androidx.core.graphics.createBitmap

object ImageUtils {
    const val width: Int = 120
    const val height: Int = 120

    fun iconToBitArray(context: Context, iconId: Int): ByteArray {
        val drawable = ContextCompat.getDrawable(context, iconId) ?: return ByteArray(0)
        val bitmap = createBitmap(width, height)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return imageToBitArray(bitmap)
    }

    fun imageToBitArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }

    fun bitArrayToBitmap(byteArray: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }
}
