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
import android.widget.Toast
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
import cenergy.central.com.pwb_store.manager.api.OfflinePriceAPI
import cenergy.central.com.pwb_store.manager.api.ProductListAPI.Companion.retrieveProducts
import cenergy.central.com.pwb_store.manager.bus.event.CategoryTwoBus
import cenergy.central.com.pwb_store.manager.bus.event.ProductFilterItemBus
import cenergy.central.com.pwb_store.manager.bus.event.SortingHeaderBus
import cenergy.central.com.pwb_store.manager.bus.event.SortingItemBus
import cenergy.central.com.pwb_store.model.*
import cenergy.central.com.pwb_store.model.body.FilterGroups
import cenergy.central.com.pwb_store.model.body.FilterGroups.Companion.createFilterGroups
import cenergy.central.com.pwb_store.model.response.OfflinePriceItem
import cenergy.central.com.pwb_store.model.response.OfflinePriceProductsResponse
import cenergy.central.com.pwb_store.model.response.ProductResponse
import cenergy.central.com.pwb_store.realm.RealmController
import cenergy.central.com.pwb_store.utils.Analytics
import cenergy.central.com.pwb_store.utils.DialogUtils
import cenergy.central.com.pwb_store.utils.Screen
import cenergy.central.com.pwb_store.view.PowerBuyPopupWindow
import cenergy.central.com.pwb_store.view.PowerBuyTextView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class ProductListFragment : Fragment(), View.OnClickListener, OnBrandFilterClickListener {
    // Analytic
    private var analytics: Analytics? = null
    private var productCount: PowerBuyTextView? = null
    private var mProductLayout: LinearLayout? = null
    private var mProductListAdapter: ProductListAdapter? = null
    private var categoriesLv3: ArrayList<Category>? = null
    private var mSortingList: SortingList? = null
    private var title: String? = null
    private var mPowerBuyPopupWindow: PowerBuyPopupWindow? = null
    private var mProgressDialog: ProgressDialog? = null
    private var isDoneFilter = false
    private var isSearch = false
    private var categoryId: String? = null
    private var brandName: String? = null
    private var categoryLv2: Category? = null
    // Page
    private var isSorting = false
    private var totalItem = 0
    private var mContext: Context? = null
    private var keyWord: String? = null
    private val onPopupDismissListener = PopupWindow.OnDismissListener { isDoneFilter = false }
    private var userInformation: UserInformation? = null
    private var database = RealmController.getInstance()

    private var products = arrayListOf<Product>()
    private var brands: ArrayList<FilterItem> = ArrayList()
    private var isClearBrands = true

    private val PER_PAGE = 20
    private var currentPage = 0
    private var totalPage = 0


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
        isClearBrands = true
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
        val productsSorted = ArrayList<Product>()
        when(sortingItem.slug){
            "price" -> {
                if (sortingItem.value == "ASC"){
                    productsSorted.addAll(products.sortedBy { it.price })
                } else {
                    productsSorted.addAll(products.sortedByDescending { it.price })
                }
            }
            "brand" -> {
                if (sortingItem.value == "ASC"){
                    productsSorted.addAll(products.sortedBy { it.brand })
                } else {
                    productsSorted.addAll(products.sortedByDescending { it.brand })
                }
            }
        }
        mProductListAdapter?.setProduct(productsSorted)
        mProgressDialog?.dismiss()
        mPowerBuyPopupWindow!!.updateSingleSortingItem(sortingItem)
        if (mPowerBuyPopupWindow!!.isShowing) {
            mPowerBuyPopupWindow!!.dismiss()
        }
    }

    // region {@link OnBrandFilterClickListener}
    override fun onClickedItem(filterItem: FilterItem?) {
        isDoneFilter = true
        if (filterItem != null) {
            isClearBrands = false
            brandName = filterItem.value // brand name
            if (mProgressDialog != null && !mProgressDialog!!.isShowing) {
                showProgressDialog()
            }
            retrieveProductList()
            mPowerBuyPopupWindow!!.updateSingleBrandFilterItem(filterItem)
        } else {
            clearBrandFilter()
        }
        if (mPowerBuyPopupWindow!!.isShowing) {
            mPowerBuyPopupWindow!!.dismiss()
        }
    }

    private fun clearBrandFilter() {
        isClearBrands = true
        brandName = "" // clear brand
        if (mProgressDialog != null && !mProgressDialog!!.isShowing) {
            showProgressDialog()
        }
        retrieveProductList()
        mPowerBuyPopupWindow!!.updateSingleBrandFilterItem(null)
    }
    // endregion

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
        if (arguments != null) {
            title = arguments!!.getString(ARG_TITLE)
            isSearch = arguments!!.getBoolean(ARG_SEARCH)
            categoryId = arguments!!.getString(ARG_DEPARTMENT_ID)
            categoryLv2 = arguments!!.getParcelable(ARG_CATEGORY)
            keyWord = arguments!!.getString(ARG_KEY_WORD)
            if (!isSearch) { // setup product filter list
                loadCategoryLv3(categoryLv2)
            }
        }

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
                if (mSortingList == null || products.isEmpty()) {
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

    private fun initInstances(rootView: View, savedInstanceState: Bundle?) {
        mProductListAdapter = ProductListAdapter(rootView.context)
        val productTitle: PowerBuyTextView = rootView.findViewById(R.id.txt_title_product)
        val layoutFilter: ConstraintLayout = rootView.findViewById(R.id.layout_filter)
        val mRecyclerView: RecyclerView = rootView.findViewById(R.id.recycler_view_list)
        productCount = rootView.findViewById(R.id.txt_product_count)
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
        val mLayoutManger = GridLayoutManager(context, 3, LinearLayoutManager.VERTICAL, false)
        mLayoutManger.spanSizeLookup = mProductListAdapter!!.spanSize
        mRecyclerView.layoutManager = mLayoutManger
        mRecyclerView.addItemDecoration(SpacesItemDecoration(0, LinearLayoutManager.VERTICAL))
        mRecyclerView.adapter = mProductListAdapter
        if (savedInstanceState == null) {
            showProgressDialog()
            retrieveProductList()
        }
    }

    private fun showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = DialogUtils.createProgressDialog(context)
            mProgressDialog!!.show()
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
        outState.putBoolean(ARG_IS_DONE, isDoneFilter)
        outState.putString(ARG_TITLE, title)
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
        productCount!!.text = name + " " + mContext!!.getString(R.string.filter_count, total.toString())
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
            if (brandName != null && brandName!!.isNotEmpty()) {
                filterGroupsList.add(createFilterGroups("brand", brandName!!, "eq"))
            }

            retrieveProducts(context!!, 100, 1, filterGroupsList, object : ApiResponseCallback<ProductResponse> {
                override fun success(response: ProductResponse?) {
                    if (activity != null) {
                        activity!!.runOnUiThread {
                            retrieveOfflinePrice(response)
                        }
                    }
                }

                override fun failure(error: APIError) {
                    if (activity != null) {
                        activity!!.runOnUiThread {
                            mProgressDialog!!.dismiss()
                            if (context != null) {
                                DialogHelper(context!!).showErrorDialog(error)
                            }
                        }
                    }
                }
            })
        }
    }

    private fun getNextPage(): Int{
        return currentPage + 1
    }

    private fun retrieveOfflinePrice(productResponse: ProductResponse?) {
        if (context != null && productResponse != null) {
            userInformation = database.userInformation
            val retailerId = userInformation?.store?.retailerId ?: ""
            //todo force send retailer id is 13
            val productListIds = productResponse.products.map { it.id }
            val productIds = TextUtils.join(",", productListIds)
            OfflinePriceAPI.retrieveOfflinePriceProducts(context!!, productIds, "13", object : ApiResponseCallback<OfflinePriceProductsResponse>{
                override fun success(response: OfflinePriceProductsResponse?) {
                    if (activity != null) {
                        activity!!.runOnUiThread {
                            if (response != null && response.items.isNotEmpty()){
                                filterProductsOfflinePrice(productResponse, response.items)
                            } else {
                                mProgressDialog!!.dismiss()
                                setTextHeader(0, title)
                                mProductListAdapter!!.setError()
                            }
                        }
                    }
                }

                override fun failure(error: APIError) {
                    if (activity != null && context != null) {
                        activity!!.runOnUiThread {
                            mProgressDialog!!.dismiss()
                            setTextHeader(0, title)
                            mProductListAdapter!!.setError()
                            DialogHelper(context!!).showErrorDialog(error)
                        }
                    }
                }
            })
        }
    }

    private fun filterProductsOfflinePrice(productResponse: ProductResponse, offlinePriceItems: ArrayList<OfflinePriceItem>){
        products.clear()
        offlinePriceItems.forEach { offlinePriceItem ->
            val offlineProduct = productResponse.products.firstOrNull { it.id.toString() == offlinePriceItem.productId}
            if (offlineProduct != null && !products.contains(offlineProduct)){
                offlineProduct.price = offlinePriceItem.price
                offlineProduct.specialPrice = offlinePriceItem.specialPrice
                offlineProduct.specialFromDate = offlinePriceItem.specialFromDate
                offlineProduct.specialToDate = offlinePriceItem.specialToDate
                products.add(offlineProduct)
            }
        }
        if (isClearBrands){
            brands.clear()
            products.distinctBy { it.brand }.forEach { product ->
                val brandOfflineProduct = productResponse.filters[0].items.firstOrNull { it.value == product.brand }
                if (brandOfflineProduct != null){
                    brands.add(brandOfflineProduct)
                }
            }
        }
        updateProductList(products)
    }

    private fun updateProductList(products: ArrayList<Product>) {
        if (products.isNotEmpty()) {
            for (filterItem in brands) {
                if (brandName != null && brandName == filterItem.value) {
                    filterItem.isSelected = true
                }
            }
            mProductListAdapter!!.setProduct(products)
            setTextHeader(products.size, title)
        } else {
            mProductListAdapter!!.setError()
            setTextHeader(0, title)
        }
        mProgressDialog!!.dismiss()
    }

    private fun loadMore(){
        if (isStillHavePages()){
            retrieveProductList()
        } else {
            updateProductList(products)
        }
    }

    private fun totalPageCal(total: Int): Int {
        val num: Int
        val x: Float = total.toFloat() / PER_PAGE
        num = Math.ceil(x.toDouble()).toInt()
        return num
    }

    private fun isStillHavePages(): Boolean {
        Log.d(TAG, "Calculator -> : $currentPage")
        Log.d(TAG, "Calculator = : $totalPage")
        return currentPage < totalPage
    }

    private fun loadCategoryLv3(categoryLv2: Category?) {
        if (context == null) return
        getInstance(context!!).retrieveCategory(categoryLv2!!.id,
                true, ArrayList(), object : ApiResponseCallback<List<Category>> {
            override fun success(response: List<Category>?) {
                if (activity != null && response != null) {
                    activity!!.runOnUiThread {
                        categoriesLv3 = ArrayList() // clear category lv3 list
                        categoriesLv3!!.addAll(response)
                        mProductLayout!!.visibility = View.VISIBLE // show product layout
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
        private const val ARG_IS_DONE = "ARG_IS_DONE"
        private const val ARG_CATEGORY = "ARG_CATEGORY"
        private const val ARG_KEY_WORD = "ARG_KEY_WORD"
        private const val ARG_IS_SORTING = "ARG_IS_SORTING"
        private const val PRODUCT_2H_FIELD = "expr-p"
        private const val PRODUCT_2H_VALUE = "(stock.salable=1 OR (stock.ispu_salable=1 AND shipping_methods='storepickup_ispu'))"

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