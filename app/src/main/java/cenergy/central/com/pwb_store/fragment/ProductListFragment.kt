package cenergy.central.com.pwb_store.fragment

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.adapter.ProductListAdapter
import cenergy.central.com.pwb_store.adapter.interfaces.OnBrandFilterClickListener
import cenergy.central.com.pwb_store.helpers.DialogHelper
import cenergy.central.com.pwb_store.manager.ApiResponseCallback
import cenergy.central.com.pwb_store.manager.HttpManagerMagento.Companion.getInstance
import cenergy.central.com.pwb_store.manager.api.ProductListAPI
import cenergy.central.com.pwb_store.manager.bus.event.CategoryTwoBus
import cenergy.central.com.pwb_store.manager.bus.event.ProductFilterItemBus
import cenergy.central.com.pwb_store.manager.bus.event.SortingHeaderBus
import cenergy.central.com.pwb_store.manager.bus.event.SortingItemBus
import cenergy.central.com.pwb_store.model.*
import cenergy.central.com.pwb_store.model.response.ProductResponse
import cenergy.central.com.pwb_store.realm.RealmController
import cenergy.central.com.pwb_store.utils.Analytics
import cenergy.central.com.pwb_store.utils.DialogUtils
import cenergy.central.com.pwb_store.utils.Screen
import cenergy.central.com.pwb_store.view.PowerBuyPopupWindow
import cenergy.central.com.pwb_store.view.PowerBuyTextView
import io.realm.RealmList
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.util.*

class ProductListFragment : Fragment(), View.OnClickListener, OnBrandFilterClickListener {
    // Analytic
    private var analytics: Analytics? = null
    private var productCount: PowerBuyTextView? = null
    private var mProductLayout: LinearLayout? = null
    private var mRecyclerView: RecyclerView? = null
    private var mProductListAdapter: ProductListAdapter? = null
    private var mLayoutManger: GridLayoutManager? = null
    private var categoriesLv3: ArrayList<Category>? = null
    private var brands: List<FilterItem> = ArrayList()
    private var mSortingList: SortingList? = null
    private var title: String? = null
    private var mPowerBuyPopupWindow: PowerBuyPopupWindow? = null
    private var mProgressDialog: ProgressDialog? = null
    private var isDoneFilter = false
    private var isSearch = false
    private var categoryId: String? = null
    private var brandName: String? = null
    //Sort
    private var sortName: String? = ""
    private var sortType: String? = ""
    private var categoryLv2: Category? = null
    // Page
    private var isSorting = false
    private var keyWord: String? = null
    private var productResponse: ProductResponse? = null
    // Realm
    private var database = RealmController.getInstance()

    private val onPopupDismissListener = PopupWindow.OnDismissListener { isDoneFilter = false }

    @Subscribe
    fun onEvent(productFilterItemBus: ProductFilterItemBus) {
        showProgressDialog()
        isDoneFilter = true
        isSorting = true
        brandName = "" // clear filter brand name
        val categoryLv3 = productFilterItemBus.productFilterItem
        val clickPosition = productFilterItemBus.position
        if (categoryLv3 != null && clickPosition != 0) {
            title = categoryLv3.departmentName
            categoryId = categoryLv3.id
        } else { // clear filter
            title = categoryLv2!!.departmentName
            categoryId = categoryLv2!!.id
        }
        val productResponseCache = database.getProductResponseByCategoryId(categoryId)
        if (productResponseCache != null){
            this.productResponse = productResponseCache
            // update text header
            setTextHeader(productResponseCache.products.size, title)

            // update product PLP
            if(productResponseCache.products.isNotEmpty()){
                mProductListAdapter!!.setProduct(productResponseCache.products)
            } else {
                mProductListAdapter!!.setError()
            }
            // update brands when use cache productResponse
            brands = if (productResponseCache.filters.isNotEmpty()){
                productResponseCache.filters[0]!!.items
            } else {
                arrayListOf()
            }
            mRecyclerView!!.scrollToPosition(0)
            dismissProgressDialog()
        } else {
            retrieveProductList(true) // load new productResponse
        }
        mPowerBuyPopupWindow!!.updateSingleProductFilterItem(categoryLv3)

        if (mPowerBuyPopupWindow!!.isShowing) {
            mPowerBuyPopupWindow!!.dismiss()
        }
    }

    @Subscribe
    fun onEvent(sortingHeaderBus: SortingHeaderBus) {
        mPowerBuyPopupWindow!!.setSortingItem(sortingHeaderBus.sortingHeader)
    }

