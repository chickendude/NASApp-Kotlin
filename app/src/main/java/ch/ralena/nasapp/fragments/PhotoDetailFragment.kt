package ch.ralena.nasapp.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import ch.ralena.nasapp.R
import ch.ralena.nasapp.inflate
import com.squareup.picasso.Callback
import com.squareup.picasso.OkHttpDownloader
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_photodetail.*
import org.jetbrains.anko.support.v4.toast

class PhotoDetailFragment : Fragment() {

	lateinit var imageUrl: String

	override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		setHasOptionsMenu(true)
		return container!!.inflate(R.layout.fragment_photodetail)
	}

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
		imageUrl = arguments.getString(KEY_IMAGE)
		val roverNameText = arguments.getString(KEY_ROVER)
		val cameraNameText = arguments.getString(KEY_CAMERA)
		val earthDateText = arguments.getString(KEY_EARTHDATE)
		Picasso.Builder(context)
				.downloader(OkHttpDownloader(context))
				.build()
				.load(imageUrl)
				.into(imageView, object: Callback {
					override fun onSuccess() {
					}

					override fun onError() {
						toast("Error loading photo.")
					}
				})
		roverName.text = roverNameText
		cameraName.text = cameraNameText
		earthDate.text = earthDateText
	}

	override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
		inflater?.inflate(R.menu.check, menu)
	}

	override fun onOptionsItemSelected(item: MenuItem?): Boolean {
		when (item?.itemId) {
			R.id.action_ok -> {
				loadPostcard()
				return true
			}
		}
		return false
	}

	private fun loadPostcard() {
		val fragment = PostcardCreateFragment()
		val bundle = Bundle()
		bundle.putString(KEY_IMAGE, imageUrl)
		fragment.arguments = bundle
		fragmentManager.beginTransaction()
				.addToBackStack(null)
				.replace(R.id.fragmentContainer, fragment)
				.commit()
	}
}