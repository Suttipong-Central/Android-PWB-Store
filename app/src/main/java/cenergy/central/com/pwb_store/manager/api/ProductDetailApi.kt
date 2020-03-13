package cenergy.central.com.pwb_store.manager.api

class ProductDetailApi {
    /**
     * @param lang = {{store}}
     * @param sku = {{sku}}
     *
     * */
    fun getPath(lang: String, sku: String): String {
        return "rest/$lang/V2/products/$sku"
    }
}