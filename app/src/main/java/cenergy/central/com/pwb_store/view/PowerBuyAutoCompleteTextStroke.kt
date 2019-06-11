package cenergy.central.com.pwb_store.view

import android.content.Context
import android.support.design.widget.TextInputLayout
import android.support.v4.content.ContextCompat
import android.text.InputFilter
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.LinearLayout
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.adapter.AddressAdapter
import cenergy.central.com.pwb_store.adapter.ProductOptionAdepter


class PowerBuyAutoCompleteTextStroke : LinearLayout {

    private lateinit var layout: LinearLayout
    private lateinit var header: PowerBuyTextView
    private lateinit var requiredField: PowerBuyTextView
    private lateinit var inputText: AutoCompleteTextView
    private lateinit var inputTextLayout: TextInputLayout
    private lateinit var icon: ImageView
    private var required = false
    private var textHeader = ""
    private var textEditText = ""
    private var enable: Boolean = true

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
        val view = View.inflate(context, R.layout.view_auto_complete_text, this)
        header = view.findViewById(R.id.txt_header)
        requiredField = view.findViewById(R.id.required_field)
        layout = view.findViewById(R.id.layout_auto_complete)
        inputText = view.findViewById(R.id.inputText)
        inputTextLayout = view.findViewById(R.id.inputTextLayout)
        icon = view.findViewById(R.id.icon_down)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        val typedArray = context.theme
                .obtainStyledAttributes(attrs, R.styleable.PowerBuyAutoCompleteTextStroke, 0, 0)

        //Get attribute values
        textHeader = typedArray.getString(R.styleable.PowerBuyAutoCompleteTextStroke_act_header)
        required = typedArray.getBoolean(R.styleable.PowerBuyAutoCompleteTextStroke_act_required, false)

        typedArray.recycle()

        prepareView()

        notifyAttributeChanged()
    }

    private fun notifyAttributeChanged() {
        if (!enable) {
            this.textEditText = ""
            layout.setOnClickListener(null)
            inputText.setOnClickListener(null)
        } else {
            layout.setOnClickListener { inputText.showDropDown() }
            inputText.setOnClickListener { inputText.showDropDown() }
        }
        this.inputTextLayout.background = ContextCompat.getDrawable(context, R.drawable.bg_input_enable)
        this.layout.background = ContextCompat.getDrawable(context,
                if (enable) R.drawable.bg_input_enable else R.drawable.bg_input_disable)
        this.icon.setColorFilter(ContextCompat.getColor(context,
                if (enable) R.color.blackText else R.color.lightGray2))
        header.text = textHeader
        if (required) {
            requiredField.visibility = View.VISIBLE
        } else {
            requiredField.visibility = View.GONE
        }
        inputText.setText(textEditText)
    }

    fun getText(): String {
        return if (inputText.text.isNotEmpty()) {
            this.inputText.text.toString()
        } else {
            ""
        }
    }

    fun setTextChangeListener(watcher: TextWatcher) {
        this.inputText.addTextChangedListener(watcher)
    }

    fun isEnable(): Boolean = this.enable

    fun setEnableInput(enable: Boolean) {
        this.enable = enable
        notifyAttributeChanged()
    }

    fun setText(input: String) {
        this.textEditText = input
        notifyAttributeChanged()
    }

    fun setEditTextInputType(inputType: Int) {
        this.inputText.inputType = inputType
    }

    fun setTextLength(maxLength: Int) {
        val filterArray = arrayOfNulls<InputFilter>(1)
        filterArray[0] = InputFilter.LengthFilter(maxLength)
        this.inputText.filters = filterArray
    }

    fun showDropDown() {
        if (enable) {
            this.inputText.showDropDown()
        }
    }

    fun setAdapter(adapter: AddressAdapter?) {
        this.inputText.setAdapter(adapter)
    }

    fun setAdapter(adapter: ProductOptionAdepter?) {
        this.inputText.setAdapter(adapter)
    }

    fun clearAllFocus() {
        this.inputText.clearFocus()
        this.inputText.dismissDropDown()
        this.clearFocus()
//        notifyAttributeChanged()
    }

    fun setError(validText: String?) {
        this.inputText.error = validText
    }

    fun getError(): CharSequence? = this.inputText.error
}