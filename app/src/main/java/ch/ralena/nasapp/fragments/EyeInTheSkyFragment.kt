package ch.ralena.nasapp.fragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ch.ralena.nasapp.R
import ch.ralena.nasapp.inflate
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.fragment_eyeinthesky.*
import org.jetbrains.anko.sdk25.coroutines.onClick

val KEY_LATITUDE = "key_latitude"
val KEY_LONGITUDE = "key_longitude"

class EyeInTheSkyFragment : Fragment() {
	var hasLocationPermisson = false
	var locationClient: FusedLocationProviderClient? = null
	var locationManager: LocationManager? = null

	val REQUEST_FINE_LOCATION = 666
	override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val view = container!!.inflate(R.layout.fragment_eyeinthesky)
		view.findViewById<MapView>(R.id.mapView).onCreate(savedInstanceState)
		return view
	}

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
		locationClient = LocationServices.getFusedLocationProviderClient(context)
		locationManager = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
		MapsInitializer.initialize(context)
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
				ActivityCompat.requestPermissions(activity,
						arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
						REQUEST_FINE_LOCATION)
			} else {
				hasLocationPermisson = true
			}
		} else {
			hasLocationPermisson = true
		}
		if (hasLocationPermisson) {
			getLocation()
		}
		mapView.getMapAsync { map ->
			map.isMyLocationEnabled = true
			map.setOnMyLocationButtonClickListener {
				getLocation()
				true
			}
		}
		satelliteImage.onClick { showImage() }
	}

	private fun showImage() {
		mapView.getMapAsync { map ->
			val location = map.cameraPosition.target

			val fragment = EyeInTheSkyDetailFragment()
			val arguments = Bundle()
			arguments.putFloat(KEY_LATITUDE, location.latitude.toFloat())
			arguments.putFloat(KEY_LONGITUDE, location.longitude.toFloat())
			fragment.arguments = arguments

			// load fragment
			fragmentManager.beginTransaction()
					.replace(R.id.fragmentContainer, fragment)
					.addToBackStack(null)
					.commit()
		}
	}

	private fun getLocation() {
		locationManager?.requestSingleUpdate(LocationManager.GPS_PROVIDER, object : LocationListener {
			override fun onLocationChanged(location: Location?) {
				mapView.getMapAsync { map ->
					map.isMyLocationEnabled = true
					map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location!!.latitude, location.longitude), 12f))
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
				mapView.getMapAsync { map ->
					map.isMyLocationEnabled = true
					map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 12f))
				}
			}
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