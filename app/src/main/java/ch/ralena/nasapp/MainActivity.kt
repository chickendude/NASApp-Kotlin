package ch.ralena.nasapp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import ch.ralena.nasapp.fragments.MainFragment

class MainActivity : AppCompatActivity() {
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
