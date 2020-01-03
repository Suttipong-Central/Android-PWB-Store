package cenergy.central.com.pwb_store.view

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import cenergy.central.com.pwb_store.R
import com.google.android.material.textfield.TextInputEditText


class PowerBuyEditTextBorder : LinearLayout {

    private lateinit var inputLayout: LinearLayout
    private var header: PowerBuyTextView? = null
    private var requiredField: PowerBuyTextView? = null
    lateinit var editText: TextInputEditText
    private var required = false
    private var textHeader = ""
    private var textEditText = ""
    private var iconAtStart: Drawable? = null
    private var isEnable: Boolean = true

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
        inputLayout = view.findViewById(R.id.input_layout)
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
        textHeader = typedArray.getString(R.styleable.PowerBuyEditTextBorder_header) ?: ""
        required = typedArray.getBoolean(R.styleable.PowerBuyEditTextBorder_required, false)
        iconAtStart = typedArray.getDrawable(R.styleable.PowerBuyEditTextBorder_pwb_icon)

        typedArray.recycle()

        prepareView()

        notifyAttributeChanged()
    }

    private fun notifyAttributeChanged() {
        inputLayout.background = ContextCompat.getDrawable(context,
                if (isEnable) R.drawable.bg_input_enable else R.drawable.bg_input_disable)
        header?.text = textHeader
        requiredField?.visibility = if (required) View.VISIBLE else View.GONE
        editText.setText(textEditText)

        // set icon
        iconAtStart?.let {
            setDrawableStart(it)
        }
    }

    fun getText(): String {
        return if (editText.text != null && editText.text!!.trim() != "") {
            this.editText.text.toString()
        } else {
            ""
        }
    }

    fun setText(input: String?) {
        this.textEditText = input ?: ""
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

    fun setDrawableStart(icon: Int) {
        editText.setCompoundDrawablesWithIntrinsicBounds(icon, 0, 0, 0)
        editText.compoundDrawablePadding = 16
    }

    fun setDrawableStart(icon: Drawable) {
        editText.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null)
        editText.compoundDrawablePadding = 16
    }

    fun setEnableInput(isEnable: Boolean) {
        this.isEnable = isEnable
        editText.isEnabled = this.isEnable
        notifyAttributeChanged()
    }

    fun setOnTextChanging(callback: TextWatcher) {
        editText.addTextChangedListener(callback)
    }
}