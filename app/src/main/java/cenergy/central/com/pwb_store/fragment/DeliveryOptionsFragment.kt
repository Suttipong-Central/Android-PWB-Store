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
import cenergy.central.com.pwb_store.adapter.DeliveryOptionsAdapter
import cenergy.central.com.pwb_store.manager.listeners.DeliveryOptionsListener
import cenergy.central.com.pwb_store.activity.interfaces.PaymentProtocol
import cenergy.central.com.pwb_store.model.DeliveryOption
import cenergy.central.com.pwb_store.model.DeliveryType

class DeliveryOptionsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private var deliveryOptions: List<DeliveryOption> = arrayListOf()
    private var paymentListener: PaymentProtocol? = null
    private var deliveryOptionsListener: DeliveryOptionsListener? = null
    private var dialogOption: String = ""
    private var tempDeliveryOptions: ArrayList<DeliveryOption> = arrayListOf()

    companion object {
        private const val DIALOG_OPTION = "dialog_option"

        fun newInstance(dialogDescription: String): DeliveryOptionsFragment {
            val fragment = DeliveryOptionsFragment()
            val args = Bundle()
            args.putString(DIALOG_OPTION, dialogDescription)
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dialogOption = arguments?.getString(DIALOG_OPTION)?:""
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
        when(dialogOption){
            getString(R.string.select_store_dialog) -> { // store pickup
                deliveryOptions.forEach {
                    if(it.methodCode == DeliveryType.STORE_PICK_UP.methodCode ){
                        tempDeliveryOptions.add(it)
                    }
                }
            }
            getString(R.string.select_shipping_dialog) -> { // select delivery options
                deliveryOptions.forEach {
                    if(it.methodCode != DeliveryType.STORE_PICK_UP.methodCode ){
                        tempDeliveryOptions.add(it)
                    }
                }
            }
        }
        deliveryOptionsAdapter.deliveryOptionList = tempDeliveryOptions
    }
}