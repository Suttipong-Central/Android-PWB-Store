package cenergy.central.com.pwb_store.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.activity.interfaces.CompareProtocol
import cenergy.central.com.pwb_store.adapter.CompareProductAdapter
import cenergy.central.com.pwb_store.extensions.getDetailList
import cenergy.central.com.pwb_store.extensions.isProductInStock
import cenergy.central.com.pwb_store.model.CompareProduct
import cenergy.central.com.pwb_store.model.response.CompareProductResponse
import cenergy.central.com.pwb_store.realm.RealmController
import kotlinx.android.synthetic.main.fragment_compare.*

class CompareFragment : Fragment() {

    private var compareProductAdapter: CompareProductAdapter? = null

    private val database = RealmController.getInstance()
    private lateinit var listener: CompareProtocol
    private var compareProductDetailList: List<CompareProductResponse> = arrayListOf()
    private var compareProducts: List<CompareProduct> = arrayListOf()

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context != null) {
            listener = context as CompareProtocol
            compareProducts = listener.getCompateProducts()
            compareProductDetailList = listener.getCompareProductDetailList()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_compare, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        compareProductAdapter = context?.let { CompareProductAdapter(it) }
        compareRecyclerView.layoutManager = LinearLayoutManager(context)
        compareRecyclerView.adapter = compareProductAdapter

        updateCompareList()
    }

    // check product in strock
    private fun investigateProductInStock() {
        compareProducts.forEach {
            it.inStock = context.isProductInStock(it)
        }
    }

    fun updateCompareList() {
        investigateProductInStock() // update product stock
        compareProductAdapter?.updateCompareProducts(compareProducts, compareProductDetailList.getDetailList())
    }

    companion object {
        const val tag = "CompareFragment"
        fun newInstance(): CompareFragment {
            val fragment = CompareFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}
