package ch.ralena.nasapp.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ch.ralena.nasapp.R
import ch.ralena.nasapp.api.nasaApi
import ch.ralena.nasapp.inflate
import ch.ralena.nasapp.models.NasaLocationAssetResults
import ch.ralena.nasapp.models.NasaLocationResults
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_eyeintheskydetail.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.support.v4.toast
import retrofit2.Call
import retrofit2.Response
import java.util.*

class EyeInTheSkyDetailFragment : Fragment() {
	var latitude: Float? = null
	var longitude: Float? = null
	var dates: ArrayList<String> = ArrayList()
	var curDateIndex: Int = 0

	override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return container!!.inflate(R.layout.fragment_eyeintheskydetail)
	}

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)

		// get passed in arguments
		latitude = arguments.getFloat(KEY_LATITUDE)
		longitude = arguments.getFloat(KEY_LONGITUDE)

		val calendar = Calendar.getInstance()
		calendar.add(Calendar.YEAR, -2)
		val year = calendar.get(Calendar.YEAR)
		val month = calendar.get(Calendar.MONTH) + 1
		val day = calendar.get(Calendar.DAY_OF_MONTH)
		val beginDate: String = String.format("%d-%d-%d", year, month, day)

		previousDateButton.onClick { loadImage(dates[--curDateIndex]) }
		nextDateButton.onClick { loadImage(dates[++curDateIndex]) }


		// load dates where an image was taken for this location
		nasaApi.getLocationAssets(latitude!!, longitude!!, beginDate)
				.enqueue(object : retrofit2.Callback<NasaLocationAssetResults> {
					override fun onResponse(call: Call<NasaLocationAssetResults>?, results: Response<NasaLocationAssetResults>?) {
						if (results!!.isSuccessful) {
							// load all the dates into our object
							dates.clear()
							results.body().results.forEach { dates.add(it.date) }
							curDateIndex = dates.size - 1
							// trim off everything after the T in the returned date string
							loadImage(dates.last())
						}
					}

					override fun onFailure(p0: Call<NasaLocationAssetResults>?, p1: Throwable?) {
						toast("Error connecting to NASA site...")
					}
				})
	}

	private fun loadImage(fullDate: String) {
		// make sure the progress bar is being shown
		progressLayout.visibility = View.VISIBLE
		progressMessage.text = "Loading image..."

		// check if next date button should be visible or not
		if (dates.last() == fullDate)
			nextDateButton.visibility = View.GONE
		else
			nextDateButton.visibility = View.VISIBLE

		// check if previous date button should be visible or not
		if (dates.first() == fullDate)
			previousDateButton.visibility = View.GONE
		else
			previousDateButton.visibility = View.VISIBLE

		val date = fullDate.substring(0, fullDate.indexOf('T'))

		// load date and image
		dateTakenLabel.text = date

		nasaApi.getLocationImage(latitude!!, longitude!!, date)
				.enqueue(object : retrofit2.Callback<NasaLocationResults> {
					override fun onFailure(call: Call<NasaLocationResults>?, p1: Throwable?) {
						toast("Error retrieving data...")
					}

					override fun onResponse(call: Call<NasaLocationResults>?, response: Response<NasaLocationResults>?) {
						if (response!!.isSuccessful) {
							loadApiResults(response.body())
						} else {
							toast("Failed to retrieve data...")
							fragmentManager.popBackStack()
						}
					}
				})
	}

	private fun loadApiResults(results: NasaLocationResults) {
		// get image url
		val imageUrl = results.url

		Picasso.with(context)
				.load(imageUrl)
				.into(imageView, object : Callback {
					override fun onSuccess() {
						progressLayout.visibility = View.GONE
					}

					override fun onError() {
						toast("There was an error loading the image...")
					}
				})
	}
}