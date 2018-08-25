package cenergy.central.com.pwb_store.view

import android.content.Context
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import cenergy.central.com.pwb_store.R

class PowerBuyIncreaseOrDecreaseView : LinearLayout {

    private lateinit var qtyText: PowerBuyTextView
    private lateinit var addButton: ImageButton
    private lateinit var removeButton: ImageButton
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
        addButton = view.findViewById(R.id.addButton)
        removeButton = view.findViewById(R.id.removeButton)

        addButton.setOnClickListener {
            if (qty < maximum) {
                listener?.onClickQuantity(QuantityAction.ACTION_INCREASE, qty)
            }
        }
        removeButton.setOnClickListener {
            if (qty > 1) {
                listener?.onClickQuantity(QuantityAction.ACTION_DECREASE, qty)
            }
        }
    }

    fun setOnClickQuantity(listener: OnViewClickListener) {
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
        qtyText.text = qty.toString()
        removeButton.isEnabled = qty > 1
        addButton.isEnabled = qty < maximum
        isDisableButton(addButton)
        isDisableButton(removeButton)
    }

    private fun isDisableButton(button: ImageButton) {
        if (!button.isEnabled) {
            button.setBackgroundColor(ContextCompat.getColor(context, R.color.disableButton))
        } else {
            button.setBackgroundColor(ContextCompat.getColor(context, R.color.powerBuyWhite))
        }
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
        fun onClickQuantity(action: QuantityAction, qty: Int)
    }
}