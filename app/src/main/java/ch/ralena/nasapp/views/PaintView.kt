package ch.ralena.nasapp.views

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.view.MotionEvent
import android.view.View
import android.view.ViewManager
import android.widget.ImageView
import org.jetbrains.anko.custom.ankoView
import org.jetbrains.anko.imageBitmap

class PaintView(context: Context) : ImageView(context), View.OnTouchListener {
	var downX: Float = 0f
	var downY: Float = 0f
	var upX: Float = 0f
	var upY: Float = 0f
	var canvas: Canvas? = null
	val paint: Paint

	init {
		setOnTouchListener(this::onTouch)
		paint = Paint()
		paint.isAntiAlias = true
		paint.isDither = true
		paint.color = 0xFFFF00
		paint.style = Paint.Style.STROKE
		paint.strokeCap = Paint.Cap.ROUND
		paint.strokeWidth = 12f
	}

	fun loadBitmap(bitmap: Bitmap) {
		val mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
		canvas = Canvas(mutableBitmap)
//		canvas.drawBitmap(bitmap, bitmap.width,bitmap.height,paint)
		imageBitmap = mutableBitmap
	}

	override fun onTouch(v: View?, event: MotionEvent?): Boolean {
		when (event?.action) {
			MotionEvent.ACTION_DOWN -> {
				downX = event.x
				downY = event.y
			}
			MotionEvent.ACTION_MOVE -> {
				upX = event.x
				upY = event.y
				canvas?.drawLine(downX, downY, upX, upY, paint)
				invalidate()
				downX = upX
				downY = upY
			}
			MotionEvent.ACTION_UP -> {
				upX = event.x
				upY = event.y
				canvas?.drawLine(downX, downY, upX, upY, paint)
				invalidate()
			}
		}
		return true
	}
}

fun ViewManager.paintView(init: PaintView.() -> Unit = {}) =
		ankoView({ PaintView(it) }, 0, init)