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
import cenergy.central.com.pwb_store.adapter.ShadeSelectAdapter
import cenergy.central.com.pwb_store.adapter.interfaces.ProductImageListener
import cenergy.central.com.pwb_store.adapter.interfaces.ShadeClickListener
import cenergy.central.com.pwb_store.extensions.*
import cenergy.central.com.pwb_store.manager.ApiResponseCallback
import cenergy.central.com.pwb_store.manager.Contextor
import cenergy.central.com.pwb_store.manager.HttpManagerMagento
import cenergy.central.com.pwb_store.model.*
import cenergy.central.com.pwb_store.model.body.OptionBody
import cenergy.central.com.pwb_store.view.PowerBuyIconButton
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_detail.*

@SuppressLint("SetTextI18n")
class DetailFragment : Fragment(), View.OnClickListener, ProductImageListener {
    private lateinit var productDetailListener: ProductDetailListener
    private var product: Product? = null
    private var childProduct: Product? = null
    private var configOptions: List<ProductOption>? = listOf()
    private var configItemOptions: ArrayList<OptionBody> = arrayListOf()
    var optionSize: OptionBody? = null
    var optionShade: OptionBody? = null
    private var productOptionShade: ProductOption? = null
    private var productOptionSize: ProductOption? = null
    private var shadeSelectedOption: ProductValue? = null
    private var sizeSelectedOption: ProductValue? = null
    private var sizeValues: List<ProductValue> = listOf()
    private val sizeAdepter by lazy { ProductOptionAdepter(context!!, R.layout.layout_text_item) }
    private var sizeAttributeId: String = ""
    private var shadeAttributeId: String = ""
    private var productChildren = arrayListOf<Product>()

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        productDetailListener = context as ProductDetailListener
        product = productDetailListener.getProduct()
        productChildren = productDetailListener.getChildProduct()
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
                        productDetailListener.addProductConfigToCart(product, childProduct, configItemOptions)
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

        if (product.typeId == "configurable" && product.extension != null) {
            configOptions = product.extension!!.productConfigOptions
            configOptions?.let { configOption ->
                productOptionShade = configOption.firstOrNull { option -> option.label == "Shade" }
                productOptionShade?.let {
                    shadeAttributeId = productOptionShade!!.attrId
                    val shadeValues = productOptionShade!!.values.filter {
                        it.valueExtension != null && it.valueExtension!!.products.isNotEmpty() }
                    val shadeAdapter = ShadeSelectAdapter(shadeValues)
                    inputProductShade.setAdapter(shadeAdapter)
                    shadeSelectedOption = shadeValues[0]
                    inputProductShade.setShadeName(shadeSelectedOption!!.valueExtension?.label ?: "")
                    optionShade = OptionBody(shadeAttributeId, shadeSelectedOption!!.index) // set default
                    inputProductShade.visibility = View.VISIBLE
                    shadeAdapter.setCallBack(object : ShadeClickListener {
                        override fun onShadeClickListener(shade: ProductValue) {
                            inputProductShade.setShadeName(shade.valueExtension?.label ?: "")
                            optionShade = OptionBody(shadeAttributeId, shade.index)
                            shadeSelectedOption = shade
                            if (productOptionSize != null){
                                handleUpdateSizeAdapter()
                            }
                            handleUpdateViewProductConfig()
                        }
                    })
                }

                productOptionSize = configOption.firstOrNull { option -> option.label == "Size" }
                productOptionSize?.let {
                    sizeAttributeId = productOptionSize!!.attrId
                    sizeValues = productOptionSize!!.values.filter { it.valueExtension != null && it.valueExtension!!.products.isNotEmpty() }
                    if (shadeSelectedOption != null){
                        handleUpdateSizeAdapter()
                    } else {
                        sizeAdepter.setItems(sizeValues)
                        sizeSelectedOption = handleDefaultSizeOption(sizeValues)
                    }
                    inputProductSize.setAdapter(sizeAdepter)
                    inputProductSize.setHeaderTextBold()
                    inputProductSize.setHeaderTextColor(R.color.nameProductColor)
                    inputProductSize.setText(sizeSelectedOption!!.valueExtension?.label ?: "")
                    optionSize = OptionBody(sizeAttributeId, sizeSelectedOption!!.index) // set default
                    inputProductSize.visibility = View.VISIBLE
                    sizeAdepter.setCallback(object : ProductOptionAdepter.OptionClickListener {
                        override fun onOptionClickListener(size: ProductValue) {
                            inputProductSize.setText(size.valueExtension?.label?: "")
                            optionSize = OptionBody(sizeAttributeId, size.index)
                            sizeSelectedOption = size
                            handleUpdateViewProductConfig()
                        }
                    })
                }
                handleUpdateViewProductConfig()
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

    private fun handleUpdateSizeAdapter() {
        val newSizeVale = arrayListOf<ProductValue>()
        shadeSelectedOption!!.valueExtension?.products?.forEach { shadeId ->
            val size = sizeValues.firstOrNull{ it.valueExtension!!.products.contains(shadeId)}
            if (size != null){
                newSizeVale.add(size)
            }
        }
        sizeAdepter.setItems(newSizeVale)
        sizeSelectedOption = newSizeVale[0]
        inputProductSize.setText(sizeSelectedOption!!.valueExtension?.label ?: "")
        optionSize = OptionBody(sizeAttributeId, sizeSelectedOption!!.index) // set default
    }

    private fun handleUpdateViewProductConfig() {
        if (shadeSelectedOption != null && sizeSelectedOption != null){
            val listProductShadeChild = shadeSelectedOption!!.valueExtension!!.products
            val listProductSizeChild = sizeSelectedOption!!.valueExtension!!.products
            val groupProductChildren = listOf(listProductShadeChild, listProductSizeChild)
            val childProductId = groupProductChildren.findIntersect()[0] // index 0 because we think just only one have intersect
            childProduct = productChildren.first { it.id == childProductId }
            updateViewProductConfig()
        } else if (shadeSelectedOption != null){
            childProduct = productChildren.first { it.id == shadeSelectedOption!!.valueExtension!!.products[0] }
            updateViewProductConfig()
        } else if (sizeSelectedOption != null){
            childProduct = productChildren.first { it.id == sizeSelectedOption!!.valueExtension!!.products[0] }
            updateViewProductConfig()
        }
    }

    private fun updateViewProductConfig() {
        // setup product image
        if (childProduct != null){
            val productImageList = childProduct!!.getProductImageList()
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
            rvProductImage.adapter = ProductImageAdapter(this, productImageList.productDetailImageItems)

            // setup product detail
            val unit = getString(R.string.baht)
            tvProductName.text = childProduct!!.name
            tvProductCode.text = "${getString(R.string.product_code)} ${childProduct!!.sku}"
            tvNormalPrice.text = childProduct!!.getDisplayOldPrice(unit)

            if (childProduct!!.isSpecialPrice()) {
                showSpecialPrice(unit, childProduct!!)
            } else {
                hideSpecialPrice()
            }
        }
    }

    private fun handleDefaultSizeOption(sizeValues: List<ProductValue>): ProductValue {
        return if (shadeSelectedOption != null){
            val shadeChildId = shadeSelectedOption!!.valueExtension!!.products.first()
            sizeValues.firstOrNull { it.valueExtension!!.products.first() == shadeChildId } ?: sizeValues[0]
        } else {
            sizeValues[0]
        }
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
        // TODO: Refactor checking app flavor
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