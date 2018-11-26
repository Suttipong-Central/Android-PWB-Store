package cenergy.central.com.pwb_store.model.response

import cenergy.central.com.pwb_store.model.Product
import com.google.gson.annotations.SerializedName

class ProductResponse(
        @SerializedName("items")
        var products: ArrayList<Product>? = arrayListOf(),
        var currentPage: Int = 0,
        @SerializedName("total_count")
        var totalCount: Int = 0) {

    fun isFirstPage(): Boolean {
        return currentPage == 1
    }

    companion object {
        fun asProductResponse(keyword: String, productSearchResponse: ProductSearchResponse): ProductResponse? {
            var productResponse: ProductResponse? = null
            val products: ArrayList<Product> = arrayListOf()
            var totalCount = 0
            if (productSearchResponse.hasPopularProducts() && productSearchResponse.hasPopularSearch()) {
//                productSearchResponse.popularProducts?.forEach { popularProduct ->
//                    val product = Product.asProduct(popularProduct)
//                    if (products.none { it.sku == product.sku }) {
//                        products.add(product)
//                    }
//                }
//                productSearchResponse.popularSearches?.forEach { popularSearch ->
//                    if(popularSearch.title?.toLowerCase() == keyword.toLowerCase()){
//                        popularSearch.productList?.forEach { productSearch ->
//                            val product = Product.asProduct(productSearch)
//                            if(products.firstOrNull{ it.sku == product.sku} == null){
//                                products.add(product)
//                            }
//                        }
//                        totalCount = popularSearch.result?: 0
//                    }
//                }
                val popularSearch = productSearchResponse.popularSearches!![0]
                popularSearch.productList?.forEach { productSearch ->
                    val product = Product.asProduct(productSearch)
                    if (products.none { it.sku == product.sku }) {
                        products.add(product)
                    }
                }
                totalCount = popularSearch.result ?: 0
                productResponse = ProductResponse(products = products, totalCount = totalCount)

            } else if (productSearchResponse.hasPopularProducts()) {
                productSearchResponse.popularProducts?.forEach { popularProduct ->
                    val product = Product.asProduct(popularProduct)
                    if (products.none { it.sku == product.sku }) {
                        products.add(product)
                    }
                }
                productResponse = ProductResponse(products = products, totalCount = products.size)
            } else if (productSearchResponse.hasPopularSearch()) {
                val popularSearch = productSearchResponse.popularSearches!![0]
                popularSearch.productList?.forEach { productSearch ->
                    val product = Product.asProduct(productSearch)
                    if (products.none { it.sku == product.sku }) {
                        products.add(product)
                    }
                }
                totalCount = popularSearch.result ?: 0
                productResponse = ProductResponse(products = products, totalCount = totalCount)
            }
            return productResponse
        }
    }
}