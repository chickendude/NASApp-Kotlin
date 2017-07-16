package ch.ralena.nasapp.adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import ch.ralena.nasapp.R
import ch.ralena.nasapp.fragments.cameraNames
import ch.ralena.nasapp.inflate

class CameraAdapter(var cameras: ArrayList<String>) :
		RecyclerView.Adapter<CameraAdapter.CameraViewHolder>() {

	override fun onBindViewHolder(holder: CameraViewHolder?, position: Int) {
		holder?.bindView(position)
	}

	override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): CameraViewHolder {
		val view = parent!!.inflate(R.layout.item_camera)
		return CameraViewHolder(view)
	}

	override fun getItemCount(): Int {
		return cameras.size
	}

	inner class CameraViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		val abbreviation: TextView
		val fullName: TextView

		init {
			abbreviation = itemView.findViewById(R.id.abbreviation)
			fullName = itemView.findViewById(R.id.fullName)
		}

		fun bindView(position: Int) {
			val cameraAbbr = cameras[position]
			abbreviation.text = cameraAbbr
			fullName.text = cameraNames[cameraAbbr]
		}
	}
}