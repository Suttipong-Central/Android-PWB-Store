package cenergy.central.com.pwb_store.fragment

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.activity.interfaces.ProductDetailListener
import cenergy.central.com.pwb_store.adapter.BadgeSelectAdapter
import cenergy.central.com.pwb_store.adapter.CreditCardPromotionAdapter
import cenergy.central.com.pwb_store.adapter.FreebiePromotionAdapter
import cenergy.central.com.pwb_store.adapter.InstallmentPlanAdapter
import cenergy.central.com.pwb_store.extensions.getInstallments
import cenergy.central.com.pwb_store.manager.ApiResponseCallback
import cenergy.central.com.pwb_store.manager.api.ProductListAPI
import cenergy.central.com.pwb_store.manager.api.PromotionAPI
import cenergy.central.com.pwb_store.model.APIError
import cenergy.central.com.pwb_store.model.Installment
import cenergy.central.com.pwb_store.model.Product
import cenergy.central.com.pwb_store.model.body.FilterGroups
import cenergy.central.com.pwb_store.model.body.SortOrder
import cenergy.central.com.pwb_store.model.response.CreditCardPromotion
import cenergy.central.com.pwb_store.model.response.ProductResponse
import cenergy.central.com.pwb_store.model.response.PromotionResponse
import cenergy.central.com.pwb_store.utils.DialogUtils
import kotlinx.android.synthetic.main.fragment_promotion.*

class ProductPromotionFragment : Fragment() {

