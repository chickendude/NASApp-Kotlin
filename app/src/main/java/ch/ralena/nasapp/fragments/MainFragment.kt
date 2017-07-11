package ch.ralena.nasapp.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ch.ralena.nasapp.R
import ch.ralena.nasapp.inflate
import kotlinx.android.synthetic.main.fragment_main.*
import org.jetbrains.anko.sdk25.coroutines.onClick

class MainFragment : Fragment() {

	override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val view = container?.inflate(R.layout.fragment_main)

		return view
	}

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
		postcardButton.onClick { loadRoverPostcardFragment() }
		eyeButton.onClick { loadEyeInTheSkyFragment() }
	}

	private fun loadEyeInTheSkyFragment() {
		val fragment = EyeInTheSkyFragment()
		fragmentManager.beginTransaction()
				.replace(R.id.fragmentContainer, fragment)
				.addToBackStack(null)
				.commit()
	}

	private fun loadRoverPostcardFragment() {
		val fragment = PostcardFragment()
		fragmentManager.beginTransaction()
				.replace(R.id.fragmentContainer, fragment)
				.addToBackStack(null)
				.commit()
	}
}