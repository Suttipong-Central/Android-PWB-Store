package cenergy.central.com.pwb_store.model

import android.os.Parcelable
import io.realm.RealmList
import io.realm.RealmModel
import io.realm.annotations.RealmClass
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.WriteWith

@Parcelize
@RealmClass
open class ProductDetailImage(
        var total: Int = 0,
        var productDetailImageItems: @WriteWith<ProductDetailImageRealmListParceler> RealmList<ProductDetailImageItem> = RealmList(),
        var viewTypeID: Int = 0,
        private var selectedProductDetailImageItem: ProductDetailImageItem? = null) : IViewType, Parcelable, RealmModel{

    override fun getViewTypeId(): Int {
        return viewTypeID
    }

    override fun setViewTypeId(id: Int) {
        viewTypeID = id
    }

    private fun validateProductDetailImageItemList() {
        var productDetailImageItem: ProductDetailImageItem
        for (i in productDetailImageItems.indices) {
            productDetailImageItem = productDetailImageItems[i]!!
            if (isSelectedProductDetailImageItemAvailable) {
                productDetailImageItem.isSelected = productDetailImageItem.imgUrl.equals(getSelectedProductDetailImageItem()!!.imgUrl, ignoreCase = true)
            }
        }
    }

    private val isSelectedProductDetailImageItemAvailable: Boolean
        get() = selectedProductDetailImageItem != null

    private fun getSelectedProductDetailImageItem(): ProductDetailImageItem? {
        return selectedProductDetailImageItem
    }

    fun setSelectedProductDetailImageItem(selectedProductDetailImageItem: ProductDetailImageItem?) {
        this.selectedProductDetailImageItem = selectedProductDetailImageItem
        validateProductDetailImageItemList()
    }
}