package cenergy.central.com.pwb_store.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import cenergy.central.com.pwb_store.R

class PowerBuyIncreaseOrDecreaseView : LinearLayout {

    private var qtyText: PowerBuyTextView? = null
    private var add: ImageView? = null
    private var remove: ImageView? = null
    private var qty: Int = 0
    private var maximum: Int = 10 // default
    private var listener: OnViewClickListener? = null

    constructor(context: Context) : super(context) {
        prepareView()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    private fun prepareView() {
        val view = View.inflate(context, R.layout.view_increase_or_decrease_qty, this)
        qtyText = view.findViewById(R.id.qty)
        add = view.findViewById(R.id.add)
        remove = view.findViewById(R.id.remove)

        add?.setOnClickListener { listener?.onClickQuantity(QuantityAction.ACTION_INCREASE, qty) }
        remove?.setOnClickListener { listener?.onClickQuantity(QuantityAction.ACTION_DECREASE, qty) }
    }

    fun setOnClickQuantity(listener: OnViewClickListener){
        this.listener = listener
    }

    private fun addQty() {
        if (qty < maximum) {
            qty += 1
            notifyAttributeChanged()
        }
    }

    private fun removeQty() {
        // Minimum = 1
        if (qty > 1) {
            qty -= 1
            notifyAttributeChanged()
        }
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        val typedArray = context.theme
                .obtainStyledAttributes(attrs, R.styleable.PowerBuyIncreaseOrDecreaseView, 0, 0)

        //Get attribute values
        qty = typedArray.getInteger(R.styleable.PowerBuyIncreaseOrDecreaseView_qty, 0)

        typedArray.recycle()

        prepareView()

        notifyAttributeChanged()
    }

    private fun notifyAttributeChanged() {
        qtyText?.text = "$qty"
    }

    fun getQty(): Int {
        return this.qty
    }

    fun setQty(qty: Int) {
        this.qty = qty
        notifyAttributeChanged()
    }

    fun setMaximum(max: Int) {
        this.maximum = max
        notifyAttributeChanged()
    }

    enum class QuantityAction {
        ACTION_INCREASE, ACTION_DECREASE
    }

    interface OnViewClickListener {
        fun onClickQuantity(action: QuantityAction, qty:Int)
    }
}