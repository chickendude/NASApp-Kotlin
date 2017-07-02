package ch.ralena.nasapp.adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import ch.ralena.nasapp.R
import ch.ralena.nasapp.inflate
import ch.ralena.nasapp.models.Photo
import com.squareup.picasso.Picasso
import io.reactivex.subjects.PublishSubject
import org.jetbrains.anko.find
import org.jetbrains.anko.sdk25.coroutines.onClick

class PostcardPickPhotoAdapter(val photos: ArrayList<Photo>) : RecyclerView.Adapter<PostcardPickPhotoAdapter.ViewHolder>() {

	val subject: PublishSubject<Photo> = PublishSubject.create()

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

		init {
			imageView = itemView!!.find(R.id.imageView)
		}

		fun bindView(photo: Photo) {
			imageView.onClick { subject.onNext(photo) }
			Picasso.with(imageView.context)
					.load(photo.img_src)
					.fit()
					.centerCrop()
					.into(imageView)
		}

	}
}