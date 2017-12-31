package ch.ralena.nasapp.fragments

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.widget.DatePicker
import io.reactivex.subjects.PublishSubject
import java.util.*

/**
 * Created by oversluij on 7/7/2017.
 */
class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {

	val observable: PublishSubject<Calendar> = PublishSubject.create()

	override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
		val calendar = arguments!!.getSerializable(KEY_EARTHDATE) as Calendar
		val year = calendar.get(Calendar.YEAR)
		val month = calendar.get(Calendar.MONTH)
		val day = calendar.get(Calendar.DAY_OF_MONTH)
		return DatePickerDialog(context, this, year, month, day)
	}

	override fun onDateSet(view: DatePicker?, year: Int, month: Int, day: Int) {
		val calendar = Calendar.getInstance()
		calendar.set(year, month, day)
		observable.onNext(calendar)
	}
}