package cenergy.central.com.pwb_store.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.adapter.StoresDeliveryAdapter
import cenergy.central.com.pwb_store.adapter.interfaces.StoreClickListener
import cenergy.central.com.pwb_store.fragment.interfaces.StorePickUpListener
import cenergy.central.com.pwb_store.manager.ApiResponseCallback
import cenergy.central.com.pwb_store.manager.HttpManagerMagento
import cenergy.central.com.pwb_store.model.APIError
import cenergy.central.com.pwb_store.model.Branch
import cenergy.central.com.pwb_store.model.response.BranchResponse

class BranchesFragment : Fragment(), StoreClickListener {

    private val storesAdapter = StoresDeliveryAdapter(this)
    var branches: ArrayList<Branch?> = arrayListOf()
    private lateinit var storesRecycler: RecyclerView
    private var listener: StorePickUpListener? = null

    private var currentPage: Int = 1
    private lateinit var layoutManager: LinearLayoutManager
    private var isLoadingMore: Boolean = false
    private var previousTotal: Int = 0
    private var totalPage: Int = 0

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
        storesRecycler.addOnScrollListener(getCustomScrolling())
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        storesRecycler.layoutManager = layoutManager
        storesRecycler.adapter = storesAdapter
        storesAdapter.branches = branches
    }

    fun updateBranches(branches: ArrayList<Branch?>, totalBranch: Int) {
        this.branches = branches
        this.totalPage = totalPageCal(totalBranch)
        storesAdapter.branches = branches
    }

    // region {@link StoreClickListener.onItemClicked}
    override fun onItemClicked(branch: Branch) {
        listener?.onUpdateStoreDetail(branch)
    }
    // endregion

    private fun totalPageCal(total: Int): Int {
        val num: Int
        val x = total.toFloat() / PER_PAGE
        num = Math.ceil(x.toDouble()).toInt()
        return num
    }

    private fun getCustomScrolling(): RecyclerView.OnScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val totalItemCount = layoutManager.itemCount
            val visibleItemCount = layoutManager.childCount
            val firstVisibleItem = layoutManager.findFirstVisibleItemPosition()

            if (isLoadingMore && totalItemCount > previousTotal) {
                isLoadingMore = false
                previousTotal = totalItemCount
            }

            val visibleThreshold = 10
            if (!isLoadingMore
                    && totalItemCount <= firstVisibleItem + visibleItemCount + visibleThreshold
                    && isStillHavePages()) {

                if (this@BranchesFragment.branches[this@BranchesFragment.branches.lastIndex] != null) {
                    this@BranchesFragment.branches.add(null)
                }

                storesAdapter.branches = this@BranchesFragment.branches
                getStoresDelivery()
                isLoadingMore = true

            }
        }
    }

    private fun isStillHavePages(): Boolean {
        return currentPage < totalPage
    }

    private fun getStoresDelivery() {
        context?.let {
            HttpManagerMagento.getInstance(it).getBranches(PER_PAGE, getNextPage(), object : ApiResponseCallback<BranchResponse> {
                override fun success(response: BranchResponse?) {
                    if (response != null) {
                        if (response.items.isNotEmpty() && isAdded) {
                            currentPage = getNextPage()
                            this@BranchesFragment.branches.remove(null)
                            this@BranchesFragment.branches.addAll(response.items)
                            storesAdapter.branches = this@BranchesFragment.branches
                            if (storesAdapter.branches.size >= response.totalBranch) {
                                storesRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {}) // clear scroll
                            }
                        }
                    }
                }

                override fun failure(error: APIError) {
                    if (isAdded) {
                        showAlertDialog("", error.errorMessage)
                    }
                }
            })
        }
    }

    private fun showAlertDialog(title: String, message: String) {
        if (context != null) {
            val builder = AlertDialog.Builder(context!!, R.style.AlertDialogTheme)
                    .setMessage(message)
                    .setPositiveButton(getString(R.string.ok_alert)) { dialog, which -> dialog.dismiss() }

            if (!TextUtils.isEmpty(title)) {
                builder.setTitle(title)
            }
            builder.show()
        }
    }

    private fun getNextPage(): Int {
        return currentPage + 1
    }
}