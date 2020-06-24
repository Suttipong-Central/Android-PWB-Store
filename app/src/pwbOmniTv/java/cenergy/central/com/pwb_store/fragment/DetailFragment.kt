package cenergy.central.com.pwb_store.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.activity.GalleryActivity
import cenergy.central.com.pwb_store.activity.interfaces.ProductDetailListener
import cenergy.central.com.pwb_store.adapter.ProductImageAdapter
import cenergy.central.com.pwb_store.adapter.interfaces.ProductImageListener
import cenergy.central.com.pwb_store.extensions.isSpecialPrice
import cenergy.central.com.pwb_store.extensions.setImage
import cenergy.central.com.pwb_store.extensions.setImageUrl
import cenergy.central.com.pwb_store.manager.Contextor
import cenergy.central.com.pwb_store.model.Product
import cenergy.central.com.pwb_store.model.ProductDetailImage
import cenergy.central.com.pwb_store.model.ProductDetailImageItem
import cenergy.central.com.pwb_store.realm.RealmController
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.pwbOmniTv.fragment_detail.*

@SuppressLint("SetTextI18n")
class DetailFragment : Fragment(), View.OnClickListener, ProductImageListener {
    private lateinit var productDetailListener: ProductDetailListener
    private var product: Product? = null
    private var productChildren = arrayListOf<Product>()
    private var imageSelectedIndex: Int = 0
    private lateinit var productImageList: ProductDetailImage

    override fun onAttach(context: Context) {
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
        product?.let { initDetail(it) }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.ivProductImage -> {
                context?.let {
                    GalleryActivity.startActivity(it, imageSelectedIndex, productImageList.productDetailImageItems)
                }
            }
        }
    }

    // region {@link ProductImageListener.onProductImageClickListener}
    override fun onProductImageClickListener(index: Int, productImage: ProductDetailImageItem) {
        this.imageSelectedIndex = index
        if (productImage.imgUrl != null) {
            ivProductImage.setImageUrl(productImage.imgUrl!!)
        } else {
            ivProductImage.setImage(R.drawable.ic_placeholder)
        }
    }
    // endregion

    fun updateImageSelected(imageSelectedIndex: Int) {
        if (productImageList.productDetailImageItems[imageSelectedIndex].imgUrl != null) {
            ivProductImage.setImageUrl(productImageList.productDetailImageItems[imageSelectedIndex].imgUrl!!)
        } else {
            ivProductImage.setImage(R.drawable.ic_placeholder)
        }
    }

    private fun initDetail(product: Product) {
        // setup product image
        productImageList = product.getProductImageList()
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

        rvProductImage.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
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

        // setup image
        ivProductImage.setOnClickListener(this)

        // setup share button
        shareButton.setOnClickListener(this)

        // setup compare button
        btnCompare.setImageDrawable(R.drawable.ic_compare_bar)
        updateCompareCheckBox()
    }

    fun updateCompareCheckBox() {
        if (product != null) {
            val compareProduct = RealmController.getInstance().getCompareProduct(product!!.sku)
            if (compareProduct != null) {
                setDisableCompareButton()
            } else {
                setEnableCompareButton()
            }
        }
    }

    fun setEnableCompareButton() {
        btnCompare.setButtonDisable(false)
        btnCompare.setOnClickListener {
            productDetailListener.addProductToCompare(product, true)
        }
    }

    fun setDisableCompareButton() {
        btnCompare.setButtonDisable(true)
        btnCompare.setOnClickListener(null)
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
}