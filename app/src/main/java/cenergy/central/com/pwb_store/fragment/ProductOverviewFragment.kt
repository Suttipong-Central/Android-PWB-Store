package cenergy.central.com.pwb_store.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.ImageView
import android.widget.LinearLayout
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.activity.interfaces.ProductDetailListener
import cenergy.central.com.pwb_store.model.Product
import cenergy.central.com.pwb_store.utils.WebViewGetScaleManager

/**
 * Created by Anuphap Suwannamas on 24/9/2018 AD.
 * Email: Anupharpae@gmail.com
 */

class ProductOverviewFragment : Fragment() {
    private var productDetailListener: ProductDetailListener? = null
    private var product: Product? = null

    // widget view
    private lateinit var overviewLayout: LinearLayout
    private lateinit var overviewWebLayout: LinearLayout
    private lateinit var specWebLayout: LinearLayout
    private lateinit var spec: WebView
    private lateinit var overview: WebView
    private lateinit var specLayout: LinearLayout
    private lateinit var overviewArrowIcon: ImageView
    private lateinit var specArrowIcon: ImageView
    private lateinit var line1: View
    private lateinit var line2: View
    private var overviewVisible = false
    private var specVisible = false
    private var getScaleManager: WebViewGetScaleManager? = null
    private var scale: Int = 0

    companion object {
        const val style = "<meta charset='UTF-8'><style> body, div, p { font-size: 15 !important; } </style>"
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        productDetailListener = context as ProductDetailListener
        product = productDetailListener?.getProduct()
        getScaleManager = WebViewGetScaleManager(context)
        if(getScaleManager != null){
            scale = getScaleManager!!.getScale()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_product_overview, container, false)
        setupView(rootView)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (product?.extension?.shortDescription != null && product?.extension?.shortDescription!!.trim().isNotBlank()) {
            overviewLayout.visibility = View.VISIBLE
            line1.visibility = View.VISIBLE
            val html = style + product?.extension?.shortDescription
            overview.loadDataWithBaseURL(null, html, "text/html", "utf-8", null)
            overview.setInitialScale(scale)
            overviewLayout.setOnClickListener { _ ->
                if (overviewVisible) {
                    overviewWebLayout.visibility = View.GONE
                    context?.let { overviewArrowIcon.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.ic_keyboard_arrow_down)) }
                    overviewVisible = false
                } else {
                    overviewWebLayout.visibility = View.VISIBLE
                    context?.let{overviewArrowIcon.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.ic_keyboard_arrow_up))}
                    overviewVisible = true
                }
            }
        } else {
            overviewLayout.visibility = View.GONE
            line1.visibility = View.GONE
        }
        if (product?.extension?.description != null && product?.extension?.description!!.trim().isNotBlank()) {
            specLayout.visibility = View.VISIBLE
            line2.visibility = View.VISIBLE
            val html = style + product?.extension?.description
            spec.loadDataWithBaseURL(null, html, "text/html", "utf-8", null)
            spec.setInitialScale(scale)
            specLayout.setOnClickListener { _ ->
                if (specVisible) {
                    specWebLayout.visibility = View.GONE
                    context?.let { specArrowIcon.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.ic_keyboard_arrow_down)) }
                    specVisible = false
                } else {
                    specWebLayout.visibility = View.VISIBLE
                    context?.let { specArrowIcon.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.ic_keyboard_arrow_up)) }
                    specVisible = true
                }
            }
        } else {
            specLayout.visibility = View.GONE
            line2.visibility = View.GONE
        }
    }

    private fun setupView(rootView: View) {
        overviewLayout = rootView.findViewById(R.id.overviewLayout)
        overviewWebLayout = rootView.findViewById(R.id.overviewWebLayout)
        overview = rootView.findViewById(R.id.overviewWeb)
        overviewArrowIcon = rootView.findViewById(R.id.overviewArrowIcon)
        line1 = rootView.findViewById(R.id.line1)
        specLayout = rootView.findViewById(R.id.specLayout)
        specWebLayout = rootView.findViewById(R.id.specWebLayout)
        spec = rootView.findViewById(R.id.specWeb)
        specArrowIcon = rootView.findViewById(R.id.specArrowIcon)
        line2 = rootView.findViewById(R.id.line2)
    }
}