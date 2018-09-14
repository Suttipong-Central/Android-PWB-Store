package cenergy.central.com.pwb_store.view

import android.content.Context
import android.text.InputFilter
import android.util.AttributeSet
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.LinearLayout
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.adapter.AddressAdapter


class PowerBuyAutoCompleteTextStroke : LinearLayout {

    private var header: PowerBuyTextView? = null
    private var requiredField: PowerBuyTextView? = null
    lateinit var inputText: AutoCompleteTextView
    private var required = false
    private var textHeader = ""
    private var textEditText = ""

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
        inputText = view.findViewById(R.id.inputText)
        inputText.setOnClickListener { inputText.showDropDown() }
        inputText.setOnFocusChangeListener { v, hasFocus ->
            if (v.id == R.id.inputText && hasFocus) {
//                inputText.error = null
                inputText.showDropDown()
            }
        }
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
        header?.text = textHeader
        if (required) {
            requiredField?.visibility = View.VISIBLE
        } else {
            requiredField?.visibility = View.GONE
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
        this.inputText.showDropDown()
    }

    fun setAdapter(adapter: AddressAdapter?) {
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