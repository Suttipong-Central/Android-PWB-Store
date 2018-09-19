package cenergy.central.com.pwb_store.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.activity.interfaces.PaymentProtocol
import cenergy.central.com.pwb_store.helpers.DateHelper
import cenergy.central.com.pwb_store.manager.listeners.OnPickDateListener
import cenergy.central.com.pwb_store.model.response.ShippingSlotResponse
import cenergy.central.com.pwb_store.view.PowerBuyEditText
import org.joda.time.DateTime
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SetTextI18n")
class DeliveryHomeFragment : Fragment(), OnPickDateListener, View.OnClickListener {

    // region data
    private var tempDateFormat = ""
    private var shippingSlotResponse: ShippingSlotResponse? = null
    private var pickedDateTime: DateTime? = null
    private val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
    private var startDate: Date? = null
    private var endDate: Date? = null
    // end data

    // region view
    private lateinit var dateText: PowerBuyEditText
    private lateinit var timeText: PowerBuyEditText
    // end region view

    // region listener
    private var listener: PaymentProtocol? = null
    // end region listener

    private var datePickerDialogFragment = DatePickerDialogFragment

    companion object {
        fun newInstance(): DeliveryHomeFragment {
            val fragment = DeliveryHomeFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        listener = context as PaymentProtocol
        shippingSlotResponse = listener?.getShippingSlot()
        shippingSlotResponse?.let { shippingSlotResponse ->
            startDate = formatter.parse(shippingSlotResponse.shippingSlot[0].shippingDate)
            endDate = formatter.parse(shippingSlotResponse.shippingSlot[shippingSlotResponse.shippingSlot.size - 1].shippingDate)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = LayoutInflater.from(context).inflate(R.layout.fragment_home_delivery, container, false)
        setupView(rootView)
        return rootView
    }

    private fun setupView(rootView: View) {
        datePickerDialogFragment.setOnPickDateListener(this)
        dateText = rootView.findViewById(R.id.edit_text_time)
        timeText = rootView.findViewById(R.id.edit_text_time2)
        dateText.setOnClickListener(this)
        timeText.setOnClickListener(this)
    }

    private fun pickDate() {
        if (pickedDateTime != null) {
            datePickerDialogFragment.newInstance(Date(pickedDateTime!!.millis), startDate!!, endDate!!).show(fragmentManager, "dialog")
        } else {
            datePickerDialogFragment.newInstance(startDate!!, startDate!!, endDate!!).show(fragmentManager, "dialog")
        }
    }

    override fun onDatePickerListener(dateTime: DateTime) {
        pickedDateTime = dateTime
        val year = if (DateHelper.isLocaleTH()) dateTime.year + 543 else dateTime.year
        tempDateFormat = "${dateTime.year}-${dateTime.monthOfYear}-${dateTime.dayOfMonth}"
        dateText.setText("${dateTime.dayOfMonth}/${dateTime.monthOfYear}/$year")
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.edit_text_time -> {
                pickDate()
            }
            R.id.edit_text_time2 -> {
                checkPickDate()
            }
        }
    }

    private fun checkPickDate() {
        if (tempDateFormat.isEmpty()) {
            context?.let { showAlertDialog(it, "", resources.getString(R.string.please_select_date)) }
        } else {
            // TODO when after selected date
        }
    }

    fun showAlertDialog(context: Context, title: String, message: String) {
        val builder = AlertDialog.Builder(context, R.style.AlertDialogTheme)
                .setMessage(message)
        builder.setPositiveButton(android.R.string.ok) { dialog, which ->
            dialog.dismiss()
        }
        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title)
        }
        builder.show()
    }
}