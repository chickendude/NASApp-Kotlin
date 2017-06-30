package ch.ralena.nasapp.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ch.ralena.nasapp.R
import org.jetbrains.anko.button
import org.jetbrains.anko.linearLayout
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.textView
import org.jetbrains.anko.verticalLayout

class MainFragment : Fragment() {

	override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return UI {
			verticalLayout {
				id = 11
				textView {
					text = "Welcome to NASApp!"
					textSize = 24f
					textAlignment = View.TEXT_ALIGNMENT_CENTER
				}
				linearLayout {
					gravity = Gravity.CENTER
					button {
						text = "Rover Postcard Maker"
						onClick {
							loadRoverPostcardFragment()
						}
					}
					button {
						text = "Eye in the Sky"
					}
				}
			}
		}.view // end of UI
	}

	private fun loadRoverPostcardFragment() {
		val fragment = PostcardFragment()
		fragmentManager.beginTransaction()
				.replace(R.id.fragmentContainer, fragment)
				.addToBackStack(null)
				.commit()
	}
}