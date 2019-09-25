package cenergy.central.com.pwb_store.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.activity.interfaces.PaymentProtocol
import cenergy.central.com.pwb_store.model.response.BranchResponse
import kotlinx.android.synthetic.main.fragment_delivery_stores.*

class DeliveryStorePickUpFragment : Fragment() {

    private var listener: PaymentProtocol? = null
    private var items: ArrayList<BranchResponse> = arrayListOf()
    private val branchesFragment = BranchesFragment.newInstance()
    private val branchDetailFragment = BranchDetailFragment.newInstance()
    private var checkoutType = CHECKOUT_STS

    companion object {
        const val TAG_FRAGMENT_STORES = "fragment_stores"
        const val TAG_FRAGMENT_STORE_DETAIL = "fragment_store_detail"
        private const val ARG_CHECKOUT_TPYE = "arg_checkout_type"
        private const val CHECKOUT_ISPU = "product_ispu"
        private const val CHECKOUT_STS = "product_sts"

        fun newInstance(is2hrProduct: Boolean = false): DeliveryStorePickUpFragment {
            val fragment = DeliveryStorePickUpFragment()
            val args = Bundle()
            args.putString(ARG_CHECKOUT_TPYE, if (is2hrProduct) CHECKOUT_ISPU else CHECKOUT_STS)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        listener = context as PaymentProtocol
        items = listener?.getBranches() ?: arrayListOf()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkoutType = arguments?.getString(ARG_CHECKOUT_TPYE) ?: CHECKOUT_STS
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = LayoutInflater.from(context).inflate(R.layout.fragment_delivery_stores, container, false)
        // create fragment branch list
        childFragmentManager.beginTransaction().replace(R.id.content_branches, branchesFragment, TAG_FRAGMENT_STORES).commit()
        // create fragment branch detail
        childFragmentManager.beginTransaction().replace(R.id.content_branch_detail, branchDetailFragment, TAG_FRAGMENT_STORE_DETAIL).commit()

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
    }

    fun updateStoreDetail(branchResponse: BranchResponse) {
        branchDetailFragment.updateBranchDetail(branchResponse)
    }

    private fun setupView() {
        titleTextView.text = getString(if (checkoutType == CHECKOUT_STS) R.string.delivery else R.string.delivery_2hr_pickup)
        branchesFragment.updateBranches(items)
    }
}
