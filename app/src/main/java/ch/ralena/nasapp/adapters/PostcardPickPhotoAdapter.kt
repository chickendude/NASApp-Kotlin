package ch.ralena.nasapp.adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import ch.ralena.nasapp.R
import ch.ralena.nasapp.inflate
import ch.ralena.nasapp.models.Photo
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import io.reactivex.subjects.PublishSubject
import org.jetbrains.anko.find
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.toast

class PostcardPickPhotoAdapter(val photos: ArrayList<Photo>) : RecyclerView.Adapter<PostcardPickPhotoAdapter.ViewHolder>() {

	data class PhotoClick(val photo: Photo, val view: ImageView)

	val observable: PublishSubject<PhotoClick> = PublishSubject.create()

	override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
		val view = parent!!.inflate(R.layout.item_postcardpickphoto)
		return ViewHolder(view)
	}

	override fun getItemCount(): Int {
		return photos.size
	}

	override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
		(holder as ViewHolder).bindView(photos[position])
	}


	inner class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
		var imageView: ImageView
		val progressBar: ProgressBar

		init {
			imageView = itemView!!.find(R.id.imageView)
			progressBar = itemView.find(R.id.progressBar)
		}

		fun bindView(photo: Photo) {
			itemView.transitionName = ""
			imageView.onClick {
				imageView.transitionName = "transitionImage"
				observable.onNext(PhotoClick(photo, imageView))
			}
			Picasso.with(imageView.context)
					.load(photo.img_src)
					.fit()
					.centerCrop()
					.into(imageView, object : Callback {
						override fun onSuccess() {
							progressBar.visibility = View.GONE
						}

						override fun onError() {
							progressBar.visibility = View.GONE
							imageView.context.toast("There was an error downloading the image")
						}

					})
		}

	}
}