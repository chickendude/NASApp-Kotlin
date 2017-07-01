package ch.ralena.nasapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import ch.ralena.nasapp.R
import ch.ralena.nasapp.fragments.cameraNames
import org.jetbrains.anko.find
import org.jetbrains.anko.layoutInflater

class CameraSpinnerAdapter(context: Context, resource: Int, var cameras: ArrayList<String>) :
		ArrayAdapter<String>(context, resource, cameras) {

	var inflater: LayoutInflater? = null

	init {
		inflater = context.layoutInflater
	}

	override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
		return createView(parent, position)

	}

	override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
		return createView(parent, position)
	}

	private fun createView(parent: ViewGroup?, position: Int): View {
		// inflate view
		val view = inflater!!.inflate(R.layout.item_camera, parent, false)
		// set up views
		val abbreviation = view.find<TextView>(R.id.abbreviation)
		val fullName = view.find<TextView>(R.id.fullName)
		abbreviation.text = cameras[position].toUpperCase() + ":"
		fullName.text = cameraNames[cameras[position]]
		// done
		return view
	}
}