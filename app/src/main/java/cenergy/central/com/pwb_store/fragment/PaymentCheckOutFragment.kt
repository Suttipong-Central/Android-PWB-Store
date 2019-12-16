package cenergy.central.com.pwb_store.fragment

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.manager.listeners.CheckoutListener
import cenergy.central.com.pwb_store.utils.Analytics
import cenergy.central.com.pwb_store.utils.Screen
import cenergy.central.com.pwb_store.utils.ValidationHelper
import cenergy.central.com.pwb_store.view.PowerBuyEditText
import cenergy.central.com.pwb_store.view.PowerBuyIconButton

class PaymentCheckOutFragment : Fragment(), TextWatcher {

    private val analytics by lazy { context?.let { Analytics(it) } }
    private var checkoutListener: CheckoutListener? = null

    private lateinit var contactInput : PowerBuyEditText
    private lateinit var okBtn : PowerBuyIconButton

    companion object {
        fun newInstance(): PaymentCheckOutFragment {
            val fragment = PaymentCheckOutFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        checkoutListener = context as CheckoutListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_payment_check_out, container, false)
        setupView(rootView)
        return rootView
    }

    override fun onResume() {
        super.onResume()
        analytics?.trackScreen(Screen.START_CHECKOUT)
    }

    override fun afterTextChanged(s: Editable?) {
        checkCanSave()
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (count == 10) {
            val textError =  ValidationHelper.getInstance(context!!).validThaiPhoneNumber(contactInput.text.toString())
            contactInput.error = textError
        }
    }

    private fun setupView(rootView: View) {
        contactInput = rootView.findViewById(R.id.contact_number_check_out)
        okBtn = rootView.findViewById(R.id.checkoutButton)
        val skipBtn: TextView = rootView.findViewById(R.id.skipButton)
        okBtn.setButtonDisable(true)
        contactInput.addTextChangedListener(this)
        skipBtn.setOnClickListener { checkoutListener?.startCheckout(null) }
    }

    private fun checkCanSave() {
        if (contactInput.text.toString().length == 10){
            okBtn.setButtonDisable(false)
            okBtn.setOnClickListener { checkOnClick() }
        } else {
            okBtn.setButtonDisable(true)
        }
    }

    private fun checkOnClick() {
        val validator = context?.let {
            ValidationHelper.getInstance(it)
        }

        val textError = validator?.validThaiPhoneNumber(contactInput.text.toString())
        if (textError ==  null) {
            contactInput.error = null
            checkoutListener?.startCheckout(contactInput.text.toString())
        } else {
            contactInput.error = textError
        }
    }
}