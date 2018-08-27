package cenergy.central.com.pwb_store.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.CardView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.manager.listeners.CheckOutClickListener
import cenergy.central.com.pwb_store.view.PowerBuyEditText

class PaymentCheckOutFragment : Fragment(), TextWatcher {

    private var checkOutClickListener: CheckOutClickListener? = null

    private lateinit var contactNoEdt : PowerBuyEditText
    private lateinit var okBtn : CardView

    companion object {
        fun newInstance(): PaymentCheckOutFragment {
            val fragment = PaymentCheckOutFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        checkOutClickListener = context as CheckOutClickListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_payment_check_out, container, false)
        setupView(rootView)
        return rootView
    }

    override fun afterTextChanged(s: Editable?) {
        checkCanSave()
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

    }

    private fun setupView(rootView: View) {
        contactNoEdt = rootView.findViewById(R.id.contact_number_check_out)
        okBtn = rootView.findViewById(R.id.ok_btn_check_out)
        val skipBtn: TextView = rootView.findViewById(R.id.skipButton)
        okBtn.isEnabled = false
        contactNoEdt.addTextChangedListener(this)
        skipBtn.setOnClickListener { checkOutClickListener?.onCheckOutListener(null) }
    }

    private fun checkCanSave() {
        if (contactNoEdt.text.toString().length == 10){
            context?.let { okBtn.setCardBackgroundColor(ContextCompat.getColor(it, R.color.powerBuyPurple)) }
            okBtn.isEnabled = true
            okBtn.setOnClickListener { checkOnClick() }
        } else {
            context?.let { okBtn.setCardBackgroundColor(ContextCompat.getColor(it, R.color.hintColor)) }
            okBtn.isEnabled = false
        }
    }

    private fun checkOnClick() {
        if( contactNoEdt.text != null && contactNoEdt.text.toString().isNotEmpty() && contactNoEdt.text.toString().length == 10){
            checkOutClickListener?.onCheckOutListener(contactNoEdt.text.toString())
        }
    }
}