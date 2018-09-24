package cenergy.central.com.pwb_store.fragment

import android.content.Context
import android.os.Bundle
import android.support.constraint.Group
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.activity.interfaces.ProductDetailListener
import cenergy.central.com.pwb_store.model.Product

/**
 * Created by Anuphap Suwannamas on 24/9/2018 AD.
 * Email: Anupharpae@gmail.com
 */

class OverviewFragment : Fragment() {
    private var productDetailListener: ProductDetailListener? = null
    private var product: Product? = null

    // widget view
    private lateinit var overviewLayout: LinearLayout
    private lateinit var overviewGroup: Group
    private lateinit var specLayout: LinearLayout
    private lateinit var specGroup: Group

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        productDetailListener = context as ProductDetailListener
        product = productDetailListener?.getProduct()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_overview, container, false)
        setupView(rootView)
        return rootView
    }

    private fun setupView(rootView: View) {
        overviewLayout = rootView.findViewById(R.id.overviewLayout)
        specLayout = rootView.findViewById(R.id.specLayout)
        overviewGroup = rootView.findViewById(R.id.overviewGroup)
        specGroup = rootView.findViewById(R.id.specGroup)

        if (product?.extension?.shortDescription != null && product?.extension?.shortDescription!!.isNotEmpty()) {
            overviewGroup.visibility = View.VISIBLE
            overviewLayout.setOnClickListener { productDetailListener?.onDisplayOverview(product?.extension?.shortDescription!!) }
        } else {
            overviewGroup.visibility = View.GONE
        }

        if (product?.extension?.description != null && product?.extension?.description!!.isNotEmpty()) {
            specGroup.visibility = View.VISIBLE
            specLayout.setOnClickListener { productDetailListener?.onDisplaySpecification(product?.extension?.description!!) }
        } else {
            specGroup.visibility = View.GONE
        }
    }
}