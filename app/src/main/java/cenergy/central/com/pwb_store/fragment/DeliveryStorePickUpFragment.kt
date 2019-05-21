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
    private var branches: ArrayList<Branch> = arrayListOf()
    private val branchesFragment = BranchesFragment.newInstance()
    private val branchDetailFragment = BranchDetailFragment.newInstance()
    private var totalBranch: Int = 0

    companion object {
        const val TAG_FRAGMENT_STORES = "fragment_stores"
        const val TAG_FRAGMENT_STORE_DETAIL = "fragment_store_detail"
        private const val ARG_TOTAL_BRANCH = "total_branch"

        fun newInstance(totalBranch: Int): DeliveryStorePickUpFragment {
            val fragment = DeliveryStorePickUpFragment()
            val args = Bundle()
            args.putInt(ARG_TOTAL_BRANCH, totalBranch)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        listener = context as PaymentProtocol
        branches = listener?.getBranches() ?: arrayListOf()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        totalBranch = arguments?.getInt(ARG_TOTAL_BRANCH, 0) ?: 0
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

    private fun updateBranches(branches: ArrayList<Branch>) {
        this.branches = branches
        branchesFragment.updateBranches(branches, totalBranch)
    }

    fun updateStoreDetail(branch: Branch) {
        branchDetailFragment.updateBranchDetail(branch)
    }

    private fun setupView() {
        childFragmentManager.beginTransaction()?.replace(R.id.content_branches, branchesFragment, TAG_FRAGMENT_STORES)?.commit()
        childFragmentManager.beginTransaction()?.replace(R.id.content_branch_detail, branchDetailFragment, TAG_FRAGMENT_STORE_DETAIL)?.commit()
    }
}
