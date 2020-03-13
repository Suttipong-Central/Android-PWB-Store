package cenergy.central.com.pwb_store.utils

import android.app.Activity
import android.content.Context
import android.net.Uri
import cenergy.central.com.pwb_store.BuildConfig
import cenergy.central.com.pwb_store.activity.ProductDetailActivity
import cenergy.central.com.pwb_store.activity.ShoppingCartActivity
import cenergy.central.com.pwb_store.manager.preferences.AppLanguage
import cenergy.central.com.pwb_store.manager.preferences.PreferenceManager

class DeepLink(private val context: Context) {

    companion object {
        const val DEEP_LINK_EXTRA_PATH_SEGMENTS = "DEEP_LINK_EXTRA_PATH_SEGMENTS"
        const val DEEP_LINK_EXTRA_URI = "DEEP_LINK_EXTRA_URI"
        const val DEEP_LINK_EXTRA_LINK = "DEEP_LINK_EXTRA_LINK"
        private const val PATH_SHOPPING_CART = "/shopping-cart"
    }

    private val prefManager = PreferenceManager(context)

    fun checkIntent(pathSegments: Array<String>, data: Uri, link: String) {

        pathSegments.forEach {
            if (it == "th" || it == "en") {
                prefManager.setDefaultLanguage(AppLanguage.fromString(it))
            }
        }

        if (link.contains(PATH_SHOPPING_CART)) {
            // open shopping cart
            if (prefManager.cartId != null){
                startShoppingCart()
            } else {
                createCart()
            }
        } else {
            // open pdp
            if (BuildConfig.FLAVOR.contentEquals("pwb")) { // is flavor PWB
                val sku = data.getQueryParameter("sku")
                if (!sku.isNullOrEmpty()) {
                    startProductDetailBySku(sku)
                }
            } else {
                // cds, rbs
                val jda = data.getQueryParameter("jda_sku")
                if (!jda.isNullOrEmpty()) {
                    startProductDetailByJda(jda)
                }
            }
        }
    }

    private fun startProductDetailBySku(sku: String) {
        ProductDetailActivity.startActivityBySku(context, sku)
        (context as Activity).finish()
    }

    private fun startProductDetailByJda(jdaSku: String) {
        ProductDetailActivity.startActivityByJDA(context, jdaSku)
        (context as Activity).finish()
    }

    private fun startShoppingCart() {
        ShoppingCartActivity.startActivity(context)
        (context as Activity).finish()
    }

    private fun createCart() {
        CartUtils(context).createCart(object : CartListener {
            override fun onCartCreated(cartId: String) {
                prefManager.setCartId(cartId)
                startShoppingCart()
            }

            override fun onFailure(messageError: String) {
                (context as Activity).showCommonDialog(messageError)
            }
        })
    }
}