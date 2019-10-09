package cenergy.central.com.pwb_store.view

import android.app.Activity
import android.content.Context
import android.support.design.widget.TextInputLayout
import android.support.v4.content.ContextCompat
import android.support.v7.widget.AppCompatAutoCompleteTextView
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.LinearLayout
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.adapter.AddressAdapter
import cenergy.central.com.pwb_store.adapter.ProductOptionAdepter
import cenergy.central.com.pwb_store.adapter.QtyAdapter


class PowerBuyAutoCompleteTextStroke : LinearLayout {

    private lateinit var layout: LinearLayout
    private lateinit var header: PowerBuyTextView
    private lateinit var requiredField: PowerBuyTextView
    private lateinit var inputText: AutoCompleteTextView
    private lateinit var inputTextLayout: TextInputLayout
    private lateinit var icon: ImageView
    private var required = false
    private var hideHeader = false
    private var textHeader = ""
    private var textEditText = ""
    private var enable: Boolean = true
    private var isQty: Boolean = false

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
        textHeader = typedArray.getString(R.styleable.PowerBuyAutoCompleteTextStroke_act_header) ?: ""
        required = typedArray.getBoolean(R.styleable.PowerBuyAutoCompleteTextStroke_act_required, false)
        hideHeader = typedArray.getBoolean(R.styleable.PowerBuyAutoCompleteTextStroke_hide_header, false)

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
            if (!isQty) {
                layout.setOnClickListener { inputText.showDropDown() }
                inputText.setOnClickListener { inputText.showDropDown() }
            } else {
                layout.setOnClickListener(null)
                inputText.setOnClickListener(null)
                icon.setOnClickListener {
                    // show dropdown and clear focus
                    inputText.showDropDown()
                    hideKeyboard(this)
                }
                inputText.setOnFocusChangeListener {  v, hasFocus ->
                    if (!hasFocus && findFocus() !is AppCompatAutoCompleteTextView) {
                        // hide keyboard when not focus
                        hideKeyboard(v)
                    } else {
                        // dismiss dropdown and show keyboard
                        showKeyboard(v)
                        this.inputText.dismissDropDown()
                    }
                }
            }
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
        if (hideHeader){
            header.visibility = View.GONE
        } else {
            header.visibility = View.VISIBLE
        }
    }

    fun getText(): String {
        return if (inputText.text.isNotEmpty()) {
            this.inputText.text.toString()
        } else {
            ""
        }
    }

    fun setIsQtyEdit(isQty: Boolean){
        this.isQty = isQty
        setEditTextInputType(InputType.TYPE_CLASS_NUMBER)
        setTextGravityCustom(Gravity.END)
        setTextLength(4)
        notifyAttributeChanged()
    }

    fun setQtyFocusable(focusable: Boolean){
        this.inputText.isFocusableInTouchMode = focusable
        this.inputText.isFocusable = focusable
    }
    fun setTextChangeListener(watcher: TextWatcher) {
        this.inputText.addTextChangedListener(watcher)
    }

    fun setOnEnterKeyListener(listener: OnKeyListener){
        this.inputText.setOnKeyListener(listener)
    }

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

    fun setTextGravityCustom(gravity: Int){
        this.inputText.gravity = gravity
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

    fun setAdapter(adapter: QtyAdapter){
        this.inputText.setAdapter(adapter)
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

    private fun hideKeyboard(view: View) {
        val inputMethodManager = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun showKeyboard(view: View) {
        val inputMethodManager = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(view, 0)
    }
}