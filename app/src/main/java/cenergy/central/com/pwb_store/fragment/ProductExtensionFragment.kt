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

        extensionTabLayout.addTab(extensionTabLayout.newTab().setTag(TAB_PROMOTION_FREEBIE).setText("โปรโมชั่นและของแถม"))
        extensionTabLayout.addTab(extensionTabLayout.newTab().setTag(TAB_DELIVERY).setText("ตัวเลือกการจัดส่ง"))
        extensionTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.tag) {
                    TAB_PROMOTION_FREEBIE -> startChildFragment(ProductFreeItemFragment())
                    TAB_DELIVERY -> startChildFragment(ProductShippingOptionFragment())
                }
            }

        })
        extensionTabLayout.getTabAt(0)?.select()
    }

    fun startChildFragment(fragment: Fragment) {
        fragmentManager?.beginTransaction()?.replace(R.id.extensionContainer, fragment)?.commit()
    }

    companion object {
        private const val TAB_PROMOTION_FREEBIE = "promotion_and_freebie"
        private const val TAB_DELIVERY = "delivery"
    }
}