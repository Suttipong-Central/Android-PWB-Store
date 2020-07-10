package cenergy.central.com.pwb_store.fragment

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.activity.interfaces.ProductDetailListener
import cenergy.central.com.pwb_store.adapter.BadgeListener
import cenergy.central.com.pwb_store.adapter.BadgeSelectAdapter
import cenergy.central.com.pwb_store.adapter.DisplayPromotionAdapter
import cenergy.central.com.pwb_store.manager.ApiResponseCallback
import cenergy.central.com.pwb_store.manager.api.ProductListAPI
import cenergy.central.com.pwb_store.manager.api.PromotionAPI
import cenergy.central.com.pwb_store.model.APIError
import cenergy.central.com.pwb_store.model.Product
import cenergy.central.com.pwb_store.model.body.FilterGroups
import cenergy.central.com.pwb_store.model.body.SortOrder
import cenergy.central.com.pwb_store.model.response.ProductResponse
import cenergy.central.com.pwb_store.model.response.PromotionResponse
import cenergy.central.com.pwb_store.utils.DialogUtils
import kotlinx.android.synthetic.main.fragment_promotion.*
import java.time.LocalDate

/**
 * Created by Anuphap Suwannamas on 24/9/2018 AD.
 * Email: Anupharpae@gmail.com
 */

class ProductPromotionFragment : Fragment() {

    private var progressDialog: ProgressDialog? = null
    private var product: Product? = null
    private var badgesSelects: ArrayList<String> = arrayListOf()
    private var freebieSKUs: ArrayList<String> = arrayListOf()
    private var freeItems: ArrayList<Product> = arrayListOf()
    private var productDetailListener: ProductDetailListener? = null
    private val badgeSelectAdapter: BadgeSelectAdapter by lazy { BadgeSelectAdapter() }
    private val displayPromotionAdapter: DisplayPromotionAdapter by lazy { DisplayPromotionAdapter() }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        productDetailListener = context as ProductDetailListener
        product = productDetailListener?.getProduct()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_promotion, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        badgesSelects = productDetailListener?.getBadgeSelects()?: arrayListOf()
        freebieSKUs = productDetailListener?.getFreebieSKUs()?: arrayListOf()
        freeItems = productDetailListener?.getFreeItems()?: arrayListOf()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isAdded) {
            setupView()
            if (badgesSelects.isEmpty() || freebieSKUs.isEmpty()){
                product?.let { retrievePromotion(it) }
            } else {
                tvPromotionNotFound.visibility = View.GONE
                badgeSelectAdapter.badgesSelect =badgesSelects
                badgeSelected(0)
            }
        }
    }

    fun badgeSelected(position: Int) {
        badgeSelectAdapter.selectedPosition = position
        when (badgesSelects[position]) {
            FREEBIE_ITEM -> {
                if (freebieSKUs.isNotEmpty()) {
                    if (freeItems.isNotEmpty()){
                        displayPromotionAdapter.items = freeItems
                    } else {
                        retrieveProductFreebies(freebieSKUs)
                    }
                } else {
                    Log.d("Product Promotion Tab", "freebieSKUs is Empty")
                    tvPromotionNotFound.visibility = View.VISIBLE
                    groupDisplayPromotion.visibility = View.GONE
                }
            }
        }
    }

    private fun setupView() {
        context?.let { context ->
            badgeRecycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            displayPromotionRecycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            badgeSelectAdapter.setListener(context)
            badgeRecycler.adapter = badgeSelectAdapter
            displayPromotionRecycler.adapter = displayPromotionAdapter
            displayPromotionRecycler.setHasFixedSize(true)
        }
    }

    private fun retrievePromotion(product: Product) {
        context?.let { context ->
            showProgressDialog()
            PromotionAPI.retrievePromotion(context, product, object : ApiResponseCallback<PromotionResponse> {
                override fun success(response: PromotionResponse?) {
                    if (response?.extension != null && response.extension!!.freeItems.isNotEmpty()) {
                        response.extension!!.freeItems[0].freebies.forEach {
                            freebieSKUs.add(it.sku)
                        }
                        badgesSelects.add(FREEBIE_ITEM)
                        productDetailListener?.setBadgeSelects(badgesSelects)
                        productDetailListener?.setFreebieSKUs(freebieSKUs)
                        //TODO next implement installment
                        badgeSelectAdapter.badgesSelect = badgesSelects
                        // Set default select is 0
                        badgeSelected(0)
                        dismissProgressDialog()
                        tvPromotionNotFound.visibility = View.GONE
                        groupDisplayPromotion.visibility = View.VISIBLE
                    } else {
                        Log.d("Product Promotion Tab", "PromotionResponse is Null")
                        dismissProgressDialog()
                        tvPromotionNotFound.visibility = View.VISIBLE
                        groupDisplayPromotion.visibility = View.GONE
                    }
                }

                override fun failure(error: APIError) {
                    Log.d("Product Promotion Tab", "${error.errorCode} ${error.errorMessage}")
                    dismissProgressDialog()
                    tvPromotionNotFound.visibility = View.VISIBLE
                    groupDisplayPromotion.visibility = View.GONE
                }
            })
        }
    }

    private fun retrieveProductFreebies(freebieSKUs: ArrayList<String>) {
        showProgressDialog()
        val result = TextUtils.join(",", freebieSKUs)
        val filterGroupsList = java.util.ArrayList<FilterGroups>()
        filterGroupsList.add(FilterGroups.createFilterGroups("sku", result, "in"))
        val sortOrders = java.util.ArrayList<SortOrder>()

        context?.let {
            ProductListAPI.retrieveProducts(it, freebieSKUs.size, 1,
                    filterGroupsList, sortOrders, object : ApiResponseCallback<ProductResponse> {
                override fun success(response: ProductResponse?) {
                    (context as Activity).runOnUiThread {
                        dismissProgressDialog()
                        if (response != null) {
                            productDetailListener?.setFreeItems(response.products)
                            displayPromotionAdapter.items = response.products
                            groupDisplayPromotion.visibility = View.VISIBLE
                        } else {
                            Log.d("Product Promotion Tab", "ProductResponse is Null")
                            tvPromotionNotFound.visibility = View.VISIBLE
                            groupDisplayPromotion.visibility = View.GONE
                        }
                    }
                }

                override fun failure(error: APIError) {
                    (context as Activity).runOnUiThread {
                        Log.d("Product Promotion Tab", "${error.errorCode} ${error.errorMessage}")
                        dismissProgressDialog()
                        tvPromotionNotFound.visibility = View.VISIBLE
                        groupDisplayPromotion.visibility = View.GONE
                    }
                }
            })
        }
    }

    private fun showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = DialogUtils.createProgressDialog(context)
            progressDialog?.show()
        } else {
            progressDialog?.show()
        }
    }

    private fun dismissProgressDialog() {
        progressDialog?.dismiss()
    }

    companion object {
        const val FREEBIE_ITEM = "Free Item"
        const val INSTALLMENT_0_PERCENT = "0% Installment"
    }
}