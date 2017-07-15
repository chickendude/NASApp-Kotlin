package ch.ralena.nasapp.fragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.*
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v7.widget.ListPopupWindow
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import ch.ralena.nasapp.R
import ch.ralena.nasapp.adapters.SearchLocationArrayAdapter
import ch.ralena.nasapp.inflate
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.fragment_eyeinthesky.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import java.util.*

val KEY_LATITUDE = "key_latitude"
val KEY_LONGITUDE = "key_longitude"

class EyeInTheSkyFragment : Fragment() {
	val KEY_MAP_TARGET: String = "key_map_target"
	val REQUEST_FINE_LOCATION = 666

	var hasLocationPermisson = false
	var locationClient: FusedLocationProviderClient? = null
	var locationManager: LocationManager? = null
	var addresses: ArrayList<Address> = ArrayList()
	lateinit var searchAdapter: SearchLocationArrayAdapter
	var mapCameraTarget: LatLng? = null
	var searchThread: Thread = Thread()
	lateinit var popup: ListPopupWindow
	lateinit var geocoder: Geocoder

	override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val view = container!!.inflate(R.layout.fragment_eyeinthesky)
		searchAdapter = SearchLocationArrayAdapter(context, addresses)
		// set up location search results popup window
		popup = ListPopupWindow(context)
		popup.setDropDownGravity(GravityCompat.START)
		popup.anchorView = view.findViewById(R.id.locationSearchEdit)

		// create geocoder to transform search text into list of locations
		geocoder = Geocoder(context, Locale.getDefault())

		// have to manually call all of map view's 'on' methods otherwise it won't work
		view.findViewById<MapView>(R.id.mapView).onCreate(savedInstanceState)
		return view
	}

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)

		// set up autocompletetextview
		locationSearchEdit.addTextChangedListener(
				object : TextWatcher {
					override fun afterTextChanged(p0: Editable?) {}

					override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

					override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
						updateAddressList()
					}
				})

		// make sure edittext isn't covered by keyboard
		activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

		// set up location services and map
		locationClient = LocationServices.getFusedLocationProviderClient(context)
		locationManager = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
		MapsInitializer.initialize(context)

		// check if we have a saved location
		if (mapCameraTarget != null) {
			requestLocationPermission()
			setUpMyLocationButton()
			mapView.getMapAsync {
				it.moveCamera(CameraUpdateFactory.newLatLngZoom(mapCameraTarget, 12f))
			}
		} else {
			getLocation()
			setUpMyLocationButton()
		}
		satelliteImage.onClick { showImage() }
		searchButton.onClick { goToAddress() }
	}

	private fun goToAddress() {
		addresses.clear()
		addresses.addAll(geocoder.getFromLocationName(locationSearchEdit.text.toString(), 10) as ArrayList<Address>)

		// if we have at least one address, take first address and jump to that location on map
		if (addresses.size > 0) {
			val latitude = addresses[0].latitude
			val longitude = addresses[0].longitude
			mapView.getMapAsync { map ->
				map.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latitude, longitude), 12f))
			}

		}
	}


	private fun setUpMyLocationButton() {
		if (hasLocationPermisson) {
			mapView.getMapAsync { map ->
				map.isMyLocationEnabled = true
				map.setOnMyLocationButtonClickListener {
					getLocation()
					true
				}
			}
		}
	}

	private fun updateAddressList() {
		if (!searchThread.isAlive) {
			searchThread = Thread(Runnable {
				// load search text
				val searchText = locationSearchEdit.text.toString()

				// clear previous list and update with new list from geocoder
				addresses.clear()
				addresses.addAll(geocoder.getFromLocationName(searchText, 10) as ArrayList<Address>)

				// if we have one or more addresses, show the popup, otherwise close it
				if (addresses.size > 0) {
					// check if an item was clicked
					searchAdapter.observable.subscribe({
						val latitude = it.latitude
						val longitude = it.longitude
						mapView.getMapAsync { map ->
							map.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latitude, longitude), 12f))
						}
						popup.dismiss()
					})

					// update popup or create it if it is currently closed
					activity.runOnUiThread {
						searchAdapter.notifyDataSetChanged()
						if (popup.isShowing) {
							searchAdapter.notifyDataSetChanged()
						} else {
							popup = ListPopupWindow(context)
							popup.setAdapter(searchAdapter)
							popup.anchorView = locationSearchEdit
							popup.show()
						}
					}
				} else {
					// if there aren't any results, close the popup
					if (popup.isShowing) {
						activity.runOnUiThread {
							popup.dismiss()
							searchAdapter.notifyDataSetChanged()
						}
					}
				}
			})
			searchThread.start()
		}
	}

	private fun showImage() {
		mapView.getMapAsync { map ->
			mapCameraTarget = map.cameraPosition.target

			val fragment = EyeInTheSkyDetailFragment()
			val arguments = Bundle()
			arguments.putFloat(KEY_LATITUDE, mapCameraTarget!!.latitude.toFloat())
			arguments.putFloat(KEY_LONGITUDE, mapCameraTarget!!.longitude.toFloat())
			fragment.arguments = arguments

			// load fragment
			fragmentManager.beginTransaction()
					.replace(R.id.fragmentContainer, fragment)
					.addToBackStack(null)
					.commit()
		}
	}

	private fun getLocation() {
		// make sure we have permission to check user's location
		requestLocationPermission()
		// if we do have permission, we can use the user's location, otherwise ignore it.
		if (hasLocationPermisson) {
			locationManager?.requestSingleUpdate(LocationManager.GPS_PROVIDER, object : LocationListener {
				override fun onLocationChanged(location: Location?) {
					mapView?.getMapAsync { map ->
						map.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location!!.latitude, location.longitude), 12f))
					}
				}

				override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
				override fun onProviderEnabled(provider: String?) {}
				override fun onProviderDisabled(provider: String?) {}
			}, null)

			// if no location change, just go back to last known location
			locationClient?.lastLocation?.addOnSuccessListener {
				if (it != null) {
					val currentLocation = LatLng(it.latitude, it.longitude)
					mapView?.getMapAsync { map ->
						map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 12f))
					}
				}
			}
		}
	}

	private fun requestLocationPermission() {
		if (!hasLocationPermisson) {
			// check if this is Android M+
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
					// use fragment's requestPermissions method
					requestPermissions(
							arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
							REQUEST_FINE_LOCATION)
				} else {
					hasLocationPermisson = true
				}
			} else {
				// if it's < Android M, we should have location permission by default
				hasLocationPermisson = true
			}
		}
	}

	override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
		if (requestCode == REQUEST_FINE_LOCATION && grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
			hasLocationPermisson = true
			setUpMyLocationButton()
			// load current location and go there
			getLocation()
		}
	}

	override fun onResume() {
		super.onResume()
		mapView.onResume()
	}

	override fun onPause() {
		super.onPause()
		mapView.onPause()
	}

	override fun onDestroy() {
		super.onDestroy()
		mapView?.onDestroy()
	}

	override fun onLowMemory() {
		super.onLowMemory()
		mapView?.onLowMemory()
	}
}