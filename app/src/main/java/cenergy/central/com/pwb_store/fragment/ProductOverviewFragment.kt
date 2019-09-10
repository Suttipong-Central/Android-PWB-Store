package cenergy.central.com.pwb_store.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.ImageView
import android.widget.LinearLayout
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.activity.interfaces.ProductDetailListener
import cenergy.central.com.pwb_store.adapter.ProductSpecAdapter
import cenergy.central.com.pwb_store.extensions.setupForDescription
import cenergy.central.com.pwb_store.model.Product
import cenergy.central.com.pwb_store.model.Specification
import cenergy.central.com.pwb_store.utils.WebViewGetScaleManager
import kotlinx.android.synthetic.main.fragment_product_overview.*
import kotlinx.android.synthetic.main.fragment_product_overview.view.*

class ProductOverviewFragment : Fragment() {
    private var productDetailListener: ProductDetailListener? = null
    private var product: Product? = null

    // widget view
    private lateinit var overviewLayout: LinearLayout
    private lateinit var overviewWebLayout: LinearLayout
    private lateinit var specListLayout: LinearLayout
    private lateinit var overview: WebView
    private lateinit var specLayout: LinearLayout
    private lateinit var overviewArrowIcon: ImageView
    private lateinit var specArrowIcon: ImageView
    private lateinit var infoLayout: LinearLayout
    private lateinit var infoWebLayout: LinearLayout
    private lateinit var infoWebView: WebView
    private lateinit var infoArrowIcon: ImageView
    private lateinit var line1: View
    private lateinit var line2: View
    private lateinit var line3: View
    private var overviewVisible = false
    private var infoVisible = false
    private var specVisible = false
    private var scale: Int = 0

    companion object {
        const val style = "<meta charset='UTF-8'><style> body, div, p { font-size: 15 !important; } </style>"
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        productDetailListener = context as ProductDetailListener
        product = productDetailListener?.getProduct()
        scale = WebViewGetScaleManager(context).getScale()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_product_overview, container, false)
        setupView(rootView)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupProductKeyFeatures()
        setupProductInfo()
        setupProductSpecification()
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
    }

    private fun setupProductKeyFeatures() {
        if (product?.extension?.shortDescription != null && product?.extension?.shortDescription!!.trim().isNotBlank()) {
            overviewLayout.visibility = View.VISIBLE
            line1.visibility = View.VISIBLE
            val html = style + product?.extension?.shortDescription
            overview.loadDataWithBaseURL(null, html, "text/html", "utf-8", null)
            overview.setInitialScale(scale)

        } else {
            overviewLayout.visibility = View.GONE
            line1.visibility = View.GONE
        }
    }

    private fun setupView(rootView: View) {
        overviewLayout = rootView.findViewById(R.id.overviewLayout)
        overviewWebLayout = rootView.findViewById(R.id.overviewWebLayout)
        overview = rootView.findViewById(R.id.overviewWeb)
        overviewArrowIcon = rootView.findViewById(R.id.overviewArrowIcon)
        line1 = rootView.findViewById(R.id.line1)
        specLayout = rootView.findViewById(R.id.specLayout)
        specListLayout = rootView.findViewById(R.id.specListLayout)
        specArrowIcon = rootView.findViewById(R.id.specArrowIcon)
        line3 = rootView.findViewById(R.id.line3)
        infoLayout = rootView.findViewById(R.id.infoLayout)
        infoWebLayout = rootView.findViewById(R.id.infowWebLayout)
        infoWebView = rootView.findViewById(R.id.infoWebView)
        infoArrowIcon = rootView.findViewById(R.id.infoArrowIcon)
        line2 = rootView.findViewById(R.id.line2)

        rootView.tvOverviewHeader.text = getString(R.string.product_overview)
        rootView.tvInfoHeader.text = getString(R.string.product_info)
        rootView.tvSpecHeader.text = getString(R.string.product_spec)

        setupOnClick()

        overview.setupForDescription()
        infoWebView.setupForDescription()
    }

    private fun setupOnClick() {
        specLayout.setOnClickListener {
            specListLayout.visibility = if (specVisible) View.GONE else View.VISIBLE
            specArrowIcon.setImageDrawable(ContextCompat.getDrawable(context!!,
                    if (specVisible) R.drawable.ic_keyboard_arrow_down
                    else R.drawable.ic_keyboard_arrow_up))
            specVisible = !specVisible
        }

        infoLayout.setOnClickListener {
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

        overviewLayout.setOnClickListener {
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