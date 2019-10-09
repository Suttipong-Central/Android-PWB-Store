package cenergy.central.com.pwb_store.fragment

import android.annotation.SuppressLint
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

class ProductExtensionFragment : Fragment() {
    private var productDetailListener: ProductDetailListener? = null
    private var product: Product? = null

    // widget view
    private lateinit var tvPromotionTabTitle: TextView
    private lateinit var ivPromotionIcon: ImageView
    private lateinit var tvDeliveryTabTitle: TextView
    private lateinit var ivDeliveryIcon: ImageView
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
        setOnTabSelectedListener()
        // setup default tab
        extensionTabLayout.getTabAt(0)?.select()

        // setup default fragment
        startChildFragment(ProductFreeItemFragment())
    }

    private fun setOnTabSelectedListener() {
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

    @SuppressLint("InflateParams")
    private fun setupView(rootView: View) {
        extensionTabLayout = rootView.findViewById(R.id.extensionTabLayout)

        val promotionTab = LayoutInflater.from(context).inflate(R.layout.custom_tab_layout, null)
        tvPromotionTabTitle = promotionTab.findViewById(R.id.tvTitle)
        ivPromotionIcon = promotionTab.findViewById(R.id.ivIcon)
        tvPromotionTabTitle.text = getString(R.string.tab_promotion)
        ivPromotionIcon.setImage(R.drawable.ic_freebies_selected)

        val deliveryTab = LayoutInflater.from(context).inflate(R.layout.custom_tab_layout, null)
        tvDeliveryTabTitle = deliveryTab.findViewById(R.id.tvTitle)
        ivDeliveryIcon = deliveryTab.findViewById(R.id.ivIcon)
        tvDeliveryTabTitle.text = getString(R.string.tab_delivery)
        ivDeliveryIcon.setImage(R.drawable.ic_fast_delivery)


        extensionTabLayout.addTab(extensionTabLayout.newTab().setTag(TAB_PROMOTION_FREEBIE).setCustomView(promotionTab))
        extensionTabLayout.addTab(extensionTabLayout.newTab().setTag(TAB_DELIVERY).setCustomView(deliveryTab))
    }

    fun startChildFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction().replace(R.id.extensionContainer, fragment).commitAllowingStateLoss()
    }

    companion object {
        private const val TAB_PROMOTION_FREEBIE = "promotion_and_freebie"
        private const val TAB_DELIVERY = "delivery"
    }
}