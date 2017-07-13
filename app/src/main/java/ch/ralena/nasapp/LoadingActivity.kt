package ch.ralena.nasapp

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_landing.*
import org.jetbrains.anko.sdk25.coroutines.onClick

class LoadingActivity : AppCompatActivity() {
	var timer: CountDownTimer? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_landing)

		skipButton.onClick { loadMainActivity() }
		timer = object : CountDownTimer(3050, 1000) {
			override fun onFinish() {
				skipButton.text = "skip 0"
				loadMainActivity()
			}

			override fun onTick(timeLeft: Long) {
				skipButton.text = "skip " + timeLeft / 1000
			}
		}.start()
	}

	private fun loadMainActivity() {
		val intent = Intent(this, MainActivity::class.java)
		startActivity(intent)
	}

	override fun onDestroy() {
		super.onDestroy()
		timer?.cancel()
	}
}
