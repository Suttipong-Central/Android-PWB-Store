package cenergy.central.com.pwb_store.activity.interfaces

import cenergy.central.com.pwb_store.model.CompareProduct
import cenergy.central.com.pwb_store.model.response.CompareProductResponse

interface CompareProtocol {
    fun getCompateProducts(): List<CompareProduct>
    fun getCompareProductDetailList(): List<CompareProductResponse>
}