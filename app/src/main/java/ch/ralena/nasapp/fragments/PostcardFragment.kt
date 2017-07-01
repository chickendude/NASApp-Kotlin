package ch.ralena.nasapp.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ch.ralena.nasapp.R
import ch.ralena.nasapp.adapters.CameraSpinnerAdapter
import ch.ralena.nasapp.inflate
import kotlinx.android.synthetic.main.fragment_postcard.*


class PostcardFragment : Fragment() {
	override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return container?.inflate(R.layout.fragment_postcard)
	}

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
		// set up camera spinner
		val adapter = CameraSpinnerAdapter(context, R.layout.item_camera, CameraSpinnerAdapter.cameraNames)
		cameraSpinner.adapter = adapter
	}
}