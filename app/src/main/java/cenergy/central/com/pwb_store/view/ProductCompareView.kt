package cenergy.central.com.pwb_store.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import cenergy.central.com.pwb_store.R

class ProductCompareView : ConstraintLayout {
    // widget view
    private var compareTextView: TextView? = null
    private var clearButton: Button? = null
    private var compareButton: Button? = null

    // data
    private var count: Int = 0

    constructor(context: Context) : super(context) {
        prepareView()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        prepareView()
        notifyAttributeChanged()
    }

    private fun prepareView() {
        val view = View.inflate(context, R.layout.widget_product_compare_view, this)
        this.visibility = View.INVISIBLE
        compareTextView = view.findViewById(R.id.compareText)
        clearButton = view.findViewById(R.id.resetButton)
        compareButton = view.findViewById(R.id.compareButton)

        // set onclick
        clearButton?.setOnClickListener { }

        compareButton?.setOnClickListener { }
    }

    private fun notifyAttributeChanged() {
        this.visibility = if (count > 0) View.VISIBLE else View.INVISIBLE
        compareTextView?.text = context.getString(if (count > 1) R.string.format_compare_products else R.string.format_add_more_to_compare, count)
    }

    fun setCompareCount(count: Int) {
        this.count = count
        notifyAttributeChanged()
    }
}