package ch.ralena.nasapp

import android.app.FragmentManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import ch.ralena.nasapp.fragments.MainFragment

class MainActivity : AppCompatActivity(), FragmentManager.OnBackStackChangedListener {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		supportFragmentManager.addOnBackStackChangedListener(this::onBackStackChanged)

		// load main fragment
		val fragment = MainFragment()
		supportFragmentManager.beginTransaction()
				.replace(R.id.fragmentContainer, fragment)
				.commit()
	}

	override fun onBackStackChanged() {
		val enable = supportFragmentManager.backStackEntryCount > 0
		supportActionBar?.setDisplayHomeAsUpEnabled(enable)
	}

	override fun onSupportNavigateUp(): Boolean {
		supportFragmentManager.popBackStack()
		return true
	}
}
