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
import cenergy.central.com.pwb_store.BuildConfig
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.adapter.ProductListAdapter
import cenergy.central.com.pwb_store.adapter.decoration.SpacesItemDecoration
import cenergy.central.com.pwb_store.adapter.interfaces.OnBrandFilterClickListener
import cenergy.central.com.pwb_store.helpers.DialogHelper
import cenergy.central.com.pwb_store.manager.ApiResponseCallback
import cenergy.central.com.pwb_store.manager.HttpManagerMagento.Companion.getInstance
import cenergy.central.com.pwb_store.manager.api.ProductListAPI.Companion.retrieveProducts
import cenergy.central.com.pwb_store.manager.bus.event.CategoryTwoBus
import cenergy.central.com.pwb_store.manager.bus.event.ProductFilterItemBus
import cenergy.central.com.pwb_store.manager.bus.event.SortingHeaderBus
import cenergy.central.com.pwb_store.manager.bus.event.SortingItemBus
import cenergy.central.com.pwb_store.model.*
import cenergy.central.com.pwb_store.model.body.FilterGroups
import cenergy.central.com.pwb_store.model.body.FilterGroups.Companion.createFilterGroups
import cenergy.central.com.pwb_store.model.body.SortOrder
import cenergy.central.com.pwb_store.model.body.SortOrder.Companion.createSortOrder
import cenergy.central.com.pwb_store.model.response.ProductResponse
import cenergy.central.com.pwb_store.realm.RealmController
import cenergy.central.com.pwb_store.utils.Analytics
import cenergy.central.com.pwb_store.utils.DialogUtils
import cenergy.central.com.pwb_store.utils.Screen
import cenergy.central.com.pwb_store.view.PowerBuyPopupWindow
import cenergy.central.com.pwb_store.view.PowerBuyTextView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.util.*
import kotlin.math.ceil

class ProductListFragment : Fragment(), View.OnClickListener, OnBrandFilterClickListener {
    // Analytic
    private var analytics: Analytics? = null
    private var productCount: PowerBuyTextView? = null
    private var layoutProgress: LinearLayout? = null
    private var mProductLayout: LinearLayout? = null
    //Data Member
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
    private var isLoadingMore = false
    private var isSorting = false
    private var mPreviousTotal = 0
    private var currentPage = 0
    private var totalPage = 0
    private var totalItem = 0
    private var mContext: Context? = null
    private var keyWord: String? = null
    private var productResponse: ProductResponse? = null
    // Realm
    private val db = RealmController.getInstance()

    private val ON_POPUP_DISMISS_LISTENER = PopupWindow.OnDismissListener { isDoneFilter = false }

