package cenergy.central.com.pwb_store.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.fragment.interfaces.StorePickUpListener
import cenergy.central.com.pwb_store.model.response.BranchResponse

class BranchDetailFragment : Fragment() {
    private lateinit var tvStoreSelect: TextView
    private lateinit var tvTitle: TextView
    private lateinit var tvTitleAddress: TextView
    private lateinit var tvAddress: TextView
    private lateinit var tvTitleContract: TextView
    private lateinit var tvContract: TextView
    private lateinit var tvTitleOpenStore: TextView
    private lateinit var tvOpenStore: TextView
    private lateinit var selectedButton: Button

    private var listener: StorePickUpListener? = null

    companion object {
        fun newInstance(): BranchDetailFragment {
            val fragment = BranchDetailFragment()
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
        val rootView = LayoutInflater.from(context).inflate(R.layout.fragment_store_detail, container, false)
        tvStoreSelect = rootView.findViewById(R.id.store_select)
        tvTitle = rootView.findViewById(R.id.store_name_title)
        tvTitleAddress = rootView.findViewById(R.id.store_address_title)
        tvAddress = rootView.findViewById(R.id.store_address_txt)
        tvTitleContract = rootView.findViewById(R.id.store_contact_title)
        tvContract = rootView.findViewById(R.id.store_contact_txt)
        tvTitleOpenStore = rootView.findViewById(R.id.store_open_title)
        tvOpenStore = rootView.findViewById(R.id.store_open_txt)
        selectedButton = rootView.findViewById(R.id.selectButton)
        hideContentView()
        return rootView
    }

    fun updateBranchDetail(branchResponse: BranchResponse) {
        val branch = branchResponse.branch
        tvTitle.text = branch.storeName
        tvAddress.text = branch.getBranchAddress()
        tvContract.text = if (branch.phone.isNotBlank()) branch.phone else "-"
        tvOpenStore.text = if (branch.description.isNotBlank()) branch.description else "-"
        selectedButton.setOnClickListener {
            listener?.onSelectedStore(branch)
        }
        showContentView()
    }

    private fun hideContentView() {
        tvTitle.visibility = View.INVISIBLE
        tvTitleAddress.visibility = View.INVISIBLE
        tvAddress.visibility = View.INVISIBLE
        tvTitleContract.visibility = View.INVISIBLE
        tvContract.visibility = View.INVISIBLE
        tvTitleOpenStore.visibility = View.INVISIBLE
        tvOpenStore.visibility = View.INVISIBLE
        selectedButton.visibility = View.INVISIBLE
    }

    private fun showContentView() {
        tvStoreSelect.visibility = View.INVISIBLE
        tvTitle.visibility = View.VISIBLE
        tvTitleAddress.visibility = View.VISIBLE
        tvAddress.visibility = View.VISIBLE
        tvTitleContract.visibility = View.VISIBLE
        tvContract.visibility = View.VISIBLE
        tvTitleOpenStore.visibility = View.VISIBLE
        tvOpenStore.visibility = View.VISIBLE
        selectedButton.visibility = View.VISIBLE
    }
}