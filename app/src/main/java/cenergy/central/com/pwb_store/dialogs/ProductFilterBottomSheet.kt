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
            val filterItem: FilterItem,
            var selected: Boolean = false,
            override var viewType: Int = ProductFilterAdapter.PRODUCT_FILTER_CHECKBOX_VIEW) : FilterView()
}

class ProductFilterBottomSheet : BottomSheetDialogFragment() {
    private val productFilterAdapter by lazy { ProductFilterAdapter() }
    private var productFilters = listOf<ProductFilter>()
    private var childCategory = ""
    var listener: ProductFilterListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.productFilters = listener?.getProductFilters() ?: listOf()
        this.childCategory = listener?.getChildCategory() ?: ""
    }

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
            adapter = productFilterAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    /**
     * Note: [Handle Product Filters]
     * filter options -- hide about rating
     * session of shipping -- show only ispu
     * **/
    private fun updateProductFilters() {
        val items = arrayListOf<FilterView>()
        val filters = productFilters.filter {
            !it.code.contains("rating")
        }
        filters.sortedBy { it.position }.forEach {
            // handle add filter
            if (it.code == "shipping_methods") {
                val childItem = it.items.firstOrNull { t -> t.value == "storepickup_ispu" }
                if (it.items.isNotEmpty() && childItem != null) {
                    items.add(FilterView.FilterTileView(it.name))
                    items.add(FilterView.FilterCheckBoxView(childItem))
                }
            } else {
                items.add(FilterView.FilterTileView(it.name))

                // handle add child filter items
                it.items.forEach { t ->
                    if (it.code == "category_id") {
                        if (childCategory.contains(t.value))
                            items.add(FilterView.FilterTopicView(t))
                    } else {
                        items.add(FilterView.FilterCheckBoxView(t))
                    }
                }
            }

        }
        productFilterAdapter.submitList(items)
    }
}
