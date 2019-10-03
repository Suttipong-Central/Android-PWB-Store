package cenergy.central.com.pwb_store.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.activity.CheckoutType
import cenergy.central.com.pwb_store.activity.interfaces.PaymentProtocol
import cenergy.central.com.pwb_store.extensions.getDiff
import cenergy.central.com.pwb_store.model.Branch
import cenergy.central.com.pwb_store.model.response.BranchResponse
import cenergy.central.com.pwb_store.realm.RealmController
import kotlinx.android.synthetic.main.fragment_delivery_stores.*

class DeliveryStorePickUpFragment : Fragment() {

    private var listener: PaymentProtocol? = null
    private var items: ArrayList<BranchResponse> = arrayListOf()
    private var displayItems: ArrayList<BranchResponse> = arrayListOf()
    private val branchesFragment = BranchesFragment.newInstance()
    private val branchDetailFragment = BranchDetailFragment.newInstance()
    private var checkoutType = CheckoutType.NORMAL

    companion object {
        const val TAG_FRAGMENT_STORES = "fragment_stores"
        const val TAG_FRAGMENT_STORE_DETAIL = "fragment_store_detail"
        private const val ARG_CHECKOUT_TPYE = "arg_checkout_type"

        fun newInstance(is2hrProduct: Boolean = false): DeliveryStorePickUpFragment {
            val fragment = DeliveryStorePickUpFragment()
            val args = Bundle()
            args.putString(ARG_CHECKOUT_TPYE, if (is2hrProduct) CheckoutType.ISPU.name else CheckoutType.NORMAL.name)
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
        val arg = arguments?.getString(ARG_CHECKOUT_TPYE)
        checkoutType = when (arg) {
            CheckoutType.ISPU.name -> CheckoutType.ISPU
            else -> CheckoutType.NORMAL
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = LayoutInflater.from(context).inflate(R.layout.fragment_delivery_stores, container, false)
        this.displayItems = getMultiStorePickup()

        if (this.items.isEmpty() || this.displayItems.isEmpty()) {
            groupDisplay.visibility = View.GONE
            storeListEmpty.visibility = View.VISIBLE
        } else {
            // create fragment branch list
            childFragmentManager.beginTransaction().replace(R.id.content_branches, branchesFragment, TAG_FRAGMENT_STORES).commit()
            // create fragment branch detail
            childFragmentManager.beginTransaction().replace(R.id.content_branch_detail, branchDetailFragment, TAG_FRAGMENT_STORE_DETAIL).commit()
            groupDisplay.visibility = View.VISIBLE
            storeListEmpty.visibility = View.GONE
        }

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
    }

    fun updateStoreDetail(branchResponse: BranchResponse) {
        branchDetailFragment.updateBranchDetail(branchResponse, this.checkoutType)
    }

    private fun setupView() {
        titleTextView.text = getString(if (checkoutType == CheckoutType.NORMAL) R.string.delivery else R.string.delivery_2hr_pickup)
        branchesFragment.updateBranches(this.displayItems, checkoutType)
    }

    /*
    * Logic display store pick up
    * */
    private fun getMultiStorePickup(): ArrayList<BranchResponse> {
        val multiStorePickup = RealmController.getInstance().storePickupLists

        return if (multiStorePickup != null && multiStorePickup.isNotEmpty()) {
            if (items.isEmpty()) return items // isEmpty?

            val storesList = arrayListOf<List<Branch>>()
            val stores = arrayListOf<Branch>()
            items.mapTo(stores, { it.branch })

            // add current store pickup
            storesList.add(stores)

            // add store pick from cache
            multiStorePickup.forEach {
                storesList.add(it.stores)
            }

            val newItem = arrayListOf<BranchResponse>()
            val diffStores = storesList.getDiff()

            items.forEach { branchResponse ->
                val result = diffStores.find { it.storeId == branchResponse.branch.storeId }
                if (result != null) {
                    newItem.add(branchResponse)
                }
            }

            return newItem
        } else {
            items
        }

    }
}