    @Subscribe
    fun onEvent(sortingItemBus: SortingItemBus) {
        showProgressDialog()
        val sortingItem = sortingItemBus.sortingItem
        isDoneFilter = true
        isSorting = true
        sortName = sortingItem.slug
        sortType = sortingItem.value
        if (sortName == "brand"){
            when(sortType){
                "ASC" -> {
                    mProductListAdapter!!.setProduct(database.getProductResponseByCategoryId(categoryId).products.sortedBy { it.brand })
                }
                else -> {
                    mProductListAdapter!!.setProduct(database.getProductResponseByCategoryId(categoryId).products.sortedByDescending { it.brand })
                }
            }
        }
        setTextHeader(database.getProductResponseByCategoryId(categoryId).products.size, title)
        mRecyclerView!!.scrollToPosition(0)
        mPowerBuyPopupWindow!!.updateSingleSortingItem(sortingItem)
        if (mPowerBuyPopupWindow!!.isShowing) {
            mPowerBuyPopupWindow!!.dismiss()
        }
        dismissProgressDialog()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        savedInstanceState?.let { onRestoreInstanceState(it) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_product_list, container, false)
        initInstances(rootView, savedInstanceState)
        return rootView
    }

    override fun onResume() {
        super.onResume()
        analytics!!.trackScreen(Screen.PRODUCT_LIST)
    }

    private fun init() {
        if (context != null) {
            analytics = Analytics(context!!)
        }
        // Init Fragment level's variable(s) here
        if (arguments != null) {
            title = arguments!!.getString(ARG_TITLE)
            isSearch = arguments!!.getBoolean(ARG_SEARCH)
            categoryId = arguments!!.getString(ARG_DEPARTMENT_ID)
            categoryLv2 = arguments!!.getParcelable(ARG_CATEGORY)
            keyWord = arguments!!.getString(ARG_KEY_WORD)
            // no search
            if (!isSearch) { // setup product filter list
                loadCategoryLv3(categoryLv2)
            }
        }
        // sorting
        val sortingItems: MutableList<SortingItem> = ArrayList()
        sortingItems.add(SortingItem(1, getString(R.string.a_to_z), "brand", "ASC", "1", false))
        sortingItems.add(SortingItem(2, getString(R.string.z_to_a), "brand", "DESC", "2", false))
        val sortingHeaders: MutableList<SortingHeader> = ArrayList()
        sortingHeaders.add(SortingHeader("0", "Sorting", "sorting", "single", sortingItems))
        mSortingList = SortingList(sortingHeaders)
        for (sortingHeader in sortingHeaders) {
            mSortingList!!.updateSortOption(sortingHeader, true)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.layout_title -> EventBus.getDefault().post(CategoryTwoBus())
            R.id.layout_product -> if (categoriesLv3 == null || categoriesLv3!!.isEmpty()) {
                mPowerBuyPopupWindow!!.dismiss()
            } else {
                mPowerBuyPopupWindow!!.setRecyclerViewFilter(categoriesLv3)
                mPowerBuyPopupWindow!!.showAsDropDown(v)
            }
            R.id.layout_sort ->  // Create productResponse for check because we mock up sort items
                if (mSortingList == null || productResponse == null || productResponse!!.products.isEmpty()) {
                    mPowerBuyPopupWindow!!.dismiss()
                } else {
                    mPowerBuyPopupWindow!!.setRecyclerViewSorting(mSortingList)
                    mPowerBuyPopupWindow!!.showAsDropDown(v)
                }
            R.id.layout_brand -> if (brands.isEmpty()) {
                mPowerBuyPopupWindow!!.dismiss()
            } else {
                mPowerBuyPopupWindow!!.setRecyclerViewFilterByBrand(brands, this)
                mPowerBuyPopupWindow!!.showAsDropDown(v)
            }
        }
    }

    private fun initInstances(rootView: View, savedInstanceState: Bundle?) { // Init 'View' instance(s) with rootView.findViewById here
//        ButterKnife.bind(this, rootView);
        mProductListAdapter = ProductListAdapter(context)
        // setup widget view
        val productTitle: PowerBuyTextView = rootView.findViewById(R.id.txt_title_product)
        val layoutFilter: ConstraintLayout = rootView.findViewById(R.id.layout_filter)
        productCount = rootView.findViewById(R.id.txt_product_count)
        //View Members
        mRecyclerView = rootView.findViewById(R.id.recycler_view_list)
        productTitle.text = title
        productCount?.text = title
        // setup onClick
        val titleLayout = rootView.findViewById<LinearLayout>(R.id.layout_title)
        mProductLayout = rootView.findViewById(R.id.layout_product)
        val sortLayout = rootView.findViewById<LinearLayout>(R.id.layout_sort)
        val brandLayout = rootView.findViewById<LinearLayout>(R.id.layout_brand)
        titleLayout.setOnClickListener(this)
        mProductLayout?.setOnClickListener(this)
        sortLayout.setOnClickListener(this)
        brandLayout.setOnClickListener(this)
        if (isSearch) {
            layoutFilter.visibility = View.GONE
        }
        if (categoriesLv3 == null) {
            mProductLayout?.visibility = View.GONE
        } else {
            mProductLayout?.visibility = View.VISIBLE
        }
        popUpShow()
        mLayoutManger = GridLayoutManager(context, 3, LinearLayoutManager.VERTICAL, false)
        mLayoutManger!!.spanSizeLookup = mProductListAdapter!!.spanSize
        mRecyclerView!!.layoutManager = mLayoutManger
        mRecyclerView!!.adapter = mProductListAdapter
        if (savedInstanceState == null) {
            showProgressDialog()
            retrieveProductList()
        }
    }

