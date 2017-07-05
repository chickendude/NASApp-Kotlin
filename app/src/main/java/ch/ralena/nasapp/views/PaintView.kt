package ch.ralena.nasapp.views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import org.jetbrains.anko.imageBitmap

class PaintView(context: Context, attributeSet: AttributeSet) : ImageView(context, attributeSet), View.OnTouchListener {
	var coordMatrix: Matrix
	var downX: Float = 0f
	var downY: Float = 0f
	var upX: Float = 0f
	var upY: Float = 0f
	var canvas: Canvas = Canvas()
	val paint: Paint
	val textPaint: Paint
	var paintBitmap: Bitmap? = null

	init {
		setOnTouchListener(this::onTouch)
		coordMatrix = Matrix()
		// drawing paint
		paint = Paint()
		paint.isAntiAlias = true
		paint.isDither = true
		paint.color = Color.GREEN
		paint.style = Paint.Style.STROKE
		paint.strokeCap = Paint.Cap.ROUND
		paint.strokeWidth = 12f
		// paint for text
		textPaint = Paint()
		textPaint.color = Color.YELLOW
		textPaint.textSize = 18f
	}

	fun loadBitmap(bitmap: Bitmap) {
		val mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
		paintBitmap = mutableBitmap
		canvas = Canvas(mutableBitmap)
//		canvas.drawBitmap(bitmap, coordMatrix, paint)
//		canvas.drawText("Wowwww", 20f, 20f, textPaint)
		imageBitmap = mutableBitmap
	}

	override fun onDraw(canvas: Canvas?) {
		super.onDraw(canvas)
	}

	override fun onTouch(v: View?, event: MotionEvent?): Boolean {
		val touchX = event!!.getX(event.actionIndex)
		val touchY = event.getY(event.actionIndex)
		val coords = floatArrayOf(touchX, touchY)
		imageMatrix.invert(coordMatrix)
		coordMatrix.postTranslate(scrollX.toFloat(), scrollY.toFloat())
		coordMatrix.mapPoints(coords)

		when (event.action) {
			MotionEvent.ACTION_DOWN -> {
				downX = coords[0]
				downY = coords[1]
			}
			MotionEvent.ACTION_MOVE -> {
				upX = coords[0]
				upY = coords[1]
				canvas.drawLine(downX, downY, upX, upY, paint)
				invalidate()
				downX = upX
				downY = upY
			}
			MotionEvent.ACTION_UP -> {
				upX = coords[0]
				upY = coords[1]
				canvas.drawLine(downX, downY, upX, upY, paint)
				invalidate()
			}
		}
		return true
	}
}

//fun ViewManager.paintView(init: PaintView.() -> Unit = {}) =
//		ankoView({ PaintView(it) }, 0, init)