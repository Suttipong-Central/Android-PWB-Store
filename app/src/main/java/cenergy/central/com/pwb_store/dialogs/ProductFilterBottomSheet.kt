package cenergy.central.com.pwb_store.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.dialogs.adapter.ProductFilterAdapter
import cenergy.central.com.pwb_store.model.FilterItem
import cenergy.central.com.pwb_store.model.ProductFilter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_product_filter.*

interface ProductFilterListener {
    fun getChildCategory(): String
    fun getProductFilters(): List<ProductFilter>
    fun getMoreFilters(): ArrayList<Pair<String, ArrayList<String>>>
    fun getTotalProducts(): Int
    fun getCategoryIdLv4(): String?

    fun onTopicClickListener(filterItem: FilterItem)
    fun onSelectFilter(filter: FilterView.FilterCheckBoxView)
    fun onUnSelectFilter(filter: FilterView.FilterCheckBoxView)
    fun onResetFilter()
    fun onCloseListener()
}

sealed class FilterView {
    abstract var viewType: Int

    data class FilterTileView(
            val title: String,
            override var viewType: Int = ProductFilterAdapter.PRODUCT_FILTER_TITLE_VIEW) : FilterView()

    data class FilterTopicView(
            val filterItem: FilterItem,
            override var viewType: Int = ProductFilterAdapter.PRODUCT_FILTER_TOPIC_VIEW) : FilterView()

    data class FilterCheckBoxView(
            val code: String,
            val filterItem: FilterItem,
            var selected: Boolean = false,
            override var viewType: Int = ProductFilterAdapter.PRODUCT_FILTER_CHECKBOX_VIEW) : FilterView()
}

class ProductFilterBottomSheet : BottomSheetDialogFragment() {
    private val productFilterAdapter by lazy { ProductFilterAdapter() }
    private var productFilters = listOf<ProductFilter>()
    private var moreFilter: ArrayList<Pair<String, ArrayList<String>>> = arrayListOf()
    private var childCategory = ""
    private var totalProducts: Int = 0
    var listener: ProductFilterListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_product_filter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        updateProductFilters()
    }

    private fun initView() {
        productFilterRecycler?.apply {
            productFilterAdapter.setFilterListener(listener)
            adapter = productFilterAdapter
            layoutManager = LinearLayoutManager(context)
        }
        closeFilterBtn?.setOnClickListener { listener?.onCloseListener() }
        applyBtn?.setOnClickListener { listener?.onCloseListener() }
        resetBtn?.setOnClickListener { listener?.onResetFilter() }
    }

    /**
     * Note: [Handle Product Filters]
     * filter options -- hide about rating and hide test option
     * session of shipping -- show only ispu
     * **/
    fun updateProductFilters() {
        this.totalProducts = listener?.getTotalProducts() ?: 0
        this.productFilters = listener?.getProductFilters() ?: listOf()
        this.childCategory = listener?.getChildCategory() ?: ""
        this.moreFilter = listener?.getMoreFilters() ?: arrayListOf()
        val items = arrayListOf<FilterView>()
        val filters = productFilters.filter {
            !it.code.contains("_rating")
        }
        filters.filter {
            !it.code.contains("_test")
        }.sortedBy { it.position }.forEach {
            // handle add filter
            if (it.code == "shipping_methods") {
                val childItem = it.items.firstOrNull { t -> t.value == "storepickup_ispu" }
                if (it.items.isNotEmpty() && childItem != null) {
                    items.add(FilterView.FilterTileView(it.name))
                    items.add(FilterView.FilterCheckBoxView(it.code, childItem,
                            // check selected
                            moreFilter.firstOrNull { moreFilter -> moreFilter.first == it.code &&
                                        moreFilter.second.firstOrNull { s -> s == childItem.value } != null } != null
                    ))
                }
            } else {
                items.add(FilterView.FilterTileView(it.name))

                // handle add child filter items
                it.items.forEach { t ->
                    if (it.code == "category_id") {
                         if (listener?.getCategoryIdLv4() != null){
                             if (childCategory.contains(t.value) && listener?.getCategoryIdLv4()!!.contains(t.value))
                                 items.add(FilterView.FilterTopicView(t))
                         } else {
                             if (childCategory.contains(t.value))
                                 items.add(FilterView.FilterTopicView(t))
                         }
                    } else {
                        items.add(FilterView.FilterCheckBoxView(it.code, t,
                                // check selected
                                moreFilter.firstOrNull { moreFilter -> moreFilter.first == it.code &&
                                moreFilter.second.firstOrNull { s -> s == t.value } != null } != null
                        ))
                    }
                }
            }

        }
        productFilterAdapter.submitList(items)
        applyBtn?.text = String.format(getString(R.string.format_add_filter), totalProducts)
    }
}
