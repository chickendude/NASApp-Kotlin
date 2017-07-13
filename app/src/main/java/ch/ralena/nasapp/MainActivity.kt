package ch.ralena.nasapp

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import ch.ralena.nasapp.fragments.BACKSTACK_PHOTOPICK
import ch.ralena.nasapp.fragments.EyeInTheSkyFragment
import ch.ralena.nasapp.fragments.PostcardFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), FragmentManager.OnBackStackChangedListener {

	var postcardFragment: Fragment? = null
	var eyeInTheSkyFragment: Fragment? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		supportFragmentManager.addOnBackStackChangedListener(this::onBackStackChanged)

		bottomNavigationView.setOnNavigationItemSelectedListener {
			supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
			val fragment: Fragment?
			if (it.itemId == R.id.marsRover) {
				if (postcardFragment == null) {
					postcardFragment = PostcardFragment()
				}
				fragment = postcardFragment
			} else {
				if (eyeInTheSkyFragment == null) {
					eyeInTheSkyFragment = EyeInTheSkyFragment()
				}
				fragment = eyeInTheSkyFragment
			}
			supportFragmentManager.beginTransaction()
					.replace(R.id.fragmentContainer, fragment)
					.commit()
			true
		}

		// load main fragment
		if (postcardFragment == null) {
			postcardFragment = PostcardFragment()
		}
		val fragment = postcardFragment
		supportFragmentManager.beginTransaction()
				.replace(R.id.fragmentContainer, fragment)
				.commit()
	}

	override fun onBackStackChanged() {
		val enable = supportFragmentManager.backStackEntryCount > 0
		supportActionBar?.setDisplayHomeAsUpEnabled(enable)
	}

	override fun onBackPressed() {
		if (supportFragmentManager.findFragmentByTag(BACKSTACK_PHOTOPICK) != null) {
			supportFragmentManager.popBackStack(BACKSTACK_PHOTOPICK, FragmentManager.POP_BACK_STACK_INCLUSIVE)
		} else
			super.onBackPressed()
	}

	override fun onSupportNavigateUp(): Boolean {
		if (supportFragmentManager.findFragmentByTag(BACKSTACK_PHOTOPICK) != null) {
			supportFragmentManager.popBackStack(BACKSTACK_PHOTOPICK, FragmentManager.POP_BACK_STACK_INCLUSIVE)
		} else {
			supportFragmentManager.popBackStack()
		}
		return true
	}
}
