package ch.ralena.nasapp.adapters

import android.graphics.drawable.Drawable
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import ch.ralena.nasapp.R
import ch.ralena.nasapp.fragments.cameraNames
import ch.ralena.nasapp.inflate
import io.reactivex.subjects.PublishSubject
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.sdk25.coroutines.onClick

class CameraAdapter(var cameras: ArrayList<String>) :
		RecyclerView.Adapter<CameraAdapter.CameraViewHolder>() {

	var currentlySelected: Int = 0
	var defaultForeground: Drawable? = null
	val observable: PublishSubject<String> = PublishSubject.create()

	override fun onBindViewHolder(holder: CameraViewHolder?, position: Int) {
		holder?.bindView(position)
	}

	override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): CameraViewHolder {
		val view = parent!!.inflate(R.layout.item_camera)
		defaultForeground = view.backgroundDrawable
		return CameraViewHolder(view)
	}

	override fun getItemCount(): Int {
		return cameras.size
	}

	inner class CameraViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		val abbreviation: TextView
		val fullName: TextView

		init {
			defaultForeground = (itemView as CardView).foreground
			abbreviation = itemView.findViewById(R.id.abbreviation)
			fullName = itemView.findViewById(R.id.fullName)
		}

		fun bindView(position: Int) {
			if (position == currentlySelected)
				(itemView as CardView).foreground = itemView.context.getDrawable(R.drawable.fg_camera)
			else
				(itemView as CardView).foreground = defaultForeground
			itemView.onClick {
				observable.onNext(cameras[position])
				currentlySelected = position
				notifyDataSetChanged()
			}

			val cameraAbbr = cameras[position]
			abbreviation.text = cameraAbbr
			fullName.text = cameraNames[cameraAbbr]
		}
	}
}