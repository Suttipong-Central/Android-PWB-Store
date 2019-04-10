package cenergy.central.com.pwb_store.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.activity.interfaces.PaymentProtocol
import cenergy.central.com.pwb_store.adapter.PaymentMethodAdapter
import cenergy.central.com.pwb_store.dialogs.interfaces.PaymentTypeClickListener

class PaymentSelectMethodFragment : Fragment() {

    private val paymentListener by lazy { context as PaymentProtocol }
    private val paymentMethods by lazy{ paymentListener.getPaymentMethods()}
    private lateinit var recycler: RecyclerView
    private lateinit var paymentTypeClickListener: PaymentTypeClickListener

    companion object {
        fun newInstance(): PaymentSelectMethodFragment {
            val fragment = PaymentSelectMethodFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        paymentTypeClickListener = context as PaymentTypeClickListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_payment_select_methods, container, false)
        setupView(rootView)
        return rootView
    }

    private fun setupView(rootView: View) {
        recycler = rootView.findViewById(R.id.recycler_select_methods)
        val selectMethodAdapter = PaymentMethodAdapter(paymentTypeClickListener)
        recycler.layoutManager = LinearLayoutManager(context)
        recycler.adapter = selectMethodAdapter
        selectMethodAdapter.paymentMethods = paymentMethods
    }

}