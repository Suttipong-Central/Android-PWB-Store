package cenergy.central.com.pwb_store.view

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import cenergy.central.com.pwb_store.R

class ProductCompareView : FrameLayout {
    // widget view
    private var compareLayout: ConstraintLayout? = null
    private var compareTextView: TextView? = null
    private var clearButton: Button? = null
    private var compareButton: Button? = null

    // data
    private var count: Int = 0
    private var listener: ProductCompareViewListener? = null

    constructor(context: Context) : super(context) {
        prepareView()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        // get attributes?
        prepareView()
        notifyAttributeChanged()
    }

    private fun prepareView() {
        val view = View.inflate(context, R.layout.widget_product_compare_view, this)
        this.visibility = View.GONE
        compareLayout = view.findViewById(R.id.compareLayout)
        compareTextView = view.findViewById(R.id.compareText)
        clearButton = view.findViewById(R.id.resetButton)
        compareButton = view.findViewById(R.id.compareButton)

        // set onclick
        clearButton?.setOnClickListener {
            listener?.resetCompareProducts()
            context?.let {
                Toast.makeText(it, it.getString(R.string.compare_cleared), Toast.LENGTH_SHORT).show()
            }
        }
        compareButton?.setOnClickListener { listener?.openComparePage() }
    }

    private fun notifyAttributeChanged() {
        // root view
        this.visibility = if (count > 0) View.VISIBLE else View.GONE
        compareLayout?.setOnClickListener {
            if (count >= 2) {
                listener?.openComparePage()
            }
        }
        // child view
        compareButton?.text = context.getString(R.string.product_compare)
        clearButton?.text = context.getString(R.string.compare_clear_all)
        compareTextView?.text = context.getString(if (count > 1) R.string.format_compare_products else R.string.format_add_more_to_compare, count)
        compareButton?.visibility = if (count >= 2) View.VISIBLE else View.GONE
    }

    fun addProductCompareViewListener(listener: ProductCompareViewListener) {
        this.listener = listener
    }

    fun setCompareCount(count: Int) {
        this.count = count
        notifyAttributeChanged()
    }

    fun refreshView() {
        notifyAttributeChanged()
    }

    interface ProductCompareViewListener {
        fun resetCompareProducts()
        fun openComparePage()
    }
}