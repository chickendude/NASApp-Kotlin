package ch.ralena.nasapp.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.CardView
import android.support.v7.widget.GridLayoutManager
import android.transition.Slide
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import ch.ralena.nasapp.R
import ch.ralena.nasapp.adapters.CameraAdapter
import ch.ralena.nasapp.api.nasaApi
import ch.ralena.nasapp.inflate
import ch.ralena.nasapp.models.NasaManifestResults
import ch.ralena.nasapp.models.NasaResults
import kotlinx.android.synthetic.main.fragment_postcard.*
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.support.v4.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

val roverNames: Array<String> = arrayOf("Curiosity", "Opportunity", "Spirit")
val cameraNames: HashMap<String, String> = hashMapOf(
		"all" to "Search all cameras",
		"fhaz" to "Front Hazard Avoidance Camera",
		"rhaz" to "Rear Hazard Avoidance Camera",
		"mast" to "Mast Camera",
		"chemcam" to "Chemistry and Camera Complex",
		"mahli" to "Mars Hand Lens Imager",
		"mardi" to "Mars Descent Imager",
		"navcam" to "Navigation Camera",
		"pancam" to "Panoramic Camera",
		"minites" to "Miniature Thermal Emission Spectrometer (Mini-TES)")
val camerasCuriosity: ArrayList<String> = arrayListOf("all", "fhaz", "rhaz", "mast", "chemcam", "mahli", "mardi", "navcam")
val camerasOpportunity: ArrayList<String> = arrayListOf("all", "fhaz", "rhaz", "navcam", "pancam", "minites")
val camerasSpirit: ArrayList<String> = arrayListOf("all", "fhaz", "rhaz", "navcam", "pancam", "minites")

val KEY_CAMERA = "key_camera"
val KEY_SOL = "key_sol"
val KEY_ROVER = "key_rover"
val KEY_EARTHDATE = "key_earthdate"
val KEY_ISSOL = "key_issol"

class PostcardFragment : Fragment() {
	var earthDate: Calendar = Calendar.getInstance()
	val rovers: ArrayList<CardView> = ArrayList()
	var selectedRover: String? = null

