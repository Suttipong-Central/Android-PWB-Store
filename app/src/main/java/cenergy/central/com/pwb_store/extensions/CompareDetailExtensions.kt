package cenergy.central.com.pwb_store.extensions

import cenergy.central.com.pwb_store.model.response.CompareProductResponse

fun List<CompareProductResponse>.getDetailList(): List<CompareProductResponse> {
    val compareDetail: ArrayList<CompareProductResponse> = arrayListOf()
    this.forEach { compareProductResponse ->
        var isAdd = false
        compareProductResponse.items.forEach {
            val text = it.value.toLowerCase()
            if ( text != "n/a" && text != "no" && text != "ไม่") {
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