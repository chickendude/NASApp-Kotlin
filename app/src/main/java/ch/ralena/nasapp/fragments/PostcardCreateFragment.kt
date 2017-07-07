package ch.ralena.nasapp.fragments

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v4.graphics.drawable.DrawableCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import ch.ralena.nasapp.R
import ch.ralena.nasapp.inflate
import ch.ralena.nasapp.views.PaintView
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import eltos.simpledialogfragment.SimpleDialog
import eltos.simpledialogfragment.color.SimpleColorDialog
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_postcardcreate.*
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.textColor
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


class PostcardCreateFragment : Fragment(), SimpleDialog.OnDialogResultListener {
	private val  TAG_PAINTCOLOR: String = "tag_paintcolor"
	private val  TAG_TEXTCOLOR: String = "tag_textcolor"
	val TAG = PostcardCreateFragment::class.java.simpleName
	val edittexts: ArrayList<EditText> = ArrayList<EditText>()
	val paintSizes: ArrayList<ImageView> = ArrayList<ImageView>()
	var textColor: Int = Color.BLACK
	var paintColor: Int = Color.BLACK
	val paintObservable: PublishSubject<Int> = PublishSubject.create()

	override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return container!!.inflate(R.layout.fragment_postcardcreate)
	}

	override fun onResume() {
		super.onResume()
		activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
	}

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
		val imageUrl = arguments.getString(KEY_IMAGE)
		val target: Target = object : com.squareup.picasso.Target {
			override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
			}

			override fun onBitmapFailed(errorDrawable: Drawable?) {
			}

			override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
				paintImageView.loadBitmap(bitmap!!, paintObservable)
			}
		}
		Picasso.with(context)
				.load(imageUrl)
				.into(target)
		setupToolButtons()
		textSizePicker.minValue = 8
		textSizePicker.maxValue = 50
		textSizePicker.value = 18
		// get correct coordinates for edittext
		paintImageView.textSubject.subscribe { coords -> addText(coords[0], coords[1]) }
		createShareButton.onClick { shareImage() }
	}

	fun setupToolButtons() {
		paintButton.onClick {
			clearAllFocus()
			// show paint tools/hide text tools
			paintToolsLayout.visibility = View.VISIBLE
			textToolsLayout.visibility = View.GONE
			// make sure paint button is highlighted
			updateColors(paintButton)
			paintImageView.action = PaintView.ACTION_PAINT
		}
		textButton.onClick {
			clearAllFocus()
			// show text tools and hide paint tools
			paintToolsLayout.visibility = View.GONE
			textToolsLayout.visibility = View.VISIBLE
			updateColors(textButton)
			paintImageView.action = PaintView.ACTION_TEXT
		}

		// load paint sizes
		paintSizes.addAll(arrayListOf(paintSizeSmall, paintSizeMediumSmall, paintSizeMediumLarge, paintSizeLarge))
		paintSizes.forEach {
			it.onClick {

			}
		}
		val fragment = this
		// set up color pickers
		paintColorPicker.onClick {
			SimpleColorDialog.build()
					.title("Paint Color")
					.colors(fragment.context, SimpleColorDialog.MATERIAL_COLOR_PALLET)
					.colorPreset(Color.RED)
					.allowCustom(true)
					.show(fragment, TAG_PAINTCOLOR)
		}
		textColorPicker.onClick {
			SimpleColorDialog.build()
					.title("Paint Color")
					.colors(fragment.context, SimpleColorDialog.MATERIAL_COLOR_PALLET)
					.colorPreset(Color.BLACK)
					.allowCustom(true)
					.show(fragment, TAG_TEXTCOLOR)
		}
	}

	private fun clearAllFocus() {
		edittexts.forEach { it.clearFocus() }
	}

	private fun updateColors(button: ImageView) {
		DrawableCompat.setTint(button.drawable, ContextCompat.getColor(getContext(), R.color.colorAccent))
		if (button != paintButton)
			DrawableCompat.setTint(paintButton.drawable, ContextCompat.getColor(getContext(), R.color.colorPrimary))
		if (button != textButton)
			DrawableCompat.setTint(textButton.drawable, ContextCompat.getColor(getContext(), R.color.colorPrimary))
	}

	private fun addText(touchX: Float, touchY: Float) {
		val edittext = EditText(context)
		// clear focus so that this edittext can receive it
		clearAllFocus()
		// clear focus from other edittexts when we click on this one
		edittext.onClick {
			clearAllFocus()
			edittext.requestFocus()
		}
		edittext.backgroundResource = 0	// remove underline from edittext
		edittext.textSize = textSizePicker.value.toFloat()
		edittext.textColor = textColor
		// if we lose focus and the edit text is empty, delete it
		edittext.setOnFocusChangeListener { view, hasFocus ->
			if (!hasFocus) {
				if (edittext.text.trim().toString() == "") {
					imageContainer.removeView(edittext)
					edittexts.remove(edittext)
				}
			}
		}
		// set up view's layout parameters
		val layoutParams = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
		layoutParams.setMargins(touchX.toInt(), touchY.toInt(), 0, 0)
		edittext.layoutParams = layoutParams
		// add edittext to the screen
		imageContainer.addView(edittext)
		edittext.requestFocus()
		val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

		edittexts.add(edittext)
	}

	private fun shareImage() {
		clearAllFocus()
		// save image and get path URI
		imageContainer.buildDrawingCache()
		val bitmap = Bitmap.createBitmap(imageContainer.drawingCache)
		val path = saveBitmap(bitmap)
		val f = File(path)
		val imageUri: Uri
		if (Build.VERSION.SDK_INT >= 24) {
			imageUri = FileProvider.getUriForFile(
					context,
					context.getApplicationContext().getPackageName() + ".provider",
					f)
		} else {
			imageUri = Uri.fromFile(f)
		}

		Log.d("TAG", imageUri.toString())

		// send share intent
		try {
			val intent = Intent(Intent.ACTION_SEND)
			intent.type = "actionText/html"
			intent.putExtra(Intent.EXTRA_SUBJECT, "Image from Mars Rover")
			intent.putExtra(Intent.EXTRA_TEXT, "Check out this image from NASA's Mars Rover!")
			intent.putExtra(Intent.EXTRA_STREAM, imageUri)
			activity.startActivity(intent)
		} catch (anfe: ActivityNotFoundException) {
			toast("It appears you don't have an e-mail app set up.")
			anfe.printStackTrace()
		}
	}

	private fun saveBitmap(bitmap: Bitmap): String {
		// Get path to External Storage
		val directory = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

		// Generating a random number to save as image name
		val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
		val filename = "Image_" + timeStamp + ".png"
		val file = File(directory, filename)
		if (file.exists()) {
			file.delete()
		}
		try {
			val out = FileOutputStream(file)
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
			out.flush()
			out.close()
		} catch (e: Exception) {
			e.printStackTrace()
		}

		// Tell your media scanner to refresh/scan for new images
		MediaScannerConnection.scanFile(context,
				arrayOf<String>(file.toString()), null,
				object : MediaScannerConnection.OnScanCompletedListener {
					override fun onScanCompleted(path: String, uri: Uri) {
						println(path)
					}
				})
		return file.getAbsolutePath()
	}

	override fun onResult(tag: String, which: Int, extras: Bundle): Boolean {
		if (which == SimpleColorDialog.BUTTON_POSITIVE && tag == TAG_PAINTCOLOR) {
			paintColor = extras.getInt(SimpleColorDialog.COLOR)
			paintObservable.onNext(paintColor)
			DrawableCompat.setTint(paintColorPicker.drawable, paintColor)
			return true
		} else if (which == SimpleColorDialog.BUTTON_POSITIVE && tag == TAG_TEXTCOLOR) {
			textColor = extras.getInt(SimpleColorDialog.COLOR)
			DrawableCompat.setTint(textColorPicker.drawable, textColor)
			return true
		}
		return false
	}

}