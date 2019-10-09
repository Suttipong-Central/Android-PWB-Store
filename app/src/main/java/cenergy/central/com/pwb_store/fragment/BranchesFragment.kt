package cenergy.central.com.pwb_store.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.activity.CheckoutType
import cenergy.central.com.pwb_store.adapter.StoresDeliveryAdapter
import cenergy.central.com.pwb_store.adapter.interfaces.StoreClickListener
import cenergy.central.com.pwb_store.fragment.interfaces.StorePickUpListener
import cenergy.central.com.pwb_store.model.response.BranchResponse

class BranchesFragment : Fragment(), StoreClickListener {

    private val storesAdapter = StoresDeliveryAdapter(this)
    private var items: ArrayList<BranchResponse> = arrayListOf()
    private lateinit var storesRecycler: RecyclerView
    private var listener: StorePickUpListener? = null


    companion object {
        private const val PER_PAGE = 13

        fun newInstance(): BranchesFragment {
            val fragment = BranchesFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        listener = context as StorePickUpListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = LayoutInflater.from(context).inflate(R.layout.fragment_stores, container, false)
        setupView(rootView)
        return rootView
    }

    private fun setupView(rootView: View) {
        storesRecycler = rootView.findViewById(R.id.recycler_view_list_stores)
        storesRecycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        storesRecycler.adapter = storesAdapter
        storesAdapter.items = items
    }

    fun updateBranches(items: ArrayList<BranchResponse>, checkoutType: CheckoutType) {
        this.items = items
        storesAdapter.updateItems(checkoutType, items)
    }

    // region {@link StoreClickListener.onItemClicked}
    override fun onItemClicked(branchResponse: BranchResponse) {
        listener?.onUpdateStoreDetail(branchResponse)
    }
    // endregion

    private fun totalPageCal(total: Int): Int {
        val num: Int
        val x = total.toFloat() / PER_PAGE
        num = Math.ceil(x.toDouble()).toInt()
        return num
    }
}