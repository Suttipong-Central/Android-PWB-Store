package cenergy.central.com.pwb_store.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.activity.interfaces.PaymentProtocol

class DeliveryStorePickUpFragment : Fragment() {

    private var listener: PaymentProtocol? = null
    private var stores: ArrayList<String> = arrayListOf()
    private val storesFragment = StoresFragment.newInstance()
    private val storeDetailFragment = StoreDetailFragment.newInstance()

    companion object {
        const val TAG_FRAGMENT_STORES = "fragment_stores"
        const val TAG_FRAGMENT_STORE_DETAIL = "fragment_store_detail"

        fun newInstance(): DeliveryStorePickUpFragment {
            val fragment = DeliveryStorePickUpFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        listener = context as PaymentProtocol
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = LayoutInflater.from(context).inflate(R.layout.fragment_delivery_stores, container, false)
        setupView()
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (stores.isEmpty()) {
            retrieveStores()
        }
    }

    private fun retrieveStores() {
        listener?.retrieveStores() //TODO: get stores
    }

    fun updateStores(stores: ArrayList<String>) {
        this.stores = stores
        storesFragment.updateStores(this.stores)
    }

    fun updateStoreDetail(store: String) {
        storeDetailFragment.updateStoreDetail(store)
    }

    private fun setupView() {
        childFragmentManager.beginTransaction()?.replace(R.id.content_stores, storesFragment, TAG_FRAGMENT_STORES)?.commit()
        childFragmentManager.beginTransaction()?.replace(R.id.content_store_detail, storeDetailFragment, TAG_FRAGMENT_STORE_DETAIL)?.commit()
    }
}