    private fun showProgressDialog() {
        if (mProgressDialog != null && !mProgressDialog!!.isShowing) {
            mProgressDialog!!.show()
        } else {
            mProgressDialog = DialogUtils.createProgressDialog(context)
            mProgressDialog?.show()
        }
    }

    private fun dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog!!.isShowing) {
            mProgressDialog!!.dismiss()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        EventBus.getDefault().register(this)
    }

    override fun onDetach() {
        EventBus.getDefault().unregister(this)
        super.onDetach()
    }

    /*
     * Save Instance State Here
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save Instance State here
        outState.putParcelable(ARG_CATEGORY, categoryLv2)
        outState.putParcelableArrayList(ARG_PRODUCT_FILTER, categoriesLv3)
        outState.putString(ARG_DEPARTMENT_ID, categoryId)
        outState.putString(ARG_SORT_NAME, sortName)
        outState.putString(ARG_SORT_TYPE, sortType)
        outState.putBoolean(ARG_IS_DONE, isDoneFilter)
        outState.putString(ARG_TITLE, title)
        outState.putString(ARG_KEY_WORD, keyWord)
        outState.putBoolean(ARG_SEARCH, isSearch)
        outState.putBoolean(ARG_IS_SORTING, isSorting)
    }

    /*
     * Restore Instance State Here
     */
    private fun onRestoreInstanceState(savedInstanceState: Bundle) { // Restore Instance State here
        categoryLv2 = savedInstanceState.getParcelable(ARG_CATEGORY)
        categoriesLv3 = savedInstanceState.getParcelableArrayList(ARG_PRODUCT_FILTER)
        categoryId = savedInstanceState.getString(ARG_DEPARTMENT_ID)
        sortName = savedInstanceState.getString(ARG_SORT_NAME)
        sortType = savedInstanceState.getString(ARG_SORT_TYPE)
        isDoneFilter = savedInstanceState.getBoolean(ARG_IS_DONE)
        title = savedInstanceState.getString(ARG_TITLE)
        keyWord = savedInstanceState.getString(ARG_KEY_WORD)
        isSearch = savedInstanceState.getBoolean(ARG_SEARCH)
        isSorting = savedInstanceState.getBoolean(ARG_IS_SORTING)
    }

    private fun popUpShow() {
        if (context != null) {
            val layoutInflater = (context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
            mPowerBuyPopupWindow = PowerBuyPopupWindow(activity, layoutInflater)
            mPowerBuyPopupWindow!!.setOnDismissListener(onPopupDismissListener)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setTextHeader(total: Int, name: String?) {
        context?.let {
            productCount!!.text = name + " " + it.getString(R.string.filter_count, total.toString())
        }
    }

    private fun retrieveProductList(force: Boolean = false) {
        if (context != null) {
            val forceRefresh = if (isSearch) true else force
            val searchTerm = if (isSearch) keyWord else categoryId

            searchTerm?.let {
                ProductListAPI().retrieveProducts(context!!, isSearch, it, forceRefresh,
                        object : ApiResponseCallback<ProductResponse> {
                            override fun success(response: ProductResponse?) {
                                if (activity != null) {
                                    activity!!.runOnUiThread {
                                        updateProductList(response)
                                    }
                                }
                            }

                            override fun failure(error: APIError) {
                                if (activity != null) {
                                    activity!!.runOnUiThread {
                                        mProgressDialog!!.dismiss()
                                        // show error dialog
                                        if (context != null) {
                                            DialogHelper(context!!).showErrorDialog(error)
                                        }
                                    }
                                }
                            }
                        })
            }
        }
    }

    private fun updateProductList(response: ProductResponse?) {
        productResponse = response
        if (response != null) {
            if (response.filters.isNotEmpty()) {
                brands = response.filters[0]!!.items
                for (filterItem in brands) {
                    if (brandName != null && brandName == filterItem.value) {
                        filterItem.isSelected = true
                    }
                }
            } else {
                // clear brand when call cat lv3 brand is empty
                brands = arrayListOf()
            }
            if (response.products.isNotEmpty()) {
                mProductListAdapter!!.setProduct(response.products)
                setTextHeader(response.totalCount, title)
            } else {
                mProductListAdapter!!.setError()
                setTextHeader(0, title)
            }
        } else {
            if (mProductListAdapter!!.itemCount == 0) {
                mProductListAdapter!!.setError()
            }
            setTextHeader(0, title)
        }
        mProgressDialog!!.dismiss()
    }

    // region {@link OnBrandFilterClickListener}
    override fun onClickedItem(filterItem: FilterItem?) {
        isDoneFilter = true
        if (filterItem != null) {
            brandName = filterItem.value // brand name
            showProgressDialog()
            val productFilterByBrand = RealmList<Product>()
            productFilterByBrand.addAll(database.getProductResponseByCategoryId(categoryId).products.filter { it.brand == brandName })
            setTextHeader(productFilterByBrand.size, title)
            mProductListAdapter!!.setProduct(productFilterByBrand)
            mRecyclerView!!.scrollToPosition(0)
            mPowerBuyPopupWindow!!.updateSingleBrandFilterItem(filterItem)
        } else {
            clearBrandFilter()
        }
        if (mPowerBuyPopupWindow!!.isShowing) {
            mPowerBuyPopupWindow!!.dismiss()
        }
        dismissProgressDialog()
    }

    private fun clearBrandFilter() {
        brandName = "" // clear brand
        showProgressDialog()
        setTextHeader(database.getProductResponseByCategoryId(categoryId).products.size, title)
        mProductListAdapter!!.setProduct(database.getProductResponseByCategoryId(categoryId).products)
        mRecyclerView!!.scrollToPosition(0)
        mPowerBuyPopupWindow!!.updateSingleBrandFilterItem(null)
    }
    // endregion

    private fun loadCategoryLv3(categoryLv2: Category?) {
        if (context == null) return
        getInstance(context!!).retrieveCategory(categoryLv2!!.id,
                true, ArrayList(), object : ApiResponseCallback<List<Category>> {
            override fun success(response: List<Category>?) {
                if (activity != null) {
                    activity!!.runOnUiThread {
                        if (response != null){
                            categoriesLv3 = ArrayList() // clear category lv3 list
                            categoriesLv3!!.addAll(response)
                            mProductLayout!!.visibility = View.VISIBLE // show product layout
                        }
                    }
                }
            }

            override fun failure(error: APIError) {
                if (activity != null) {
                    activity!!.runOnUiThread {
                        Log.e(TAG, "onFailure: " + error.errorUserMessage)
                        categoriesLv3 = null
                        mProductLayout!!.visibility = View.GONE
                    }
                }
            }
        })
    }

    companion object {
        private val TAG = ProductListFragment::class.java.simpleName
        private const val ARG_TITLE = "ARG_TITLE"
        private const val ARG_SEARCH = "ARG_SEARCH"
        private const val ARG_DEPARTMENT_ID = "ARG_DEPARTMENT_ID"
        private const val ARG_STORE_ID = "ARG_STORE_ID"
        private const val ARG_PRODUCT_FILTER = "ARG_PRODUCT_FILTER"
        private const val ARG_SORT_NAME = "ARG_SORT_NAME"
        private const val ARG_SORT_TYPE = "ARG_SORT_TYPE"
        private const val ARG_IS_DONE = "ARG_IS_DONE"
        private const val ARG_CATEGORY = "ARG_CATEGORY"
        private const val ARG_KEY_WORD = "ARG_KEY_WORD"
        private const val ARG_IS_SORTING = "ARG_IS_SORTING"

        fun newInstance(title: String?, search: Boolean, departmentId: String?,
                        storeId: String?, category: Category?, keyWord: String?): ProductListFragment {
            val fragment = ProductListFragment()
            val args = Bundle()
            args.putString(ARG_TITLE, title)
            args.putBoolean(ARG_SEARCH, search)
            args.putString(ARG_DEPARTMENT_ID, departmentId)
            args.putString(ARG_STORE_ID, storeId)
            args.putParcelable(ARG_CATEGORY, category)
            args.putString(ARG_KEY_WORD, keyWord)
            fragment.arguments = args
            return fragment
        }

        @JvmStatic
        fun newInstance(category: Category, search: Boolean, keyWord: String?): ProductListFragment {
            val fragment = ProductListFragment()
            val args = Bundle()
            args.putString(ARG_TITLE, category.departmentName)
            args.putBoolean(ARG_SEARCH, search)
            args.putString(ARG_DEPARTMENT_ID, category.id)
            args.putString(ARG_KEY_WORD, keyWord)
            args.putParcelable(ARG_CATEGORY, category)
            fragment.arguments = args
            return fragment
        }
    }
}