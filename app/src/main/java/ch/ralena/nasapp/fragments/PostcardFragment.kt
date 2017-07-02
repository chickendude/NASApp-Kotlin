package ch.ralena.nasapp.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import ch.ralena.nasapp.R
import ch.ralena.nasapp.adapters.CameraSpinnerAdapter
import ch.ralena.nasapp.adapters.RoverSpinnerAdapter
import ch.ralena.nasapp.inflate
import kotlinx.android.synthetic.main.fragment_postcard.*
import org.jetbrains.anko.sdk25.coroutines.onClick

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

class PostcardFragment : Fragment() {

	override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return container?.inflate(R.layout.fragment_postcard)
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

		// buttons
		searchButton.onClick { newSearch() }
	}

	private fun newSearch() {
		val rover = roverSpinner.selectedItem.toString().toLowerCase()
		val camera = cameraSpinner.selectedItem.toString()
		val sol = if (dateText.text.toString() == "") 1 else Integer.parseInt(dateText.text.toString())

		val fragment = PostcardPickPhotoFragment()
		val arguments = Bundle()
		arguments.putString(KEY_ROVER, rover)
		arguments.putString(KEY_CAMERA, camera)
		arguments.putInt(KEY_SOL, sol)
		fragment.arguments = arguments

		fragmentManager.beginTransaction()
				.replace(R.id.fragmentContainer, fragment)
				.addToBackStack(null)
				.commit()
	}
}