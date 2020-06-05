package cenergy.central.com.pwb_store.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.activity.AvailableProtocol
import cenergy.central.com.pwb_store.adapter.AvailableStoreAdapter
import cenergy.central.com.pwb_store.model.StoreStock
import cenergy.central.com.pwb_store.realm.RealmController
import kotlinx.android.synthetic.main.fragment_avaliable.*

class AvailableFragment : Fragment() {

    private var listener: AvailableProtocol? = null
    private val userInformation by lazy { RealmController.getInstance().userInformation }
    private lateinit var recyclerView: RecyclerView
    private lateinit var availableStoreAdapter: AvailableStoreAdapter

    private var storeAvailableList: List<StoreStock> = arrayListOf()
    private var sortedBy: Int = SORT_NONE

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as AvailableProtocol
        listener?.let {
            this.storeAvailableList = it.getStoreAvailable()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_avaliable, container, false)
        setupView(rootView)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupStoreStocks(storeAvailableList)
        setupOnClickHeader()
    }

    private fun setupStoreStocks(items: List<StoreStock>) {
        if (userInformation != null && userInformation.store != null) {
            availableStoreAdapter.setStoreStockItems(userInformation.store!!.retailerId, items, sortedBy)
        } else {
            availableStoreAdapter.setStoreStockItems("", storeAvailableList, sortedBy)
        }
    }

    private fun setupView(rootView: View) {
        // Init 'View' instance(s) with rootView.findViewById here
        recyclerView = rootView.findViewById(R.id.recycler_view)
        availableStoreAdapter = AvailableStoreAdapter()
        recyclerView.setHasFixedSize(true)
        recyclerView.isNestedScrollingEnabled = true
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = availableStoreAdapter

    }

    private fun setupOnClickHeader() {
        tvStoreCode.setOnClickListener {
            clearArrowSorted()
            if (sortedBy == SORT_STORE_CODE_BY_ASC) {
                setSorted(tvStoreCode, false)
                sortItems(SORT_STORE_CODE_BY_DESC)
            } else {
                setSorted(tvStoreCode)
                sortItems(SORT_STORE_CODE_BY_ASC)
            }
        }

        tvStoreName.setOnClickListener {
            clearArrowSorted()
            if (sortedBy == SORT_STORE_NAME_BY_ASC) {
                setSorted(tvStoreName, false)
                sortItems(SORT_STORE_NAME_BY_DESC)
            } else {
                setSorted(tvStoreName)
                sortItems(SORT_STORE_NAME_BY_ASC)
            }
        }

        tvStoreStock.setOnClickListener {
            clearArrowSorted()
            if (sortedBy == SORT_STORE_STOCK_BY_ASC) {
                setSorted(tvStoreStock, false)
                sortItems(SORT_STORE_STOCK_BY_DESC)
            } else {
                setSorted(tvStoreStock)
                sortItems(SORT_STORE_STOCK_BY_ASC)
            }
        }
    }

    private fun clearArrowSorted() {
        tvStoreCode.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
        tvStoreName.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
        tvStoreStock.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
    }

    private fun setSorted(view: TextView, isAsc: Boolean = true) {
        view.setCompoundDrawablesWithIntrinsicBounds(0, 0, if (isAsc) R.drawable.ic_sort_asc else R.drawable.ic_sort_desc, 0)
    }

    private fun sortItems(sortBy: Int) {
        this.sortedBy = sortBy
        when (sortBy) {
            SORT_STORE_CODE_BY_ASC -> {
                setupStoreStocks(storeAvailableList.sortedBy { it.sellerCode })
            }

            SORT_STORE_CODE_BY_DESC -> {
                setupStoreStocks(storeAvailableList.sortedByDescending { it.sellerCode })
            }

            SORT_STORE_NAME_BY_ASC -> {
                setupStoreStocks(storeAvailableList.sortedBy { it.name })
            }

            SORT_STORE_NAME_BY_DESC -> {
                setupStoreStocks(storeAvailableList.sortedByDescending { it.name })
            }

            SORT_STORE_STOCK_BY_ASC -> {
                setupStoreStocks(storeAvailableList.sortedBy { it.qty })
            }

            SORT_STORE_STOCK_BY_DESC -> {
                setupStoreStocks(storeAvailableList.sortedByDescending { it.qty })
            }
        }
    }

    companion object {
        // sort type
        const val SORT_NONE = 0
        const val SORT_STORE_CODE_BY_ASC = 10
        const val SORT_STORE_CODE_BY_DESC = 11
        const val SORT_STORE_NAME_BY_ASC = 20
        const val SORT_STORE_NAME_BY_DESC = 21
        const val SORT_STORE_STOCK_BY_ASC = 30
        const val SORT_STORE_STOCK_BY_DESC = 31

        fun newInstance(): AvailableFragment {
            val fragment = AvailableFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}
