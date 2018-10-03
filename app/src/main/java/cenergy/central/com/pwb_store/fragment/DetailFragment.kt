package cenergy.central.com.pwb_store.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearSnapHelper
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.activity.interfaces.ProductDetailListener
import cenergy.central.com.pwb_store.adapter.ProductImageAdapter
import cenergy.central.com.pwb_store.adapter.interfaces.ProductImageListener
import cenergy.central.com.pwb_store.extensions.setImageUrl
import cenergy.central.com.pwb_store.manager.Contextor
import cenergy.central.com.pwb_store.model.Product
import cenergy.central.com.pwb_store.model.ProductDetailImageItem
import cenergy.central.com.pwb_store.view.PowerBuyTextView
import com.bumptech.glide.Glide


/**
 * Created by Anuphap Suwannamas on 21/9/2018 AD.
 * Email: Anupharpae@gmail.com
 */

class DetailFragment : Fragment(), View.OnClickListener, ProductImageListener {
    private var productDetailListener: ProductDetailListener? = null
    private var product: Product? = null

    // widget view
    private lateinit var ivProductImage: ImageView
    private lateinit var rvProductImage: RecyclerView
    private lateinit var tvProductName: TextView
    private lateinit var tvStock: TextView
    private lateinit var tvProductCode: TextView
    private lateinit var tvTitleSpecialPrice: TextView
    private lateinit var tvSpecialPrice: TextView
    private lateinit var tvNormalPrice: PowerBuyTextView
    private lateinit var addItemButton: CardView
    private lateinit var storeButton: CardView
    private lateinit var compareButton: CardView

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        productDetailListener = context as ProductDetailListener
        product = productDetailListener?.getProduct()
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
            R.id.card_view_store -> {
                productDetailListener?.onDisplayAvailableStore(product)
            }

            R.id.card_view_add_compare -> {
                productDetailListener?.addProductToCompare(product)
            }

            R.id.card_view_add_to_cart -> {
                productDetailListener?.addProductToCart(product)
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
        tvStock = rootView.findViewById(R.id.txt_stock)
        tvProductCode = rootView.findViewById(R.id.txt_view_product_code)
        tvTitleSpecialPrice = rootView.findViewById(R.id.txt_name_price)
        tvSpecialPrice = rootView.findViewById(R.id.txt_sale_price)
        tvNormalPrice = rootView.findViewById(R.id.txt_regular)
        addItemButton = rootView.findViewById(R.id.card_view_add_to_cart)
        storeButton = rootView.findViewById(R.id.card_view_store)
        compareButton = rootView.findViewById(R.id.card_view_add_compare)
    }

    @SuppressLint("SetTextI18n")
    private fun initDetail(product: Product) {
        // setup product image
        val productImageList = product.getProductImageList()
        if (productImageList.productDetailImageItems.size > 0) {
            Glide.with(Contextor.getInstance().context)
                    .load(productImageList.productDetailImageItems[0].imgUrl)
                    .placeholder(R.drawable.ic_pwb_logo_detail)
                    .crossFade()
                    .fitCenter()
                    .into(ivProductImage)
        } else {
            ivProductImage.setImageDrawable(context?.let { ContextCompat.getDrawable(it, R.drawable.ic_pwb_logo_detail) })
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
        if (product.specialPrice > 0) {
            if (product.price != product.specialPrice) {
                tvSpecialPrice.text = product.getDisplaySpecialPrice(unit)
                tvTitleSpecialPrice.text = getString(R.string.name_price)
                tvNormalPrice.setEnableStrikeThrough(true)
            } else {
                tvSpecialPrice.text = ""
                tvTitleSpecialPrice.text = ""
            }
        }
        val txtStock: String
        if (product.extension?.stokeItem?.isInStock == true) {
            when {
                product.extension?.stokeItem?.qty!! > 5 -> {
                    txtStock = getString(R.string.product_stock_more)
                    context?.let { tvStock.setTextColor(ContextCompat.getColor(it, R.color.inStockColor)) }
                }
                product.extension?.stokeItem?.qty!! > 1 -> {
                    txtStock = getString(R.string.product_stock_medium)
                    context?.let { tvStock.setTextColor(ContextCompat.getColor(it, R.color.powerBuyOrange)) }
                }
                product.extension?.stokeItem?.qty!! > 0 -> {
                    txtStock = getString(R.string.product_stock_less)
                    context?.let { tvStock.setTextColor(ContextCompat.getColor(it, R.color.salePriceColor)) }
                }
                else -> {
                    txtStock = getString(R.string.product_out_stock)
                    context?.let { tvStock.setTextColor(ContextCompat.getColor(it, R.color.salePriceColor)) }
                }
            }
            context?.let { addItemButton.setCardBackgroundColor(ContextCompat.getColor(it, R.color.powerBuyPurple)) }
            addItemButton.setOnClickListener(this)
        } else {
            txtStock = getString(R.string.product_out_stock)
            context?.let { tvStock.setTextColor(ContextCompat.getColor(it, R.color.salePriceColor)) }
        }
        tvStock.text = txtStock

        // setup onclick
        storeButton.setOnClickListener(this)
        compareButton.setOnClickListener(this)
    }
}