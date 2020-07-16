package cenergy.central.com.pwb_store.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

<<<<<<< HEAD
@Parcelize
=======
>>>>>>> 81acff96... EOR-159 show filter options by bottom sheet dialog
data class ProductFilter(
        var name: String = "",
        @SerializedName("attribute_code")
        var code: String = "",
        var items: ArrayList<FilterItem> = arrayListOf(),
        var position: Int = 0
<<<<<<< HEAD
) : Parcelable
=======
)
>>>>>>> 81acff96... EOR-159 show filter options by bottom sheet dialog

@Parcelize
data class FilterItem(
        var label: String = "",
        var value: String = "",
        var count: Int = 0,
        var isSelected: Boolean = false,
        private var viewTypeId: Int = 0
) : IViewType, Parcelable {
    override fun getViewTypeId(): Int = viewTypeId

    override fun setViewTypeId(id: Int) {
        this.viewTypeId = id
    }
}
