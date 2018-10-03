package cenergy.central.com.pwb_store.fragment

import android.content.Context
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.activity.interfaces.ProductDetailListener
import cenergy.central.com.pwb_store.extensions.setImage
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        extensionTabLayout.getTabAt(0)?.select()

        // setup default fragment
        startChildFragment(ProductFreeItemFragment())
    }

    private fun setupView(rootView: View) {
        extensionTabLayout = rootView.findViewById(R.id.extensionTabLayout)

        val promotionTab = LayoutInflater.from(context).inflate(R.layout.custom_tab_layout, null)
        val tvPromotionTabTitle = promotionTab.findViewById<TextView>(R.id.tvTitle)
        val ivPromotionIcon = promotionTab.findViewById<ImageView>(R.id.ivIcon)
        tvPromotionTabTitle.text = getString(R.string.tab_promotion)
        ivPromotionIcon.setImage(R.drawable.ic_freebies_selected)

        val deliveryTab = LayoutInflater.from(context).inflate(R.layout.custom_tab_layout, null)
        val tvDeliveryTabTitle = deliveryTab.findViewById<TextView>(R.id.tvTitle)
        val ivDeliveryIcon = deliveryTab.findViewById<ImageView>(R.id.ivIcon)
        tvDeliveryTabTitle.text = getString(R.string.tab_delivery)
        ivDeliveryIcon.setImage(R.drawable.ic_fast_delivery)


        extensionTabLayout.addTab(extensionTabLayout.newTab().setTag(TAB_PROMOTION_FREEBIE).setCustomView(promotionTab))
        extensionTabLayout.addTab(extensionTabLayout.newTab().setTag(TAB_DELIVERY).setCustomView(deliveryTab))

        extensionTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                when (tab?.tag) {
                    TAB_PROMOTION_FREEBIE -> {
                        tvPromotionTabTitle.text = getString(R.string.tab_promotion)
                        tvPromotionTabTitle.setTextColor(ContextCompat.getColor(context!!, R.color.graySelect))
                        ivPromotionIcon.setImage(R.drawable.ic_freebie)
                    }
                    TAB_DELIVERY -> {
                        tvDeliveryTabTitle.text = getString(R.string.tab_delivery)
                        tvDeliveryTabTitle.setTextColor(ContextCompat.getColor(context!!, R.color.graySelect))
                        ivDeliveryIcon.setImage(R.drawable.ic_fast_delivery)
                    }
                }
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.tag) {
                    TAB_PROMOTION_FREEBIE -> {
                        tvPromotionTabTitle.text = getString(R.string.tab_promotion)
                        tvPromotionTabTitle.setTextColor(ContextCompat.getColor(context!!, R.color.blackText))
                        ivPromotionIcon.setImage(R.drawable.ic_freebies_selected)
                        startChildFragment(ProductFreeItemFragment())
                    }
                    TAB_DELIVERY -> {
                        tvDeliveryTabTitle.text = getString(R.string.tab_delivery)
                        tvDeliveryTabTitle.setTextColor(ContextCompat.getColor(context!!, R.color.blackText))
                        ivDeliveryIcon.setImage(R.drawable.ic_fast_delivery_selected)
                        startChildFragment(ProductShippingOptionFragment())
                    }
                }
            }

        })
    }

    fun startChildFragment(fragment: Fragment) {
        fragmentManager?.beginTransaction()?.replace(R.id.extensionContainer, fragment)?.commit()
    }

    companion object {
        private const val TAB_PROMOTION_FREEBIE = "promotion_and_freebie"
        private const val TAB_DELIVERY = "delivery"
    }
}