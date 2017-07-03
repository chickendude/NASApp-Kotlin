package ch.ralena.nasapp.fragments

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ch.ralena.nasapp.views.PaintView
import ch.ralena.nasapp.views.paintView
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import org.jetbrains.anko.support.v4.UI

class PostcardCreateFragment : Fragment() {
	lateinit var paintView: PaintView
	override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val ui = UI {
			paintView = paintView {}
		}.view
		var canvas = Canvas()

		val imageUrl = arguments.getString(KEY_IMAGE)
		val target: Target = object: com.squareup.picasso.Target {
			override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
			}

			override fun onBitmapFailed(errorDrawable: Drawable?) {
			}

			override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
				paintView.loadBitmap(bitmap!!)
			}
		}
		Picasso.with(context)
				.load(imageUrl)
				.into(target)
		return ui
	}
}