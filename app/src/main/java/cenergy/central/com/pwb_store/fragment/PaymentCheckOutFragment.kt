package cenergy.central.com.pwb_store.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.CardView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.manager.listeners.CheckOutClickListener
import cenergy.central.com.pwb_store.view.PowerBuyEditText

class PaymentCheckOutFragment : Fragment() {

    private var checkOutClickListener: CheckOutClickListener? = null

    private lateinit var contactNoEdt : PowerBuyEditText
    private lateinit var okBtn : CardView

    companion object {
        fun newInstance(): ProductDetailFragment {
            val fragment = ProductDetailFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        checkOutClickListener = context as CheckOutClickListener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_payment_check_out, container, false)
        setupView(rootView)
        return rootView
    }

    private fun setupView(rootView: View) {
        contactNoEdt = rootView.findViewById(R.id.contact_number_check_out)
        okBtn = rootView.findViewById(R.id.ok_btn_check_out)

        okBtn.setOnClickListener { checkOnClick() }
    }

    private fun checkOnClick() {
        if( contactNoEdt.text != null && contactNoEdt.text.toString().isNotEmpty()){
            checkOutClickListener?.onCheckOutListener(contactNoEdt.text.toString())
        }
    }
}