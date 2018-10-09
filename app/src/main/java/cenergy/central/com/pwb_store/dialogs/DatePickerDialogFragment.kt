package cenergy.central.com.pwb_store.dialogs

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.ContextThemeWrapper
import android.view.Window
import android.widget.DatePicker
import android.widget.Toast
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.dialogs.interfaces.PickDateListener
import org.joda.time.DateTime
import java.util.*


class DatePickerDialogFragment : DialogFragment(), OnDateSetListener {

    private var date: Date? = null
    private var startDate: Date? = null
    private var lastDate: Date? = null
    private val minCalendar = Calendar.getInstance()
    private val maxCalendar = Calendar.getInstance()


    companion object {
        private var pickDateListener: PickDateListener? = null
        private const val ARG_DATE = "date"
        private const val ARG_START_DATE = "start_date"
        private const val ARG_LAST_DATE = "last_date"

        fun newInstance(date: Date, startDate: Date, lastDate: Date): DatePickerDialogFragment {
            val dialogFragment = DatePickerDialogFragment()
            val args = Bundle()
            args.putLong(ARG_DATE, date.time)
            args.putLong(ARG_START_DATE, startDate.time)
            args.putLong(ARG_LAST_DATE, lastDate.time)
            dialogFragment.arguments = args
            return dialogFragment
        }

        fun setOnPickDateListener(pickDateListener: PickDateListener) {
            Companion.pickDateListener = pickDateListener
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        if (arguments != null) {
            date = Date(arguments!!.getLong(ARG_DATE))
            startDate = Date(arguments!!.getLong(ARG_START_DATE))
            lastDate = Date(arguments!!.getLong(ARG_LAST_DATE))
        }

        val calendar = Calendar.getInstance()
        if (date != null) {
            calendar.time = date
        }
//        val year = if (DateHelper.isLocaleTH()) calendar.get(Calendar.YEAR) + 543 else calendar.get(Calendar.YEAR)
//        calendar.set(year, calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

        if (startDate != null) {
            minCalendar.time = startDate
        }
//        val yearMin = if (DateHelper.isLocaleTH()) minCalendar.get(Calendar.YEAR) + 543 else minCalendar.get(Calendar.YEAR)
//        minCalendar.set(yearMin, minCalendar.get(Calendar.MONTH), minCalendar.get(Calendar.DAY_OF_MONTH))
        minCalendar.set(minCalendar.get(Calendar.YEAR), minCalendar.get(Calendar.MONTH), minCalendar.get(Calendar.DAY_OF_MONTH))

        if (lastDate != null) {
            checkLastDate()
            maxCalendar.time = lastDate
        }
//        val yearMax = if (DateHelper.isLocaleTH()) maxCalendar.get(Calendar.YEAR) + 543 else maxCalendar.get(Calendar.YEAR)
//        maxCalendar.set(yearMax, maxCalendar.get(Calendar.MONTH), maxCalendar.get(Calendar.DAY_OF_MONTH))
        maxCalendar.set(maxCalendar.get(Calendar.YEAR), maxCalendar.get(Calendar.MONTH), maxCalendar.get(Calendar.DAY_OF_MONTH))

        val themeContext = ContextThemeWrapper(context, R.style.ThemeDatePicker)
        val dialog = DatePickerDialog(themeContext, this,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

        val datePicker = dialog.datePicker
        datePicker.minDate = minCalendar.timeInMillis
        datePicker.maxDate = maxCalendar.timeInMillis
        datePicker.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    private fun checkLastDate() {
        if(minCalendar.get(Calendar.MONTH) == maxCalendar.get(Calendar.MONTH)){
            if(maxCalendar.get(Calendar.DAY_OF_MONTH) - minCalendar.get(Calendar.DAY_OF_MONTH) <= 14){

            }
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        val month = monthOfYear + 1 // Solve calendar first month start at 0
        val tempDateTimePicker = DateTime(year, month, dayOfMonth, 0, 0)
        if (tempDateTimePicker.millis >= minCalendar.timeInMillis && tempDateTimePicker.millis <= maxCalendar.timeInMillis) {
//            val yearPicker = if (DateHelper.isLocaleTH()) year - 543 else year
//            val pickedDateTime = DateTime(yearPicker, month, dayOfMonth, 0, 0)
            pickDateListener?.onDatePickerListener(tempDateTimePicker)
        } else {
            Toast.makeText(activity, getString(R.string.check_your_date), Toast.LENGTH_LONG).show()
        }
    }
}
