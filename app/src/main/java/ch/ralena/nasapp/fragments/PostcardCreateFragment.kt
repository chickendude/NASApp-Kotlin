package ch.ralena.nasapp.fragments

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
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
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import ch.ralena.nasapp.R
import ch.ralena.nasapp.inflate
import ch.ralena.nasapp.objects.ImageAnnotation
import ch.ralena.nasapp.views.PaintView
import com.jakewharton.rxbinding2.widget.RxTextView
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.android.synthetic.main.fragment_postcardcreate.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.support.v4.toast
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


class PostcardCreateFragment : Fragment() {
	val TAG = PostcardCreateFragment::class.java.simpleName
	val edittexts: ArrayList<EditText> = ArrayList<EditText>()

	override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
		return container!!.inflate(R.layout.fragment_postcardcreate)
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
				paintImageView.loadBitmap(bitmap!!)
			}
		}
		Picasso.with(context)
				.load(imageUrl)
				.into(target)
		setupToolButtons()
		// get correct coordinates for edittext
		paintImageView.textSubject.subscribe { coords -> addText(coords[0], coords[1]) }
		createShareButton.onClick { shareImage() }
	}

	fun setupToolButtons() {
		paintButton.onClick {
			updateColors(paintButton)
			paintImageView.action = PaintView.ACTION_PAINT
		}
		textButton.onClick {
			updateColors(textButton)
			paintImageView.action = PaintView.ACTION_TEXT
		}
	}

	private fun updateColors(button: ImageView) {
		DrawableCompat.setTint(button.drawable, ContextCompat.getColor(getContext(), R.color.colorAccent))
		if (button != paintButton)
			DrawableCompat.setTint(paintButton.drawable, ContextCompat.getColor(getContext(), R.color.colorPrimary))
		if (button != textButton)
			DrawableCompat.setTint(textButton.drawable, ContextCompat.getColor(getContext(), R.color.colorPrimary))
	}

	private fun addText(touchX: Float, touchY: Float) {
		val imageAnnotation = ImageAnnotation(touchX, touchY)
		val edittext = EditText(context)
		// clear focus so that this edittext can receive it
		edittexts.forEach { it.clearFocus() }
		// clear focus from other edittexts when we click on this one
		edittext.onClick {
			edittexts.forEach { it.clearFocus() }
			edittext.requestFocus()
		}
		edittext.setOnFocusChangeListener { view, hasFocus ->
			if (!hasFocus) {
				edittext.clearFocus()
				if (edittext.text.toString() == "") {
					imageContainer.removeView(edittext)
				}
			}
		}
		val layoutParams = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
		layoutParams.setMargins(touchX.toInt(), touchY.toInt(), 0, 0)
		edittext.layoutParams = layoutParams
		imageContainer.addView(edittext)
		edittext.requestFocus()
		edittexts.add(edittext)
		// update annotations list
		RxTextView.textChanges(edittext)
				.subscribe { chars ->
					imageAnnotation.title = chars.toString()
				}
	}

	private fun shareImage() {
		addTextsToBitmap()
		// save image and get path URI
		val path = saveBitmap(paintImageView.paintBitmap!!)
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
		} catch (anf: ActivityNotFoundException) {
			toast("It appears you don't have an e-mail app set up.")
			anf.printStackTrace()
		}
	}

	private fun addTextsToBitmap() {
		edittexts
				.filterNot { it.text.trim() == "" }
				.forEach { paintImageView.addText(it.x, it.y, it.text.toString(), R.color.colorAccent, 30f) }
	}

	private fun saveBitmap(bitmap: Bitmap): String {
		// Get path to External Storage
		val directory = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

		// Generating a random number to save as image name
		val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
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
}