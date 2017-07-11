package ch.ralena.nasapp.fragments

import android.Manifest
import android.content.pm.PackageManager
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
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.fragment_eyeinthesky.*
import org.jetbrains.anko.support.v4.toast

class EyeInTheSkyFragment : Fragment() {
	var hasLocationPermisson = false

	val REQUEST_FINE_LOCATION = 666
	override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val view = container!!.inflate(R.layout.fragment_eyeinthesky)
		view.findViewById<MapView>(R.id.mapView).onCreate(savedInstanceState)
		return view
	}

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
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
			mapView.getMapAsync { map ->
				val sydney = LatLng(-33.867, 151.206)
				map.isMyLocationEnabled = true
				map.moveCamera(CameraUpdateFactory.newLatLng(sydney))
			}
		} else {
			toast("Need location permission")
			fragmentManager.popBackStack()
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