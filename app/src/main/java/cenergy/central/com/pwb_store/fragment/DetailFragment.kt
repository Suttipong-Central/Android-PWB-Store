package cenergy.central.com.pwb_store.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearSnapHelper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import cenergy.central.com.pwb_store.BuildConfig
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.activity.interfaces.ProductDetailListener
import cenergy.central.com.pwb_store.adapter.ProductImageAdapter
import cenergy.central.com.pwb_store.adapter.ProductOptionAdepter
import cenergy.central.com.pwb_store.adapter.interfaces.ProductImageListener
import cenergy.central.com.pwb_store.extensions.*
import cenergy.central.com.pwb_store.manager.ApiResponseCallback
import cenergy.central.com.pwb_store.manager.Contextor
import cenergy.central.com.pwb_store.manager.HttpManagerMagento
import cenergy.central.com.pwb_store.model.*
import cenergy.central.com.pwb_store.model.body.OptionBody
import cenergy.central.com.pwb_store.view.PowerBuyIconButton
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_detail.*


class DetailFragment : Fragment(), View.OnClickListener, ProductImageListener {
    private lateinit var productDetailListener: ProductDetailListener
    private var product: Product? = null

    private lateinit var sizeAdepter: ProductOptionAdepter
    private lateinit var shadeAdepter: ProductOptionAdepter
    private var configOptions: List<ProductOption>? = listOf()
    private var configItemOptions: ArrayList<OptionBody> = arrayListOf()
    var optionSize: OptionBody? = null
    var optionShade: OptionBody? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        productDetailListener = context as ProductDetailListener
        product = productDetailListener.getProduct()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        product?.let { initDetail(it) }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.availableStoreButton -> {
                productDetailListener.onDisplayAvailableStore(product)
            }

            R.id.addToCompareButton -> {
                productDetailListener.addProductToCompare(product)
            }

            R.id.addToCartButton -> {
                if (!(view as PowerBuyIconButton).isDisable) {
                    product ?: return
                    if (product!!.typeId == "configurable" && !configOptions.isNullOrEmpty()) {
                        configOptions?.forEach { productOption ->
                            when (productOption.label) {
                                "Size" -> {
                                    configItemOptions.add(optionSize!!)
                                }
                                "Shade" -> {
                                    configItemOptions.add(optionShade!!)
                                }
                            }
                        }
                        productDetailListener.addProductConfigToCart(product, configItemOptions)
                        configItemOptions.clear()
                    } else {
                        productDetailListener.addProductToCart(product)
                    }
                }
            }

            R.id.add1HourButton -> {
                onAddToCartBy1Hrs()
            }

