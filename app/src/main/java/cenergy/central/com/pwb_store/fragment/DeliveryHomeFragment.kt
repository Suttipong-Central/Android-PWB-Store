package cenergy.central.com.pwb_store.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.AppCompatAutoCompleteTextView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.manager.listeners.OnPickDateListener
import cenergy.central.com.pwb_store.view.PowerBuyEditText
import org.joda.time.DateTime
import java.util.*

@SuppressLint("SetTextI18n")
class DeliveryHomeFragment : Fragment(), OnPickDateListener {

    private var datePickerDialogFragment = DatePickerDialogFragment
    private lateinit var dateText: AppCompatAutoCompleteTextView
    private lateinit var timeText: PowerBuyEditText
    private var pickedDateTime: DateTime? = null

    companion object {
        fun newInstance(): DeliveryHomeFragment {
            val fragment = DeliveryHomeFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = LayoutInflater.from(context).inflate(R.layout.fragment_delivery, container, false)
        setupView(rootView)
        return rootView
    }

    private fun setupView(rootView: View) {
        datePickerDialogFragment.setOnPickDateListener(this)
        dateText = rootView.findViewById(R.id.edit_text_time)
        timeText = rootView.findViewById(R.id.edit_text_time2)
        dateText.setOnFocusChangeListener { v, hasFocus ->
            if (v.id == R.id.edit_text_time) {
                if (hasFocus) pickDate()
            }
        }
    }

    private fun pickDate() {
        val imm = context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(dateText.windowToken, 0)
        if (pickedDateTime != null) {
            datePickerDialogFragment.newInstance(Date(pickedDateTime!!.millis)).show(fragmentManager, "dialog")
        } else {
            val dateTime = DateTime.now()
            datePickerDialogFragment.newInstance(Date(dateTime.millis)).show(fragmentManager, "dialog")
        }
        timeText.requestFocus()
    }

    override fun onDatePickerListener(dateTime: DateTime) {
        pickedDateTime = dateTime
        dateText.setText("${dateTime.dayOfMonth}/${dateTime.monthOfYear}/${dateTime.year}")
    }
}