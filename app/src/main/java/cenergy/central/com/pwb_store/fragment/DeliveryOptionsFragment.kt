package cenergy.central.com.pwb_store.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.adapter.DeliveryOptionsAdapter
import cenergy.central.com.pwb_store.manager.listeners.DeliveryOptionsClickListener
import cenergy.central.com.pwb_store.manager.listeners.GetDeliveryOptionsListener
import cenergy.central.com.pwb_store.model.DeliveryOption

class DeliveryOptionsFragment : Fragment() {

    lateinit var recyclerView: RecyclerView
    var deliveryOptionsList: List<DeliveryOption> = arrayListOf()
    var getDeliveryOptionListener: GetDeliveryOptionsListener? = null
    var deliveryOptionsClickListener: DeliveryOptionsClickListener? = null

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
        getDeliveryOptionListener = context as GetDeliveryOptionsListener
        deliveryOptionsClickListener = context as DeliveryOptionsClickListener
        if (getDeliveryOptionListener != null) {
            deliveryOptionsList = getDeliveryOptionListener!!.getDeliveryOptionsList()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = LayoutInflater.from(context).inflate(R.layout.fragment_delivery_options, container, false)
        setupView(rootView)
        return rootView
    }

    private fun setupView(rootView: View) {
        recyclerView = rootView.findViewById(R.id.delivery_options_recycler)
        val deliveryOptionsAdapter = DeliveryOptionsAdapter(deliveryOptionsClickListener)
        val gridLayoutManager = GridLayoutManager(rootView.context, 2, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = gridLayoutManager
        deliveryOptionsAdapter.deliveryOptionList = deliveryOptionsList
        recyclerView.adapter = deliveryOptionsAdapter
    }
}