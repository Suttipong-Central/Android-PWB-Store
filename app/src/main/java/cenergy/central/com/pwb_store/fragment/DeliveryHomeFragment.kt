package cenergy.central.com.pwb_store.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.CardView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.activity.interfaces.PaymentProtocol
import cenergy.central.com.pwb_store.fragment.interfaces.DeliveryHomeListener
import cenergy.central.com.pwb_store.helpers.DateHelper
import cenergy.central.com.pwb_store.fragment.interfaces.PickDateListener
import cenergy.central.com.pwb_store.fragment.interfaces.TimeSlotClickListener
import cenergy.central.com.pwb_store.model.response.ShippingSlotResponse
import cenergy.central.com.pwb_store.model.response.Slot
import cenergy.central.com.pwb_store.view.PowerBuyEditText
import org.joda.time.DateTime
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SetTextI18n")
class DeliveryHomeFragment : Fragment(), PickDateListener, TimeSlotClickListener, View.OnClickListener {

    // region data
    private var tempDate = ""
    private var shippingSlotResponse: ShippingSlotResponse? = null
    private var pickedDateTime: DateTime? = null
    private val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
    private var startDate: Date? = null
    private var endDate: Date? = null
    private var slot: Slot? = null
    private var date: Int = 0
    private var month: Int = 0
    private var year: Int = 0
    // end data

    // region view
    private lateinit var dateText: PowerBuyEditText
    private lateinit var timeText: PowerBuyEditText
    private lateinit var paymentButton: CardView
    // end region view

    // region listener
    private var listener: PaymentProtocol? = null
    private var deliveryHomeListener: DeliveryHomeListener? = null
    // end region listener

    private var datePickerDialogFragment = DatePickerDialogFragment
    private var timeSlotDialogFragment = TimeSlotDialogFragment
    private var timeSlotDialog = DialogFragment()

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
        deliveryHomeListener = context as DeliveryHomeListener
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
        timeSlotDialogFragment.setOnPickDateListener(this)
        dateText = rootView.findViewById(R.id.edit_text_time)
        timeText = rootView.findViewById(R.id.edit_text_time2)
        paymentButton = rootView.findViewById(R.id.home_delivery_button_payment)
        dateText.setOnClickListener(this)
        timeText.setOnClickListener(this)
        paymentButton.setOnClickListener(this)
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
        tempDate = formatter.format(Date(dateTime.millis))
        dateText.setText("${dateTime.dayOfMonth}/${dateTime.monthOfYear}/$year")
        this.date = dateTime.dayOfMonth
        this.month = dateTime.monthOfYear
        this.year = dateTime.year
    }

    override fun onTimeSlotClickListener(slot: Slot) {
        this.slot = slot
        timeText.setText(slot.description)
        timeSlotDialog.dismiss()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.edit_text_time -> {
                pickDate()
            }
            R.id.edit_text_time2 -> {
                checkPickDate()
            }
            R.id.home_delivery_button_payment -> {
                checkPayment()
            }
        }
    }

    private fun checkPickDate() {
        if (tempDate.isEmpty()) {
            context?.let { showAlertDialog(it, "", resources.getString(R.string.please_select_date)) }
        } else {
            shippingSlotResponse?.let { it ->
                val shippingSlot = it.shippingSlot.firstOrNull { it.shippingDate == tempDate }
                if(shippingSlot != null){
                    timeSlotDialog = timeSlotDialogFragment.newInstance(shippingSlot.slot)
                    timeSlotDialog.show(fragmentManager, "dialog")
                }
            }
        }
    }

    private fun checkPayment() {
        if (slot != null && date != 0 && month != 0 && year != 0 && tempDate.isNotEmpty()){
            deliveryHomeListener?.onPaymentClickListener(slot!!, date, month, year, tempDate)
        } else {
            if(tempDate.isEmpty() && shippingSlotResponse == null){
                context?.let { showAlertDialog(it, "", resources.getString(R.string.please_select_date_and_time)) }
            } else {
                context?.let { showAlertDialog(it, "", resources.getString(R.string.please_try_again)) }
            }
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