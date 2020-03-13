package cenergy.central.com.pwb_store.view

import android.content.Context
import androidx.core.content.ContextCompat
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.extensions.setImage

class PowerBuyIconButton : LinearLayout {

    private lateinit var pwbButton: LinearLayout
    private lateinit var icon: ImageView
    private lateinit var textView: PowerBuyTextView
    var isDisable: Boolean = false
    private var isHideIcon: Boolean = false
    private var isDefaultButton: Boolean = false
    private var textInput = ""
    private var colorIcon = R.color.white
    private var colorText = R.color.primaryButtonTextColor
    private var iconDefaultImage = R.drawable.ic_question_mark

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
        val view = View.inflate(context, R.layout.view_icon_button, this)
        pwbButton = view.findViewById(R.id.pwbButton)
        icon = view.findViewById(R.id.iconImage)
        textView = view.findViewById(R.id.buttonText)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        val typedArray = context.theme
                .obtainStyledAttributes(attrs, R.styleable.PowerBuyIconButton, 0, 0)

        //Get attribute values
        isDisable = typedArray.getBoolean(R.styleable. PowerBuyIconButton_btn_disable, false)
        isHideIcon = typedArray.getBoolean(R.styleable.PowerBuyIconButton_icon_invisible, false)
        isDefaultButton = typedArray.getBoolean(R.styleable.PowerBuyIconButton_btn_default, false)
        textInput = typedArray.getString(R.styleable.PowerBuyIconButton_text)?:""

        typedArray.recycle()

        prepareView()

        notifyAttributeChanged()
    }

    private fun notifyAttributeChanged() {
        if (isDisable) {
            pwbButton.isEnabled = false
            pwbButton.background = ContextCompat.getDrawable(context, R.drawable.button_unselected)
            icon.setColorFilter(ContextCompat.getColor(context, R.color.white))
            textView.setTextColor(ContextCompat.getColor(context, R.color.white))
        } else {
            pwbButton.isEnabled = true
            if(isDefaultButton){
                pwbButton.background = ContextCompat.getDrawable(context, R.drawable.button_default)
                colorIcon = R.color.defaultIconButtonColor
                colorText = R.color.defaultTextButtonColor
            } else {
                pwbButton.background = ContextCompat.getDrawable(context, R.drawable.button_primary)
            }
            icon.setColorFilter(ContextCompat.getColor(context, colorIcon))
            textView.setTextColor(ContextCompat.getColor(context, colorText))
        }

        if (isHideIcon){
            icon.visibility = View.GONE
        } else {
            icon.visibility = View.VISIBLE
        }

        icon.setImage(iconDefaultImage)
        textView.text = textInput
    }

    fun setText(input: String){
        this.textInput = input
        notifyAttributeChanged()
    }

    fun setIconColor(color: Int){
        this.colorIcon = color
        notifyAttributeChanged()
    }

    fun setTextColor(color: Int){
        this.colorText = color
        notifyAttributeChanged()
    }

    fun setImageDrawable(imageDrawable: Int){
        this.iconDefaultImage = imageDrawable
        notifyAttributeChanged()
    }

    fun setButtonDefault(default: Boolean){
        this.isDefaultButton = default
        notifyAttributeChanged()
    }

    fun setButtonDisable(disable: Boolean){
        this.isDisable = disable
        notifyAttributeChanged()
    }

    fun setButtonHideIcon(hide: Boolean){
        this.isHideIcon = hide
        notifyAttributeChanged()
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return !isDisable
    }
}