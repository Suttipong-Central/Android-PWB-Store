package cenergy.central.com.pwb_store.view

import android.content.Context
import android.support.design.widget.TextInputEditText
import android.text.InputFilter
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import cenergy.central.com.pwb_store.R
import android.app.Activity
import android.graphics.drawable.Drawable
import android.view.inputmethod.InputMethodManager


class PowerBuyEditTextBorder : LinearLayout {

    private var header: PowerBuyTextView? = null
    private var requiredField: PowerBuyTextView? = null
    lateinit var editText: TextInputEditText
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
        val view = View.inflate(context, R.layout.view_edit_text_boder, this)
        header = view.findViewById(R.id.txt_header)
        requiredField = view.findViewById(R.id.required_field)
        editText = view.findViewById(R.id.edit_text)
        editText.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus && findFocus() !is PowerBuyEditTextBorder) {
                hideKeyboard(v)
            } else {
                showKeyboard(v)
            }
        }
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        val typedArray = context.theme
                .obtainStyledAttributes(attrs, R.styleable.PowerBuyEditTextBorder, 0, 0)

        //Get attribute values
        textHeader = typedArray.getString(R.styleable.PowerBuyEditTextBorder_header)
        required = typedArray.getBoolean(R.styleable.PowerBuyEditTextBorder_required, false)

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
        editText.setText(textEditText)
    }

    fun getText(): String {
        return if(editText.text != null) {
            this.editText.text.toString()
        } else {
            ""
        }
    }

    fun setText(input: String) {
        this.textEditText = input
        notifyAttributeChanged()
    }

    fun setEditTextInputType(inputType: Int) {
        this.editText.inputType = inputType
    }

    fun setTextLength(maxLength: Int) {
        val filterArray = arrayOfNulls<InputFilter>(1)
        filterArray[0] = InputFilter.LengthFilter(maxLength)
        this.editText.filters = filterArray
    }

    fun setError(validText: String?) {
        this.editText.error = validText
    }

    fun getError(): CharSequence? = this.editText.error

    fun hideKeyboard(view: View) {
        val inputMethodManager = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun showKeyboard(view: View) {
        val inputMethodManager = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(view, 0)
    }

    fun setDrawableStart(icon: Int){
        editText.setCompoundDrawablesWithIntrinsicBounds(icon, 0, 0, 0)
        editText.compoundDrawablePadding = 16
    }
}