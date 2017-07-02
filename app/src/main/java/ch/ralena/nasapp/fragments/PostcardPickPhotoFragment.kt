package ch.ralena.nasapp.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.util.Log
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
		// set up recyclerview
		val adapter = PostcardPickPhotoAdapter(photos)
		recyclerView.adapter = adapter
		recyclerView.layoutManager = GridLayoutManager(context, 4)

		nasaApi.getPhotosBySol(rover, sol, camera)
				.enqueue(object : Callback<NasaResults> {
					override fun onResponse(call: Call<NasaResults>?, response: Response<NasaResults>?) {
						if (response!!.isSuccessful) {
							var nasaResults: NasaResults? = null
							nasaResults = response.body()
							if (nasaResults.photos.isNotEmpty()) {
								photos.clear()
								photos.addAll(nasaResults.photos)
								adapter.notifyDataSetChanged()
								photos.forEach { Log.d("PickPhotoFragment", it.img_src) }
							} else {
								toast("Your search didn't produce any results")
								fragmentManager.popBackStack()
							}
						} else {
							toast("There was an error retrieving the search results.")
						}
					}

					override fun onFailure(p0: Call<NasaResults>?, p1: Throwable?) {
						toast("There was an error retrieving the search results.")
					}
				})
	}
}