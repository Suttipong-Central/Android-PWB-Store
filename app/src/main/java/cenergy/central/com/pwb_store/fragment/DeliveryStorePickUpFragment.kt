package cenergy.central.com.pwb_store.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.adapter.StoresDeliveryAdapter

class DeliveryStorePickUpFragment : Fragment() {

    lateinit var storesRecycler: RecyclerView

    companion object {
        fun newInstance(): DeliveryStorePickUpFragment {
            val fragment = DeliveryStorePickUpFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = LayoutInflater.from(context).inflate(R.layout.fragment_delivery_stores, container, false)
        setupView(rootView)
        return rootView
    }

    private fun setupView(rootView: View) {
        storesRecycler = rootView.findViewById(R.id.recycler_view_list_stores)
        storesRecycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        val storesDeliveryAdapter = StoresDeliveryAdapter()
        storesRecycler.adapter = storesDeliveryAdapter
    }
}