    private var progressDialog: ProgressDialog? = null
    private var product: Product? = null
    private var badgesSelects: ArrayList<Int> = arrayListOf()
    private var freebieSKUs: ArrayList<String> = arrayListOf()
    private var freeItems: ArrayList<Product> = arrayListOf()
    private var creditCardOnTopList: ArrayList<CreditCardPromotion> = arrayListOf()
    private var installmentPlanList: ArrayList<Installment> = arrayListOf()
    private var productDetailListener: ProductDetailListener? = null
    private val badgeSelectAdapter: BadgeSelectAdapter by lazy { BadgeSelectAdapter() }
    private val freebiePromotionAdapter: FreebiePromotionAdapter by lazy { FreebiePromotionAdapter() }
    private val creditCardPromotionAdapter: CreditCardPromotionAdapter by lazy { CreditCardPromotionAdapter() }
    private val installmentPlanAdapter: InstallmentPlanAdapter by lazy { InstallmentPlanAdapter() }

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
        badgesSelects = productDetailListener?.getBadgeSelects() ?: arrayListOf()
        freebieSKUs = productDetailListener?.getFreebieSKUs() ?: arrayListOf()
        freeItems = productDetailListener?.getFreeItems() ?: arrayListOf()
        creditCardOnTopList = productDetailListener?.getCreditCardPromotionList() ?: arrayListOf()
        installmentPlanList = productDetailListener?.getInstallmentPlanList() ?: arrayListOf()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isAdded) {
            setupView()
            if (badgesSelects.isEmpty()) {
                product?.let { retrievePromotion(it) }
            } else {
                badgeSelectAdapter.badgesSelect = badgesSelects
                badgeSelected(0)
            }
        }
    }

    fun badgeSelected(position: Int) {
        if (badgeSelectAdapter.selectedPosition == null || badgeSelectAdapter.selectedPosition != position) {
            badgeSelectAdapter.selectedPosition = position
            when (badgesSelects[position]) {
                FREEBIE_ITEM -> {
                    if (freebieSKUs.isNotEmpty()) {
                        titleDisplayPromotion.text = getString(R.string.get_free_item)
                        icPromotion.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.ic_freebie))
                        if (freeItems.isNotEmpty()) {
                            freebiePromotionAdapter.items = freeItems
                        } else {
                            retrieveProductFreebies(freebieSKUs)
                        }
                        displayPromotionRecycler.adapter = freebiePromotionAdapter
                        tvPromotionNotFound.visibility = View.GONE
                        groupPromotion.visibility = View.VISIBLE
                    } else {
                        displayNotHavePromotion()
                    }
                }

                CREDIT_CARD_ON_TOP -> {
                    if (creditCardOnTopList.isNotEmpty()) {
                        titleDisplayPromotion.text = getString(R.string.title_credit_card_on_top)
                        icPromotion.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.ic_credit_card_white))
                        creditCardPromotionAdapter.items = creditCardOnTopList
                        displayPromotionRecycler.adapter = creditCardPromotionAdapter
                        tvPromotionNotFound.visibility = View.GONE
                        groupPromotion.visibility = View.VISIBLE
                    } else {
                        displayNotHavePromotion()
                    }
                }
                INSTALLMENT_PLANS -> {
                    if (installmentPlanList.isNotEmpty()) {
                        titleDisplayPromotion.text = getString(R.string.title_installment_plans)
                        icPromotion.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.ic_credit_card_white))
                        installmentPlanAdapter.items = installmentPlanList
                        displayPromotionRecycler.adapter = installmentPlanAdapter
                        tvPromotionNotFound.visibility = View.GONE
                        groupPromotion.visibility = View.VISIBLE
                    } else {
                        displayNotHavePromotion()
                    }
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
            displayPromotionRecycler.setHasFixedSize(true)
        }
    }

    private fun retrievePromotion(product: Product) {
        context?.let { context ->
            showProgressDialog()
            PromotionAPI.retrievePromotion(context, product, object : ApiResponseCallback<PromotionResponse> {
                override fun success(response: PromotionResponse?) {
                    if (response?.extension != null) {
                        // Free item
                        if (response.extension!!.freeItems.isNotEmpty()) {
                            response.extension!!.freeItems[0].freebies.forEach {
                                freebieSKUs.add(it.sku)
                            }
                            badgesSelects.add(FREEBIE_ITEM)
                            productDetailListener?.setFreebieSKUs(freebieSKUs)
                        }
                        // CC on top
                        if (response.extension!!.creditCardPromotions.isNotEmpty()) {
                            badgesSelects.add(CREDIT_CARD_ON_TOP)
                            creditCardOnTopList.addAll(response.extension!!.creditCardPromotions)
                            productDetailListener?.setCreditCardPromotionList(creditCardOnTopList)
                        }
                        // Installment plans
                        if (product.getInstallments().isNotEmpty()) {
                            badgesSelects.add(INSTALLMENT_PLANS)
                            installmentPlanList.addAll(product.getInstallments())
                            productDetailListener?.setInstallmentPlanList(installmentPlanList)
                        }
                        if (badgesSelects.isNotEmpty()) {
                            productDetailListener?.setBadgeSelects(badgesSelects)
                            badgeSelectAdapter.badgesSelect = badgesSelects
                            // Set default select is
                            badgeSelected(0)
                        } else {
                            displayNotHavePromotion()
                        }
                    } else {
                        displayNotHavePromotion()
                    }
                    dismissProgressDialog()
                }

                override fun failure(error: APIError) {
                    Log.d(TAG_LOG_PROMOTION, "${error.errorCode} ${error.errorMessage}")
                    dismissProgressDialog()
                    displayNotHavePromotion()
                }
            })
        }
    }

    private fun displayNotHavePromotion() {
        tvPromotionNotFound.visibility = View.VISIBLE
        groupPromotion.visibility = View.GONE
    }

    private fun retrieveProductFreebies(freebieSKUs: ArrayList<String>) {
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
                            freebiePromotionAdapter.items = response.products
                        } else {
                            displayNotHavePromotion()
                        }
                    }
                }

                override fun failure(error: APIError) {
                    (context as Activity).runOnUiThread {
                        Log.d(TAG_LOG_PROMOTION, "${error.errorCode} ${error.errorMessage}")
                        dismissProgressDialog()
                        displayNotHavePromotion()
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
        const val FREEBIE_ITEM = 0
        const val CREDIT_CARD_ON_TOP = 1
        const val INSTALLMENT_PLANS = 2
        const val TAG_LOG_PROMOTION = "Product Promotion Tab"
    }
}