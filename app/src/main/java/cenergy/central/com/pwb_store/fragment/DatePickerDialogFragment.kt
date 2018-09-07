package cenergy.central.com.pwb_store.fragment

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
import cenergy.central.com.pwb_store.helpers.DateHelper
import cenergy.central.com.pwb_store.manager.listeners.OnPickDateListener
import org.joda.time.DateTime
import java.util.*


class DatePickerDialogFragment : DialogFragment(), OnDateSetListener {

    private var date: Date? = null

    companion object {
        private var onPickDateListener: OnPickDateListener? = null
        private const val ARG_DATE = "date"

        fun newInstance(date: Date): DatePickerDialogFragment {
            val dialogFragment = DatePickerDialogFragment()
            val args = Bundle()
            args.putLong(ARG_DATE, date.time)
            dialogFragment.arguments = args
            return dialogFragment
        }

        fun setOnPickDateListener(onPickDateListener: OnPickDateListener) {
            this.onPickDateListener = onPickDateListener
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        if (arguments != null) {
            date = Date(arguments!!.getLong(ARG_DATE, 0))
        }

        val calendar = Calendar.getInstance()

        if (date != null) {
            calendar.time = date
        }

        val buddhistYear = if (DateHelper.isLocaleTH()) calendar.get(Calendar.YEAR) + 543 else calendar.get(Calendar.YEAR)
        calendar.set(buddhistYear, calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

        val minCalendar = Calendar.getInstance()
        val yearMin = if (DateHelper.isLocaleTH()) minCalendar.get(Calendar.YEAR) + 543 else minCalendar.get(Calendar.YEAR)
        minCalendar.set(yearMin, minCalendar.get(Calendar.MONTH), 1)

        val maxCalendar = Calendar.getInstance()
        val yearMax = if (DateHelper.isLocaleTH()) maxCalendar.get(Calendar.YEAR) + 543 else maxCalendar.get(Calendar.YEAR)
        maxCalendar.set(yearMax, maxCalendar.get(Calendar.MONTH), maxCalendar.get(Calendar.DAY_OF_MONTH))

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

    override fun onDateSet(view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        var month = monthOfYear
        // Solve calendar first month start at 0
        month += 1
        // Create date
        val localeYear = if (DateHelper.isLocaleTH()) year - 543 else year

        val pickedDateTime = DateTime(localeYear, month, dayOfMonth, 0, 0)
        if (pickedDateTime.millis <= System.currentTimeMillis()) {
            // Response
            onPickDateListener?.onDatePickerListener(pickedDateTime)
        } else {
            Toast.makeText(activity, getString(R.string.check_your_date), Toast.LENGTH_LONG).show()
        }
    }
}
