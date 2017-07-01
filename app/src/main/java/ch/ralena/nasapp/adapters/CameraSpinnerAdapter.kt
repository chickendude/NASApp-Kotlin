package ch.ralena.nasapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import ch.ralena.nasapp.R
import org.jetbrains.anko.find
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.layoutInflater

class CameraSpinnerAdapter(context: Context, resource: Int, cameraNames: Array<String>) : ArrayAdapter<String>(context, resource, cameraNames) {
	companion object {
		val cameraNames: Array<String> = arrayOf("Curiosity", "Opportunity", "Spirit")
	}

	val cameraImages: IntArray = intArrayOf(R.drawable.curiosity, R.drawable.opportunity, R.drawable.spirit)

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
		val view = inflater!!.inflate(R.layout.item_camera, parent, false)
		val cameraName = view.find<TextView>(R.id.cameraName)
		val cameraImage = view.find<ImageView>(R.id.cameraImage)
		cameraName.text = cameraNames[position]
		cameraImage.imageResource = cameraImages[position]
		return view
	}
}