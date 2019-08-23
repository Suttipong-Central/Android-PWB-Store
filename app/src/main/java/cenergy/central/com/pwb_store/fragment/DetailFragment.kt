package cenergy.central.com.pwb_store.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearSnapHelper
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import cenergy.central.com.pwb_store.BuildConfig
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.activity.interfaces.ProductDetailListener
import cenergy.central.com.pwb_store.adapter.ProductImageAdapter
import cenergy.central.com.pwb_store.adapter.ProductOptionAdepter
import cenergy.central.com.pwb_store.adapter.interfaces.ProductImageListener
import cenergy.central.com.pwb_store.extensions.isProductInStock
import cenergy.central.com.pwb_store.extensions.setImageUrl
import cenergy.central.com.pwb_store.manager.Contextor
import cenergy.central.com.pwb_store.model.Product
import cenergy.central.com.pwb_store.model.ProductDetailImageItem
import cenergy.central.com.pwb_store.model.body.OptionBody
import cenergy.central.com.pwb_store.realm.RealmController
import cenergy.central.com.pwb_store.view.PowerBuyAutoCompleteTextStroke
import cenergy.central.com.pwb_store.view.PowerBuyIconButton
import cenergy.central.com.pwb_store.view.PowerBuyTextView
import com.bumptech.glide.Glide


class DetailFragment : Fragment(), View.OnClickListener, ProductImageListener {
    private lateinit var productDetailListener: ProductDetailListener
    private var product: Product? = null

    // widget view
    private lateinit var ivProductImage: ImageView
    private lateinit var rvProductImage: RecyclerView
    private lateinit var tvProductName: TextView
    private lateinit var tvProductCode: TextView
    private lateinit var tvTitleSpecialPrice: TextView
    private lateinit var tvSpecialPrice: TextView
    private lateinit var tvNormalPrice: PowerBuyTextView
    private lateinit var addItemButton: PowerBuyIconButton
    private lateinit var storeButton: PowerBuyIconButton
    private lateinit var compareCheckBox: CheckBox

    private lateinit var productSizeSelect: PowerBuyAutoCompleteTextStroke
    private lateinit var productShadeSelect: PowerBuyAutoCompleteTextStroke

