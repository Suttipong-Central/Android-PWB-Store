package cenergy.central.com.pwb_store.model

import com.google.gson.annotations.SerializedName
import io.realm.RealmList
import io.realm.RealmModel
import io.realm.annotations.Ignore
import io.realm.annotations.RealmClass

@RealmClass
open class ProductFilter (
    var name: String = "",
    @SerializedName("attribute_code")
    var code: String = "",
    var items: RealmList<FilterItem> = RealmList(),
    var position: Int = 0): RealmModel

@RealmClass
open class FilterItem(
        var label: String = "",
        var value: String = "",
        var count: Int = 0,
        @Ignore
        var isSelected: Boolean = false,
        @Ignore
        private var viewTypeId: Int = 0) : IViewType, RealmModel
{
    override fun getViewTypeId(): Int = viewTypeId

    override fun setViewTypeId(id: Int) {
        this.viewTypeId = id
    }
}
