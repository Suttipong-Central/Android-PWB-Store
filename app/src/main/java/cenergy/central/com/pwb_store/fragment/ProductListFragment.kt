package cenergy.central.com.pwb_store.fragment

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cenergy.central.com.pwb_store.BuildConfig
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.adapter.ProductListAdapter
import cenergy.central.com.pwb_store.adapter.decoration.SpacesItemDecoration
import cenergy.central.com.pwb_store.adapter.interfaces.OnBrandFilterClickListener
import cenergy.central.com.pwb_store.dialogs.FilterView
import cenergy.central.com.pwb_store.dialogs.ProductFilterBottomSheet
import cenergy.central.com.pwb_store.dialogs.ProductFilterListener
import cenergy.central.com.pwb_store.helpers.DialogHelper
import cenergy.central.com.pwb_store.manager.ApiResponseCallback
import cenergy.central.com.pwb_store.manager.HttpManagerMagento
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
import cenergy.central.com.pwb_store.model.response.ProductAvailableResponse
import cenergy.central.com.pwb_store.model.response.ProductResponse
import cenergy.central.com.pwb_store.realm.RealmController
import cenergy.central.com.pwb_store.utils.Analytics
import cenergy.central.com.pwb_store.utils.DialogUtils
import cenergy.central.com.pwb_store.utils.ProductListSorting
import cenergy.central.com.pwb_store.utils.Screen
import cenergy.central.com.pwb_store.view.PowerBuyPopupWindow
import cenergy.central.com.pwb_store.view.PowerBuyTextView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.fragment_product_list.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import kotlin.math.ceil

@SuppressLint("SetTextI18n")
class ProductListFragment : Fragment(), View.OnClickListener, OnBrandFilterClickListener, ProductFilterListener {
    // Analytic
    private var analytics: Analytics? = null

    // View
    private var productCount: PowerBuyTextView? = null
    private var filterCountTv: PowerBuyTextView? = null
    private var layoutProgress: LinearLayout? = null
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<*>

    // Data Member
    private var mProductListAdapter: ProductListAdapter? = null
    private var mLayoutManger: GridLayoutManager? = null
    private var categoriesLv3: ArrayList<Category>? = null
    private var mSortingList: SortingList? = null
    private var title: String? = null
    private var mPowerBuyPopupWindow: PowerBuyPopupWindow? = null
    private var mProgressDialog: ProgressDialog? = null
    private var isDoneFilter = false
    private var isSearch = false
    private var categoryId: String? = null
    private var brandName: String? = null
    private var moreFilter: ArrayList<Pair<String, ArrayList<String>>> = arrayListOf()
    private var categoryIdLv4: String? = null

    // Sort
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

    // Filter
    private var filterCount = 0

    // Database
    private val database by lazy { RealmController.getInstance() }

    private val onPopupDismissListener = PopupWindow.OnDismissListener { isDoneFilter = false }

    private val scrollListener: RecyclerView.OnScrollListener = object : RecyclerView.OnScrollListener() {
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
        sortName = sortingItem.slug
        sortType = sortingItem.value
        resetPage()
        retrieveProductList()
        mPowerBuyPopupWindow!!.updateSingleSortingItem(sortingItem)
        if (mPowerBuyPopupWindow!!.isShowing) {
            mPowerBuyPopupWindow!!.dismiss()
        }
    }

    // region {@link OnBrandFilterClickListener}
    override fun onClickedItem(filterItem: FilterItem?) {
        isDoneFilter = true
        resetPage()
        if (filterItem != null) {
            brandName = filterItem.value // brand name
            showProgressDialog()
            retrieveProductList()
            mPowerBuyPopupWindow?.updateSingleBrandFilterItem(filterItem)
        } else {
            clearBrandFilter()
        }
        if (mPowerBuyPopupWindow != null && mPowerBuyPopupWindow!!.isShowing) {
            mPowerBuyPopupWindow?.dismiss()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        if (savedInstanceState == null) {
            arguments?.let { onRestoreInstanceState(it) }
        } else {
            onRestoreInstanceState(savedInstanceState)
            currentPage = 0
            retrieveProductList()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_product_list, container, false)
        initInstances(rootView, savedInstanceState)
        resetPage()
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupFilterOptions()
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
        }
        resetPage()
        setupSorting()
    }

