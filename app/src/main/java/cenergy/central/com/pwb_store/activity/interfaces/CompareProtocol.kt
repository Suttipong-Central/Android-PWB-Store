package cenergy.central.com.pwb_store.activity.interfaces

import cenergy.central.com.pwb_store.model.response.CompareProductResponse

interface CompareProtocol {
    fun getCompareProductDetailList(): List<CompareProductResponse>
}