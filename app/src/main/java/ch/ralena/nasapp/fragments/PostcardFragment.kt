package ch.ralena.nasapp.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.textView
import org.jetbrains.anko.verticalLayout


class PostcardFragment : Fragment() {
	override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val fragmentViews = UI {
			verticalLayout {
				textView {
					text = "Rover Postcard"
				}
			}
		}.view

		return fragmentViews
	}
}