package cenergy.central.com.pwb_store.utils

import android.app.Activity
import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

enum class Screen(val id: String) {
    LOGIN("Login"),
    CATEGORY_LV1("CategoryLv1"),
    CATEGORY_LV2("CategoryLv2"),
    SEARCH("Search"),
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

enum class CustomerSource(val source: String) {
    BU("BU"), T1("T1"), HDL("HDL"), NEW_USER("NewUser")
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

    fun trackLoginSuccess(origin: String?) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ORIGIN, origin ?: "not_found")
        trackEvent(FirebaseAnalytics.Event.LOGIN, bundle)
    }

    fun trackViewCategoryLv1(categoryId: String, categoryName: String) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, categoryId)
        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, categoryName)
        trackEvent("view_category", bundle)
    }

    fun trackSearch(keyword: String) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.SEARCH_TERM, keyword)
        trackEvent(FirebaseAnalytics.Event.SEARCH, bundle)
    }

    fun trackViewProductList(categoryId: String, categoryName: String) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, categoryId)
        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, categoryName)
        trackEvent(FirebaseAnalytics.Event.VIEW_ITEM_LIST, bundle)
    }

    fun trackViewItem(productSku: String) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, productSku)
        trackEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle)
    }

    fun trackAddToCart(productSku: String, type: String) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, productSku)
        bundle.putString(FirebaseAnalytics.Param.METHOD, type)
        trackEvent(FirebaseAnalytics.Event.ADD_TO_CART, bundle)
    }

    fun trackViewStoreStock(productSku: String) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, productSku)
        trackEvent("view_store_stock", bundle)
    }

    fun trackAddToCompare(productSku: String) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, productSku)
        trackEvent("add_to_compare", bundle)
    }

    fun trackSearchByBarcode(barcode: String) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.SEARCH_TERM, barcode)
        trackEvent("search_by_barcode", bundle)
    }

    fun trackStartCheckout() {
        trackEvent("start_checkout", Bundle())
    }

    fun trackRemoveItemFromCart(productSku: String) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, productSku)
        trackEvent(FirebaseAnalytics.Event.REMOVE_FROM_CART, bundle)
    }

    fun trackCustomerSource(customerSource: CustomerSource) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.SOURCE, customerSource.source)
        trackEvent("checkout_customer_source", bundle)
    }

    fun trackSelectDelivery(deliveryMethod: String) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.METHOD, deliveryMethod)
        trackEvent("select_delivery", bundle)
    }

    fun trackSelectPayment(paymentMethod: String) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.METHOD, paymentMethod)
        trackEvent("select_payment", bundle)
    }

    fun trackOrderSuccess(paymentMethod: String, deliveryMethod: String, origin: String) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.METHOD, paymentMethod)
        bundle.putString(FirebaseAnalytics.Param.SHIPPING, deliveryMethod)
        bundle.putString(FirebaseAnalytics.Param.ORIGIN, origin)
        trackEvent("order_success", bundle)
    }
}