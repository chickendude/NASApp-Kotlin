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
import android.support.v4.content.FileProvider
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RelativeLayout
import ch.ralena.nasapp.R
import ch.ralena.nasapp.inflate
import ch.ralena.nasapp.objects.ImageAnnotation
import ch.ralena.nasapp.views.PaintView
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
	val annotations: ArrayList<ImageAnnotation> = ArrayList<ImageAnnotation>()
	val edittexts: ArrayList<EditText> = ArrayList<EditText>()

	override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
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
		paintImageView.textSubject.subscribe { coords -> addText(coords[0].toInt(), coords[1].toInt()) }
		createShareButton.onClick { shareImage() }
	}

	fun setupToolButtons() {
		paintButton.onClick { paintImageView.action = PaintView.ACTION_PAINT }
		textButton.onClick { paintImageView.action = PaintView.ACTION_TEXT }
	}

	private fun addText(touchX: Int, touchY: Int) {
		annotations.add(ImageAnnotation(touchX, touchY))
		val edittext = EditText(context)
		edittext.setOnFocusChangeListener { view, hasFocus ->
			if(!hasFocus) {
				edittext.clearFocus()
				if (edittext.text.toString() == "") {
					imageContainer.removeView(edittext)
				}
			}
		}
		val layoutParams = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
		layoutParams.setMargins(touchX, touchY, 0, 0)
		edittext.layoutParams = layoutParams
		imageContainer.addView(edittext)
		edittext.requestFocus()
		edittexts.add(edittext)
	}

	private fun shareImage() {
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
			intent.type = "text/html"
			intent.putExtra(Intent.EXTRA_SUBJECT, "Image from Mars Rover")
			intent.putExtra(Intent.EXTRA_TEXT, "Check out this image from NASA's Mars Rover!")
			intent.putExtra(Intent.EXTRA_STREAM, imageUri)
			activity.startActivity(intent)
		} catch (anf: ActivityNotFoundException) {
			toast("It appears you don't have an e-mail app set up.")
			anf.printStackTrace()
		}
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