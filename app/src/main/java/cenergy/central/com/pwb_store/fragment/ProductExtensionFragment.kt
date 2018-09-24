package cenergy.central.com.pwb_store.fragment

import android.content.Context
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.activity.interfaces.ProductDetailListener
import cenergy.central.com.pwb_store.model.Product

/**
 * Created by Anuphap Suwannamas on 24/9/2018 AD.
 * Email: Anupharpae@gmail.com¬
 */

class ProductExtensionFragment : Fragment() {
    private var productDetailListener: ProductDetailListener? = null
    private var product: Product? = null

    // widget view
    private lateinit var extensionTabLayout: TabLayout

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        productDetailListener = context as ProductDetailListener
        product = productDetailListener?.getProduct()
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_product_extension, container, false)
        setupView(rootView)
        return rootView
    }

    private fun setupView(rootView: View) {
        extensionTabLayout = rootView.findViewById(R.id.extensionTabLayout)

        extensionTabLayout.addTab(extensionTabLayout.newTab().setText("โปรโมชั่นและของแถม"))
        extensionTabLayout.addTab(extensionTabLayout.newTab().setText("ตัวเลือกการจัดส่ง"))
    }
}