package cenergy.central.com.pwb_store.utils

import android.app.Activity
import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

enum class Screen(val id: String) {
    LOGIN("Login"),
    CATEGORY_LV1("CategoryLv1"),
    CATEGORY_LV2("CategoryLv2"),
    PRODUCT_LIST("ProductList"),
    PRODUCT_DETAIL("ProductDetail"),
    SEARCH_BY_BARCODE("SearchByBarcode"),
    COMPARE_PRODUCT("CompareProduct"),
    SHOPPING_CART("ShoppingCart"),
    HISTORY("History"),
    INVENTORY_CHECKS("InventoryChecks"),
    START_CHECKOUT("StartCheckout"),
    SHIPING_AND_BILLING_ADDRESSES("ShippingAndBillingAddresses"),
    SELECT_DELIVERY("SelectDelivery"),
    SELECT_PAYMENT("SelectPayment"),
    ORDER_SUCCESS("OrderSuccess")
}

class Analytics(context: Context) {
    private var firebaseAnalytics = FirebaseAnalytics.getInstance(context)
    private var context: Context? = context

    // region track screen
    fun trackScreen(screen: Screen) {
        firebaseAnalytics.setCurrentScreen(context as Activity, screen.id, null)
    }

    // region track event
    private fun trackEvent(eventName: String, params: Bundle) {
        firebaseAnalytics.logEvent(eventName, params)
    }

    fun trackFollowAlertEvent(alertId: String, alertName: String) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, alertId)
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, alertName)
        trackEvent("follow_alert_detail", bundle)
    }

}