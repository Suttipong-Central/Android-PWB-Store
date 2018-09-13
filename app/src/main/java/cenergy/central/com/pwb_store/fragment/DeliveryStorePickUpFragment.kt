package cenergy.central.com.pwb_store.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.activity.interfaces.PaymentProtocol
import cenergy.central.com.pwb_store.fragment.interfaces.StorePickUpListener

class DeliveryStorePickUpFragment : Fragment(), StorePickUpListener {

    private var listener: PaymentProtocol? = null

    private var stores: ArrayList<String> = arrayListOf()

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
        val fragment = childFragmentManager.findFragmentByTag(TAG_FRAGMENT_STORES)
        if (fragment != null) {
            val storesFragment = fragment as StoresFragment
            storesFragment.updateStores(this.stores)
        }
    }

    private fun updateStoreDetail(store: String) {
        val fragment = childFragmentManager.findFragmentByTag(TAG_FRAGMENT_STORE_DETAIL)
        if (fragment != null) {
            val storeDetailFragment = fragment as StoreDetailFragment
            storeDetailFragment.updateStoreDetail(store)
        }
    }

    private fun setupView() {
        childFragmentManager.beginTransaction().replace(R.id.content_stores, StoresFragment.newInstance(), TAG_FRAGMENT_STORES).commit()

        childFragmentManager.beginTransaction().replace(R.id.content_store_detail, StoreDetailFragment.newInstance(), TAG_FRAGMENT_STORE_DETAIL).commit()
    }

    // region {@link StorePickUpListener}
    override fun onUpdateStoreDetail(store: String) {
        //TODO: update storeDetail
        updateStoreDetail(store)
    }

    override fun onSeletedStore(store: String) {
        // TODO: on selected store
    }
    // endregion
}
