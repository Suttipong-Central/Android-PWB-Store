package cenergy.central.com.pwb_store.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import cenergy.central.com.pwb_store.BuildConfig
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.activity.interfaces.ProductDetailListener
import cenergy.central.com.pwb_store.adapter.ProductSpecAdapter
import cenergy.central.com.pwb_store.extensions.setupForDescription
import cenergy.central.com.pwb_store.model.Product
import cenergy.central.com.pwb_store.model.Specification
import cenergy.central.com.pwb_store.utils.WebViewGetScaleManager
import kotlinx.android.synthetic.main.fragment_product_overview.*

class ProductOverviewFragment : Fragment() {
    private var productDetailListener: ProductDetailListener? = null
    private var product: Product? = null

    private var overviewVisible = false
    private var infoVisible = false
    private var specVisible = false
    private var scale: Int = 0

    companion object {
        const val style = "<meta charset='UTF-8'><style> body, div, p { font-size: 15 !important; } </style>"
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        productDetailListener = context as ProductDetailListener
        product = productDetailListener?.getProduct()
        scale = WebViewGetScaleManager(context).getScale()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_product_overview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupProductSpecification()
        setupProductInfo()
        setupProductKeyFeatures()
        setupTextHeader()
        setupOnClick()
        if (BuildConfig.FLAVOR == "pwbOmniTV"){
            checkExplainOverview()
            checkExplainInfo()
            checkExplainSpec()
        }
    }

    private fun setupProductSpecification() {
        if (product?.extension == null) {
            return
        }
        val productExtension = product?.extension!!

        if (productExtension.specifications.isNotEmpty()) {
            specLayout.visibility = View.VISIBLE
            line3.visibility = View.VISIBLE

            setupSpecificationList(productExtension.specifications)
        } else {
            specLayout.visibility = View.GONE
            line3.visibility = View.GONE
        }
    }

    private fun setupSpecificationList(specifications: List<Specification>) {
        val items = arrayListOf<Specification>()
        // TBD- only it is ready to display
        specifications.forEach {
            if (it.value!= null && it.value!!.isNotBlank()) {
                items.add(it)
            }
        }

        rvSpecifications?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = ProductSpecAdapter(items)
        }
    }

    private fun setupProductInfo() {
        if (product?.extension?.description != null && product?.extension?.description!!.trim().isNotBlank()) {
            infoLayout.visibility = View.VISIBLE
            line2.visibility = View.VISIBLE
            val html = style + product?.extension?.description
            infoWebView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null)
            infoWebView.setInitialScale(scale)
        } else {
            infoLayout.visibility = View.GONE
            line2.visibility = View.GONE
        }
        infoWebView.setupForDescription()
    }

    private fun setupProductKeyFeatures() {
        if (product?.extension?.shortDescription != null && product?.extension?.shortDescription!!.trim().isNotBlank()) {
            overviewLayout.visibility = View.VISIBLE
            line1.visibility = View.VISIBLE
            val html = style + product?.extension?.shortDescription
            overviewWeb.loadDataWithBaseURL(null, html, "text/html", "utf-8", null)
            overviewWeb.setInitialScale(scale)
        } else {
            overviewLayout.visibility = View.GONE
            line1.visibility = View.GONE
        }
        overviewWeb.setupForDescription()
    }

    private fun setupTextHeader(){
        tvOverviewHeader.text = getString(R.string.product_overview)
        tvInfoHeader.text = getString(R.string.product_info)
        tvSpecHeader.text = getString(R.string.product_spec)
    }

    private fun setupOnClick() {
        specLayout.setOnClickListener {
            checkExplainSpec()
        }

        infoLayout.setOnClickListener {
            checkExplainInfo()
        }

        overviewLayout.setOnClickListener {
            checkExplainOverview()
        }
    }

    private fun checkExplainSpec(){
        if (!product!!.extension!!.specifications.isNullOrEmpty()) {
            if (specVisible){
                specListLayout.visibility =  View.GONE
                context?.let { specArrowIcon.setImageDrawable(
                        ContextCompat.getDrawable(it, R.drawable.ic_keyboard_arrow_down)) }
                specVisible = false
            } else {
                specListLayout.visibility =  View.VISIBLE
                context?.let { specArrowIcon.setImageDrawable(
                        ContextCompat.getDrawable(it, R.drawable.ic_keyboard_arrow_up)) }
                specVisible = true
            }
        }
    }

    private fun checkExplainInfo(){
        if (product?.extension?.description != null && product?.extension?.description!!.trim().isNotBlank()) {
            if (infoVisible) {
                infoWebLayout.visibility = View.GONE
                context?.let { infoArrowIcon.setImageDrawable(
                        ContextCompat.getDrawable(it, R.drawable.ic_keyboard_arrow_down)) }
                infoVisible = false
            } else {
                infoWebLayout.visibility = View.VISIBLE
                context?.let { infoArrowIcon.setImageDrawable(
                        ContextCompat.getDrawable(it, R.drawable.ic_keyboard_arrow_up)) }
                infoVisible = true
            }
        }
    }

    private fun checkExplainOverview(){
        if (product?.extension?.shortDescription != null && product?.extension?.shortDescription!!.trim().isNotBlank()) {
            if (overviewVisible) {
                overviewWebLayout.visibility = View.GONE
                context?.let { overviewArrowIcon.setImageDrawable(
                        ContextCompat.getDrawable(it, R.drawable.ic_keyboard_arrow_down)) }
                overviewVisible = false
            } else {
                overviewWebLayout.visibility = View.VISIBLE
                context?.let { overviewArrowIcon.setImageDrawable(
                        ContextCompat.getDrawable(it, R.drawable.ic_keyboard_arrow_up)) }
                overviewVisible = true
            }
        }
    }
}