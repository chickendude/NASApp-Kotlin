package ch.ralena.nasapp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.LinearLayout
import ch.ralena.nasapp.fragments.MainFragment

class MainActivity : AppCompatActivity() {
	var containerView: LinearLayout? = null


	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		// load main fragment
		val fragment = MainFragment()
		supportFragmentManager.beginTransaction()
				.replace(R.id.fragmentContainer, fragment)
				.commit()
	}
}
