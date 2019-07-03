package cenergy.central.com.pwb_store.model

import com.google.gson.annotations.SerializedName

data class ProductFilter (
    var name: String = "",
    @SerializedName("attribute_code")
    var code: String = "",
    var items: ArrayList<FilterItem> = arrayListOf(),
    var position: Int = 0
)

data class FilterItem(
        var label: String = "",
        var value: String = "",
        var count: Int = 0,
        var isSelected: Boolean = false,
        private var viewTypeId: Int = 0
) : IViewType {
    override fun getViewTypeId(): Int = viewTypeId

    override fun setViewTypeId(id: Int) {
        this.viewTypeId = id
    }
}
