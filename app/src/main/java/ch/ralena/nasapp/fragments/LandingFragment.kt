package ch.ralena.nasapp.fragments

import android.os.Bundle
import android.os.CountDownTimer
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ch.ralena.nasapp.R
import ch.ralena.nasapp.inflate
import kotlinx.android.synthetic.main.fragment_landing.*
import org.jetbrains.anko.sdk25.coroutines.onClick

class LandingFragment : Fragment() {
	var timer: CountDownTimer? = null
	var counter: Int = 4

	override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return container!!.inflate(R.layout.fragment_landing)
	}

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
		skipButton.onClick { loadMainFragment() }
		timer = object : CountDownTimer(3050, 1000) {
			override fun onFinish() {
				skipButton.text = "skip 0"
				loadMainFragment()
			}

			override fun onTick(timeLeft: Long) {
				skipButton.text = "skip " + timeLeft / 1000
			}
		}.start()
	}

	private fun updateCounter() {
		skipButton.text = "skip " + counter
		counter--
	}

	private fun loadMainFragment() {
		val fragment = MainFragment()
		fragmentManager.beginTransaction()
				.replace(R.id.fragmentContainer, fragment)
				.commit()

	}

	override fun onDestroy() {
		super.onDestroy()
		timer?.cancel()
	}
}