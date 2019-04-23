package cenergy.central.com.pwb_store.model

import com.google.gson.annotations.SerializedName

class ProductFilter (
    var name: String = "",
    @SerializedName("attribute_code")
    var code: String = "",
    var items: ArrayList<FilterItem> = arrayListOf(),
    var position: Int = 0
)

class FilterItem(
        var label: String = "",
        var value: String = "",
        var count: Int = 0
)
