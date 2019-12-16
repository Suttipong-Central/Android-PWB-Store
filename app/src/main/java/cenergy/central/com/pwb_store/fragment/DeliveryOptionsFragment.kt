package cenergy.central.com.pwb_store.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.activity.interfaces.PaymentProtocol
import cenergy.central.com.pwb_store.adapter.DeliveryOptionsAdapter
import cenergy.central.com.pwb_store.manager.listeners.DeliveryOptionsListener
import cenergy.central.com.pwb_store.model.DeliveryOption
import cenergy.central.com.pwb_store.model.DeliveryType
import cenergy.central.com.pwb_store.utils.Analytics
import cenergy.central.com.pwb_store.utils.Screen

class DeliveryOptionsFragment : Fragment() {
    private val analytics by lazy { context?.let { Analytics(it) } }

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

    override fun onResume() {
        super.onResume()
        analytics?.trackScreen(Screen.SELECT_DELIVERY)
    }

    private fun setupView(rootView: View) {
        recyclerView = rootView.findViewById(R.id.delivery_options_recycler)
        val deliveryOptionsAdapter = DeliveryOptionsAdapter(deliveryOptionsListener)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = deliveryOptionsAdapter
        deliveryOptionsAdapter.deliveryOptionList = deliveryOptions
    }
}