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
) {
    companion object {
        const val COMPARE_ITEM_SHORT_DESCRIPTION_CODE = "short_description"
    }
}