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
import cenergy.central.com.pwb_store.adapter.StoresDeliveryAdapter
import cenergy.central.com.pwb_store.adapter.interfaces.StoreClickListener
import cenergy.central.com.pwb_store.fragment.interfaces.StorePickUpListener
import cenergy.central.com.pwb_store.model.Branch

class BranchesFragment : Fragment(), StoreClickListener {

    private val storesAdapter = StoresDeliveryAdapter(this)
    var branches: List<Branch> = arrayListOf()
    private lateinit var storesRecycler: RecyclerView
    private var listener: StorePickUpListener? = null

    companion object {
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
        storesAdapter.branches = branches
    }

    fun updateBranches(branches: List<Branch>) {
        this.branches = branches
        storesAdapter.branches = branches
    }

    // region {@link StoreClickListener.onItemClicked}
    override fun onItemClicked(branch: Branch) {
        listener?.onUpdateStoreDetail(branch)
    }
    // endregion
}