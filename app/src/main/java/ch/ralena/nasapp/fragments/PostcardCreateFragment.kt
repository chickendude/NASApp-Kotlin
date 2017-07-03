package ch.ralena.nasapp.fragments

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ch.ralena.nasapp.R
import ch.ralena.nasapp.inflate
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.android.synthetic.main.fragment_postcardcreate.*

class PostcardCreateFragment : Fragment() {
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
	}
}