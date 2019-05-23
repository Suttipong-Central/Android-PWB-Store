package cenergy.central.com.pwb_store.view

import android.content.Context
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import cenergy.central.com.pwb_store.BuildConfig
import cenergy.central.com.pwb_store.R

class PowerBuyBackButton : RelativeLayout {

    private lateinit var pwbButton: RelativeLayout
    private lateinit var icon: ImageView
    private lateinit var textview: PowerBuyTextView
    var isDisable: Boolean = false
    var isHideIcon: Boolean = false
    private var textInput = ""

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
        val view = View.inflate(context, R.layout.view_back_button, this)
        pwbButton = view.findViewById(R.id.pwbButton)
        icon = view.findViewById(R.id.iconImage)
        textview = view.findViewById(R.id.buttonText)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        val typedArray = context.theme
                .obtainStyledAttributes(attrs, R.styleable.PowerBuyBackButton, 0, 0)

        //Get attribute values
        isDisable = typedArray.getBoolean(R.styleable.PowerBuyBackButton_disable, false)
        isHideIcon = typedArray.getBoolean(R.styleable.PowerBuyBackButton_hide_icon, false)

        typedArray.recycle()

        prepareView()

        notifyAttributeChanged()
    }

    private fun notifyAttributeChanged() {
        if (isDisable) {
            pwbButton.background = ContextCompat.getDrawable(context, R.drawable.button_unselected)
        } else {
            pwbButton.background = ContextCompat.getDrawable(context, R.drawable.button_default)
        }

        if (isHideIcon){
            icon.visibility = View.GONE
        } else {
            icon.visibility = View.VISIBLE
        }

        icon.setColorFilter(ContextCompat.getColor(context, R.color.iconArrowBack))
        textview.setTextColor(ContextCompat.getColor(context, R.color.defaultTextButtonColor))
        textview.text = textInput
    }

    fun setText(input: String){
        this.textInput = input
        notifyAttributeChanged()
    }
}