    private val SCROLL: RecyclerView.OnScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val totalItemCount = mLayoutManger!!.itemCount
            val visibleItemCount = mLayoutManger!!.childCount
            val firstVisibleItem = mLayoutManger!!.findFirstVisibleItemPosition()
            if (isLoadingMore && totalItemCount > mPreviousTotal) {
                isLoadingMore = false
                mPreviousTotal = totalItemCount
            }
            val visibleThreshold = 10
            if (!isLoadingMore
                    && totalItemCount <= firstVisibleItem + visibleItemCount + visibleThreshold && isStillHavePages) {
                layoutProgress!!.visibility = View.VISIBLE
                retrieveProductList()
                isLoadingMore = true
            }
        }
    }

    @Subscribe
    fun onEvent(productFilterItemBus: ProductFilterItemBus) {
        showProgressDialog()
        isDoneFilter = true
        isSorting = true
        brandName = "" // clear filter brand name
        currentPage = 1 // clear current page
        val categoryLv3 = productFilterItemBus.productFilterItem
        val clickPosition = productFilterItemBus.position
        if (categoryLv3 != null && clickPosition != 0) {
            title = categoryLv3.departmentName
            categoryId = categoryLv3.id
        } else { // clear filter
            title = categoryLv2!!.departmentName
            categoryId = categoryLv2!!.id
        }
        resetPage()
        retrieveProductList()
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
        resetPage()
        sortName = sortingItem.slug
        sortType = sortingItem.value
        retrieveProductList()
        mPowerBuyPopupWindow!!.updateSingleSortingItem(sortingItem)
        if (mPowerBuyPopupWindow!!.isShowing) {
            mPowerBuyPopupWindow!!.dismiss()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        savedInstanceState?.let { onRestoreInstanceState(it) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_product_list, container, false)
        initInstances(rootView, savedInstanceState)
        resetPage()
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
        resetPage()
        // sorting
        val sortingItems: MutableList<SortingItem> = ArrayList()
        sortingItems.add(SortingItem(1, getString(R.string.low_to_high), "price", "ASC", "1", false))
        sortingItems.add(SortingItem(2, getString(R.string.high_to_low), "price", "DESC", "2", false))
        sortingItems.add(SortingItem(3, getString(R.string.a_to_z), "brand", "ASC", "3", false))
        sortingItems.add(SortingItem(4, getString(R.string.z_to_a), "brand", "DESC", "4", false))
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
        mProductListAdapter = ProductListAdapter(rootView.context)
        mProductListAdapter!!.showLoading()
        // setup widget view
        val productTitle: PowerBuyTextView = rootView.findViewById(R.id.txt_title_product)
        val layoutFilter: ConstraintLayout = rootView.findViewById(R.id.layout_filter)
        productCount = rootView.findViewById(R.id.txt_product_count)
        //View Members
        val mRecyclerView: RecyclerView = rootView.findViewById(R.id.recycler_view_list)
        layoutProgress = rootView.findViewById(R.id.layout_progress)
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
        mRecyclerView.layoutManager = mLayoutManger
        mRecyclerView.addItemDecoration(SpacesItemDecoration(0, LinearLayoutManager.VERTICAL))
        mRecyclerView.adapter = mProductListAdapter
        if (savedInstanceState == null) {
            showProgressDialog()
            retrieveProductList()
        }
        var scrollPosition = 0
        // If a layout manager has already been set, get current scroll position.
        if (mRecyclerView.layoutManager != null) {
            scrollPosition = (mRecyclerView.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
        }
        mRecyclerView.scrollToPosition(scrollPosition)
        mRecyclerView.addOnScrollListener(SCROLL)
    }

    private fun resetPage() {
        currentPage = 0
        totalPage = 1
        isLoadingMore = true
        mPreviousTotal = 0
        if (!isSorting) {
            sortName = ""
            sortType = ""
        }
    }

    //return currentPage + PER_PAGE;
    private val nextPage: Int
        get() = currentPage + 1

    private val isStillHavePages: Boolean
        get() = currentPage < totalPage

    private fun showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = DialogUtils.createProgressDialog(context)
            mProgressDialog?.show()
        } else {
            mProgressDialog!!.show()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        EventBus.getDefault().register(this)
        mContext = context
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
        outState.putInt(ARG_PAGE, currentPage)
        outState.putString(ARG_KEY_WORD, keyWord)
        outState.putBoolean(ARG_SEARCH, isSearch)
        outState.putBoolean(ARG_IS_SORTING, isSorting)
    }

    /*
     * Restore Instance State Here
     */
    private fun onRestoreInstanceState(savedInstanceState: Bundle) {
        categoryLv2 = savedInstanceState.getParcelable(ARG_CATEGORY)
        categoriesLv3 = savedInstanceState.getParcelableArrayList(ARG_PRODUCT_FILTER)
        categoryId = savedInstanceState.getString(ARG_DEPARTMENT_ID)
        sortName = savedInstanceState.getString(ARG_SORT_NAME)
        sortType = savedInstanceState.getString(ARG_SORT_TYPE)
        isDoneFilter = savedInstanceState.getBoolean(ARG_IS_DONE)
        title = savedInstanceState.getString(ARG_TITLE)
        currentPage = savedInstanceState.getInt(ARG_PAGE)
        keyWord = savedInstanceState.getString(ARG_KEY_WORD)
        isSearch = savedInstanceState.getBoolean(ARG_SEARCH)
        isSorting = savedInstanceState.getBoolean(ARG_IS_SORTING)
    }

    private fun popUpShow() {
        if (context != null) {
            val layoutInflater = (context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
            mPowerBuyPopupWindow = PowerBuyPopupWindow(activity, layoutInflater)
            mPowerBuyPopupWindow!!.setOnDismissListener(ON_POPUP_DISMISS_LISTENER)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setTextHeader(total: Int, name: String?) {
        productCount!!.text = name + " " + mContext!!.getString(R.string.filter_count, total.toString())
    }

    private fun totalPageCal(total: Int): Int {
        val num: Int
        val x = total.toFloat() / PER_PAGE
        Log.d(TAG, "Calculator : $x")
        num = ceil(x.toDouble()).toInt()
        return num
    }

    private fun retrieveProductList() {
        if (context != null) {
            val filterGroupsList = ArrayList<FilterGroups>()
            if (isSearch) {
                filterGroupsList.add(createFilterGroups("search_term", keyWord!!, "eq"))
            } else {
                filterGroupsList.add(createFilterGroups("category_id", categoryId!!, "eq"))
            }
            if (BuildConfig.FLAVOR !== "cds") {
                filterGroupsList.add(createFilterGroups(PRODUCT_2H_FIELD, PRODUCT_2H_VALUE, "eq"))
            }
            filterGroupsList.add(createFilterGroups("status", "1", "eq"))
            filterGroupsList.add(createFilterGroups("visibility", "4", "eq"))
            filterGroupsList.add(createFilterGroups("price", "0", "gt"))

            if (!isChatAndShop()){
                // is staff level is not chat and shop
                filterGroupsList.add(createFilterGroups(CHAT_AND_SHOP_FIELD, db.userInformation.store?.storeId.toString(), "eq"))
            }

            // TODO We have to do not display market place product
            filterGroupsList.add(createFilterGroups("marketplace_seller", "null"))

            if (brandName != null && brandName!!.isNotEmpty()) {
                filterGroupsList.add(createFilterGroups("brand", brandName!!, "eq"))
            }
            val sortOrders = ArrayList<SortOrder>()
            if (sortName!!.isNotEmpty() && sortType!!.isNotEmpty()) {
                val sortOrder = createSortOrder(sortName!!, sortType!!)
                sortOrders.add(sortOrder)
            }
            retrieveProducts(context!!, PER_PAGE, nextPage, filterGroupsList, sortOrders, object : ApiResponseCallback<ProductResponse> {
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
                            layoutProgress!!.visibility = View.GONE
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

    private fun updateProductList(response: ProductResponse?) {
        productResponse = response
        if (response != null) {
            if (response.filters.isNotEmpty()) {
                brands = response.filters[0].items
                for (filterItem in brands) {
                    if (brandName != null && brandName == filterItem.value) {
                        filterItem.isSelected = true
                    }
                }
            }
            if (response.products.isNotEmpty()) {
                totalItem = response.totalCount
                totalPage = totalPageCal(totalItem)
                currentPage = nextPage
                response.currentPage = currentPage
                // implement price per store if staff not chat and shop
                if (!isChatAndShop()) {
                    response.products.forEach { product ->
                        val offlinePriceItem = product.getPricePerStore()
                        if (offlinePriceItem != null) {
                            product.price = offlinePriceItem.price
                            if (offlinePriceItem.specialPrice > 0) {
                                product.specialPrice = offlinePriceItem.specialPrice
                                product.specialFromDate = null
                                product.specialToDate = null
                                if (offlinePriceItem.specialFromDate != null) {
                                    product.specialFromDate = offlinePriceItem.specialFromDate
                                }
                                if (offlinePriceItem.specialToDate != null) {
                                    product.specialToDate = offlinePriceItem.specialToDate
                                }
                            } else {
                                product.specialPrice = 0.0
                                product.specialFromDate = null
                                product.specialToDate = null
                            }
                        }
                    }
                }
                mProductListAdapter!!.setProduct(response)
            } else {
                mProductListAdapter!!.setError()
            }
            setTextHeader(totalItem, title)
        } else {
            if (mProductListAdapter!!.itemCount == 0) {
                mProductListAdapter!!.setError()
            }
            setTextHeader(totalItem, title)
        }
        layoutProgress!!.visibility = View.GONE
        mProgressDialog!!.dismiss()
    }

    // region {@link OnBrandFilterClickListener}
    override fun onClickedItem(filterItem: FilterItem?) {
        isDoneFilter = true
        resetPage()
        if (filterItem != null) {
            brandName = filterItem.value // brand name
            if (mProgressDialog != null && !mProgressDialog!!.isShowing) {
                showProgressDialog()
            }
            if (mPowerBuyPopupWindow!!.isShowing) {
                mPowerBuyPopupWindow!!.dismiss()
            }
            retrieveProductList()
            mPowerBuyPopupWindow!!.updateSingleBrandFilterItem(filterItem)
        } else {
            clearBrandFilter()
        }
    }

    private fun clearBrandFilter() {
        brandName = "" // clear brand
        if (mProgressDialog != null && !mProgressDialog!!.isShowing) {
            showProgressDialog()
        }
        retrieveProductList()
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

    private fun isChatAndShop(): Boolean{
        return db.userInformation.user?.userLevel == 3L
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
        private const val ARG_PAGE = "ARG_PAGE"
        private const val ARG_CATEGORY = "ARG_CATEGORY"
        private const val ARG_KEY_WORD = "ARG_KEY_WORD"
        private const val ARG_IS_SORTING = "ARG_IS_SORTING"
        private const val PRODUCT_2H_FIELD = "expr-p"
        private const val PRODUCT_2H_VALUE = "(stock.salable=1 OR (stock.ispu_salable=1 AND shipping_methods='storepickup_ispu'))"
        private const val CHAT_AND_SHOP_FIELD = "retailer_id"
        //Pagination
        private const val PER_PAGE = 20

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