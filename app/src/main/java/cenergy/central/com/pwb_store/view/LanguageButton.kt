package cenergy.central.com.pwb_store.view

import android.content.Context
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import cenergy.central.com.pwb_store.R

class LanguageButton : RadioGroup {

    // widget view
    private lateinit var languageToggleGroup: RadioGroup
    private lateinit var thaiToggle: RadioButton
    private lateinit var engToggle: RadioButton

    constructor(context: Context) : super(context) {
        prepareView()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    private fun prepareView() {
        val view = View.inflate(context, R.layout.view_switch_language, this)
        languageToggleGroup = view.findViewById(R.id.toggle_language)
        thaiToggle = view.findViewById(R.id.lang_th)
        engToggle = view.findViewById(R.id.lang_en)

        languageToggleGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.lang_th -> {
                    thaiToggle.setTextColor(ContextCompat.getColor(context, R.color.powerBuyPurple))
                    engToggle.setTextColor(ContextCompat.getColor(context, R.color.powerBuyWhite))
//                    languageListener?.onChangedLanguage(AppLanguage.TH)
                }
                R.id.lang_en -> {
                    engToggle.setTextColor(ContextCompat.getColor(context, R.color.powerBuyPurple))
                    thaiToggle.setTextColor(ContextCompat.getColor(context, R.color.powerBuyWhite))

//                    languageListener?.onChangedLanguage(AppLanguage.EN)
                }
            }
        }
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        val typedArray = context.theme
                .obtainStyledAttributes(attrs, R.styleable.PowerBuyAutoCompleteTextStroke, 0, 0)

        //Get attribute values
//        textHeader = typedArray.getString(R.styleable.PowerBuyAutoCompleteTextStroke_act_header)
//        required = typedArray.getBoolean(R.styleable.PowerBuyAutoCompleteTextStroke_act_required, false)

        typedArray.recycle()

        prepareView()

        notifyAttributeChanged()
    }

    private fun notifyAttributeChanged() {

    }
}