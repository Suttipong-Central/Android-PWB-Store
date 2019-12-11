package cenergy.central.com.pwb_store.view

import android.content.Context
import androidx.constraintlayout.widget.ConstraintLayout
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import cenergy.central.com.pwb_store.R

class StockIndicatorView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var stockOnline: TextView? = null
    private var stockStoreOffline: TextView? = null
    private var stockStoresOffline: TextView? = null

    init {
        View.inflate(context, R.layout.widget_stock_indicator, this)
        stockOnline = findViewById(R.id.tvStockOnline)
        stockStoreOffline = findViewById(R.id.tvStockStoreOffline)
        stockStoresOffline = findViewById(R.id.tvStockStoresOffline)

        reset()
    }

    private fun reset() {
        stockOnline?.setText(R.string.label_stock_online)
        stockStoreOffline?.setText(R.string.label_stock_store_offline)
        stockStoresOffline?.setText(R.string.label_stock_stores_offline)
    }

    fun setState(hasOnline: Boolean = false, hasStoreOffline: Boolean = false, hasStoresOffline: Boolean = false) {
        stockOnline?.setCompoundDrawablesWithIntrinsicBounds(getIcon(hasOnline), 0, 0 ,0)
        stockStoreOffline?.setCompoundDrawablesWithIntrinsicBounds(getIcon(hasStoreOffline), 0, 0 ,0)
        stockStoresOffline?.setCompoundDrawablesWithIntrinsicBounds(getIcon(hasStoresOffline), 0, 0 ,0)
    }

    fun setOnClickOtherStores(onClickListener: OnClickListener) {
        stockStoresOffline?.setOnClickListener(onClickListener)
    }

    private fun getIcon(inStock: Boolean): Int {
       return if (inStock) R.drawable.shape_circle_green else R.drawable.shape_circle_red
    }
}