            R.id.shareButton -> {
                productDetailListener.onShareButtonClickListener()
            }
        }
    }

    // region {@link ProductImageListener.onProductImageClickListener}
    override fun onProductImageClickListener(productImage: ProductDetailImageItem) {
        ivProductImage.setImageUrl(productImage.imgUrl)
    }
    // endregion

    private fun setupView() {
        // is pwb?
        when (BuildConfig.FLAVOR) {
            "pwb" -> {
                stockIndicatorLoading.show()
                stockIndicatorView.setOnClickOtherStores(View.OnClickListener {
                    productDetailListener.onDisplayAvailableStore(product)
                })
                layoutBottomButton.visibility = View.VISIBLE
                loadStockData(product)
            }
            else -> {
                layoutBottomButton.visibility = View.GONE
                stockIndicatorView.visibility = View.GONE
                stockIndicatorLoading.dismiss()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initDetail(product: Product) {
        // setup product image
        val productImageList = product.getProductImageList()
        if (productImageList.productDetailImageItems.size > 0) {
            Glide.with(Contextor.getInstance().context)
                    .load(productImageList.productDetailImageItems[0].imgUrl)
                    .placeholder(R.drawable.ic_placeholder)
                    .crossFade()
                    .fitCenter()
                    .into(ivProductImage)
        } else {
            ivProductImage.setImageDrawable(context?.let { ContextCompat.getDrawable(it, R.drawable.ic_placeholder) })
        }

        rvProductImage.layoutManager = LinearLayoutManager(context, LinearLayout.HORIZONTAL, false)
        rvProductImage.adapter = ProductImageAdapter(this, productImageList.productDetailImageItems)
        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(rvProductImage)

        // setup product detail
        val unit = getString(R.string.baht)
        tvProductName.text = product.name
        tvProductCode.text = "${getString(R.string.product_code)} ${product.sku}"
        tvNormalPrice.text = product.getDisplayOldPrice(unit)

        if (product.isSpecialPrice()) {
            showSpecialPrice(unit, product)
        } else {
            hideSpecialPrice()
        }

        if (product.is1HourProduct()) {
            badge1Hour.set1HourBadge()
        } else {
            badge1Hour.setImageDrawable(null)
        }

        if (product.typeId == "configurable" && !product.extension?.productConfigOptions.isNullOrEmpty()) {
            configOptions = product.extension!!.productConfigOptions
            configOptions?.forEach { configOption ->
                when (configOption.label) {
                    "Size" -> {
                        sizeAdepter = ProductOptionAdepter(context!!, R.layout.layout_text_item, arrayListOf())
                        sizeAdepter.setItems(configOption.values)
                        inputProductSize.setAdapter(sizeAdepter)

                        val defaultOption = configOption.values[0]
                        inputProductSize.setText(defaultOption.valueExtension?.label ?: "")
                        optionSize = OptionBody(configOption.attrId, defaultOption.index) // set default

                        inputProductSize.visibility = View.VISIBLE
                        sizeAdepter.setCallback(object : ProductOptionAdepter.OptionClickListener {
                            override fun onOptionClickListener(optionValue: Int, label: String) {
                                inputProductSize.setText(label)
                                optionSize = OptionBody(configOption.attrId, optionValue)
                            }
                        })
                    }
                    "Shade" -> {
                        shadeAdepter = ProductOptionAdepter(context!!, R.layout.layout_text_item, arrayListOf())
                        shadeAdepter.setItems(configOption.values)
                        inputProductShade.setAdapter(shadeAdepter)

                        val defaultOption = configOption.values[0]
                        inputProductShade.setText(defaultOption.valueExtension?.label ?: "")
                        optionShade = OptionBody(configOption.attrId, defaultOption.index) // set default

                        inputProductShade.visibility = View.VISIBLE
                        shadeAdepter.setCallback(object : ProductOptionAdepter.OptionClickListener {
                            override fun onOptionClickListener(optionValue: Int, label: String) {
                                inputProductShade.setText(label)
                                optionShade = OptionBody(configOption.attrId, optionValue)
                            }
                        })
                    }
                }
            }
        } else {
            inputProductSize.visibility = View.GONE
            inputProductShade.visibility = View.GONE
        }

        // setup add item button
        addToCartButton.setImageDrawable(R.drawable.ic_shopping_cart)
        if (BuildConfig.FLAVOR == "pwb"){
            hideAddToCartButton(product.isSalable())
        }
        addToCartButton.setOnClickListener(this)
        // check disable product
//        disableAddToCartButton(!context.isProductInStock(product))

        add1HourButton.setImageDrawable(R.drawable.ic_1_hour_pick_up)
        add1HourButton.setOnClickListener(this)
        hideAdd1HourItemButton(product.is1HourProduct())

        // setup available store button
        availableStoreButton.setImageDrawable(R.drawable.ic_store)
        availableStoreButton.setOnClickListener(this)

        // setup add to compare button
        addToCompareButton.setImageDrawable(R.drawable.ic_compare_bar)
        addToCompareButton.setOnClickListener(this)

        shareButton.setOnClickListener(this)
    }

    private fun showSpecialPrice(unit: String, product: Product) {
        if (product.specialPrice > 0) {
            if (product.price != product.specialPrice) {
                tvSpecialPrice.text = product.getDisplaySpecialPrice(unit)
                tvTitleSpecialPrice.text = getString(R.string.name_price)
                tvNormalPrice.setEnableStrikeThrough(true)
                tvSpecialPrice.visibility = View.VISIBLE
                tvTitleSpecialPrice.visibility = View.VISIBLE
            } else {
                tvSpecialPrice.visibility = View.GONE
                tvTitleSpecialPrice.visibility = View.GONE
            }
        }
    }

    private fun hideSpecialPrice() {
        tvSpecialPrice.visibility = View.GONE
        tvTitleSpecialPrice.visibility = View.GONE
        tvNormalPrice.setEnableStrikeThrough(false)
    }

    private fun hideAddToCartButton(isSalable: Boolean = true) {
        if (isSalable) {
            addToCartButton.visibility = View.VISIBLE
        } else {
            addToCartButton.visibility = View.GONE
        }
    }

    private fun hideAdd1HourItemButton(is1Hour: Boolean = true) {
        if (is1Hour && BuildConfig.FLAVOR == "pwb") {
            add1HourButton.visibility = View.VISIBLE
        } else {
            add1HourButton.visibility = View.GONE
        }
    }

    private fun loadStockData(product: Product?) {
        if (product == null) {
            stockIndicatorLoading.dismiss()
            return
        }

        stockIndicatorLoading.show()
        context?.let {
            HttpManagerMagento.getInstance(it).getAvailableStore(product.sku,
                    object : ApiResponseCallback<List<StoreAvailable>> {

                        override fun success(response: List<StoreAvailable>?) {
                            activity?.runOnUiThread {
                                handleStockSuccess(response)
                                stockIndicatorLoading.dismiss()
                            }
                        }

                        override fun failure(error: APIError) {
                            Log.e(TAG, "getAvailableStore: ${error.errorMessage}")
                            activity?.runOnUiThread {
                                handleStockFailure()
                                stockIndicatorLoading.dismiss()
                            }
                        }
                    })
        }
    }

    private fun handleStockFailure() {
        product?.let {
            val inStock = context.isProductInStock(it)
            stockIndicatorView.setState(hasOnline = inStock, hasStoreOffline = false, hasStoresOffline = false)
        } ?: run {
            stockIndicatorView.setState() // default was false
        }
        stockIndicatorView.visibility = View.VISIBLE
    }

    private fun handleStockSuccess(response: List<StoreAvailable>?) {
        response?.let {
            val inStock = if (product != null) context.isProductInStock(product!!) else false
            val stockAvailability = it.getStockAvailability()
            stockIndicatorView.setState(hasOnline = inStock,
                    hasStoreOffline = stockAvailability.first,
                    hasStoresOffline = stockAvailability.second)
        } ?: run {
            handleStockFailure()
        }
        stockIndicatorView.visibility = View.VISIBLE
    }

    private fun onAddToCartBy1Hrs() {
        productDetailListener.addProduct1HrsToCart(product)
    }

    companion object {
        private const val TAG = "DetailFragment"
    }
}