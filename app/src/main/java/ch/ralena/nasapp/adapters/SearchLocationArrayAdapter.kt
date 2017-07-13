package ch.ralena.nasapp.adapters

import android.content.Context
import android.location.Address
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import ch.ralena.nasapp.R
import ch.ralena.nasapp.inflate
import io.reactivex.subjects.PublishSubject
import org.jetbrains.anko.sdk25.coroutines.onClick

class SearchLocationArrayAdapter(ctx: Context, val addresses: ArrayList<Address>) : ArrayAdapter<Address>(ctx, R.layout.item_searchlocation, addresses) {

	val observable: PublishSubject<Address> = PublishSubject.create()

	override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
		var view: View? = convertView
		val holder: SearchViewHolder?

		if (view == null) {
			view = parent!!.inflate(R.layout.item_searchlocation)
			holder = SearchViewHolder(view)
			view.tag = holder
		} else {
			holder = convertView!!.tag as SearchViewHolder
		}
		holder.bindView(addresses[position])
		return view
	}

	inner class SearchViewHolder(itemView: View) {
		val itemView: View
		val featureName: TextView
		val subThoroughfareName: TextView
		val thoroughfareName: TextView
		val localityName: TextView
		val adminAreaName: TextView
		val postalCode: TextView
		val countryName: TextView

		init {
			this.itemView = itemView
			featureName = itemView.findViewById(R.id.featureName)
			subThoroughfareName = itemView.findViewById(R.id.subThoroughfareName)
			thoroughfareName = itemView.findViewById(R.id.thoroughfareName)
			localityName = itemView.findViewById(R.id.localityName)
			adminAreaName = itemView.findViewById(R.id.adminAreaName)
			postalCode = itemView.findViewById(R.id.postalCode)
			countryName = itemView.findViewById(R.id.countryName)
		}

		fun bindView(location: Address) {
			// pass clicks to fragment
			itemView.onClick { observable.onNext(location) }
			// load view values and hide empty views
			featureName.text = location.featureName
			featureName.visibility = if (featureName.text == "") View.GONE else View.VISIBLE
			subThoroughfareName.text = location.subThoroughfare
			subThoroughfareName.visibility = if (subThoroughfareName.text == "") View.GONE else View.VISIBLE
			thoroughfareName.text = location.thoroughfare
			thoroughfareName.visibility = if (thoroughfareName.text == "") View.GONE else View.VISIBLE
			localityName.text = if (location.locality == null) "" else location.locality + ","
			localityName.visibility = if (localityName.text == "") View.GONE else View.VISIBLE
			adminAreaName.text = location.adminArea
			adminAreaName.visibility = if (adminAreaName.text == "") View.GONE else View.VISIBLE
			postalCode.text = location.postalCode
			postalCode.visibility = if (postalCode.text == "") View.GONE else View.VISIBLE
			countryName.text = location.countryName
			countryName.visibility = if (countryName.text == "") View.GONE else View.VISIBLE
		}

	}

}