    private lateinit var sizeAdepter: ProductOptionAdepter
    private lateinit var shadeAdepter: ProductOptionAdepter
    private var configItemOptions: ArrayList<OptionBody> = arrayListOf()
    var optionSize: OptionBody? = null
    var optionShade: OptionBody? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        productDetailListener = context as ProductDetailListener
        product = productDetailListener.getProduct()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_detail, container, false)
        setupView(rootView)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        product?.let { initDetail(it) }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.availableStoreButton -> {
                if (!storeButton.isDisable) {
                    productDetailListener.onDisplayAvailableStore(product)
                }
            }

            R.id.addToCartButton -> {
                if (!(view as PowerBuyIconButton).isDisable) {
                    product ?: return
                    if (product!!.typeId == "configurable") {
                        if (optionSize == null) {
                            Toast.makeText(context, "Size null", Toast.LENGTH_LONG).show()
                            return
                        }
                        if (optionShade == null) {
                            Toast.makeText(context, "Shade null", Toast.LENGTH_LONG).show()
                            return
                        }
                        configItemOptions.add(optionSize!!)
                        configItemOptions.add(optionShade!!)
                        productDetailListener.addProductConfigToCart(product, configItemOptions)
                    } else {
                        productDetailListener.addProductToCart(product)
                    }
                }
            }
        }
    }

    // region {@link ProductImageListener.onProductImageClickListener}
    override fun onProductImageClickListener(productImage: ProductDetailImageItem) {
        ivProductImage.setImageUrl(productImage.imgUrl)
    }
    // endregion

    private fun setupView(rootView: View) {
        ivProductImage = rootView.findViewById(R.id.ivProductImage)
        rvProductImage = rootView.findViewById(R.id.rvProductImage)
        tvProductName = rootView.findViewById(R.id.tvProductName)
        tvProductCode = rootView.findViewById(R.id.txt_view_product_code)
        tvTitleSpecialPrice = rootView.findViewById(R.id.txt_name_price)
        tvSpecialPrice = rootView.findViewById(R.id.txt_sale_price)
        tvNormalPrice = rootView.findViewById(R.id.txt_regular)
        addItemButton = rootView.findViewById(R.id.addToCartButton)
        storeButton = rootView.findViewById(R.id.availableStoreButton)
        compareCheckBox = rootView.findViewById(R.id.compareCheckBox)

        when (BuildConfig.FLAVOR) {
            "cds", "rbs" -> storeButton.setButtonDisable(true)
        }

        productSizeSelect = rootView.findViewById(R.id.inputProductSize)
        productShadeSelect = rootView.findViewById(R.id.inputProductShade)
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

        val configOptions = product.extension?.productConfigOptions
        if (product.typeId == "configurable" && configOptions != null) {
            configOptions.forEach { configOption ->
                when (configOption.label) {
                    "Size" -> {
                        sizeAdepter = ProductOptionAdepter(context!!, R.layout.layout_text_item, arrayListOf())
                        sizeAdepter.setItems(configOption.values)
                        productSizeSelect.setAdapter(sizeAdepter)

                        val defaultOption = configOption.values[0]
                        optionSize = OptionBody(configOption.attrId, defaultOption.index) // set default
                        productSizeSelect.setText(defaultOption.valueExtension?.label ?: "")

                        productSizeSelect.visibility = View.VISIBLE
                        sizeAdepter.setCallback(object : ProductOptionAdepter.OptionClickListener {
                            override fun onOptionClickListener(optionValue: Int, label: String) {
                                productSizeSelect.setText(label)
                                optionSize = OptionBody(configOption.attrId, optionValue)
                            }
                        })
                    }
                    "Shade" -> {
                        shadeAdepter = ProductOptionAdepter(context!!, R.layout.layout_text_item, arrayListOf())
                        shadeAdepter.setItems(configOption.values)
                        productShadeSelect.setAdapter(shadeAdepter)

                        val defaultOption = configOption.values[0]
                        optionShade = OptionBody(configOption.attrId, defaultOption.index) // set default
                        productShadeSelect.setText(defaultOption.valueExtension?.label ?: "")

                        productShadeSelect.visibility = View.VISIBLE
                        shadeAdepter.setCallback(object : ProductOptionAdepter.OptionClickListener {
                            override fun onOptionClickListener(optionValue: Int, label: String) {
                                productShadeSelect.setText(label)
                                optionShade = OptionBody(configOption.attrId, optionValue)
                            }
                        })
                    }
                }
            }
        } else {
            productSizeSelect.visibility = View.GONE
            productShadeSelect.visibility = View.GONE
        }

        // setup add item button
        addItemButton.setImageDrawable(R.drawable.ic_shopping_cart)
        if (product.extension?.stokeItem?.isInStock == true) {
            addItemButton.setButtonDisable(false)
            addItemButton.setOnClickListener(this)
        } else {
            disableAddToCartButton()
        }

        // setup available store button
        storeButton.setImageDrawable(R.drawable.ic_store)
        storeButton.setOnClickListener(this)

        // setup add to compare check box
        updateCompareCheckBox()
        compareCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
            productDetailListener.addProductToCompare(product, isChecked)
        }

        // check disable product
        disableAddToCartButton(!context.isProductInStock(product))
    }

    fun updateCompareCheckBox() {
        if (product != null) {
            val compareProduct = RealmController.getInstance().getCompareProduct(product!!.sku)
            compareCheckBox.isChecked = compareProduct != null
        }
    }

    fun unCheckCompareCheckBox() {
        compareCheckBox.isChecked = false
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

    fun disableAddToCartButton(isDisable: Boolean = true) {
        addItemButton.setButtonDisable(isDisable)
    }
}