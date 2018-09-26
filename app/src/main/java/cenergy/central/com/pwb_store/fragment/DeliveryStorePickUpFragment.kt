package cenergy.central.com.pwb_store.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.activity.interfaces.PaymentProtocol
import cenergy.central.com.pwb_store.model.Branch

class DeliveryStorePickUpFragment : Fragment() {

    private var listener: PaymentProtocol? = null
    private var branches: List<Branch> = arrayListOf()
    private val branchesFragment = BranchesFragment.newInstance()
    private val branchDetailFragment = BranchDetailFragment.newInstance()

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
        branches = listener?.getBranches() ?: arrayListOf()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = LayoutInflater.from(context).inflate(R.layout.fragment_delivery_stores, container, false)
        setupView()
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateBranches(branches)
    }

    private fun updateBranches(branches: List<Branch>) {
        this.branches = branches
        branchesFragment.updateBraches(branches)
    }

    fun updateStoreDetail(branch: Branch) {
        branchDetailFragment.updateBranchDetail(branch)
    }

    private fun setupView() {
        childFragmentManager.beginTransaction()?.replace(R.id.content_branches, branchesFragment, TAG_FRAGMENT_STORES)?.commit()
        childFragmentManager.beginTransaction()?.replace(R.id.content_branch_detail, branchDetailFragment, TAG_FRAGMENT_STORE_DETAIL)?.commit()
    }
}
