package cenergy.central.com.pwb_store.model.response

import com.google.gson.annotations.SerializedName

class CompareProductResponse(
        @SerializedName("attribute_code")
        val code: String = "",
        @SerializedName("attribute_label")
        val label: String = "",
        val items: ArrayList<CompareItem>
)

class CompareItem(
        val sku: String = "",
        val value: String = ""
)