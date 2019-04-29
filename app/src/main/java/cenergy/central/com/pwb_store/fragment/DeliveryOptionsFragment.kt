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
import cenergy.central.com.pwb_store.adapter.DeliveryOptionsAdapter
import cenergy.central.com.pwb_store.manager.listeners.DeliveryOptionsListener
import cenergy.central.com.pwb_store.model.DeliveryOption
import cenergy.central.com.pwb_store.model.DeliveryType

class DeliveryOptionsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private var deliveryOptions: List<DeliveryOption> = arrayListOf()
    private var paymentListener: PaymentProtocol? = null
    private var deliveryOptionsListener: DeliveryOptionsListener? = null

    companion object {
        fun newInstance(): DeliveryOptionsFragment {
            val fragment = DeliveryOptionsFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        paymentListener = context as PaymentProtocol
        deliveryOptionsListener = context as DeliveryOptionsListener
        deliveryOptions = paymentListener?.getDeliveryOptions() ?: arrayListOf()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = LayoutInflater.from(context).inflate(R.layout.fragment_delivery_options, container, false)
        setupView(rootView)
        return rootView
    }

    private fun setupView(rootView: View) {
        recyclerView = rootView.findViewById(R.id.delivery_options_recycler)
        val deliveryOptionsAdapter = DeliveryOptionsAdapter(deliveryOptionsListener)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = deliveryOptionsAdapter
        deliveryOptionsAdapter.deliveryOptionList = getDeliveryOptionList()

    }

    private fun getDeliveryOptionList(): List<DeliveryOption> {
        val deliveryMethods = arrayListOf<DeliveryOption>()
        deliveryOptions.forEach {
            if(DeliveryType.fromString(it.methodCode) != null){
                deliveryMethods.add(it)
            }
        }
        return deliveryMethods
    }
}