package ch.ralena.nasapp.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import ch.ralena.nasapp.R
import ch.ralena.nasapp.adapters.CameraSpinnerAdapter
import ch.ralena.nasapp.adapters.RoverSpinnerAdapter
import ch.ralena.nasapp.inflate
import kotlinx.android.synthetic.main.fragment_postcard.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import java.util.*

val roverNames: Array<String> = arrayOf("Curiosity", "Opportunity", "Spirit")
val cameraNames: HashMap<String, String> = hashMapOf(
		"fhaz" to "Front Hazard Avoidance Camera",
		"rhaz" to "Rear Hazard Avoidance Camera",
		"mast" to "Mast Camera",
		"chemcam" to "Chemistry and Camera Complex",
		"mahli" to "Mars Hand Lens Imager",
		"mardi" to "Mars Descent Imager",
		"navcam" to "Navigation Camera",
		"pancam" to "Panoramic Camera",
		"minites" to "Miniature Thermal Emission Spectrometer (Mini-TES)")
val camerasCuriosity: ArrayList<String> = arrayListOf("fhaz", "rhaz", "mast", "chemcam", "mahli", "mardi", "navcam")
val camerasOpportunity: ArrayList<String> = arrayListOf("fhaz", "rhaz", "navcam", "pancam", "minites")
val camerasSpirit: ArrayList<String> = arrayListOf("fhaz", "rhaz", "navcam", "pancam", "minites")

val KEY_CAMERA = "key_camera"
val KEY_SOL = "key_sol"
val KEY_ROVER = "key_rover"
val KEY_EARTHDATE = "key_earthdate"
val KEY_ISSOL = "key_issol"

class PostcardFragment : Fragment() {
	var earthDate: Calendar = Calendar.getInstance()

	override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
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
		val cameras: ArrayList<String> = ArrayList(camerasOpportunity)

		// camera spinner
		val cameraAdapter = CameraSpinnerAdapter(context, R.layout.item_camera, cameras)
		cameraSpinner.adapter = cameraAdapter

		// set up rover spinner
		val roverAdapter = RoverSpinnerAdapter(context, R.layout.item_rover, roverNames)
		roverSpinner.adapter = roverAdapter
		roverSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
			override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
				cameras.clear()
				when (roverNames[position]) {
					"Curiosity" -> cameras.addAll(camerasCuriosity)
					"Opportunity" -> cameras.addAll(camerasOpportunity)
					"Spirit" -> cameras.addAll(camerasSpirit)
					else -> cameras.addAll(camerasCuriosity)
				}
				cameraAdapter.notifyDataSetChanged()
			}

			override fun onNothingSelected(parent: AdapterView<*>) {}
		}

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
			override fun onNothingSelected(p0: AdapterView<*>?) {

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

		// buttons
		searchButton.onClick { newSearch() }
	}

	private fun updateEarthDate() {
		val year = earthDate.get(Calendar.YEAR)
		val month = earthDate.get(Calendar.MONTH)
		val day = earthDate.get(Calendar.DAY_OF_MONTH)
		earthDateText.text = "$year-$month-$day"
	}

	private fun newSearch() {
		val rover = roverSpinner.selectedItem.toString().toLowerCase()
		val camera = cameraSpinner.selectedItem.toString()
		val isSol = solDateSpinner.selectedItem.toString().toLowerCase().contains("sol")
		val sol = if (solDateText.text.toString() == "") 1 else Integer.parseInt(solDateText.text.toString())
		val date = if (earthDateText.text.toString() == "") "2000-1-1" else earthDateText.text.toString()

		val fragment = PostcardPickPhotoFragment()
		val arguments = Bundle()
		arguments.putString(KEY_ROVER, rover)
		arguments.putString(KEY_CAMERA, camera)
		arguments.putInt(KEY_SOL, sol)
		arguments.putString(KEY_EARTHDATE, date)
		arguments.putBoolean(KEY_ISSOL, isSol)
		fragment.arguments = arguments

		fragmentManager.beginTransaction()
				.replace(R.id.fragmentContainer, fragment)
				.addToBackStack(null)
				.commit()
	}
}