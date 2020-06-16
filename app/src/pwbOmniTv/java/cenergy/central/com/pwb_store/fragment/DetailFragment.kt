package cenergy.central.com.pwb_store.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.activity.interfaces.ProductDetailListener
import cenergy.central.com.pwb_store.adapter.ProductImageAdapter
import cenergy.central.com.pwb_store.adapter.interfaces.ProductImageListener
import cenergy.central.com.pwb_store.extensions.isSpecialPrice
import cenergy.central.com.pwb_store.extensions.setImageUrl
import cenergy.central.com.pwb_store.manager.ApiResponseCallback
import cenergy.central.com.pwb_store.manager.Contextor
import cenergy.central.com.pwb_store.manager.HttpManagerMagento
import cenergy.central.com.pwb_store.model.APIError
import cenergy.central.com.pwb_store.model.Product
import cenergy.central.com.pwb_store.model.ProductDetailImageItem
import cenergy.central.com.pwb_store.model.StoreAvailable
import cenergy.central.com.pwb_store.realm.RealmController
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.pwbOmniTv.fragment_detail.*

@SuppressLint("SetTextI18n")
class DetailFragment : Fragment(), ProductImageListener {
    private lateinit var productDetailListener: ProductDetailListener
    private var product: Product? = null
    private var productChildren = arrayListOf<Product>()

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

    // region {@link ProductImageListener.onProductImageClickListener}
    override fun onProductImageClickListener(productImage: ProductDetailImageItem) {
        ivProductImage.setImageUrl(productImage.imgUrl)
    }
    // endregion

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

        // region compare
//        addToCompare.setImageDrawable(R.drawable.ic_compare_bar)
//        setEnableCompareButton()
//        updateAddToCompareButton()
        // end region compare

        tvAvailableHere.visibility = if (product.availableThisStore) View.VISIBLE else View.GONE
        shareButton.visibility = View.GONE
    }

    fun updateAddToCompareButton() {
        if (product != null) {
            val compareProduct = RealmController.getInstance().getCompareProduct(product!!.sku)
            if (compareProduct != null){
                setDisableCompareButton()
            }
        }
    }

    fun setEnableCompareButton() {
        addToCompare.setButtonDisable(false)
        addToCompare.setOnClickListener {
            productDetailListener.addProductToCompare(product, true)
        }
    }

    fun setDisableCompareButton(){
        addToCompare.setButtonDisable(true)
        addToCompare.setOnClickListener(null)
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