    private fun setupFilterOptions() {
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer)
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    val bottomSheetFragment = childFragmentManager.findFragmentByTag(TAG_FILTERS_FRAGMENT)
                    if (bottomSheetFragment != null) {
                        childFragmentManager.beginTransaction()
                                .remove(bottomSheetFragment)
                                .commit()
                    }
                }
            }
        })

        filterOptionsButton.setOnClickListener {
            showFilterOptions()
        }
    }

    private fun setupSorting() {
        // setup sorting
        val sortingItems = context?.let { ProductListSorting.getSortingItems(it) } ?: arrayListOf()
        val sortingHeaders: ArrayList<SortingHeader> = arrayListOf()
        sortingHeaders.add(SortingHeader("0", "Sorting", "sorting", "single", sortingItems))
        mSortingList = SortingList(sortingHeaders)
        for (sortingHeader in sortingHeaders) {
            mSortingList!!.updateSortOption(sortingHeader, true)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.layout_title -> EventBus.getDefault().post(CategoryTwoBus())
            R.id.layout_sort ->  // Create productResponse for check because we mock up sort items
                if (mSortingList == null || productResponse == null || productResponse!!.products.isEmpty()
                        || childFragmentManager.findFragmentByTag(TAG_FILTERS_FRAGMENT) != null) {
                    mPowerBuyPopupWindow!!.dismiss()
                } else {
                    mPowerBuyPopupWindow!!.setRecyclerViewSorting(mSortingList)
                    mPowerBuyPopupWindow!!.showAsDropDown(v)
                }
        }
    }

    private fun initInstances(rootView: View, savedInstanceState: Bundle?) {
        // Init 'View' instance(s) with rootView.findViewById here
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
        val sortLayout = rootView.findViewById<LinearLayout>(R.id.layout_sort)
        titleLayout.setOnClickListener(this)
        sortLayout.setOnClickListener(this)

        filterCountTv = rootView.findViewById(R.id.txt_filter)
        updateFilterCount()
        if (isSearch) {
            layoutFilter.visibility = View.GONE
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
        mRecyclerView.addOnScrollListener(scrollListener)
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
        sortName = savedInstanceState.getString(ARG_SORT_NAME, "")
        sortType = savedInstanceState.getString(ARG_SORT_TYPE, "")
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
            mPowerBuyPopupWindow!!.setOnDismissListener(onPopupDismissListener)
        }
    }

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

            // TODO We have to do not display market place product
            filterGroupsList.add(createFilterGroups("marketplace_seller", "null"))

            if (moreFilter.isNotEmpty()) {
                moreFilter.forEach {
                    val filterValues = TextUtils.join(",", it.second)
                    filterGroupsList.add(createFilterGroups(it.first, filterValues, "in"))
                }
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
                            handleProductAvailable(response)
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

    private fun handleProductAvailable(productResponse: ProductResponse?) {
        context?.let {
            if (productResponse != null) {
                val retailerId = database?.userInformation?.store?.storeId?.toString() ?: ""
                val resultSKUs = TextUtils.join(",", productResponse.products.map { it.sku })
                HttpManagerMagento.getInstance(context!!).retrieveProductAvailable(retailerId, resultSKUs, object : ApiResponseCallback<List<ProductAvailableResponse>> {
                    override fun success(response: List<ProductAvailableResponse>?) {
                        productResponse.products.forEach { product ->
                            val productAvailable = response?.find { it.sku == product.sku }
                            if (productAvailable != null) {
                                product.availableThisStore = productAvailable.quantity > 0
                            }
                        }
                        updateProductList(productResponse)
                    }

                    override fun failure(error: APIError) {
                        Log.d("ProductListAPI", "API product available fail")
                        updateProductList(productResponse)
                    }
                })
            } else {
                updateProductList(productResponse)
            }
        }
    }

    private fun updateProductList(response: ProductResponse?) {
        if (response != null) {
            this.productResponse = response
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
                        } else {
                            // this case is don't have offline price will display online normal price only
                            product.specialPrice = 0.0
                            product.specialFromDate = null
                            product.specialToDate = null
                        }
                    }
                }
                mProductListAdapter?.setProduct(response)
            } else {
                mProductListAdapter?.setError()
            }
            setTextHeader(totalItem, title)
            // however must be update filter option
            (childFragmentManager.findFragmentByTag(TAG_FILTERS_FRAGMENT) as ProductFilterBottomSheet?)?.updateProductFilters()
        } else {
            if (mProductListAdapter?.itemCount == 0) {
                mProductListAdapter?.setError()
            }
            setTextHeader(totalItem, title)
        }
        layoutProgress?.visibility = View.GONE
        mProgressDialog?.dismiss()
    }

    private fun clearBrandFilter() {
        brandName = "" // clear brand
        showProgressDialog()
        retrieveProductList()
        mPowerBuyPopupWindow?.updateSingleBrandFilterItem(null)
    }
    // endregion

    // region {@link ProductFilterListener}
    override fun getTotalProducts(): Int = productResponse?.totalCount ?: 0

    override fun getChildCategory(): String = categoryLv2?.children ?: ""

    override fun getProductFilters(): List<ProductFilter> = productResponse?.filters
            ?: arrayListOf()

    override fun getCategoryIdLv4(): String? = categoryIdLv4

    override fun getMoreFilters(): ArrayList<Pair<String, ArrayList<String>>> = this.moreFilter

    override fun onTopicClickListener(filterItem: FilterItem) {
        this.title = filterItem.label
        this.categoryIdLv4 = filterItem.value
        this.categoryId = filterItem.value
        moreFilter = arrayListOf()
        resetPage()
        showProgressDialog()
        retrieveProductList()
    }

    override fun onSelectFilter(filter: FilterView.FilterCheckBoxView) {
        if (moreFilter.firstOrNull { it.first == filter.code } == null) {
            moreFilter.add(Pair(first = filter.code, second = arrayListOf(filter.filterItem.value)))
        } else {
            moreFilter.first { it.first == filter.code }.second.add(filter.filterItem.value)
        }
        resetPage()
        showProgressDialog()
        retrieveProductList()
    }

    override fun onUnSelectFilter(filter: FilterView.FilterCheckBoxView) {
        if (moreFilter.firstOrNull { it.first == filter.code } != null) {
            if (moreFilter.first { it.first == filter.code }.second.size > 1) {
                moreFilter.first { it.first == filter.code }.second.remove(filter.filterItem.value)
            } else {
                moreFilter.remove(moreFilter.first { it.first == filter.code })
            }
        }
        resetPage()
        showProgressDialog()
        retrieveProductList()
    }

    override fun onResetFilter() {
        if (categoryIdLv4 != null) {
            categoryId = categoryIdLv4
        }
        moreFilter = arrayListOf()
        resetPage()
        showProgressDialog()
        retrieveProductList()
    }

    override fun onCloseListener() {
        hideFilterOptions()
    }
    // endregion

    override fun onUpdateFilterCount(count: Int) {
        filterCount = count
        updateFilterCount()
    }

    private fun updateFilterCount() {
        if (filterCount > 0) {
            filterCountTv?.text = "${getText(R.string.filters)} ($filterCount)"
        } else {
            filterCountTv?.text = getText(R.string.filters)
        }
    }

    private fun showFilterOptions() {
        val productFilterFragment = childFragmentManager.findFragmentByTag(
                TAG_FILTERS_FRAGMENT) as ProductFilterBottomSheet?
                ?: run { ProductFilterBottomSheet() }
        productFilterFragment.listener = this

        val layoutParams: CoordinatorLayout.LayoutParams = bottomSheetContainer.layoutParams
                as CoordinatorLayout.LayoutParams
        bottomSheetContainer.layoutParams = layoutParams

        childFragmentManager.beginTransaction()
                .replace(bottomSheetContainer.id, productFilterFragment, TAG_FILTERS_FRAGMENT)
                .commit()
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun isChatAndShop(): Boolean {
        // level 3 is chat and shop
        return database.userInformation.user?.userLevel == 3L
    }

    private fun hideFilterOptions() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    companion object {
        private val TAG = ProductListFragment::class.java.simpleName
        private const val TAG_FILTERS_FRAGMENT = "TAG_FILTERS_FRAGMENT"
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