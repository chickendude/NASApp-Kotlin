package ch.ralena.nasapp

import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import ch.ralena.nasapp.fragments.BACKSTACK_PHOTOPICK
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

	fun setUpViewPager() {

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
