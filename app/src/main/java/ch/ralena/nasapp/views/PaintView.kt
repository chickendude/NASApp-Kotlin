package ch.ralena.nasapp.views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import io.reactivex.subjects.PublishSubject
import org.jetbrains.anko.imageBitmap

class PaintView(context: Context, attributeSet: AttributeSet) : ImageView(context, attributeSet), View.OnTouchListener {
	companion object {
		// constants
		val ACTION_PAINT = "action_paint"
		val ACTION_TEXT = "action_text"
	}

	var coordMatrix: Matrix
	var downX: Float = 0f
	var downY: Float = 0f
	var upX: Float = 0f
	var upY: Float = 0f
	var canvas: Canvas = Canvas()
	val paint: Paint
	var textPaint: Paint
	var paintBitmap: Bitmap? = null
	var action: String = ACTION_PAINT

	val textSubject: PublishSubject<FloatArray> = PublishSubject.create()

	init {
		setOnTouchListener(this::onTouch)
		coordMatrix = Matrix()
		// drawing actionPaint
		paint = Paint()
		paint.isAntiAlias = true
		paint.isDither = true
		paint.color = Color.GREEN
		paint.style = Paint.Style.STROKE
		paint.strokeCap = Paint.Cap.ROUND
		paint.strokeWidth = 12f
		// actionPaint for actionText
		textPaint = Paint()
	}

	fun loadBitmap(bitmap: Bitmap) {
		val mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
		paintBitmap = mutableBitmap
		canvas = Canvas(mutableBitmap)
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

		when (action) {
			ACTION_PAINT -> actionPaint(event, coords)
			ACTION_TEXT -> actionText(coords)
		}

		return true
	}

	fun addText(textBitmap: Bitmap) {
		canvas.drawBitmap(textBitmap, Matrix(), null)
	}

	fun actionText(coords: FloatArray) {
		textSubject.onNext(coords)
	}

	private fun actionPaint(event: MotionEvent, coords: FloatArray) {
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
	}
}

//fun ViewManager.paintView(init: PaintView.() -> Unit = {}) =
//		ankoView({ PaintView(it) }, 0, init)