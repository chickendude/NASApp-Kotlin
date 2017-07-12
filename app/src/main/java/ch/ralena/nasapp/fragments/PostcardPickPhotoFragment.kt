package ch.ralena.nasapp.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ch.ralena.nasapp.R
import ch.ralena.nasapp.adapters.PostcardPickPhotoAdapter
import ch.ralena.nasapp.api.nasaApi
import ch.ralena.nasapp.inflate
import ch.ralena.nasapp.models.NasaResults
import ch.ralena.nasapp.models.Photo
import kotlinx.android.synthetic.main.fragment_postcardpickphoto.*
import org.jetbrains.anko.support.v4.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

val KEY_IMAGE = "key_image"
val BACKSTACK_PHOTOPICK = "backstack_photopick"

class PostcardPickPhotoFragment : Fragment() {
	lateinit var photos: ArrayList<Photo>

	override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		photos = arrayListOf()
		return container!!.inflate(R.layout.fragment_postcardpickphoto)
	}

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
		val rover = arguments.getString(KEY_ROVER)
		val camera = arguments.getString(KEY_CAMERA)
		val sol = arguments.getInt(KEY_SOL)
		val earthDate = arguments.getString(KEY_EARTHDATE)
		val isSol = arguments.getBoolean(KEY_ISSOL)

		// set up recyclerview
		val adapter = PostcardPickPhotoAdapter(photos)
		recyclerView.adapter = adapter
		recyclerView.layoutManager = GridLayoutManager(context, 4)
		adapter.subject.subscribe { photo -> loadPhoto(photo) }

		val apiCall: Call<NasaResults>
		if (isSol)
			apiCall = nasaApi.getPhotosBySol(rover, sol, camera)
		else
			apiCall = nasaApi.getPhotosByEarthDate(rover, earthDate, camera)
		// add callback to api call
		apiCall.enqueue(object : Callback<NasaResults> {
			override fun onResponse(call: Call<NasaResults>?, response: Response<NasaResults>?) {
				if (response!!.isSuccessful) {
					progressLayout.visibility = View.GONE
					var nasaResults: NasaResults? = null
					nasaResults = response.body()
					if (nasaResults.photos.isNotEmpty()) {
						photos.clear()
						photos.addAll(nasaResults.photos)
						adapter.notifyDataSetChanged()
					} else {
						toast("Your button_search didn't produce any results")
						fragmentManager.popBackStack()
					}
				} else {
					toast("There was an error retrieving the button_search results.")
				}
			}

			override fun onFailure(p0: Call<NasaResults>?, p1: Throwable?) {
				toast("There was an error retrieving the button_search results.")
			}
		})

	}

	private fun loadPhoto(photo: Photo) {
		val fragment = PhotoDetailFragment()
		val bundle = Bundle()
		bundle.putString(KEY_IMAGE, photo.img_src)
		bundle.putString(KEY_ROVER, photo.rover.name)
		bundle.putString(KEY_EARTHDATE, photo.earth_date)
		bundle.putString(KEY_CAMERA, photo.camera.full_name)
		fragment.arguments = bundle
		fragmentManager.beginTransaction()
				.replace(R.id.fragmentContainer, fragment, BACKSTACK_PHOTOPICK)
				.addToBackStack(BACKSTACK_PHOTOPICK)
				.commit()
	}
}