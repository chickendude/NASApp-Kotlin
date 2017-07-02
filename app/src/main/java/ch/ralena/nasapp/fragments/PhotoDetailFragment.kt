package ch.ralena.nasapp.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ch.ralena.nasapp.R
import ch.ralena.nasapp.inflate
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_photodetail.*

class PhotoDetailFragment : Fragment() {
	override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return container!!.inflate(R.layout.fragment_photodetail)
	}

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
		val imageUrl = arguments.getString(KEY_IMAGE)
		val roverNameText = arguments.getString(KEY_ROVER)
		val cameraNameText = arguments.getString(KEY_CAMERA)
		Picasso.with(context)
				.load(imageUrl)
				.into(imageView)
		roverName.text = roverNameText
		cameraName.text = cameraNameText
	}
}