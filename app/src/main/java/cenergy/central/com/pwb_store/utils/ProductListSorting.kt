package cenergy.central.com.pwb_store.utils

import android.content.Context
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.model.SortingItem

object ProductListSorting {
    fun getSortingItems(context: Context) = arrayListOf(
            SortingItem(1, context.getString(R.string.sorting_view_count), "view_count", "DESC", "0", false),
            SortingItem(2, context.getString(R.string.sorting_best_sellers), "sold_qty", "DESC", "0", false),
            SortingItem(3, context.getString(R.string.sorting_biggest_saving), "price_discount", "DESC", "0", false),
            SortingItem(4, context.getString(R.string.sorting_new), "created_at", "DESC", "0", false),
            SortingItem(5, context.getString(R.string.sorting_price_asc), "price", "ASC", "0", false),
            SortingItem(6, context.getString(R.string.sorting_price_desc), "price", "DESC", "0", false),
            SortingItem(7, context.getString(R.string.sorting_brands_asc), "brand", "ASC", "0", false),
            SortingItem(8, context.getString(R.string.sorting_brands_desc), "brand", "DESC", "0", false),
            SortingItem(9, context.getString(R.string.sorting_most_favorites), "favorite_count", "DESC", "0", false)
    )
}