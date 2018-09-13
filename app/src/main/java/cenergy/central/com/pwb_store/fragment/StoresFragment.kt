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
import cenergy.central.com.pwb_store.model.AddressInformation

class StoresFragment : Fragment(){

    var stores: ArrayList<AddressInformation> = arrayListOf()
    private lateinit var storesRecycler: RecyclerView

    companion object {
        fun newInstance(): StoresFragment {
            val fragment = StoresFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = LayoutInflater.from(context).inflate(R.layout.fragment_stores, container)
        setupView(rootView)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    private fun setupView(rootView: View) {
        storesRecycler = rootView.findViewById(R.id.recycler_view_list_stores)
        storesRecycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        val storesDeliveryAdapter = StoresDeliveryAdapter()
        storesRecycler.adapter = storesDeliveryAdapter
        storesDeliveryAdapter.stores = stores
    }
}