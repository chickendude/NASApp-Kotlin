package ch.ralena.nasapp.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import ch.ralena.nasapp.R
import ch.ralena.nasapp.adapters.CameraSpinnerAdapter
import ch.ralena.nasapp.adapters.roverNames
import ch.ralena.nasapp.inflate
import kotlinx.android.synthetic.main.fragment_postcard.*
import org.jetbrains.anko.support.v4.toast




class PostcardFragment : Fragment() {
	override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return container?.inflate(R.layout.fragment_postcard)
	}

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
		// set up camera spinner
		val adapter = CameraSpinnerAdapter(context, R.layout.item_rover, roverNames)
		roverSpinner.adapter = adapter
		roverSpinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
			override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
				toast(roverNames[position])
			}

			override fun onNothingSelected(parent: AdapterView<*>) {
			}
		})
	}
}