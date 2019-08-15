package cenergy.central.com.pwb_store.extensions

import cenergy.central.com.pwb_store.model.response.CompareProductResponse

fun List<CompareProductResponse>.getDetailList(): List<CompareProductResponse> {
    val compareDetail: ArrayList<CompareProductResponse> = arrayListOf()
    this.forEach { compareProductResponse ->
        var isAdd = false
        compareProductResponse.items.forEach {
            if (it.value != "N/A") {
                isAdd = true
            }
        }
        if (isAdd) {
            compareDetail.add(compareProductResponse)
            isAdd = false
        }
    }
    return compareDetail
}