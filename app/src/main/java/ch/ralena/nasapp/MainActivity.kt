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

//	override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
//		if (ev!!.action == MotionEvent.ACTION_DOWN) {
//			val v: View? = currentFocus
//			if (v is EditText) {
//				val rect = Rect()
//				v.getGlobalVisibleRect(rect)
//				if (!rect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
//					v.clearFocus()
//					val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//					imm.hideSoftInputFromWindow(v.windowToken, 0)
//				}
//			}
//		}
//		return super.dispatchTouchEvent(ev)
//	}
}