	override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		Log.d("TAG", "onCreateView")
		return container?.inflate(R.layout.fragment_postcard)
	}

	override fun onPause() {
		super.onPause()
		activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
	}

	override fun onResume() {
		super.onResume()
		activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
	}

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)

		Log.d("TAG", "onactivitycreated")

		// set up cameras and camera spinner
		val cameras: ArrayList<String> = ArrayList(camerasOpportunity)
		val cameraAdapter = CameraAdapter(cameras)
		cameraRecyclerView.adapter = cameraAdapter
		cameraRecyclerView.layoutManager = GridLayoutManager(context, 3)


		// load rovers into arraylist and set up on click events
		rovers.clear()
		rovers.addAll(arrayListOf(opportunityLayout, spiritLayout, curiosityLayout))
		rovers.forEach {
			val bg = it.backgroundDrawable
			it.onClick {
				rovers.forEach { it.backgroundDrawable = bg }
				it!!.backgroundDrawable = activity.getDrawable(R.drawable.bg_edittext)

				cameras.clear()
				when (it) {
					curiosityLayout -> {
						selectedRover = "curiosity"
						cameras.addAll(camerasCuriosity)
					}
					opportunityLayout -> {
						selectedRover = "opportunity"
						cameras.addAll(camerasOpportunity)
					}
					spiritLayout -> {
						selectedRover = "spirit"
						cameras.addAll(camerasSpirit)
					}
				}
				cameraAdapter.notifyDataSetChanged()
			}
		}

		// when returning, make sure correct rover is selected
		if (selectedRover != null) {
			when (selectedRover) {
				"opportunity" -> rovers[0].callOnClick()
				"spirit" -> rovers[1].callOnClick()
				"curiosity" -> rovers[2].callOnClick()
			}
		} else
			rovers[0].callOnClick()

		// sol/date spinner
		solDateSpinner.adapter = object : ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, arrayOf("Martian Sol", "Earth Date")) {
			override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
				// remove extra padding from view
				val view = super.getView(position, convertView, parent)
				view!!.setPadding(0, view.paddingTop, 0, view.paddingBottom)
				return view
			}
		}
		solDateSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
			override fun onNothingSelected(view: AdapterView<*>?) {

			}

			override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
				if (position == 0) {
					// show sol and hide earth
					solDateText.visibility = View.VISIBLE
					earthDateText.visibility = View.INVISIBLE
				} else {
					// show earth/hide sol
					solDateText.visibility = View.INVISIBLE
					earthDateText.visibility = View.VISIBLE
				}
			}
		}

		// open date picker when clicking on earth date textview
		updateEarthDate()
		earthDateText.onClick {
			val dateFragment = DatePickerFragment()
			val arguments = Bundle()
			arguments.putSerializable(KEY_EARTHDATE, earthDate)
			dateFragment.arguments = arguments
			// subscribe for results from date picker and update textview
			dateFragment.observable.subscribe {
				earthDate = it
				updateEarthDate()
			}
			dateFragment.show(fragmentManager, null)
		}

		// default sol value
		solDateText.setText("1")

		// buttons
		searchButton.onClick { newSearch() }
		randomButton.onClick {
			val rand = Random()
			val roverIndex = rand.nextInt(roverNames.size)
			nasaApi.getRoverManifest(roverNames[roverIndex])
					.enqueue(object : Callback<NasaManifestResults> {
						override fun onFailure(p0: Call<NasaManifestResults>?, p1: Throwable?) {
							toast("The button_search failed.")
						}

						override fun onResponse(call: Call<NasaManifestResults>?, response: Response<NasaManifestResults>?) {
							if (response!!.isSuccessful) {
								// pull data from results
								val manifestResults = response.body().photo_manifest
								val rover = manifestResults.name
								val cameraObj = manifestResults.photos[rand.nextInt(manifestResults.photos.size)]
								val camera = cameraObj.cameras[rand.nextInt(cameraObj.cameras.size)]
								val sol = Integer.parseInt(cameraObj.sol)
								// now load the picture
								nasaApi.getPhotosBySol(rover, sol, camera)
										.enqueue(object : Callback<NasaResults> {
											override fun onResponse(call: Call<NasaResults>?, response: Response<NasaResults>?) {
												if (response!!.isSuccessful) {
													var nasaResults = response.body()
													if (nasaResults.photos.isNotEmpty()) {
														val photo = nasaResults.photos[rand.nextInt(nasaResults.photos.size)]
														// load detail fragment
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
													} else {
														toast("Search didn't produce any results")
													}
												} else {
													toast("There was an error retrieving the button_search results.")
												}
											}

											override fun onFailure(p0: Call<NasaResults>?, p1: Throwable?) {
												toast("There was an error retrieving the button_search results.")
											}
										})
							} else {
								toast("There was an error...")
							}
						}

					})
		}
	}

	private fun updateEarthDate() {
		val year = earthDate.get(Calendar.YEAR)
		val month = earthDate.get(Calendar.MONTH)
		val day = earthDate.get(Calendar.DAY_OF_MONTH)
		earthDateText.text = "$year-$month-$day"
	}

	private fun newSearch() {
		// pull data from spinners and edit texts
		val camera = ""//cameraRecyclerView.selectedItem.toString()
		val isSol = solDateSpinner.selectedItem.toString().toLowerCase().contains("sol")
		val sol = if (solDateText.text.toString() == "") 1 else Integer.parseInt(solDateText.text.toString())
		val date = if (earthDateText.text.toString() == "") "2000-1-1" else earthDateText.text.toString()

		val fragment = PostcardPickPhotoFragment()
		val arguments = Bundle()
		arguments.putString(KEY_ROVER, selectedRover)
		arguments.putString(KEY_CAMERA, if (camera.toLowerCase() == "all") null else camera)
		arguments.putInt(KEY_SOL, sol)
		arguments.putString(KEY_EARTHDATE, date)
		arguments.putBoolean(KEY_ISSOL, isSol)
		fragment.arguments = arguments
		// set up transitions
		val slide = Slide()
		slide.slideEdge = Gravity.LEFT

		fragment.enterTransition = slide
		exitTransition = slide

		fragmentManager.beginTransaction()
				.replace(R.id.fragmentContainer, fragment)
				.addToBackStack(null)
				.commit()
	}
}